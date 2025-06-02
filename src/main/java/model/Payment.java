package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private int paymentId;
    private String cardNumber; // References BankCard
    private BigDecimal amount;
    private LocalDateTime paymentDate;

    public Payment(LocalDateTime paymentDate, BigDecimal amount, String cardNumber) {

        this.paymentDate = paymentDate;
        this.amount = amount;
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
}