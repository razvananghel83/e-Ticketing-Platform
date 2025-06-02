package cli;

import model.Event;
import model.User;
import service.EventService;
import service.UserService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserMenu {

    private final UserService userService;
    private final EventService eventService;
    private final Scanner scanner;

    public UserMenu(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
        this.scanner = new Scanner(System.in);
    }

    public void showUserMenu() {
        Optional<User> currentUser = userService.getCurrentUser();
        
        if (currentUser.isEmpty()) {
            System.out.println("Access denied. Please log in to access this menu.");
            return;
        }

        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. View Profile");
            System.out.println("2. Edit Profile");
            
            if ("ORGANISER".equals(currentUser.get().getUserType())) {
                System.out.println("3. Manage Events");
                System.out.println("4. Create New Event");
            }
            
            System.out.println("5. Logout");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> showProfile();
                case 2 -> editProfile();
                case 3 -> {
                    if ("ORGANISER".equals(currentUser.get().getUserType())) {
                        manageEvents();
                    } else {
                        System.out.println("Invalid option");
                    }
                }
                case 4 -> {
                    if ("ORGANISER".equals(currentUser.get().getUserType())) {
                        createEvent();
                    } else {
                        System.out.println("Invalid option");
                    }
                }
                case 5 -> {
                    logout();
                    return;
                }
                case 6 -> {
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void showProfile() {
        userService.getCurrentUser().ifPresentOrElse(
                user -> System.out.println("Profile:\nUsername: " + user.getUsername() +
                                           "\nEmail: " + user.getEmail() +
                                           "\nUser Type: " + user.getUserType()),
                () -> System.out.println("No user is logged in.")
        );
    }

    private void editProfile() {
        System.out.println("Enter new username:");
        String newUsername = scanner.nextLine();
        System.out.println("Enter new email:");
        String newEmail = scanner.nextLine();
        System.out.println("Enter new password (leave empty to keep current):");
        String newPassword = scanner.nextLine();

        try {
            userService.updateUser(newUsername, newEmail, newPassword);
            System.out.println("Profile updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }
    
    private void createEvent() {
        User organizer = userService.getCurrentUser().orElse(null);
        if (organizer == null || !"ORGANISER".equals(organizer.getUserType())) {
            System.out.println("Only organizers can create events.");
            return;
        }
        
        try {
            System.out.println("\n=== Create New Event ===");
            
            System.out.println("Enter event name:");
            String name = scanner.nextLine();
            
            System.out.println("Enter location ID:");
            int locationId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter event date and time (yyyy-MM-dd HH:mm):");
            String dateTime = scanner.nextLine();
            
            System.out.println("Enter artists/performers:");
            String artists = scanner.nextLine();
            
            System.out.println("Enter event type (e.g., CONCERT, SPORTS, THEATER):");
            String eventType = scanner.nextLine();
            
            System.out.println("Enter event description:");
            String description = scanner.nextLine();
            
            Event newEvent = eventService.createEvent(
                organizer, 
                locationId, 
                name, 
                dateTime, 
                artists, 
                eventType, 
                description
            );
            
            System.out.println("Event created successfully with ID: " + newEvent.getEventId());
            
        } catch (Exception e) {
            System.out.println("Error creating event: " + e.getMessage());
        }
    }
    
    private void manageEvents() {
        User organizer = userService.getCurrentUser().orElse(null);
        if (organizer == null || !"ORGANISER".equals(organizer.getUserType())) {
            System.out.println("Only organizers can manage events.");
            return;
        }
        
        List<Event> events = eventService.getEventsByOrganizer(organizer);
        
        if (events.isEmpty()) {
            System.out.println("You haven't created any events yet.");
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        while (true) {
            System.out.println("\n=== Your Events ===");
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                System.out.printf("%d. %s - %s%n", 
                    i + 1, 
                    event.getName(), 
                    event.getDate().format(formatter)
                );
            }
            
            System.out.println("\nOptions:");
            System.out.println("1. Edit an event");
            System.out.println("2. Back to user menu");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice == 1) {
                System.out.print("Enter event number to edit: ");
                int eventIndex = scanner.nextInt();
                scanner.nextLine();
                
                if (eventIndex < 1 || eventIndex > events.size()) {
                    System.out.println("Invalid event number.");
                    continue;
                }
                
                editEvent(events.get(eventIndex - 1));
                events = eventService.getEventsByOrganizer(organizer);

            } else if (choice == 2) {
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
    
    private void editEvent(Event event) {
        User organizer = userService.getCurrentUser().orElse(null);
        if (organizer == null) return;
        
        System.out.println("\n=== Edit Event: " + event.getName() + " ===");
        
        System.out.println("Enter new event name (current: " + event.getName() + "):");
        String name = scanner.nextLine();
        if (name.isEmpty()) name = event.getName();
        
        System.out.println("Enter new location ID (current: " + event.getLocationId() + "):");
        String locationIdStr = scanner.nextLine();
        int locationId = locationIdStr.isEmpty() ? event.getLocationId() : Integer.parseInt(locationIdStr);
        
        System.out.println("Enter new event date and time (yyyy-MM-dd HH:mm) (current: " + 
            event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "):");
        String dateTime = scanner.nextLine();
        
        System.out.println("Enter new artists/performers (current: " + event.getArtists() + "):");
        String artists = scanner.nextLine();
        if (artists.isEmpty()) artists = event.getArtists();
        
        System.out.println("Enter new event type (current: " + event.getEventType() + "):");
        String eventType = scanner.nextLine();
        if (eventType.isEmpty()) eventType = event.getEventType();
        
        System.out.println("Enter new event description (current: " + event.getDescription() + "):");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = event.getDescription();
        
        try {
            eventService.updateEvent(
                organizer,
                event.getEventId(),
                locationId,
                name,
                dateTime,
                artists,
                eventType,
                description
            );
            
            System.out.println("Event updated successfully!");
            
        } catch (Exception e) {
            System.out.println("Error updating event: " + e.getMessage());
        }
    }

    private void logout() {
        userService.logout();
        System.out.println("You have been logged out.");
    }
}