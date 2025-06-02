package model;

import java.time.LocalDateTime;
import java.util.List;

public class Event {

    private int eventId;
    private int locationId;
    private int userId; // References Organiser
    private String name;
    private LocalDateTime date;
    private String artists;
    private String eventType;
    private String description;
    private List<TicketType> ticketTypes;

    public Event(int locationId, int userId, String name, LocalDateTime date, String artists,
                 String eventType, String description, List<TicketType> ticketTypes) {

        this.locationId = locationId;
        this.userId = userId;
        this.name = name;
        this.date = date;
        this.artists = artists;
        this.eventType = eventType;
        this.description = description;
        this.ticketTypes = ticketTypes;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(List<TicketType> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

}