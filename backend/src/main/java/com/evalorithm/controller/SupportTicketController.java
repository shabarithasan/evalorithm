package com.evalorithm.controller;

import com.evalorithm.dto.request.SupportTicketRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SupportTicketResponse;
import com.evalorithm.enums.SupportTicketStatus;
import com.evalorithm.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support-tickets")
@RequiredArgsConstructor
@Tag(name = "Support Tickets", description = "Support ticket management endpoints")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    @Operation(summary = "Create support ticket")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> create(
            @Valid @RequestBody SupportTicketRequest request,
            @RequestParam Long userId) {
        SupportTicketResponse response = supportTicketService.createTicket(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Support ticket created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update ticket status")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> update(
            @PathVariable Long id,
            @RequestParam SupportTicketStatus status,
            @RequestParam(required = false) String resolution) {
        SupportTicketResponse response = supportTicketService.updateTicket(id, status, resolution);
        return ResponseEntity.ok(ApiResponse.success("Ticket updated", response));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign ticket to admin")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> assign(
            @PathVariable Long id,
            @RequestParam Long adminId) {
        SupportTicketResponse response = supportTicketService.assignTicket(id, adminId);
        return ResponseEntity.ok(ApiResponse.success("Ticket assigned", response));
    }

    @GetMapping("/my/{userId}")
    @Operation(summary = "Get my tickets")
    public ResponseEntity<ApiResponse<List<SupportTicketResponse>>> getMyTickets(@PathVariable Long userId) {
        List<SupportTicketResponse> response = supportTicketService.getMyTickets(userId);
        return ResponseEntity.ok(ApiResponse.success("My tickets retrieved", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all tickets (admin)")
    public ResponseEntity<ApiResponse<PageResponse<SupportTicketResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<SupportTicketResponse> response = supportTicketService.getAllTickets(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(ApiResponse.success("All tickets retrieved", response));
    }
}
