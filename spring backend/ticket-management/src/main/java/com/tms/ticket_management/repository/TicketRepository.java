package com.tms.ticket_management.repository;

import com.tms.ticket_management.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByBookingRefIgnoreCase(String bookingRef);

    List<Ticket> findByAttendeeEmailIgnoreCaseOrderByPurchasedAtDesc(String attendeeEmail);

    Optional<Ticket> findByQrCode(String qrCode);
}