package service;

import model.BankCard;
import persistence.BankCardRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class BankCardService {

    private final BankCardRepository bankCardRepository;

    public BankCardService() {
        this.bankCardRepository = BankCardRepository.getInstance();
    }

    public BankCard addCard(String cardNumber, String cvv, String cardHolderName, LocalDate expiryDate, int userId) {
        validateCardDetails(cardNumber, cvv, expiryDate);

        BankCard card = new BankCard(cardNumber, cvv, cardHolderName, expiryDate, userId);
        return bankCardRepository.save(card);
    }

    public Optional<BankCard> getCardByNumber(String cardNumber) {
        return bankCardRepository.findById(cardNumber);
    }

    public List<BankCard> getCardsByUserId(int userId) {
        return bankCardRepository.findByUserId(userId);
    }

    public List<BankCard> getAllCards() {
        return bankCardRepository.findAll();
    }

    public BankCard updateCard(String cardNumber, String cvv, String cardHolderName, LocalDate expiryDate) {
        Optional<BankCard> cardOpt = bankCardRepository.findById(cardNumber);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card with number " + cardNumber + " not found");
        }

        BankCard card = cardOpt.get();

        boolean updated = false;

        if (cvv != null && !cvv.isEmpty()) {
            validateCvv(cvv);
            card.setCvv(cvv);
            updated = true;
        }

        if (cardHolderName != null && !cardHolderName.isEmpty()) {
            card.setCardHolderName(cardHolderName);
            updated = true;
        }

        if (expiryDate != null) {
            validateExpiryDate(expiryDate);
            card.setExpiryDate(expiryDate);
            updated = true;
        }

        if (updated) {
            bankCardRepository.update(card);
        }

        return card;
    }

    public void deleteCard(String cardNumber) {
        Optional<BankCard> cardOpt = bankCardRepository.findById(cardNumber);
        if (cardOpt.isEmpty()) {
            throw new IllegalArgumentException("Card with number " + cardNumber + " not found");
        }

        bankCardRepository.delete(cardOpt.get());
    }

    private void validateCardDetails(String cardNumber, String cvv, LocalDate expiryDate) {
        validateCardNumber(cardNumber);
        validateCvv(cvv);
        validateExpiryDate(expiryDate);
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be empty");
        }

        if (!cardNumber.matches("\\d{16}")) {
            throw new IllegalArgumentException("Card number must be exactly 16 digits");
        }
    }

    private void validateCvv(String cvv) {
        if (cvv == null || cvv.isEmpty()) {
            throw new IllegalArgumentException("CVV cannot be empty");
        }

        // CVV should be 3-4 digits
        if (!cvv.matches("\\d{3}")) {
            throw new IllegalArgumentException("CVV must be exactly 3 digits");
        }
    }

    private void validateExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date cannot be null");
        }

        if (expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Card is expired");
        }
    }
}
