package com.tms.ticket_management.dto;

import com.tms.ticket_management.model.Ticket;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketDTO {

    private Long id;
    private String title;
    private String description;
    private Ticket.Status status;
    private Ticket.Priority priority;
    private UserDTO createdBy;
    private UserDTO assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TicketDTO fromTicket(Ticket ticket) {
        TicketDTO dto=new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        if(ticket.getCreatedBy() != null)
            dto.setCreatedBy(UserDTO.fromUser(ticket.getCreatedBy()));
        if(ticket.getAssignedTo() != null)
            dto.setAssignedTo(UserDTO.fromUser(ticket.getAssignedTo()));
        return dto;
    }
}