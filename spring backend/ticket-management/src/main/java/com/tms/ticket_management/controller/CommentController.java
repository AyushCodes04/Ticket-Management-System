package com.tms.ticket_management.controller;

import com.tms.ticket_management.dto.CommentDTO;
import com.tms.ticket_management.model.Comment;
import com.tms.ticket_management.service.CommentService;
import com.tms.ticket_management.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<CommentDTO> addComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(CommentDTO.fromComment(commentService.addComment(comment)));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByTicket(@PathVariable Long ticketId) {
        return ticketService.getTicketById(ticketId)
                .map(ticket -> ResponseEntity.ok(commentService.getCommentsByTicket(ticket)
                        .stream()
                        .map(CommentDTO::fromComment)
                        .toList()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}