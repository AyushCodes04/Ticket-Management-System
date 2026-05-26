package com.tms.ticket_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="validations")
public class ValidationResultEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private LocalDateTime time;

    @Column(nullable=false)
    private String bookingRef;

    private String attendeeName;
    private String eventName;

    @Column(nullable=false)
    private String status; // VALID, INVALID, ALREADY USED

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
}
