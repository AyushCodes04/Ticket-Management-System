import javafx.application.Application;
import javafx.stage.Stage;
import service.EventService;
import service.TicketService;
import service.UserService;
import ui.MainScreen;
import util.Navigator;

/**
 * This class starts the JavaFX application.
 */
public class Main extends Application {

    /**
     * This method creates the services and opens the first screen.
     */
    @Override
    public void start(Stage primaryStage) {
        EventService eventService = new EventService();
        TicketService ticketService = new TicketService(eventService);
        UserService userService = new UserService();

        primaryStage.setTitle("EventHub - Event Ticket Management System");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        Navigator.getInstance().initialize(primaryStage, new MainScreen(eventService, ticketService, userService));
    }

    /**
     * This method launches the JavaFX runtime.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
