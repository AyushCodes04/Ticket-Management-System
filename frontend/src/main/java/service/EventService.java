package service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import config.ApiConfig;
import model.Event;
import util.HttpClientUtil;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventService {
    private final Gson gson;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EventService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString()))
                .create();
    }

    public List<Event> getAllEvents() {
        try {
            String json = HttpClientUtil.get(ApiConfig.EVENTS, null);
            Type listType = new TypeToken<ArrayList<Event>>(){}.getType();
            List<Event> list = gson.fromJson(json, listType);
            if (list == null) {
                return new ArrayList<>();
            }
            return list.stream()
                    .sorted(Comparator.comparing(this::safeDate))
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Event getEventById(String id) {
        try {
            String json = HttpClientUtil.get(ApiConfig.EVENTS + "/" + id, null);
            return gson.fromJson(json, Event.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createEvent(Event event) {
        try {
            if (event.getId() == null || event.getId().isBlank()) {
                event.setId(java.util.UUID.randomUUID().toString());
            }
            event.setCreatedAt(LocalDateTime.now());
            event.setStatus(resolveStatus(event.getDate()));
            String jsonBody = gson.toJson(event);
            HttpClientUtil.post(ApiConfig.EVENTS, jsonBody, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(String id) {
        try {
            HttpClientUtil.delete(ApiConfig.EVENTS + "/" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void incrementSoldTickets(String eventId, String ticketTypeName, int quantity) {
        // Backend handles sold quantities automatically inside purchase transaction.
        // Keeping this method signature to avoid compilation issues.
    }

    private LocalDate safeDate(Event event) {
        try {
            return LocalDate.parse(event.getDate(), formatter);
        } catch (Exception exception) {
            return LocalDate.now();
        }
    }

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
