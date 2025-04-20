package model;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class BankCard {

    private User cardHolder;
    private String number;
    private String cvv;
    private String cardHolderName;
    private LocalDate expiryDate;

    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{16}");
    private static final Pattern CVV_PATTERN = Pattern.compile("\\d{3}");
    private static final Pattern CARD_HOLDER_PATTERN = Pattern.compile("([A-Z][a-z]+\\s)+[A-Z][a-z]+");

    public BankCard(User cardHolder, String number, String cvv, String cardHolderName, LocalDate expiryDate) {

        this.cardHolder = cardHolder;
        setNumber(number);
        setCvv(cvv);
        setCardHolderName(cardHolderName);
        setExpiryDate(expiryDate);
    }

    public void setNumber(String number) {
        if (!CARD_NUMBER_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("Card number must have exactly 16 digits!");
        }
        this.number = number;
    }

    public void setCvv(String cvv) {
        if (!CVV_PATTERN.matcher(cvv).matches()) {
            throw new IllegalArgumentException("CVV must have exactly 3 digits!");
        }
        this.cvv = cvv;
    }

    public void setCardHolderName(String cardHolderName) {
        if (!CARD_HOLDER_PATTERN.matcher(cardHolderName).matches()) {
            throw new IllegalArgumentException("Cardholder name must have at least 2 words, each starting with a capital letter!");
        }
        this.cardHolderName = cardHolderName;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null || !expiryDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Card expiry date must be in the future!");
        }
        this.expiryDate = expiryDate;
    }

    public User getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(User cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getNumber() {
        return number;
    }

    public String getCvv() {
        return cvv;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

}
