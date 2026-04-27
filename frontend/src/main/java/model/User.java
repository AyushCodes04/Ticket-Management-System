package model;

/**
 * This class stores the user details used by the UI.
 */
public class User {
    private String id;
    private String name;
    private String email;
    private String role;

    /**
     * This constructor creates a user object.
     */
    public User(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    /**
     * This method returns the user id.
     */
    public String getId() {
        return id;
    }

    /**
     * This method sets the user id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method returns the user name.
     */
    public String getName() {
        return name;
    }

    /**
     * This method sets the user name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns the user email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method sets the user email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This method returns the user role.
     */
    public String getRole() {
        return role;
    }

    /**
     * This method sets the user role.
     */
    public void setRole(String role) {
        this.role = role;
    }
}
