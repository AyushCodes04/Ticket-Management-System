package com.tms.ticket_management.repository;

import com.tms.ticket_management.model.Comment;
import com.tms.ticket_management.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTicket(Ticket ticket);
}