import cli.AuthCLI;
import persistence.EventRepository;
import persistence.LocationRepository;
import service.BankCardService;
import service.EventService;
import service.UserService;

public class Main {

    public static void main(String[] args) {

        UserService userService = new UserService();
        EventService eventService = new EventService(EventRepository.getInstance(), LocationRepository.getInstance());
        BankCardService bankCardService = new BankCardService();
        new AuthCLI(userService, eventService, bankCardService).showAuthMenu();
    }
}