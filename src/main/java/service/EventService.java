package service;

import model.Event;
import model.Location;
import model.TicketType;
import model.User;
import persistance.EventRepository;
import persistance.LocationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    public EventService(EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    /**
     * Creates a new event for an organizer
     */
    public Event createEvent(User organizer, int locationId, String name, String dateTimeStr, 
                            String artists, String eventType, String description) throws IllegalArgumentException {
        
        if (!"ORGANISER".equals(organizer.getUserType())) {
            throw new IllegalArgumentException("Only organizers can create events");
        }
        
        locationRepository.findById(String.valueOf(locationId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid location"));
            
        LocalDateTime eventDateTime;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            eventDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd HH:mm");
        }
        
        if (eventDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date must be in the future");
        }
        
        Event newEvent = new Event(
            locationId,
            organizer.getUserId(),
            name,
            eventDateTime,
            artists,
            eventType,
            description,
            new ArrayList<>()
        );
        
        return eventRepository.save(newEvent);
    }
    
    /**
     * Updates an existing event
     */
    public Event updateEvent(User organizer, int eventId, int locationId, String name, 
                           String dateTimeStr, String artists, String eventType, 
                           String description) throws IllegalArgumentException {
        
        if (!"ORGANISER".equals(organizer.getUserType())) {
            throw new IllegalArgumentException("Only organizers can edit events");
        }
        
        Event event = eventRepository.findById(String.valueOf(eventId))
            .orElseThrow(() -> new IllegalArgumentException("Event not found"));
            
        if (event.getUserId() != organizer.getUserId()) {
            throw new IllegalArgumentException("You can only edit your own events");
        }
        
        locationRepository.findById(String.valueOf(locationId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid location"));
            
        if (dateTimeStr != null && !dateTimeStr.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime eventDateTime = LocalDateTime.parse(dateTimeStr, formatter);
                
                if (eventDateTime.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Event date must be in the future");
                }
                
                event.setDate(eventDateTime);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd HH:mm");
            }
        }
        
        event.setLocationId(locationId);
        event.setName(name);
        event.setArtists(artists);
        event.setEventType(eventType);
        event.setDescription(description);
        
        eventRepository.update(event);
        return event;
    }
    
    /**
     * Get all events created by an organizer
     */
    public List<Event> getEventsByOrganizer(User organizer) {
        if (!"ORGANISER".equals(organizer.getUserType())) {
            return new ArrayList<>();
        }
        return eventRepository.findByOrganiser(organizer.getUserId());
    }
    
    /**
     * Get event by ID
     */
    public Optional<Event> getEventById(int eventId) {
        return eventRepository.findById(String.valueOf(eventId));
    }
    
    /**
     * Get all events
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}