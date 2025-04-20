package services;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GeneralService {

    private List<Organiser> organisers = new ArrayList<>();
    private List<Participant> participants = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<BankCard> bankCards = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();


    public void startCLI() {

        Scanner sc = new Scanner(System.in);
        showStart(sc);
    }

    public void showStart(Scanner sc){

        int x = 4;
        while (x > 3){

            System.out.println("Welcome to the E-Ticketing Platform! Select one of the following operations:");
            System.out.println("(1) Login");
            System.out.println("(2) Register");
            System.out.println("(3) Exit");

            x = sc.nextInt();

            switch (x) {
                case 1:
                    login(sc);
                    break;
                case 2:
                    register(sc);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public void register(Scanner sc){

        System.out.println("Register your account!");
        System.out.println("Are you a participant or organiser?");
        System.out.println("(1) Participant");
        System.out.println("(2) Organiser");
        int x = sc.nextInt();

        if (x < 1 || x > 2) {
            System.out.println("Invalid option!");
            register(sc);
            return;
        }

        System.out.println("Enter your username:");
        String username = sc.next();

        for (Participant participant : participants)
            if (participant.getUsername().equals(username)) {
                System.out.println("Username already exists! Try again:");
                username = "Invalid";
                register(sc);
                break;
            }
        if (username.equals("Invalid")) return;

        System.out.println("Enter your email:");
        String email = sc.next();

        for (Participant participant : participants)
            if (participant.getEmail().equals(email)) {
                System.out.println("Email already associated with an account! Use another email:");
                email = "Invalid";
                register(sc);
                break;
            }
        if (email.equals("Invalid")) return;

        System.out.println("Enter a password:");
        String password = sc.next();

        switch (x) {
            case 1:
                Participant participant = new Participant(username, email, password);
                participants.add(participant);
                break;
            case 2:
                Organiser organiser = new Organiser(username, email, password);
                organisers.add(organiser);
                break;
            default:
                break;
        }

    }

    public void login(Scanner sc){

        System.out.println("Login to your account!");
        System.out.println("Enter your username:");
        String username = sc.next();
        String requiredPassword = "";
        boolean found = false;

        for (Participant participant : participants)
            if (participant.getUsername().equals(username)) {

                requiredPassword = participant.getPassword();
                found = true;
                break;
            }
        for (Organiser organiser : organisers)
            if (organiser.getUsername().equals(username)) {

                requiredPassword = organiser.getPassword();
                found = true;
                break;
            }

        if (!found) {
            System.out.println("Invalid username!");
            login(sc);
            return;
        }

        System.out.println("Enter your password:");
        String password = sc.next();
        found = false;

        while( found == false ){

            if ( !password.equals(requiredPassword) ) {

                System.out.println("Invalid password!");
                System.out.println("Enter your password:");
                password = sc.next();
            }
            else {
                found = true;
                System.out.println("Login successful!");
            }
        }

    }

    

    public static void main(String[] args) {

        GeneralService service = new GeneralService();
        service.startCLI();
    }
}