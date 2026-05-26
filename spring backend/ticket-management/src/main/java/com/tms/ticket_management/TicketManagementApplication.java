package com.tms.ticket_management;

import com.tms.ticket_management.model.Event;
import com.tms.ticket_management.model.Ticket;
import com.tms.ticket_management.model.User;
import com.tms.ticket_management.repository.EventRepository;
import com.tms.ticket_management.repository.TicketRepository;
import com.tms.ticket_management.repository.UserRepository;
import com.tms.ticket_management.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class TicketManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketManagementApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(UserRepository userRepository,
                                         UserService userService,
                                         EventRepository eventRepository,
                                         TicketRepository ticketRepository) {
        return args -> {
            // Seed Users
            if (userRepository.findByEmail("organizer@eventhub.com").isEmpty()) {
                User user = new User();
                user.setUsername("Organizer");
                user.setEmail("organizer@eventhub.com");
                user.setPassword("organizer123");
                user.setRole(User.Role.ORGANIZER);
                userService.registerUser(user);
            }

            if (userRepository.findByEmail("aman@example.com").isEmpty()) {
                User user = new User();
                user.setUsername("Aman Sharma");
                user.setEmail("aman@example.com");
                user.setPassword("attendee123");
                user.setRole(User.Role.ATTENDEE);
                userService.registerUser(user);
            }

            if (userRepository.findByEmail("staff@eventhub.com").isEmpty()) {
                User user = new User();
                user.setUsername("Gate Staff");
                user.setEmail("staff@eventhub.com");
                user.setPassword("staff123");
                user.setRole(User.Role.STAFF);
                userService.registerUser(user);
            }

            // Seed Events
            if (eventRepository.count() == 0) {
                Event e1 = new Event(
                        "EVT001",
                        "Tech Summit 2025",
                        "India's biggest tech conference with keynotes, workshops and networking sessions.",
                        "15/08/2025",
                        "10:00 AM",
                        "Bharat Mandapam",
                        "Pragati Maidan, New Delhi",
                        "Tech",
                        500,
                        "UPCOMING",
                        LocalDateTime.now().minusDays(10),
                        List.of(
                                new Event.TicketType("VIP Pass", 2999, 50, 23),
                                new Event.TicketType("Professional", 1499, 150, 67),
                                new Event.TicketType("Student", 499, 300, 143)
                        )
                );

                Event e2 = new Event(
                        "EVT002",
                        "Arijit Singh Live in Concert",
                        "An unforgettable evening of soulful melodies and Bollywood classics.",
                        "22/08/2025",
                        "07:00 PM",
                        "NSIC Exhibition Ground",
                        "New Delhi",
                        "Concert",
                        8000,
                        "UPCOMING",
                        LocalDateTime.now().minusDays(10),
                        List.of(
                                new Event.TicketType("Platinum", 8999, 100, 78),
                                new Event.TicketType("Gold", 4999, 500, 234),
                                new Event.TicketType("Silver", 2499, 2000, 876),
                                new Event.TicketType("General", 999, 5400, 1234)
                        )
                );

                Event e3 = new Event(
                        "EVT003",
                        "IPL 2025 Final",
                        "The ultimate cricket showdown. Two champions battle for the IPL trophy.",
                        "01/06/2025",
                        "07:30 PM",
                        "Wankhede Stadium",
                        "Mumbai",
                        "Sports",
                        33000,
                        "ONGOING",
                        LocalDateTime.now().minusDays(10),
                        List.of(
                                new Event.TicketType("Corporate Box", 15000, 200, 200),
                                new Event.TicketType("Premium Stand", 5000, 2000, 1876),
                                new Event.TicketType("North Stand", 2000, 10000, 8934),
                                new Event.TicketType("General", 800, 20800, 15678)
                        )
                );

                Event e4 = new Event(
                        "EVT004",
                        "Aura Cultural Fest 2025",
                        "Three days of dance, music, fashion, comedy and celebrity performances.",
                        "10/09/2025",
                        "11:00 AM",
                        "Delhi University North Campus",
                        "New Delhi",
                        "Cultural",
                        2000,
                        "UPCOMING",
                        LocalDateTime.now().minusDays(10),
                        List.of(
                                new Event.TicketType("All Access Pass", 799, 2000, 0)
                        )
                );

                eventRepository.saveAll(List.of(e1, e2, e3, e4));
            }

            // Seed Tickets
            if (ticketRepository.count() == 0) {
                Ticket t1 = new Ticket(
                        null,
                        "EH-2025-000111",
                        "EVT001",
                        "Tech Summit 2025",
                        "VIP Pass",
                        1,
                        2999.0,
                        "Aman Sharma",
                        "aman@example.com",
                        "CONFIRMED",
                        LocalDateTime.now().minusDays(5),
                        "EH-2025-000111",
                        false
                );

                Ticket t2 = new Ticket(
                        null,
                        "EH-2025-000222",
                        "EVT001",
                        "Tech Summit 2025",
                        "Professional",
                        2,
                        2998.0,
                        "Aman Sharma",
                        "aman@example.com",
                        "USED",
                        LocalDateTime.now().minusDays(4),
                        "EH-2025-000222",
                        true
                );

                Ticket t3 = new Ticket(
                        null,
                        "EH-2025-000333",
                        "EVT003",
                        "IPL 2025 Final",
                        "Premium Stand",
                        3,
                        15000.0,
                        "Aman Sharma",
                        "aman@example.com",
                        "CONFIRMED",
                        LocalDateTime.now().minusDays(3),
                        "EH-2025-000333",
                        false
                );

                ticketRepository.saveAll(List.of(t1, t2, t3));
            }
        };
    }
}
