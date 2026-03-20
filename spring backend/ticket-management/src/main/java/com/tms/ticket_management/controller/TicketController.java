package com.tms.ticket_management.controller;

import com.tms.ticket_management.dto.TicketDTO;
import com.tms.ticket_management.model.Ticket;
import com.tms.ticket_management.model.User;
import com.tms.ticket_management.service.TicketService;
import com.tms.ticket_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(@RequestBody Ticket ticket, Authentication authentication) {
        String username=authentication.getName();
        // tickets.created_by is NOT NULL in DB, so we must set it from the authenticated user.
        User user=userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: "+username));
        ticket.setCreatedBy(user);
        return ResponseEntity.ok(TicketDTO.fromTicket(ticketService.createTicket(ticket)));
    }

    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets()
                .stream()
                .map(TicketDTO::fromTicket)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(TicketDTO::fromTicket)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TicketDTO>> getTicketsByStatus(@PathVariable Ticket.Status status) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status)
                .stream()
                .map(TicketDTO::fromTicket)
                .toList());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketDTO>> getTicketsByUser(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(ticketService.getTicketsByUser(user)
                        .stream()
                        .map(TicketDTO::fromTicket)
                        .toList()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        return ResponseEntity.ok(TicketDTO.fromTicket(ticketService.updateTicket(id, ticket)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}