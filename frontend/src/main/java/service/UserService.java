// UserService.java
// kaam: dummy login/signup simulation + session state manage karta hai
//
package service;

import model.User;

public class UserService {
    private User currentUser;

    private static final String ORGANIZER_EMAIL = "organizer@eventhub.com";
    private static final String ORGANIZER_PASSWORD = "organizer123";
    private static final String ATTENDEE_EMAIL = "aman@example.com";
    private static final String ATTENDEE_PASSWORD = "attendee123";
    private static final String STAFF_EMAIL = "staff@eventhub.com";
    private static final String STAFF_PASSWORD = "staff123";

    // dummy ids
    private static final String ORG_ID = "ORG-001";
    private static final String ATD_ID = "ATD-001";
    private static final String STF_ID = "STF-001";

    /**
     * This method returns the current logged in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    // session clear karta hai
    public void signOut() {
        currentUser = null;
    }

    // role + credentials validate karke user set karta hai
    public User login(String role, String email, String password) {
        if (role == null || email == null || password == null) {
            return null;
        }
        String normalizedEmail = email.trim().toLowerCase();
        String normalizedRole = role.trim().toUpperCase();
        String normalizedPassword = password;

        return switch (normalizedRole) {
            case "ORGANIZER" -> {
                if (ORGANIZER_EMAIL.equals(normalizedEmail) && ORGANIZER_PASSWORD.equals(normalizedPassword)) {
                    currentUser = new User(ORG_ID, "Organizer", ORGANIZER_EMAIL, "ORGANIZER");
                    yield currentUser;
                }
                yield null;
            }
            case "ATTENDEE" -> {
                if (ATTENDEE_EMAIL.equals(normalizedEmail) && ATTENDEE_PASSWORD.equals(normalizedPassword)) {
                    currentUser = new User(ATD_ID, "Aman Sharma", ATTENDEE_EMAIL, "ATTENDEE");
                    yield currentUser;
                }
                yield null;
            }
            case "STAFF" -> {
                if (STAFF_EMAIL.equals(normalizedEmail) && STAFF_PASSWORD.equals(normalizedPassword)) {
                    currentUser = new User(STF_ID, "Gate Staff", STAFF_EMAIL, "STAFF");
                    yield currentUser;
                }
                yield null;
            }
            default -> null;
        };
    }

    // organizer card/Ui ke liye relevant user return karta hai
    public User getOrganizerUser() {
        if (currentUser != null && "ORGANIZER".equalsIgnoreCase(currentUser.getRole())) {
            return currentUser;
        }
        return new User(ORG_ID, "Organizer", ORGANIZER_EMAIL, "ORGANIZER");
    }

    // attendee card/Ui ke liye relevant user return karta hai
    public User getAttendeeUser() {
        if (currentUser != null && "ATTENDEE".equalsIgnoreCase(currentUser.getRole())) {
            return currentUser;
        }
        return new User(ATD_ID, "Aman Sharma", ATTENDEE_EMAIL, "ATTENDEE");
    }

    // staff card/Ui ke liye relevant user return karta hai
    public User getStaffUser() {
        if (currentUser != null && "STAFF".equalsIgnoreCase(currentUser.getRole())) {
            return currentUser;
        }
        return new User(STF_ID, "Gate Staff", STAFF_EMAIL, "STAFF");
    }
}
