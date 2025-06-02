package model;

import java.util.List;

public class Participant extends User {

    private List<BankCard> bankCards;

    public Participant(String username, String email, String password, String userType, List<BankCard> bankCards) {
        super(username, email, password, userType);
        this.bankCards = bankCards;
    }

    public List<BankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(List<BankCard> bankCards) {
        this.bankCards = bankCards;
    }
}