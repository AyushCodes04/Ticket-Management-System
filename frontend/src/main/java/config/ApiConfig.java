package config;

public final class ApiConfig {
    public static final String BASE_URL = "http://localhost:8080";
    
    public static final String AUTH_LOGIN = BASE_URL + "/api/auth/login";
    public static final String EVENTS = BASE_URL + "/api/events";
    
    public static final String TICKETS_PURCHASE = BASE_URL + "/api/tickets/purchase";
    public static final String TICKETS_ATTENDEE = BASE_URL + "/api/tickets/attendee";
    public static final String TICKETS_VALIDATE = BASE_URL + "/api/tickets/validate";
    public static final String TICKETS_USE = BASE_URL + "/api/tickets/use";
    
    public static final String VALIDATIONS_TODAY = BASE_URL + "/api/tickets/validations/today";
    public static final String VALIDATIONS_STATUS_TODAY = BASE_URL + "/api/tickets/validations/status-today";
    public static final String VALIDATIONS_RECENT = BASE_URL + "/api/tickets/validations/recent";

    private ApiConfig() {
    }
}
