package com.tms.ticket_management.dto;

import com.tms.ticket_management.model.Comment;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;
    private String content;
    private Long ticketId;
    private UserDTO createdBy;
    private LocalDateTime createdAt;

    public static CommentDTO fromComment(Comment comment) {
        CommentDTO dto=new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        if(comment.getTicket() != null)
            dto.setTicketId(comment.getTicket().getId());
        if(comment.getCreatedBy() != null)
            dto.setCreatedBy(UserDTO.fromUser(comment.getCreatedBy()));
        return dto;
    }
}