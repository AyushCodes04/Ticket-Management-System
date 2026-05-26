package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

        var currentUser = userService.getCurrentUser();
        roleCards.getChildren().addAll(
            createRoleCard(
                "🎯", "Organizer",
                "Events create karo, sales track karo, stats dekho.",
                "Demo: organizer@eventhub.com / organizer123",
                currentUser != null && "ORGANIZER".equalsIgnoreCase(currentUser.getRole())
                    ? "Continue to Dashboard"
                    : "Login",
                () -> {
                    if (currentUser != null && "ORGANIZER".equalsIgnoreCase(currentUser.getRole())) {
                        Navigator.getInstance().navigateTo(new OrganizerScreen(eventService, ticketService, userService));
                        return;
                    }
                    openLoginDialog("ORGANIZER", "Organizer Login", "organizer@eventhub.com", () ->
                        Navigator.getInstance().navigateTo(new OrganizerScreen(eventService, ticketService, userService)));
                }
            ),
            createRoleCard(
                "🎟", "Attendee",
                "Events browse karo, tickets buy karo, booking history dekho.",
                "Demo: aman@example.com / attendee123",
                currentUser != null && "ATTENDEE".equalsIgnoreCase(currentUser.getRole())
                    ? "Continue to Browse"
                    : "Login",
                () -> {
                    if (currentUser != null && "ATTENDEE".equalsIgnoreCase(currentUser.getRole())) {
                        Navigator.getInstance().navigateTo(new AttendeeScreen(eventService, ticketService, userService));
                        return;
                    }
                    openLoginDialog("ATTENDEE", "Attendee Login", "aman@example.com", () ->
                        Navigator.getInstance().navigateTo(new AttendeeScreen(eventService, ticketService, userService)));
                }
            ),
            createRoleCard(
                "✅", "Staff",
                "Gate entry pe tickets validate karo aur recent scans dekho.",
                "Demo: staff@eventhub.com / staff123",
                currentUser != null && "STAFF".equalsIgnoreCase(currentUser.getRole())
                    ? "Continue to Scanner"
                    : "Login",
                () -> {
                    if (currentUser != null && "STAFF".equalsIgnoreCase(currentUser.getRole())) {
                        Navigator.getInstance().navigateTo(new StaffScreen(eventService, ticketService, userService));
                        return;
                    }
                    openLoginDialog("STAFF", "Staff Login", "staff@eventhub.com", () ->
                        Navigator.getInstance().navigateTo(new StaffScreen(eventService, ticketService, userService)));
                }
            )
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
    private VBox createRoleCard(String icon, String title, String description, String hint, String buttonText, Runnable action) {
        VBox card = new VBox(Theme.SPACE_4);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(Theme.SPACE_6));
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        Theme.styleRaisedCard(card);
        Theme.installCardHover(card, card);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Theme.heading2());

        Label titleLabel = new Label(title);
        Theme.styleText(titleLabel, Theme.heading3(), Theme.TEXT_PRIMARY);

        Label descriptionLabel = new Label(description);
        Theme.styleText(descriptionLabel, Theme.body(), Theme.TEXT_SECONDARY);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setAlignment(Pos.CENTER);

        Label hintLabel = new Label(hint);
        Theme.styleText(hintLabel, Theme.small(), Theme.TEXT_SECONDARY);
        hintLabel.setWrapText(true);
        hintLabel.setAlignment(Pos.CENTER);

        Button actionButton = Theme.createPrimaryButton(buttonText);
        actionButton.setMaxWidth(Double.MAX_VALUE);
        actionButton.setOnAction(event -> action.run());

        VBox.setVgrow(descriptionLabel, Priority.ALWAYS);
        card.getChildren().addAll(iconLabel, titleLabel, descriptionLabel, hintLabel, actionButton);
        return card;
    }

    /**
     * This method login dialog show karke success par onSuccess run karta hai.
     */
    private void openLoginDialog(String role, String title, String emailHint, Runnable onSuccess) {
        Stage dialog = new Stage();
        dialog.initOwner(Navigator.getInstance().getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        VBox content = new VBox(Theme.SPACE_4);
        content.setPadding(new Insets(Theme.SPACE_6));
        Theme.stylePage(content);

        Label heading = new Label(title);
        Theme.styleText(heading, Theme.heading2(), Theme.TEXT_PRIMARY);

        Label emailLabel = new Label("Email");
        Theme.styleText(emailLabel, Theme.body(), Theme.TEXT_PRIMARY);
        TextField emailField = Theme.createTextField("Email");
        emailField.setText(emailHint);

        Label passLabel = new Label("Password");
        Theme.styleText(passLabel, Theme.body(), Theme.TEXT_PRIMARY);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Theme.styleInput(passwordField);

        Label errorLabel = new Label();
        Theme.styleText(errorLabel, Theme.small(), Theme.ERROR);

        Button loginButton = Theme.createPrimaryButton("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setOnAction(e -> {
            errorLabel.setText("");
            Theme.clearError(emailField);
            Theme.clearError(passwordField);

            var user = userService.login(role, emailField.getText(), passwordField.getText());
            if (user == null) {
                Theme.markError(emailField);
                Theme.markError(passwordField);
                errorLabel.setText("Login failed. Demo credentials use karo.");
                return;
            }
            dialog.close();
            onSuccess.run();
        });

        content.getChildren().addAll(
            heading,
            emailLabel,
            emailField,
            passLabel,
            passwordField,
            errorLabel,
            loginButton
        );

        dialog.setScene(new Scene(content, 420, 360));
        dialog.showAndWait();
    }
}
