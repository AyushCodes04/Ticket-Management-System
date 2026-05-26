// DummyData.java
// kaam: sample events aur tickets ko hardcoded dummy list ke form mein rakhta hai
// 
package data;

import model.Event;
import model.Ticket;

import java.time.LocalDateTime;
import java.util.List;

public final class DummyData {
    public static final List<Event> events = List.of(
        new Event(
            "EVT001",
            "Tech Summit 2025",
            "India's biggest tech conference with keynotes, workshops and networking sessions.",
            "15/08/2025",
            "10:00 AM",
            "Bharat Mandapam",
            "Pragati Maidan, New Delhi",
            "Tech",
            500,
            List.of(
                new Event.TicketType("VIP Pass", 2999, 50, 23),
                new Event.TicketType("Professional", 1499, 150, 67),
                new Event.TicketType("Student", 499, 300, 143)
            ),
            "UPCOMING",
            LocalDateTime.of(2025, 6, 1, 10, 0)
        ),
        new Event(
            "EVT002",
            "Arijit Singh Live in Concert",
            "An unforgettable evening of soulful melodies and Bollywood classics.",
            "22/08/2025",
            "07:00 PM",
            "NSIC Exhibition Ground",
            "New Delhi",
            "Concert",
            8000,
            List.of(
                new Event.TicketType("Platinum", 8999, 100, 78),
                new Event.TicketType("Gold", 4999, 500, 234),
                new Event.TicketType("Silver", 2499, 2000, 876),
                new Event.TicketType("General", 999, 5400, 1234)
            ),
            "UPCOMING",
            LocalDateTime.of(2025, 6, 5, 10, 0)
        ),
        new Event(
            "EVT003",
            "IPL 2025 Final",
            "The ultimate cricket showdown. Two champions battle for the IPL trophy.",
            "01/06/2025",
            "07:30 PM",
            "Wankhede Stadium",
            "Mumbai",
            "Sports",
            33000,
            List.of(
                new Event.TicketType("Corporate Box", 15000, 200, 200),
                new Event.TicketType("Premium Stand", 5000, 2000, 1876),
                new Event.TicketType("North Stand", 2000, 10000, 8934),
                new Event.TicketType("General", 800, 20800, 15678)
            ),
            "ONGOING",
            LocalDateTime.of(2025, 6, 10, 10, 0)
        ),
        new Event(
            "EVT004",
            "Aura Cultural Fest 2025",
            "Three days of dance, music, fashion, comedy and celebrity performances.",
            "10/09/2025",
            "11:00 AM",
            "Delhi University North Campus",
            "New Delhi",
            "Cultural",
            2000,
            List.of(
                // Aura ke All Access Pass ke liye total/sold data spec mein incomplete tha, isliye total = capacity aur sold = 0 rakha
                new Event.TicketType("All Access Pass", 799, 2000, 0)
            ),
            "UPCOMING",
            LocalDateTime.of(2025, 6, 15, 10, 0)
        )
    );

    public static final List<Ticket> tickets = List.of(
        new Ticket(
            "EH-2025-000111",
            "EVT001",
            "Tech Summit 2025",
            "VIP Pass",
            1,
            2999,
            "Aman Sharma",
            "aman@example.com",
            "CONFIRMED",
            LocalDateTime.of(2025, 8, 10, 9, 15)
        ),
        new Ticket(
            "EH-2025-000222",
            "EVT001",
            "Tech Summit 2025",
            "Professional",
            2,
            2998,
            "Aman Sharma",
            "aman@example.com",
            "USED",
            LocalDateTime.of(2025, 8, 14, 18, 30)
        ),
        new Ticket(
            "EH-2025-000333",
            "EVT003",
            "IPL 2025 Final",
            "Premium Stand",
            3,
            15000,
            "Aman Sharma",
            "aman@example.com",
            "CONFIRMED",
            LocalDateTime.of(2025, 5, 25, 13, 45)
        )
    );

    private DummyData() {
    }

    // events list ka safe copy return karta hai
    public static List<Event> getEvents() {
        return events.stream().map(Event::copy).toList();
    }

    // tickets list ka safe copy return karta hai
    public static List<Ticket> getTickets() {
        return tickets.stream().map(DummyData::copyTicket).toList();
    }

    // Ticket object ka fresh instance banata hai
    private static Ticket copyTicket(Ticket ticket) {
        return new Ticket(
            ticket.getBookingRef(),
            ticket.getEventId(),
            ticket.getEventName(),
            ticket.getTicketType(),
            ticket.getQuantity(),
            ticket.getTotalAmount(),
            ticket.getAttendeeName(),
            ticket.getAttendeeEmail(),
            ticket.getStatus(),
            ticket.getPurchasedAt()
        );
    }
}

