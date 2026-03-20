package com.tms.ticket_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false, length=1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Priority priority;

    @ManyToOne
    @JoinColumn(name="created_by", nullable=false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name="assigned_to")
    private User assignedTo;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(name="qr_code", unique=true)
    private String qrCode;

    // DB constraint: tickets.is_used is NOT NULL. Default to false so inserts succeed.
    @Column(name="is_used", nullable=false)
    private Boolean isUsed = false;

    @OneToMany(mappedBy="ticket", cascade=CascadeType.ALL)
    private List<Comment> comments;

    @PrePersist
    protected void onCreate() {
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt=LocalDateTime.now();
    }

    public enum Status {
        OPEN, IN_PROGRESS, CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}