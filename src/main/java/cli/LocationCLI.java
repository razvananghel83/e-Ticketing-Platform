package cli;

import model.Location;
import model.User;
import service.LocationService;
import service.UserService;

import java.util.List;
import java.util.Scanner;

public class LocationCLI {

    private final LocationService locationService;
    private final UserService userService;
    private final Scanner scanner;

    public LocationCLI(LocationService locationService, UserService userService) {
        this.locationService = locationService;
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }


    public void showLocationMenu() {
        User currentUser = userService.getCurrentUser().orElse(null);

        if (currentUser == null || !"ORGANISER".equals(currentUser.getUserType())) {
            System.out.println("Access denied. Only organizers can manage locations.");
            return;
        }

        while (true) {
            System.out.println("\n=== Location Management ===\n");
            System.out.println("1. View All Locations");
            System.out.println("2. Search Locations by Name");
            System.out.println("3. Add New Location");
            System.out.println("4. Update Existing Location");
            System.out.println("5. Delete Location");
            System.out.println("6. Back to User Menu");
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
                    case 1 -> listAllLocations();
                    case 2 -> searchLocationsByName();
                    case 3 -> addLocation();
                    case 4 -> updateLocation();
                    case 5 -> deleteLocation();
                    case 6 -> { return; }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listAllLocations() {
        List<Location> locations = locationService.getAllLocations();

        if (locations.isEmpty()) {
            System.out.println("No locations found.");
            return;
        }

        System.out.println("\n=== All Locations ===\n");
        displayLocationsList(locations);
    }

    private void searchLocationsByName() {
        System.out.print("Enter location name to search: ");
        String namePattern = scanner.nextLine().trim();

        if (namePattern.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        List<Location> locations = locationService.searchLocationsByName(namePattern);

        if (locations.isEmpty()) {
            System.out.println("No locations found matching '" + namePattern + "'.");
            return;
        }

        System.out.println("\n=== Locations matching '" + namePattern + "' ===\n");
        displayLocationsList(locations);
    }

    private void addLocation() {
        System.out.println("\n=== Add New Location ===\n");

        System.out.print("Enter location name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Location name cannot be empty.");
            return;
        }

        System.out.print("Enter location type (e.g., STADIUM, THEATER, CONCERT_HALL): ");
        String type = scanner.nextLine().trim();

        if (type.isEmpty()) {
            System.out.println("Location type cannot be empty.");
            return;
        }

        try {
            Location location = locationService.createLocation(name, type);
            System.out.println("Location added successfully with ID: " + location.getLocationId());
        } catch (Exception e) {
            System.out.println("Error adding location: " + e.getMessage());
        }
    }

    private void updateLocation() {
        List<Location> locations = locationService.getAllLocations();

        if (locations.isEmpty()) {
            System.out.println("No locations available to update.");
            return;
        }

        System.out.println("\n=== Update Location ===\n");
        displayLocationsList(locations);

        System.out.print("Enter ID of location to update: ");
        int locationId;

        try {
            locationId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid location ID.");
            return;
        }

        if (locationService.getLocationById(locationId).isEmpty()) {
            System.out.println("Location with ID " + locationId + " not found.");
            return;
        }

        Location location = locationService.getLocationById(locationId).get();

        System.out.println("Current name: " + location.getName());
        System.out.print("Enter new name (or press Enter to keep current): ");
        String name = scanner.nextLine().trim();

        System.out.println("Current type: " + location.getType());
        System.out.print("Enter new type (or press Enter to keep current): ");
        String type = scanner.nextLine().trim();

        try {
            locationService.updateLocation(locationId, 
                    name.isEmpty() ? location.getName() : name, 
                    type.isEmpty() ? location.getType() : type);
            System.out.println("Location updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating location: " + e.getMessage());
        }
    }

    private void deleteLocation() {
        List<Location> locations = locationService.getAllLocations();

        if (locations.isEmpty()) {
            System.out.println("No locations available to delete.");
            return;
        }

        System.out.println("\n=== Delete Location ===\n");
        displayLocationsList(locations);

        System.out.print("Enter ID of location to delete: ");
        int locationId;

        try {
            locationId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid location ID.");
            return;
        }

        if (locationService.getLocationById(locationId).isEmpty()) {
            System.out.println("Location with ID " + locationId + " not found.");
            return;
        }

        System.out.print("Are you sure you want to delete this location? (y/n): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if ("y".equals(confirmation)) {
            try {
                locationService.deleteLocation(locationId);
                System.out.println("Location deleted successfully!");
            } catch (Exception e) {
                System.out.println("Error deleting location: " + e.getMessage());
            }
        } else {
            System.out.println("Deletion canceled.");
        }
    }

    private void displayLocationsList(List<Location> locations) {
        for (Location location : locations) {
            System.out.printf("ID: %d | Name: %s | Type: %s%n", 
                    location.getLocationId(), 
                    location.getName(), 
                    location.getType());
        }
        System.out.println();
    }
}

