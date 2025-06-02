package model;

import java.util.List;

public class Organiser extends User {

    private List<Event> organizedEvents;

    public Organiser(String username, String email, String password, String userType, List<Event> organizedEvents) {

        super(username, email, password, userType);
        this.organizedEvents = organizedEvents;
    }

    public List<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    public void setOrganizedEvents(List<Event> organizedEvents) {
        this.organizedEvents = organizedEvents;
    }
}