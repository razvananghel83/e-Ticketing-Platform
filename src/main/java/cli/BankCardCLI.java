package cli;

import model.BankCard;
import model.User;
import service.BankCardService;
import service.UserService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class BankCardCLI {

    private final BankCardService bankCardService;
    private final UserService userService;
    private final Scanner scanner;
    private final DateTimeFormatter shortExpiryFormatter = DateTimeFormatter.ofPattern("MM/yy");
    private final DateTimeFormatter longExpiryFormatter = DateTimeFormatter.ofPattern("MM/yyyy");

    public BankCardCLI(BankCardService bankCardService, UserService userService) {
        this.bankCardService = bankCardService;
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void showBankCardMenu() {
        Optional<User> currentUser = userService.getCurrentUser();

        if (currentUser.isEmpty()) {
            System.out.println("Access denied. Please log in to access this menu.");
            return;
        }

        if (!"PARTICIPANT".equals(currentUser.get().getUserType())) {
            System.out.println("Only participants can manage bank cards.");
            return;
        }

        while (true) {
            System.out.println("\n=== Bank Card Management ===\n");
            System.out.println("1. View My Cards");
            System.out.println("2. Add New Card");
            System.out.println("3. Update Card");
            System.out.println("4. Delete Card");
            System.out.println("5. Back to User Menu");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            try {
                switch (choice) {
                    case 1 -> listUserCards();
                    case 2 -> addCard();
                    case 3 -> updateCard();
                    case 4 -> deleteCard();
                    case 5 -> { return; }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listUserCards() {
        User user = userService.getCurrentUser().orElseThrow();
        List<BankCard> cards = bankCardService.getCardsByUserId(user.getUserId());

        if (cards.isEmpty()) {
            System.out.println("You don't have any bank cards registered.");
            return;
        }

        System.out.println("\n=== Your Bank Cards ===\n");
        for (int i = 0; i < cards.size(); i++) {
            BankCard card = cards.get(i);
            String maskedNumber = maskCardNumber(card.getCardNumber());

            System.out.printf("%d. %s - %s - Expires: %s\n", 
                i + 1,
                maskedNumber, 
                card.getCardHolderName(),
                card.getExpiryDate().format(DateTimeFormatter.ofPattern("MM/yyyy"))
            );
        }
    }

    private void addCard() {
        User user = userService.getCurrentUser().orElseThrow();

        System.out.println("\n=== Add New Bank Card ===\n");

        String cardNumber = getValidatedInput("Enter card number (16 digits): ", this::validateCardNumber);
        String cvv = getValidatedInput("Enter CVV (3 digits): ", this::validateCvv);
        String cardHolderName = getValidatedInput("Enter card holder name: ", this::validateCardHolderName);
        LocalDate expiryDate = getValidatedExpiryDate("Enter expiry date (MM/YY): ");

        try {
            BankCard card = bankCardService.addCard(cardNumber, cvv, cardHolderName, expiryDate, user.getUserId());
            System.out.println("Card added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding card: " + e.getMessage());
        }
    }

    private void updateCard() {
        User user = userService.getCurrentUser().orElseThrow();
        List<BankCard> cards = bankCardService.getCardsByUserId(user.getUserId());

        if (cards.isEmpty()) {
            System.out.println("You don't have any bank cards to update.");
            return;
        }

        listUserCards();
        int cardIndex = getValidatedIntInput("Enter the number of the card to update: ", 1, cards.size()) - 1;
        BankCard selectedCard = cards.get(cardIndex);

        System.out.println("\n=== Update Card ===\n");
        System.out.println("Leave fields blank to keep current values.");

        System.out.println("Current CVV: ***");
        String cvv = scanner.nextLine().trim();

        System.out.println("Current Card Holder: " + selectedCard.getCardHolderName());
        String cardHolderName = scanner.nextLine().trim();

        System.out.println("Current Expiry Date: " + selectedCard.getExpiryDate().format(DateTimeFormatter.ofPattern("MM/yyyy")));
        String expiryDateStr = scanner.nextLine().trim();

        LocalDate expiryDate = null;
        if (!expiryDateStr.isEmpty()) {
            try {
                // Try to parse using different formats
                YearMonth yearMonth;
                if (expiryDateStr.matches("\\d{2}/\\d{2}")) {
                    // Format: MM/YY
                    yearMonth = YearMonth.parse(expiryDateStr, shortExpiryFormatter);
                } else if (expiryDateStr.matches("\\d{2}/\\d{4}")) {
                    // Format: MM/YYYY
                    yearMonth = YearMonth.parse(expiryDateStr, longExpiryFormatter);
                } else {
                    throw new DateTimeParseException("Invalid format", expiryDateStr, 0);
                }

                expiryDate = yearMonth.atEndOfMonth();
                validateExpiryDate(expiryDate);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use MM/YY or MM/YYYY format. Using current expiry date.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + " Using current expiry date.");
            }
        }

        try {
            bankCardService.updateCard(
                selectedCard.getCardNumber(),
                cvv.isEmpty() ? null : cvv,
                cardHolderName.isEmpty() ? null : cardHolderName,
                expiryDate
            );
            System.out.println("Card updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating card: " + e.getMessage());
        }
    }

    /**
     * Deletes an existing card
     */
    private void deleteCard() {
        User user = userService.getCurrentUser().orElseThrow();
        List<BankCard> cards = bankCardService.getCardsByUserId(user.getUserId());

        if (cards.isEmpty()) {
            System.out.println("You don't have any bank cards to delete.");
            return;
        }

        listUserCards();
        int cardIndex = getValidatedIntInput("Enter the number of the card to delete: ", 1, cards.size()) - 1;
        BankCard selectedCard = cards.get(cardIndex);

        System.out.print("Are you sure you want to delete this card? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("y")) {
            try {
                bankCardService.deleteCard(selectedCard.getCardNumber());
                System.out.println("Card deleted successfully!");
            } catch (Exception e) {
                System.out.println("Error deleting card: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 4) {
            return cardNumber;
        }

        String firstFour = cardNumber.substring(0, 4);
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return firstFour + " **** **** " + lastFour;
    }


    private String getValidatedInput(String prompt, java.util.function.Predicate<String> validator) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
        } while (!validator.test(input));
        return input;
    }

    private int getValidatedIntInput(String prompt, int min, int max) {
        int input;
        while (true) {
            System.out.print(prompt);
            try {
                input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private LocalDate getValidatedExpiryDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                // Try to parse using different formats
                YearMonth yearMonth;
                if (input.matches("\\d{2}/\\d{2}")) {
                    // Format: MM/YY
                    yearMonth = YearMonth.parse(input, shortExpiryFormatter);
                } else if (input.matches("\\d{2}/\\d{4}")) {
                    // Format: MM/YYYY
                    yearMonth = YearMonth.parse(input, longExpiryFormatter);
                } else {
                    throw new DateTimeParseException("Invalid format", input, 0);
                }

                LocalDate expiryDate = yearMonth.atEndOfMonth();
                validateExpiryDate(expiryDate);
                return expiryDate;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use MM/YY or MM/YYYY format (e.g., 07/25 or 07/2025).");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean validateCardNumber(String cardNumber) {
        if (!cardNumber.matches("\\d{16}")) {
            System.out.println("Card number must be exactly 16 digits.");
            return false;
        }
        return true;
    }

    private boolean validateCvv(String cvv) {
        if (!cvv.matches("\\d{3}")) {
            System.out.println("CVV must be exactly 3 digits.");
            return false;
        }
        return true;
    }

    private boolean validateCardHolderName(String name) {
        if (name.isEmpty()) {
            System.out.println("Card holder name cannot be empty.");
            return false;
        }
        return true;
    }

    private void validateExpiryDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Card is expired.");
        }
    }
}
