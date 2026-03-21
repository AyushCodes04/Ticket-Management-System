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
@Table(name="comments")
public class Comment {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=1000)
    private String content;

    @ManyToOne
    @JoinColumn(name="ticket_id", nullable=false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name="created_by", nullable=false)
    private User createdBy;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt=LocalDateTime.now();
    }
}