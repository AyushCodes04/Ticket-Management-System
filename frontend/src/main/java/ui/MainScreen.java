package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import service.EventService;
import service.TicketService;
import service.UserService;
import util.Navigator;
import util.Theme;

/**
 * This class builds the landing screen for all user roles.
 */
public class MainScreen implements Navigator.Screen {
    private final EventService eventService;
    private final TicketService ticketService;
    private final UserService userService;

    /**
     * This constructor stores service dependencies for navigation.
     */
    public MainScreen(EventService eventService, TicketService ticketService, UserService userService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.userService = userService;
    }

    /**
     * This method creates the landing screen layout.
     */
    @Override
    public Parent createView() {
        BorderPane root = new BorderPane();
        Theme.stylePage(root);
        root.setPadding(new Insets(Theme.SPACE_8));

        VBox content = new VBox(Theme.SPACE_8);
        content.setAlignment(Pos.CENTER);

        StackPane logo = new StackPane();
        logo.setPrefSize(88, 88);
        logo.setMaxSize(88, 88);
        logo.setBackground(new Background(new BackgroundFill(Theme.PRIMARY, new CornerRadii(44), Insets.EMPTY)));
        Label logoText = new Label("E");
        Theme.styleText(logoText, Theme.heading1(), Theme.SURFACE);
        logo.getChildren().add(logoText);

        Label titleLabel = new Label("EventHub");
        Theme.styleText(titleLabel, Theme.heading1(), Theme.PRIMARY);

        Label taglineLabel = new Label("Your complete event ticketing solution");
        Theme.styleText(taglineLabel, Theme.body(), Theme.TEXT_SECONDARY);

        FlowPane roleCards = new FlowPane();
        roleCards.setAlignment(Pos.CENTER);
        roleCards.setHgap(Theme.SPACE_6);
        roleCards.setVgap(Theme.SPACE_6);
        roleCards.setMaxWidth(1160);

        roleCards.getChildren().addAll(
            createRoleCard("🎯", "Event Organizer", "Create events, manage ticket sales, track revenue and attendance", "Go to Dashboard",
                () -> Navigator.getInstance().navigateTo(new OrganizerScreen(eventService, ticketService, userService))),
            createRoleCard("🎟", "Event Attendee", "Browse events, purchase tickets, view your booking history", "Browse Events",
                () -> Navigator.getInstance().navigateTo(new AttendeeScreen(eventService, ticketService, userService))),
            createRoleCard("✅", "Event Staff", "Validate tickets at entry, scan QR codes, manage gate access", "Open Scanner",
                () -> Navigator.getInstance().navigateTo(new StaffScreen(ticketService, userService)))
        );

        VBox.setVgrow(roleCards, Priority.ALWAYS);
        content.getChildren().addAll(logo, titleLabel, taglineLabel, roleCards);

        Label footer = new Label("© 2025 EventHub | PBL Java Project");
        Theme.styleText(footer, Theme.small(), Theme.TEXT_SECONDARY);
        BorderPane.setAlignment(footer, Pos.CENTER);
        BorderPane.setMargin(footer, new Insets(Theme.SPACE_6, 0, 0, 0));

        root.setCenter(content);
        root.setBottom(footer);
        return root;
    }

    /**
     * This method creates one role entry card.
     */
    private VBox createRoleCard(String icon, String title, String description, String buttonText, Runnable action) {
        VBox card = new VBox(Theme.SPACE_4);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(Theme.SPACE_6));
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        Theme.styleRaisedCard(card);
        Theme.installCardHover(card, card);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 34px;");

        Label titleLabel = new Label(title);
        Theme.styleText(titleLabel, Theme.heading3(), Theme.TEXT_PRIMARY);

        Label descriptionLabel = new Label(description);
        Theme.styleText(descriptionLabel, Theme.body(), Theme.TEXT_SECONDARY);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setAlignment(Pos.CENTER);

        Button actionButton = Theme.createPrimaryButton(buttonText);
        actionButton.setMaxWidth(Double.MAX_VALUE);
        actionButton.setOnAction(event -> action.run());

        VBox.setVgrow(descriptionLabel, Priority.ALWAYS);
        card.getChildren().addAll(iconLabel, titleLabel, descriptionLabel, actionButton);
        return card;
    }
}
