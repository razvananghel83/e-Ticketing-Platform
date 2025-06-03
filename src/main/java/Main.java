import cli.AuthCLI;
import persistence.EventRepository;
import persistence.LocationRepository;
import persistence.UserRepository;
import service.EventService;
import service.UserService;

public class Main {

    public static void main(String[] args) {

        UserService userService = new UserService(UserRepository.getInstance());
        EventService eventService = new EventService(EventRepository.getInstance(), LocationRepository.getInstance());
        new AuthCLI(userService, eventService).showAuthMenu();
    }
}