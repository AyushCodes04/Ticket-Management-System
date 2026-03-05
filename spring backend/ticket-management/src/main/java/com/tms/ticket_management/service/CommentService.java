package com.tms.ticket_management.service;

import com.tms.ticket_management.model.Comment;
import com.tms.ticket_management.model.Ticket;
import com.tms.ticket_management.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTicket(Ticket ticket) {
        return commentRepository.findByTicket(ticket);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}