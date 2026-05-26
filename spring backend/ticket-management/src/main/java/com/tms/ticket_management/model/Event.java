package com.tms.ticket_management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="events")
public class Event {

    @Id
    private String id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, length=1000)
    private String description;

    @Column(nullable=false)
    private String date;

    @Column(nullable=false)
    private String time;

    @Column(nullable=false)
    private String venueName;

    @Column(nullable=false)
    private String venueAddress;

    @Column(nullable=false)
    private String category;

    @Column(nullable=false)
    private int maxCapacity;

    @Column(nullable=false)
    private String status;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_ticket_types", joinColumns = @JoinColumn(name = "event_id"))
    private List<TicketType> ticketTypes;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketType {
        @Column(name = "type_name", nullable = false)
        private String typeName;

        @Column(nullable = false)
        private double price;

        @Column(name = "total_quantity", nullable = false)
        private int totalQuantity;

        @Column(name = "sold_quantity", nullable = false)
        private int soldQuantity;
    }
}
