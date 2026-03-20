package com.tms.ticket_management.repository;

import com.tms.ticket_management.model.Ticket;
import com.tms.ticket_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCreatedBy(User user);

    List<Ticket> findByAssignedTo(User user);

    List<Ticket> findByStatus(Ticket.Status status);

    Optional<Ticket> findByQrCode(String qrCode);
}