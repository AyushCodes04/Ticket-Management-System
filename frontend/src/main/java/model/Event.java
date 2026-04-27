package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class stores the event details used across the application.
 */
public class Event {
    private String id;
    private String name;
    private String description;
    private String date;
    private String time;
    private String venueName;
    private String venueAddress;
    private String category;
    private int maxCapacity;
    private List<TicketType> ticketTypes;
    private String status;
    private LocalDateTime createdAt;

    /**
     * This constructor creates an empty event object.
     */
    public Event() {
        this.ticketTypes = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.status = "UPCOMING";
    }

    /**
     * This constructor creates a full event object.
     */
    public Event(String id, String name, String description, String date, String time,
                 String venueName, String venueAddress, String category, int maxCapacity,
                 List<TicketType> ticketTypes, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venueName = venueName;
        this.venueAddress = venueAddress;
        this.category = category;
        this.maxCapacity = maxCapacity;
        this.ticketTypes = new ArrayList<>(ticketTypes);
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * This method creates a safe copy of the event.
     */
    public Event copy() {
        List<TicketType> copiedTypes = ticketTypes.stream().map(TicketType::copy).toList();
        return new Event(id, name, description, date, time, venueName, venueAddress, category, maxCapacity, copiedTypes, status, createdAt);
    }

    /**
     * This method returns the total number of sold tickets.
     */
    public int getTotalTicketsSold() {
        return ticketTypes.stream().mapToInt(TicketType::getSoldQuantity).sum();
    }

    /**
     * This method returns the total available ticket quantity.
     */
    public int getTotalTicketQuantity() {
        return ticketTypes.stream().mapToInt(TicketType::getTotalQuantity).sum();
    }

    /**
     * This method returns the revenue earned by the event.
     */
    public double getRevenue() {
        return ticketTypes.stream().mapToDouble(type -> type.getPrice() * type.getSoldQuantity()).sum();
    }

    /**
     * This method returns the lowest available ticket price.
     */
    public double getMinimumPrice() {
        return ticketTypes.stream().mapToDouble(TicketType::getPrice).min().orElse(0);
    }

    /**
     * This method returns the highest available ticket price.
     */
    public double getMaximumPrice() {
        return ticketTypes.stream().mapToDouble(TicketType::getPrice).max().orElse(0);
    }

    /**
     * This method returns the event id.
     */
    public String getId() {
        return id;
    }

    /**
     * This method sets the event id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method returns the event name.
     */
    public String getName() {
        return name;
    }

    /**
     * This method sets the event name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns the event description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method sets the event description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method returns the event date.
     */
    public String getDate() {
        return date;
    }

    /**
     * This method sets the event date.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * This method returns the event time.
     */
    public String getTime() {
        return time;
    }

    /**
     * This method sets the event time.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * This method returns the event venue name.
     */
    public String getVenueName() {
        return venueName;
    }

    /**
     * This method sets the event venue name.
     */
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    /**
     * This method returns the venue address.
     */
    public String getVenueAddress() {
        return venueAddress;
    }

    /**
     * This method sets the venue address.
     */
    public void setVenueAddress(String venueAddress) {
        this.venueAddress = venueAddress;
    }

    /**
     * This method returns the event category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * This method sets the event category.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * This method returns the max capacity.
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * This method sets the max capacity.
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * This method returns the ticket types.
     */
    public List<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    /**
     * This method sets the ticket types.
     */
    public void setTicketTypes(List<TicketType> ticketTypes) {
        this.ticketTypes = new ArrayList<>(ticketTypes);
    }

    /**
     * This method returns the event status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method sets the event status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * This method returns the creation time.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * This method sets the creation time.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This inner class stores ticket type details for an event.
     */
    public static class TicketType {
        private String typeName;
        private double price;
        private int totalQuantity;
        private int soldQuantity;

        /**
         * This constructor creates a ticket type object.
         */
        public TicketType(String typeName, double price, int totalQuantity, int soldQuantity) {
            this.typeName = typeName;
            this.price = price;
            this.totalQuantity = totalQuantity;
            this.soldQuantity = soldQuantity;
        }

        /**
         * This method creates a copy of the ticket type.
         */
        public TicketType copy() {
            return new TicketType(typeName, price, totalQuantity, soldQuantity);
        }

        /**
         * This method returns the type name.
         */
        public String getTypeName() {
            return typeName;
        }

        /**
         * This method sets the type name.
         */
        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        /**
         * This method returns the price.
         */
        public double getPrice() {
            return price;
        }

        /**
         * This method sets the price.
         */
        public void setPrice(double price) {
            this.price = price;
        }

        /**
         * This method returns the total quantity.
         */
        public int getTotalQuantity() {
            return totalQuantity;
        }

        /**
         * This method sets the total quantity.
         */
        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        /**
         * This method returns the sold quantity.
         */
        public int getSoldQuantity() {
            return soldQuantity;
        }

        /**
         * This method sets the sold quantity.
         */
        public void setSoldQuantity(int soldQuantity) {
            this.soldQuantity = soldQuantity;
        }

        @Override
        public String toString() {
            return typeName + " - Rs." + price;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof TicketType that)) {
                return false;
            }
            return Double.compare(that.price, price) == 0 && totalQuantity == that.totalQuantity && Objects.equals(typeName, that.typeName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeName, price, totalQuantity);
        }
    }
}
