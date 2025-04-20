package model;

import java.util.ArrayList;

public class Payment {

    private BankCard card;
    private ArrayList<TicketType> tickets;
    private int amount;

    public Payment(BankCard card, ArrayList<TicketType> tickets) {

        this.card = card;
        this.tickets = tickets;
        this.amount = tickets.stream().mapToInt(TicketType::getPrice).sum();
    }

    public void setCard(BankCard card) {
        this.card = card;
    }

    public BankCard getCard() {
        return card;
    }

    public ArrayList<TicketType> getTickets() {
        return tickets;
    }

    public int getAmount() {
        return amount;
    }

    public void removeTicket(TicketType ticket){

        if (this.tickets.contains(ticket)) {
            tickets.remove(ticket);
            this.amount = tickets.stream().mapToInt(TicketType::getPrice).sum();
        }
    }

}
