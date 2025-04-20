package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Organiser extends User {

    private ArrayList<Event> organizedEvents;

    public Organiser(String username, String email, String password) {

        super(username, email, password);
        this.organizedEvents = new ArrayList<>();
    }

    public ArrayList<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    public void organizeEvent(Event event) {
        if (event != null && event.getLoclDate().isAfter(LocalDate.now()))
            organizedEvents.add(event);
        else
            System.out.println("Evenimentul nu exista sau se desfasoara in trecut!");
    }
}
