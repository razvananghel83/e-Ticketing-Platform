package model;

import java.time.LocalDateTime;

public class Ticket {

    private int ticketId;
    private int ticketTypeId;
    private int paymentId;
    private int participantId;
    private LocalDateTime createdAt;

    public Ticket(LocalDateTime createdAt, int participantId, int paymentId, int ticketTypeId) {

        this.createdAt = createdAt;
        this.participantId = participantId;
        this.paymentId = paymentId;
        this.ticketTypeId = ticketTypeId;
    }

    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(int ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }
}