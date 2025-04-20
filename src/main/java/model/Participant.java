package model;

import java.util.ArrayList;

public class Participant extends User {

    private ArrayList<Ticket> bookedTickets;

    public Participant(String username, String email, String password) {

        super(username, email, password);
        this.bookedTickets = new ArrayList<>();
    }

    public void bookTicket(Ticket ticket) {
        if (ticket != null)
            bookedTickets.add(ticket);
        else
            System.out.println("Biletul nu exista!");

    }

    public ArrayList<Ticket> getBookedTickets() {
        return bookedTickets;
    }
}
