package com.tms.ticket_management.service;

import com.tms.ticket_management.model.Event;
import com.tms.ticket_management.model.Ticket;
import com.tms.ticket_management.model.ValidationResultEntity;
import com.tms.ticket_management.repository.TicketRepository;
import com.tms.ticket_management.repository.ValidationResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventService eventService;
    private final ValidationResultRepository validationResultRepository;
    private final Random random = new Random();

    @Transactional
    public String purchaseTickets(String eventId, Map<String, Integer> quantities, String name, String email) {
        Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        String bookingRef = "EH" + LocalDate.now().getYear() % 100 + "-" + (1000 + random.nextInt(9000));
        boolean atLeastOne = false;

        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            String typeName = entry.getKey();
            int qty = entry.getValue();
            if (qty <= 0) continue;

            Event.TicketType ticketType = event.getTicketTypes().stream()
                    .filter(t -> t.getTypeName().equalsIgnoreCase(typeName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Ticket type " + typeName + " not found"));

            int remaining = ticketType.getTotalQuantity() - ticketType.getSoldQuantity();
            if (qty > remaining) {
                throw new IllegalArgumentException("Requested quantity for " + typeName + " is not available");
            }

            // Update sold quantity in Event
            ticketType.setSoldQuantity(ticketType.getSoldQuantity() + qty);

            // Create Ticket
            Ticket ticket = new Ticket();
            ticket.setBookingRef(bookingRef);
            ticket.setEventId(event.getId());
            ticket.setEventName(event.getName());
            ticket.setTicketType(ticketType.getTypeName());
            ticket.setQuantity(qty);
            ticket.setTotalAmount(ticketType.getPrice() * qty);
            ticket.setAttendeeName(name);
            ticket.setAttendeeEmail(email.toLowerCase(Locale.ROOT));
            ticket.setStatus("CONFIRMED");
            ticket.setPurchasedAt(LocalDateTime.now());
            ticket.setQrCode(UUID.randomUUID().toString());
            ticket.setIsUsed(false);

            ticketRepository.save(ticket);
            atLeastOne = true;
        }

        if (!atLeastOne) {
            throw new IllegalArgumentException("Must select at least one ticket to purchase");
        }

        eventService.save(event);
        return bookingRef;
    }

    public List<Ticket> getTicketsByAttendee(String email) {
        return ticketRepository.findByAttendeeEmailIgnoreCaseOrderByPurchasedAtDesc(email);
    }

    @Transactional
    public ValidationResultEntity validateTicket(String bookingRef) {
        String normalized = bookingRef == null ? "" : bookingRef.trim();
        List<Ticket> tickets = ticketRepository.findByAttendeeEmailIgnoreCaseOrderByPurchasedAtDesc(""); // DUMMY load, we find by bookingRef
        Optional<Ticket> ticketOpt = ticketRepository.findByBookingRefIgnoreCase(normalized);

        ValidationResultEntity result = new ValidationResultEntity();
        result.setTime(LocalDateTime.now());
        result.setBookingRef(normalized);

        if (ticketOpt.isEmpty()) {
            result.setStatus("INVALID");
            result.setMessage("This booking reference was not found");
        } else {
            Ticket ticket = ticketOpt.get();
            result.setTicket(ticket);
            result.setAttendeeName(ticket.getAttendeeName());
            result.setEventName(ticket.getEventName());

            if ("USED".equalsIgnoreCase(ticket.getStatus()) || Boolean.TRUE.equals(ticket.getIsUsed())) {
                result.setStatus("ALREADY USED");
                result.setMessage("This ticket has already been used");
            } else {
                result.setStatus("VALID");
                result.setMessage("Ticket is ready for entry");
            }
        }

        return validationResultRepository.save(result);
    }

    @Transactional
    public void markTicketAsUsed(String bookingRef) {
        Ticket ticket = ticketRepository.findByBookingRefIgnoreCase(bookingRef)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with booking reference: " + bookingRef));
        ticket.setStatus("USED");
        ticket.setIsUsed(true);
        ticketRepository.save(ticket);
    }

    public List<ValidationResultEntity> getRecentValidations() {
        List<ValidationResultEntity> all = validationResultRepository.findByOrderByTimeDesc();
        if (all.size() > 10) {
            return all.subList(0, 10);
        }
        return all;
    }

    public long getTotalValidatedToday() {
        LocalDate today = LocalDate.now();
        return validationResultRepository.findAll().stream()
                .filter(v -> v.getTime().toLocalDate().isEqual(today))
                .count();
    }

    public long getCountForStatusToday(String status) {
        LocalDate today = LocalDate.now();
        return validationResultRepository.findAll().stream()
                .filter(v -> v.getTime().toLocalDate().isEqual(today))
                .filter(v -> v.getStatus().equalsIgnoreCase(status))
                .count();
    }

    public Optional<Ticket> getTicketByQRCode(String qrCode) {
        return ticketRepository.findByQrCode(qrCode);
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    @Transactional
    public Ticket markTicketAsUsedByQRCode(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with QR code: " + qrCode));
        ticket.setStatus("USED");
        ticket.setIsUsed(true);
        return ticketRepository.save(ticket);
    }
}
