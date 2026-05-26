package service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.ApiConfig;
import model.User;
import util.HttpClientUtil;

public class UserService {
    private User currentUser;
    private String token;

    public User getCurrentUser() {
        return currentUser;
    }

    public String getToken() {
        return token;
    }

    public void signOut() {
        currentUser = null;
        token = null;
    }

    public User login(String role, String email, String password) {
        if (role == null || email == null || password == null) {
            return null;
        }
        String normalizedEmail = email.trim().toLowerCase();
        String normalizedPassword = password;

        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("username", normalizedEmail);
            requestBody.addProperty("password", normalizedPassword);

            String responseStr = HttpClientUtil.post(ApiConfig.AUTH_LOGIN, requestBody.toString(), null);
            JsonObject responseObj = JsonParser.parseString(responseStr).getAsJsonObject();

            this.token = responseObj.get("token").getAsString();
            String authority = responseObj.get("role").getAsString(); // e.g. "ROLE_ORGANIZER"
            String cleanedRole = authority.replace("ROLE_", "");

            // If the login succeeded but the returned role does not match what the user selected on the login page, reject login
            if (!cleanedRole.equalsIgnoreCase(role)) {
                token = null;
                return null;
            }

            // Capitalize display name
            String displayName = cleanedRole.charAt(0) + cleanedRole.substring(1).toLowerCase();
            if ("Attendee".equalsIgnoreCase(displayName)) {
                displayName = "Aman Sharma"; // Match default UI expectation
            } else if ("Staff".equalsIgnoreCase(displayName)) {
                displayName = "Gate Staff";
            }

            currentUser = new User(normalizedEmail, displayName, normalizedEmail, cleanedRole);
            return currentUser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getOrganizerUser() {
        if (currentUser != null && "ORGANIZER".equalsIgnoreCase(currentUser.getRole())) {
            return currentUser;
        }
        return new User("organizer@eventhub.com", "Organizer", "organizer@eventhub.com", "ORGANIZER");
    }

    public User getAttendeeUser() {
        if (currentUser != null && "ATTENDEE".equalsIgnoreCase(currentUser.getRole())) {
            return currentUser;
        }
        return new User("aman@example.com", "Aman Sharma", "aman@example.com", "ATTENDEE");
    }

    public User getStaffUser() {
        if (currentUser != null && "STAFF".equalsIgnoreCase(currentUser.getRole())) {
            return currentUser;
        }
        return new User("staff@eventhub.com", "Gate Staff", "staff@eventhub.com", "STAFF");
    }
}
