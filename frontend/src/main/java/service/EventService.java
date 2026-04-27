package service;

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
        events.add(createSeedEvent("EVT-1001", "Tech Summit 2025", "A full day conference on AI, cloud, and product engineering.",
            "15/08/2025", "10:00 AM", "City Convention Center", "Delhi", "Tech", 250,
            List.of(new Event.TicketType("VIP", 1999, 50, 38), new Event.TicketType("General", 599, 200, 126)), "COMPLETED"));
        events.add(createSeedEvent("EVT-1002", "Arijit Singh Live", "An unforgettable live concert experience with premium stage production.",
            "22/08/2025", "07:00 PM", "NSIC Ground", "Mumbai", "Concert", 900,
            List.of(new Event.TicketType("Gold", 3499, 100, 92), new Event.TicketType("Silver", 1499, 300, 247), new Event.TicketType("General", 699, 500, 344)), "COMPLETED"));
        events.add(createSeedEvent("EVT-1003", "IPL Final 2025", "Championship night with live entertainment and fan zones.",
            "01/09/2025", "07:30 PM", "Wankhede Stadium", "Mumbai", "Sports", 700,
            List.of(new Event.TicketType("Premium", 5000, 200, 186), new Event.TicketType("Standard", 2000, 500, 462)), "COMPLETED"));
        events.add(createSeedEvent("EVT-1004", "Cultural Fest UPES", "Student performances, food stalls, games, and live acts across campus.",
            "10/09/2025", "05:00 PM", "UPES Campus", "Dehradun", "Cultural", 400,
            List.of(new Event.TicketType("Paid", 199, 300, 211), new Event.TicketType("Free", 0, 100, 94)), "COMPLETED"));
        events.add(createSeedEvent("EVT-1005", "Startup India Summit", "Networking, investor sessions, startup showcases, and keynote talks.",
            "25/09/2025", "09:00 AM", "Pragati Maidan", "Delhi", "Tech", 450,
            List.of(new Event.TicketType("Delegate", 2999, 150, 117), new Event.TicketType("Student", 499, 300, 203)), "COMPLETED"));
        events.add(createSeedEvent("EVT-1006", "Comedy Night with Zakir Khan", "A sold-out stand-up evening with premium auditorium seating.",
            "05/10/2025", "08:00 PM", "Siri Fort Auditorium", "Delhi", "Cultural", 330,
            List.of(new Event.TicketType("VIP", 1999, 80, 64), new Event.TicketType("General", 799, 250, 193)), "COMPLETED"));
        events.add(createSeedEvent("EVT-2001", "Design Futures Expo", "Hands-on sessions and talks focused on product design and creative tooling.",
            LocalDate.now().plusDays(2).format(formatter), "11:00 AM", "Bharat Mandapam", "Delhi", "Tech", 260,
            List.of(new Event.TicketType("Workshop", 1299, 80, 22), new Event.TicketType("Visitor", 399, 180, 71)), "UPCOMING"));
        events.add(createSeedEvent("EVT-2002", "Weekend Indie Music Fest", "Open air indie music festival with food popups and local bands.",
            LocalDate.now().plusWeeks(2).format(formatter), "06:30 PM", "Phoenix Grounds", "Bengaluru", "Concert", 520,
            List.of(new Event.TicketType("Front Row", 2499, 120, 39), new Event.TicketType("General", 899, 400, 145)), "UPCOMING"));
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
