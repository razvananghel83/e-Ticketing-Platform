package model;

import java.time.LocalDate;

public class BankCard {

    private String cardNumber;
    private String cvv;
    private LocalDate expiryDate;
    private String cardHolderName;
    private int userId; // References Participant

    public BankCard(String cardNumber, String cvv, String cardHolderName, LocalDate expiryDate, int userId) {

        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
}