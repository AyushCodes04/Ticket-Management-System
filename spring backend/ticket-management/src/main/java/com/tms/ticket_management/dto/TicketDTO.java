package com.tms.ticket_management.dto;

import com.tms.ticket_management.model.Ticket;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDTO {

    private Long id;
    private String bookingRef;
    private String eventId;
    private String eventName;
    private String ticketType;
    private int quantity;
    private double totalAmount;
    private String attendeeName;
    private String attendeeEmail;
    private String status;
    private LocalDateTime purchasedAt;
    private String qrCode;
    private Boolean isUsed;

    public static TicketDTO fromTicket(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setBookingRef(ticket.getBookingRef());
        dto.setEventId(ticket.getEventId());
        dto.setEventName(ticket.getEventName());
        dto.setTicketType(ticket.getTicketType());
        dto.setQuantity(ticket.getQuantity());
        dto.setTotalAmount(ticket.getTotalAmount());
        dto.setAttendeeName(ticket.getAttendeeName());
        dto.setAttendeeEmail(ticket.getAttendeeEmail());
        dto.setStatus(ticket.getStatus());
        dto.setPurchasedAt(ticket.getPurchasedAt());
        dto.setQrCode(ticket.getQrCode());
        dto.setIsUsed(ticket.getIsUsed());
        return dto;
    }
}