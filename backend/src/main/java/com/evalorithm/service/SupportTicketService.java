package com.evalorithm.service;

import com.evalorithm.dto.request.SupportTicketRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SupportTicketResponse;
import com.evalorithm.enums.SupportTicketStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupportTicketService {

    SupportTicketResponse createTicket(SupportTicketRequest request, Long userId);

    SupportTicketResponse updateTicket(Long ticketId, SupportTicketStatus status, String resolution);

    SupportTicketResponse assignTicket(Long ticketId, Long adminId);

    List<SupportTicketResponse> getMyTickets(Long userId);

    PageResponse<SupportTicketResponse> getAllTickets(Pageable pageable);
}
