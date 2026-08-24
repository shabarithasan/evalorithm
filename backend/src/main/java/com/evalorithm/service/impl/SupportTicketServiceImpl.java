package com.evalorithm.service.impl;

import com.evalorithm.dto.request.SupportTicketRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SupportTicketResponse;
import com.evalorithm.entity.SupportTicket;
import com.evalorithm.entity.User;
import com.evalorithm.enums.SupportTicketStatus;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.SupportTicketRepository;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.SupportTicketService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SupportTicketResponse createTicket(SupportTicketRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        SupportTicket ticket = SupportTicket.builder()
                .user(user)
                .subject(request.getSubject())
                .description(request.getDescription())
                .status(SupportTicketStatus.OPEN)
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .build();

        ticket = supportTicketRepository.save(ticket);
        return mapToResponse(ticket);
    }

    @Override
    @Transactional
    public SupportTicketResponse updateTicket(Long ticketId, SupportTicketStatus status, String resolution) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        if (status != null) ticket.setStatus(status);
        if (resolution != null) ticket.setResolution(resolution);
        if (status == SupportTicketStatus.RESOLVED || status == SupportTicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        ticket = supportTicketRepository.save(ticket);
        return mapToResponse(ticket);
    }

    @Override
    @Transactional
    public SupportTicketResponse assignTicket(Long ticketId, Long adminId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", adminId));

        ticket.setAssignedTo(admin);
        ticket.setStatus(SupportTicketStatus.IN_PROGRESS);
        ticket = supportTicketRepository.save(ticket);
        return mapToResponse(ticket);
    }

    @Override
    public List<SupportTicketResponse> getMyTickets(Long userId) {
        return supportTicketRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PageResponse<SupportTicketResponse> getAllTickets(Pageable pageable) {
        Page<SupportTicket> page = supportTicketRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<SupportTicketResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    private SupportTicketResponse mapToResponse(SupportTicket ticket) {
        return SupportTicketResponse.builder()
                .id(ticket.getId())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus().name())
                .priority(ticket.getPriority())
                .assignedToName(ticket.getAssignedTo() != null ?
                        ticket.getAssignedTo().getFirstName() + " " + ticket.getAssignedTo().getLastName() : null)
                .resolution(ticket.getResolution())
                .createdAt(ticket.getCreatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }
}
