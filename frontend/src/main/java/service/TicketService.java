package service;

import model.Event;
import model.Ticket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * This class handles ticket purchase and validation using dummy in-memory data.
 */
public class TicketService {
    private final EventService eventService;
    private final List<Ticket> purchasedTickets = new ArrayList<>();
    private final List<ValidationResult> validationHistory = new ArrayList<>();
    private final Random random = new Random();

    /**
     * This constructor prepares sample bookings.
     */
    public TicketService(EventService eventService) {
        this.eventService = eventService;
        seedSampleTickets();
    }

    /**
     * This method purchases tickets and returns a booking reference.
     */
    public String purchaseTicket(String eventId, String type, int qty, String name, String email) {
        // TODO: Replace with database call
        return purchaseSingleTicket(eventId, type, qty, name, email, generateBookingRef());
    }

    /**
     * This method purchases multiple ticket types under one booking reference.
     */
    public String purchaseTickets(String eventId, Map<String, Integer> quantities, String name, String email) {
        // TODO: Replace with database call
        String bookingRef = generateBookingRef();
        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            if (entry.getValue() > 0) {
                purchaseSingleTicket(eventId, entry.getKey(), entry.getValue(), name, email, bookingRef);
            }
        }
        return bookingRef;
    }

    /**
     * This method purchases one ticket line using a supplied booking reference.
     */
    private String purchaseSingleTicket(String eventId, String type, int qty, String name, String email, String bookingRef) {
        Event event = eventService.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        Event.TicketType ticketType = event.getTicketTypes().stream()
            .filter(item -> item.getTypeName().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ticket type not found"));
        int remaining = ticketType.getTotalQuantity() - ticketType.getSoldQuantity();
        if (qty <= 0 || qty > remaining) {
            throw new IllegalArgumentException("Requested quantity is not available");
        }
        Ticket ticket = new Ticket(
            bookingRef,
            event.getId(),
            event.getName(),
            ticketType.getTypeName(),
            qty,
            ticketType.getPrice() * qty,
            name,
            email.toLowerCase(Locale.ROOT),
            "CONFIRMED",
            LocalDateTime.now()
        );
        purchasedTickets.add(ticket);
        eventService.incrementSoldTickets(eventId, type, qty);
        return bookingRef;
    }

    /**
     * This method validates a booking reference and stores the validation result.
     */
    public ValidationResult validateTicket(String bookingRef) {
        // TODO: Replace with database call
        String normalized = bookingRef == null ? "" : bookingRef.trim().toUpperCase(Locale.ROOT);
        Ticket ticket = purchasedTickets.stream()
            .filter(item -> item.getBookingRef().equalsIgnoreCase(normalized))
            .findFirst()
            .orElse(null);
        ValidationResult result;
        if (ticket == null) {
            result = new ValidationResult(LocalDateTime.now(), normalized, "", "", "INVALID", "This booking reference was not found", null);
        } else if ("USED".equalsIgnoreCase(ticket.getStatus())) {
            result = new ValidationResult(LocalDateTime.now(), ticket.getBookingRef(), ticket.getAttendeeName(), ticket.getEventName(), "ALREADY USED", "This ticket has already been used", ticket);
        } else {
            result = new ValidationResult(LocalDateTime.now(), ticket.getBookingRef(), ticket.getAttendeeName(), ticket.getEventName(), "VALID", "Ticket is ready for entry", ticket);
        }
        validationHistory.add(0, result);
        if (validationHistory.size() > 10) {
            validationHistory.remove(validationHistory.size() - 1);
        }
        return result;
    }

    /**
     * This method returns tickets by attendee email.
     */
    public List<Ticket> getTicketsByAttendee(String email) {
        // TODO: Replace with database call
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        return purchasedTickets.stream()
            .filter(ticket -> ticket.getAttendeeEmail().equalsIgnoreCase(normalizedEmail))
            .sorted(Comparator.comparing(Ticket::getPurchasedAt).reversed())
            .toList();
    }

    /**
     * This method marks a ticket as used.
     */
    public void markTicketAsUsed(String bookingRef) {
        // TODO: Replace with database call
        purchasedTickets.stream()
            .filter(ticket -> ticket.getBookingRef().equalsIgnoreCase(bookingRef))
            .findFirst()
            .ifPresent(ticket -> ticket.setStatus("USED"));
    }

    /**
     * This method returns the recent validation history.
     */
    public List<ValidationResult> getRecentValidations() {
        // TODO: Replace with database call
        return new ArrayList<>(validationHistory);
    }

    /**
     * This method returns the total validated count for today.
     */
    public long getTotalValidatedToday() {
        // TODO: Replace with database call
        return validationHistory.stream().filter(item -> item.getTime().toLocalDate().isEqual(LocalDate.now())).count();
    }

    /**
     * This method returns the total count for one validation status today.
     */
    public long getCountForStatusToday(String status) {
        // TODO: Replace with database call
        return validationHistory.stream()
            .filter(item -> item.getTime().toLocalDate().isEqual(LocalDate.now()))
            .filter(item -> Objects.equals(item.getStatus(), status))
            .count();
    }

    /**
     * This method adds sample bookings for attendee and staff flows.
     */
    private void seedSampleTickets() {
        Event techSummit = eventService.getAllEvents().stream().filter(event -> event.getName().equals("Tech Summit 2025")).findFirst().orElse(null);
        Event designExpo = eventService.getAllEvents().stream().filter(event -> event.getName().equals("Design Futures Expo")).findFirst().orElse(null);
        if (techSummit != null) {
            purchasedTickets.add(new Ticket("EH25-4D92", techSummit.getId(), techSummit.getName(), "VIP", 2, 3998, "Aman Sharma", "aman@example.com", "CONFIRMED", LocalDateTime.now().minusDays(1)));
            purchasedTickets.add(new Ticket("EH25-2B71", techSummit.getId(), techSummit.getName(), "General", 1, 599, "Aman Sharma", "aman@example.com", "USED", LocalDateTime.now().minusDays(3)));
        }
        if (designExpo != null) {
            purchasedTickets.add(new Ticket("EH26-8Q14", designExpo.getId(), designExpo.getName(), "Visitor", 3, 1197, "Aman Sharma", "aman@example.com", "CONFIRMED", LocalDateTime.now().minusHours(7)));
        }
        validationHistory.add(new ValidationResult(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 12)), "EH25-2B71", "Aman Sharma", "Tech Summit 2025", "ALREADY USED", "This ticket has already been used", null));
        validationHistory.add(new ValidationResult(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 5)), "ZZ99-1111", "", "", "INVALID", "This booking reference was not found", null));
        validationHistory.add(new ValidationResult(LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 58)), "EH25-4D92", "Aman Sharma", "Tech Summit 2025", "VALID", "Ticket is ready for entry", null));
    }

    /**
     * This method generates a simple booking reference.
     */
    private String generateBookingRef() {
        return "EH" + LocalDate.now().getYear() % 100 + "-" + (1000 + random.nextInt(9000));
    }

    /**
     * This class stores a validation attempt for the staff panel.
     */
    public static class ValidationResult {
        private final LocalDateTime time;
        private final String bookingRef;
        private final String attendeeName;
        private final String eventName;
        private final String status;
        private final String message;
        private final Ticket ticket;

        /**
         * This constructor creates a validation result.
         */
        public ValidationResult(LocalDateTime time, String bookingRef, String attendeeName, String eventName,
                                String status, String message, Ticket ticket) {
            this.time = time;
            this.bookingRef = bookingRef;
            this.attendeeName = attendeeName;
            this.eventName = eventName;
            this.status = status;
            this.message = message;
            this.ticket = ticket;
        }

        /**
         * This method returns the validation time.
         */
        public LocalDateTime getTime() {
            return time;
        }

        /**
         * This method returns the booking reference.
         */
        public String getBookingRef() {
            return bookingRef;
        }

        /**
         * This method returns the attendee name.
         */
        public String getAttendeeName() {
            return attendeeName;
        }

        /**
         * This method returns the event name.
         */
        public String getEventName() {
            return eventName;
        }

        /**
         * This method returns the validation status.
         */
        public String getStatus() {
            return status;
        }

        /**
         * This method returns the result message.
         */
        public String getMessage() {
            return message;
        }

        /**
         * This method returns the related ticket if available.
         */
        public Ticket getTicket() {
            return ticket;
        }

        /**
         * This method formats the validation time for the table.
         */
        public String getFormattedTime() {
            return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
        }
    }
}
