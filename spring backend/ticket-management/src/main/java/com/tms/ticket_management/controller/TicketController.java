package com.tms.ticket_management.controller;

import com.tms.ticket_management.model.Ticket;
import com.tms.ticket_management.model.ValidationResultEntity;
import com.tms.ticket_management.service.TicketService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<Map<String, String>> purchaseTickets(@RequestBody PurchaseRequest request) {
        String bookingRef = ticketService.purchaseTickets(
                request.getEventId(),
                request.getQuantities(),
                request.getName(),
                request.getEmail()
        );
        return ResponseEntity.ok(Map.of("bookingRef", bookingRef));
    }

    @GetMapping("/attendee")
    public ResponseEntity<List<Ticket>> getTicketsByAttendee(@RequestParam String email) {
        return ResponseEntity.ok(ticketService.getTicketsByAttendee(email));
    }

    @GetMapping("/validate/{bookingRef}")
    public ResponseEntity<ValidationResultEntity> validateTicket(@PathVariable String bookingRef) {
        return ResponseEntity.ok(ticketService.validateTicket(bookingRef));
    }

    @PostMapping("/use/{bookingRef}")
    public ResponseEntity<Void> markTicketAsUsed(@PathVariable String bookingRef) {
        ticketService.markTicketAsUsed(bookingRef);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validations/today")
    public ResponseEntity<Long> getTotalValidatedToday() {
        return ResponseEntity.ok(ticketService.getTotalValidatedToday());
    }

    @GetMapping("/validations/status-today/{status}")
    public ResponseEntity<Long> getCountForStatusToday(@PathVariable String status) {
        return ResponseEntity.ok(ticketService.getCountForStatusToday(status));
    }

    @GetMapping("/validations/recent")
    public ResponseEntity<List<ValidationResultEntity>> getRecentValidations() {
        return ResponseEntity.ok(ticketService.getRecentValidations());
    }

    @Data
    public static class PurchaseRequest {
        private String eventId;
        private Map<String, Integer> quantities;
        private String name;
        private String email;
    }
}