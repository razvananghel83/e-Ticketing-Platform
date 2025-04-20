package model;

public class Ticket extends TicketType {

    private static int nextId = 1;
    private final int id;
    private Participant participant;

    public Ticket(Event event, Organiser organiser, String typeName, String description, int price, Participant participant) {

        super(event, organiser, typeName, description, price);
        this.id = ++nextId;
        this.participant = participant;
    }

    public int getId() {
        return id;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
