package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class Event {

    private String name;
    private Organiser organiser;
    private String venue;
    private String location;
    private TreeSet<String> artists;
    private LocalDate date;

    private String type;
    private String description;

    private int capacity;
    private int availableTickets;

    private static final Set<String> ALLOWED_TYPES = Set.of("Concert", "Festival", "Exhibition", "Theater", "Sport", "Standup",
            "Conference", "Party");

    // Constructor
    public Event(String name, Organiser organiser, String venue, String location, String[] artists, String stringDate,
                 String type, String description, int capacity ){

        this.name = name;
        this.organiser = organiser;
        this.venue = venue;
        this.location = location;
        this.artists = new TreeSet<>(Arrays.asList(artists));
        this.date = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        if (ALLOWED_TYPES.contains(type)) this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.availableTickets = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organiser getOrganiser() {
        return organiser;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String[] getArtists() {
        return artists.toArray(new String[artists.size()]);
    }

    public LocalDate getLoclDate() {
        return date;
    }

    public void setDate(String stringDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
        this.date = LocalDate.from(LocalDateTime.parse(stringDate, formatter));
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    // Methods
    public void increaseAvailableTickets(int amount) {
        this.availableTickets += amount;
    }

    public void increaseCapacity(int amount) {
        this.capacity += amount;
    }

    public void addArtists(String[] newArtists){
        Collections.addAll(this.artists, newArtists);
    }

    public void removeArtists(String[] artistsToRemove){

        for(String artist : artistsToRemove)
            this.artists.remove(artist);
    }

    public String getDate() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm");
        return this.date.format(formatter);
    }

}

