package com.tms.ticket_management.service;

import com.tms.ticket_management.model.Ticket;
import com.tms.ticket_management.model.User;
import com.tms.ticket_management.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket createTicket(Ticket ticket) {
        // DB constraint: tickets.is_used is NOT NULL, and we default it to false on create.
        if(ticket.getIsUsed() == null) {
            ticket.setIsUsed(false);
        }
        // qr_code is nullable in DB, but other parts of the app may expect it to exist.
        if(ticket.getQrCode() == null) {
            ticket.setQrCode(UUID.randomUUID().toString());
        }
        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepository.findByCreatedBy(user);
    }

    public List<Ticket> getTicketsByStatus(Ticket.Status status) {
        return ticketRepository.findByStatus(status);
    }

    public Ticket updateTicket(Long id, Ticket updatedTicket) {
        return ticketRepository.findById(id).map(ticket -> {
            ticket.setTitle(updatedTicket.getTitle());
            ticket.setDescription(updatedTicket.getDescription());
            ticket.setStatus(updatedTicket.getStatus());
            ticket.setPriority(updatedTicket.getPriority());
            ticket.setAssignedTo(updatedTicket.getAssignedTo());
            return ticketRepository.save(ticket);
        }).orElseThrow(() -> new RuntimeException("Ticket not found with id: "+id));
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public Optional<Ticket> getTicketByQRCode(String qrCode) {
        return ticketRepository.findByQrCode(qrCode);
    }

    public Ticket markTicketAsUsed(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found with QR code: " + qrCode));
        ticket.setStatus(Ticket.Status.CLOSED);
        ticket.setIsUsed(true);
        return ticketRepository.save(ticket);
    }
}