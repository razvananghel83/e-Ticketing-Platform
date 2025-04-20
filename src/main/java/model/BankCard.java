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
            throw new IllegalArgumentException("Numărul cardului trebuie să aibă exact 16 cifre!");
        }
        this.number = number;
    }

    public void setCvv(String cvv) {
        if (!CVV_PATTERN.matcher(cvv).matches()) {
            throw new IllegalArgumentException("CVV-ul trebuie să aibă exact 3 cifre!");
        }
        this.cvv = cvv;
    }

    public void setCardHolderName(String cardHolderName) {
        if (!CARD_HOLDER_PATTERN.matcher(cardHolderName).matches()) {
            throw new IllegalArgumentException("Numele deținătorului cardului trebuie să aibă cel puțin două cuvinte care " +
                    "să înceapă cu majuscule!");
        }
        this.cardHolderName = cardHolderName;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null || !expiryDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de expirare a cardului trebuie să fie ulterioară datei curente!");
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
