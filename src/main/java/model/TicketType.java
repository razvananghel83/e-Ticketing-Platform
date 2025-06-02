package model;

import java.math.BigDecimal;

public class TicketType {
    private int ticketTypeId;
    private int eventId;
    private String name;
    private String description;
    private BigDecimal price;
    private int maxQuantity;

    public TicketType(int maxQuantity, BigDecimal price, String description, String name, int eventId) {

        this.maxQuantity = maxQuantity;
        this.price = price;
        this.description = description;
        this.name = name;
        this.eventId = eventId;
    }

    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(int ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}