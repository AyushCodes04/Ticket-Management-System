package service;

import data.DummyData;
import model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This class manages event data using in-memory dummy records.
 */
public class EventService {
    private final List<Event> events = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * This constructor loads sample events.
     */
    public EventService() {
        seedEvents();
    }

    /**
     * This method returns all events.
     */
    public List<Event> getAllEvents() {
        // TODO: Replace with database call
        return events.stream()
            .sorted(Comparator.comparing(this::safeDate))
            .map(Event::copy)
            .toList();
    }

    /**
     * This method finds one event by id.
     */
    public Event getEventById(String id) {
        // TODO: Replace with database call
        return events.stream()
            .filter(event -> event.getId().equals(id))
            .findFirst()
            .map(Event::copy)
            .orElse(null);
    }

    /**
     * This method creates a new event.
     */
    public void createEvent(Event event) {
        // TODO: Replace with database call
        Event stored = event.copy();
        stored.setId(UUID.randomUUID().toString());
        stored.setCreatedAt(LocalDateTime.now());
        stored.setStatus(resolveStatus(stored.getDate()));
        events.add(stored);
    }

    /**
     * This method deletes an event by id.
     */
    public void deleteEvent(String id) {
        // TODO: Replace with database call
        events.removeIf(event -> event.getId().equals(id));
    }

    /**
     * This method updates sold quantities after a purchase.
     */
    public void incrementSoldTickets(String eventId, String ticketTypeName, int quantity) {
        // TODO: Replace with database call
        Optional<Event> eventOptional = events.stream().filter(item -> item.getId().equals(eventId)).findFirst();
        if (eventOptional.isEmpty()) {
            return;
        }
        Event event = eventOptional.get();
        for (Event.TicketType ticketType : event.getTicketTypes()) {
            if (ticketType.getTypeName().equalsIgnoreCase(ticketTypeName)) {
                ticketType.setSoldQuantity(ticketType.getSoldQuantity() + quantity);
                break;
            }
        }
    }

    /**
     * This method seeds the application with sample events.
     */
    private void seedEvents() {
        // hardcoded dummy events ka source DummyData hi rahega
        events.clear();
        events.addAll(DummyData.getEvents());
    }

    /**
     * This method creates one sample event.
     */
    private Event createSeedEvent(String id, String name, String description, String date, String time,
                                  String venueName, String venueAddress, String category, int maxCapacity,
                                  List<Event.TicketType> ticketTypes, String status) {
        return new Event(id, name, description, date, time, venueName, venueAddress, category, maxCapacity, ticketTypes, status, LocalDateTime.now().minusDays(10));
    }

    /**
     * This method safely parses the date for sorting.
     */
    private LocalDate safeDate(Event event) {
        try {
            return LocalDate.parse(event.getDate(), formatter);
        } catch (Exception exception) {
            return LocalDate.now();
        }
    }

    /**
     * This method calculates the event status from its date.
     */
    private String resolveStatus(String date) {
        try {
            LocalDate eventDate = LocalDate.parse(date, formatter);
            if (eventDate.isAfter(LocalDate.now())) {
                return "UPCOMING";
            }
            if (eventDate.isEqual(LocalDate.now())) {
                return "ONGOING";
            }
            return "COMPLETED";
        } catch (Exception exception) {
            return "UPCOMING";
        }
    }
}
