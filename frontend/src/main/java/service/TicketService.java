package service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import config.ApiConfig;
import model.Ticket;
import util.HttpClientUtil;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TicketService {
    private final EventService eventService;
    private final Gson gson;

    public TicketService(EventService eventService) {
        this.eventService = eventService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString()))
                .create();
    }

    public String purchaseTicket(String eventId, String type, int qty, String name, String email) {
        Map<String, Integer> quantities = new HashMap<>();
        quantities.put(type, qty);
        return purchaseTickets(eventId, quantities, name, email);
    }

    public String purchaseTickets(String eventId, Map<String, Integer> quantities, String name, String email) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("eventId", eventId);
            requestBody.addProperty("name", name);
            requestBody.addProperty("email", email);

            JsonObject qtyJson = new JsonObject();
            for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
                qtyJson.addProperty(entry.getKey(), entry.getValue());
            }
            requestBody.add("quantities", qtyJson);

            String response = HttpClientUtil.post(ApiConfig.TICKETS_PURCHASE, requestBody.toString(), null);
            JsonObject responseObj = JsonParser.parseString(response).getAsJsonObject();
            return responseObj.get("bookingRef").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public ValidationResult validateTicket(String bookingRef) {
        try {
            String encodedRef = URLEncoder.encode(bookingRef, StandardCharsets.UTF_8);
            String url = ApiConfig.TICKETS_VALIDATE + "/" + encodedRef;
            String response = HttpClientUtil.get(url, null);

            JsonObject obj = JsonParser.parseString(response).getAsJsonObject();
            LocalDateTime time = obj.has("time") && !obj.get("time").isJsonNull() 
                    ? LocalDateTime.parse(obj.get("time").getAsString()) 
                    : LocalDateTime.now();
            String ref = obj.has("bookingRef") && !obj.get("bookingRef").isJsonNull() ? obj.get("bookingRef").getAsString() : "";
            String attName = obj.has("attendeeName") && !obj.get("attendeeName").isJsonNull() ? obj.get("attendeeName").getAsString() : "";
            String evtName = obj.has("eventName") && !obj.get("eventName").isJsonNull() ? obj.get("eventName").getAsString() : "";
            String status = obj.has("status") && !obj.get("status").isJsonNull() ? obj.get("status").getAsString() : "INVALID";
            String msg = obj.has("message") && !obj.get("message").isJsonNull() ? obj.get("message").getAsString() : "";

            Ticket ticket = null;
            if (obj.has("ticket") && !obj.get("ticket").isJsonNull()) {
                ticket = gson.fromJson(obj.get("ticket"), Ticket.class);
            }

            return new ValidationResult(time, ref, attName, evtName, status, msg, ticket);
        } catch (Exception e) {
            e.printStackTrace();
            return new ValidationResult(LocalDateTime.now(), bookingRef, "", "", "INVALID", e.getMessage(), null);
        }
    }

    public List<Ticket> getTicketsByAttendee(String email) {
        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            String url = ApiConfig.TICKETS_ATTENDEE + "?email=" + encodedEmail;
            String response = HttpClientUtil.get(url, null);
            Type listType = new TypeToken<ArrayList<Ticket>>(){}.getType();
            List<Ticket> list = gson.fromJson(response, listType);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void markTicketAsUsed(String bookingRef) {
        try {
            String encodedRef = URLEncoder.encode(bookingRef, StandardCharsets.UTF_8);
            String url = ApiConfig.TICKETS_USE + "/" + encodedRef;
            HttpClientUtil.post(url, "", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ValidationResult> getRecentValidations() {
        try {
            String response = HttpClientUtil.get(ApiConfig.VALIDATIONS_RECENT, null);
            JsonArray arr = JsonParser.parseString(response).getAsJsonArray();
            List<ValidationResult> results = new ArrayList<>();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                LocalDateTime time = obj.has("time") && !obj.get("time").isJsonNull() 
                        ? LocalDateTime.parse(obj.get("time").getAsString()) 
                        : LocalDateTime.now();
                String ref = obj.has("bookingRef") && !obj.get("bookingRef").isJsonNull() ? obj.get("bookingRef").getAsString() : "";
                String attName = obj.has("attendeeName") && !obj.get("attendeeName").isJsonNull() ? obj.get("attendeeName").getAsString() : "";
                String evtName = obj.has("eventName") && !obj.get("eventName").isJsonNull() ? obj.get("eventName").getAsString() : "";
                String status = obj.has("status") && !obj.get("status").isJsonNull() ? obj.get("status").getAsString() : "";
                String msg = obj.has("message") && !obj.get("message").isJsonNull() ? obj.get("message").getAsString() : "";

                Ticket ticket = null;
                if (obj.has("ticket") && !obj.get("ticket").isJsonNull()) {
                    ticket = gson.fromJson(obj.get("ticket"), Ticket.class);
                }

                results.add(new ValidationResult(time, ref, attName, evtName, status, msg, ticket));
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public long getTotalValidatedToday() {
        try {
            String response = HttpClientUtil.get(ApiConfig.VALIDATIONS_TODAY, null);
            return Long.parseLong(response.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getCountForStatusToday(String status) {
        try {
            String url = ApiConfig.VALIDATIONS_STATUS_TODAY + "/" + status;
            String response = HttpClientUtil.get(url, null);
            return Long.parseLong(response.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static class ValidationResult {
        private final LocalDateTime time;
        private final String bookingRef;
        private final String attendeeName;
        private final String eventName;
        private final String status;
        private final String message;
        private final Ticket ticket;

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

        public LocalDateTime getTime() {
            return time;
        }

        public String getBookingRef() {
            return bookingRef;
        }

        public String getAttendeeName() {
            return attendeeName;
        }

        public String getEventName() {
            return eventName;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Ticket getTicket() {
            return ticket;
        }

        public String getFormattedTime() {
            return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
        }
    }
}
