package service;

import model.User;

/**
 * This class provides user data for the UI and is ready for future auth integration.
 */
public class UserService {

    /**
     * This method returns the current logged in user for now.
     */
    public User getCurrentUser() {
        // TODO: Replace with database call
        return new User("ORG-001", "Organizer", "organizer@eventhub.com", "ORGANIZER");
    }

    /**
     * This method returns a dummy organizer user.
     */
    public User getOrganizerUser() {
        // TODO: Replace with database call
        return new User("ORG-001", "Organizer", "organizer@eventhub.com", "ORGANIZER");
    }

    /**
     * This method returns a dummy attendee user.
     */
    public User getAttendeeUser() {
        // TODO: Replace with database call
        return new User("ATD-001", "Aman Sharma", "aman@example.com", "ATTENDEE");
    }

    /**
     * This method returns a dummy staff user.
     */
    public User getStaffUser() {
        // TODO: Replace with database call
        return new User("STF-001", "Gate Staff", "staff@eventhub.com", "STAFF");
    }
}
