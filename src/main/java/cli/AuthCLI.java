package cli;

import service.EventService;
import service.UserService;
import java.util.Scanner;

public class AuthCLI {

    private final UserService userService;
    private final UserMenu userMenu;
    private final Scanner scanner;

        public AuthCLI(UserService userService, EventService eventService) {

        this.userService = userService;
        this.userMenu = new UserMenu(userService, eventService);
        this.scanner = new Scanner(System.in);
    }

    public void showAuthMenu() {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> loginUser();
                case 3 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("User type (PARTICIPANT/ORGANISER): ");
        String userType = scanner.nextLine();

        try {
            userService.register(username, email, password, userType);
            System.out.println("Registration successful!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loginUser() {
        System.out.print("Enter username/email: ");
        String identifier = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            userService.login(identifier, password);
            System.out.println("Login successful!");
            userMenu.showUserMenu();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}