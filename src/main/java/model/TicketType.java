package model;

public abstract class TicketType {

    protected final Event event;
    protected final Organiser organiser;
    protected final String typeName;
    protected String description;
    protected int price;

    protected TicketType(Event event, Organiser organiser, String typeName, String description, int price) {

        this.event = event;
        this.organiser = organiser;
        this.typeName = typeName;
        this.description = description;
        this.price = price;
    }

    public Event getEvent() {
        return event;
    }

    public Organiser getOrganiser() {
        return organiser;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
