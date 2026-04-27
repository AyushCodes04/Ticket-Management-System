package model;

import java.time.LocalDateTime;

/**
 * This class stores a purchased booking record.
 */
public class Ticket {
    private String bookingRef;
    private String eventId;
    private String eventName;
    private String ticketType;
    private int quantity;
    private double totalAmount;
    private String attendeeName;
    private String attendeeEmail;
    private String status;
    private LocalDateTime purchasedAt;

    /**
     * This constructor creates a ticket booking object.
     */
    public Ticket(String bookingRef, String eventId, String eventName, String ticketType, int quantity,
                  double totalAmount, String attendeeName, String attendeeEmail, String status,
                  LocalDateTime purchasedAt) {
        this.bookingRef = bookingRef;
        this.eventId = eventId;
        this.eventName = eventName;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.attendeeName = attendeeName;
        this.attendeeEmail = attendeeEmail;
        this.status = status;
        this.purchasedAt = purchasedAt;
    }

    /**
     * This method returns the booking reference.
     */
    public String getBookingRef() {
        return bookingRef;
    }

    /**
     * This method sets the booking reference.
     */
    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    /**
     * This method returns the event id.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * This method sets the event id.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * This method returns the event name.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * This method sets the event name.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * This method returns the ticket type.
     */
    public String getTicketType() {
        return ticketType;
    }

    /**
     * This method sets the ticket type.
     */
    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    /**
     * This method returns the quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * This method sets the quantity.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * This method returns the total amount.
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * This method sets the total amount.
     */
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * This method returns the attendee name.
     */
    public String getAttendeeName() {
        return attendeeName;
    }

    /**
     * This method sets the attendee name.
     */
    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    /**
     * This method returns the attendee email.
     */
    public String getAttendeeEmail() {
        return attendeeEmail;
    }

    /**
     * This method sets the attendee email.
     */
    public void setAttendeeEmail(String attendeeEmail) {
        this.attendeeEmail = attendeeEmail;
    }

    /**
     * This method returns the booking status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method sets the booking status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * This method returns the purchase time.
     */
    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    /**
     * This method sets the purchase time.
     */
    public void setPurchasedAt(LocalDateTime purchasedAt) {
        this.purchasedAt = purchasedAt;
    }
}
