package com.tms.ticket_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String bookingRef;

    @Column(nullable=false)
    private String eventId;

    @Column(nullable=false)
    private String eventName;

    @Column(nullable=false)
    private String ticketType;

    @Column(nullable=false)
    private int quantity;

    @Column(nullable=false)
    private double totalAmount;

    @Column(nullable=false)
    private String attendeeName;

    @Column(nullable=false)
    private String attendeeEmail;

    @Column(nullable=false)
    private String status; // e.g. CONFIRMED, USED

    @Column(nullable=false)
    private LocalDateTime purchasedAt;

    @Column(name="qr_code", unique=true)
    private String qrCode;

    @Column(name="is_used", nullable=false)
    private Boolean isUsed = false;
}