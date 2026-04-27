package ui;

import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.Ticket;
import model.User;
import service.TicketService;
import service.UserService;
import ui.components.Navbar;
import ui.components.StatusBar;
import util.Navigator;
import util.Theme;

/**
 * This class builds the staff validation panel.
 */
public class StaffScreen implements Navigator.Screen {
    private final TicketService ticketService;
    private final User staffUser;

    private VBox resultArea;
    private TableView<TicketService.ValidationResult> tableView;
    private Label totalValidatedLabel;
    private Label validLabel;
    private Label invalidLabel;
    private Label usedLabel;

    /**
     * This constructor stores the services used by the staff screen.
     */
    public StaffScreen(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.staffUser = userService.getStaffUser();
    }

    /**
     * This method creates the staff screen root.
     */
    @Override
    public Parent createView() {
        BorderPane root = new BorderPane();
        Theme.stylePage(root);

        Button backButton = Theme.createSecondaryButton("← Back");
        backButton.setOnAction(event -> Navigator.getInstance().goBack());
        Label staffLabel = new Label(staffUser.getName());
        Theme.styleText(staffLabel, Theme.body(), Theme.TEXT_SECONDARY);
        root.setTop(new Navbar(backButton, "Ticket Validation — Staff Panel", staffLabel));

        TextField bookingField = Theme.createTextField("Enter Booking Reference...");
        bookingField.setStyle(bookingField.getStyle() + "-fx-font-size: 18px;");

        Button validateButton = Theme.createPrimaryButton("Validate");
        validateButton.setMaxWidth(Double.MAX_VALUE);

        resultArea = new VBox(Theme.SPACE_4);

        VBox leftPanel = new VBox(Theme.SPACE_4);
        leftPanel.setPadding(new Insets(Theme.SPACE_6));
        Theme.styleCard(leftPanel);
        leftPanel.getChildren().addAll(
            Theme.createSectionTitle("Validate Ticket"),
            bookingField,
            validateButton,
            resultArea
        );

        validateButton.setOnAction(event -> handleValidation(bookingField, validateButton));

        tableView = new TableView<>();
        Theme.styleTable(tableView);
        Theme.styleTableHeaders(tableView);
        tableView.getColumns().addAll(
            createColumn("Time", TicketService.ValidationResult::getFormattedTime),
            createColumn("Booking Ref", TicketService.ValidationResult::getBookingRef),
            createColumn("Attendee", result -> result.getAttendeeName().isBlank() ? "-" : result.getAttendeeName()),
            createColumn("Event", result -> result.getEventName().isBlank() ? "-" : result.getEventName()),
            createStatusColumn()
        );

        VBox rightPanel = new VBox(Theme.SPACE_4);
        rightPanel.setPadding(new Insets(Theme.SPACE_6));
        Theme.styleCard(rightPanel);
        rightPanel.getChildren().addAll(Theme.createSectionTitle("Recent Scans"), tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        HBox content = new HBox(Theme.SPACE_6, leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        totalValidatedLabel = createStatLabel("Total Validated Today: 0", Theme.TEXT_PRIMARY);
        validLabel = createStatLabel("Valid: 0", Theme.SUCCESS);
        invalidLabel = createStatLabel("Invalid: 0", Theme.ERROR);
        usedLabel = createStatLabel("Already Used: 0", Theme.WARNING);

        StatusBar statusBar = new StatusBar();
        statusBar.setMessage("Ready for manual validation");
        statusBar.setRightContent(totalValidatedLabel, validLabel, invalidLabel, usedLabel);

        BorderPane.setMargin(content, new Insets(Theme.SPACE_6));
        root.setCenter(content);
        root.setBottom(statusBar);

        refreshValidationData();
        return root;
    }

    /**
     * This method handles ticket validation with a short loading state.
     */
    private void handleValidation(TextField bookingField, Button validateButton) {
        Theme.clearError(bookingField);
        resultArea.getChildren().clear();
        if (bookingField.getText().isBlank()) {
            Theme.markError(bookingField);
            resultArea.getChildren().add(createResultCard("⚠️", "Enter a booking reference to continue.", Theme.WARNING, null));
            return;
        }
        validateButton.setDisable(true);
        resultArea.getChildren().add(Theme.createMutedLabel("Checking booking reference..."));
        PauseTransition pause = new PauseTransition(Duration.millis(450));
        pause.setOnFinished(done -> {
            TicketService.ValidationResult result = ticketService.validateTicket(bookingField.getText().trim());
            resultArea.getChildren().setAll(createValidationCard(result));
            validateButton.setDisable(false);
            refreshValidationData();
        });
        pause.play();
    }

    /**
     * This method creates the validation result card.
     */
    private VBox createValidationCard(TicketService.ValidationResult result) {
        Ticket ticket = result.getTicket();
        VBox extra = null;
        if (ticket != null && "VALID".equals(result.getStatus())) {
            Button markUsedButton = Theme.createPrimaryButton("Mark as Used");
            markUsedButton.setOnAction(event -> {
                ticketService.markTicketAsUsed(ticket.getBookingRef());
                resultArea.getChildren().setAll(createResultCard("✅", "Ticket marked as used successfully.", Theme.SUCCESS, null));
                refreshValidationData();
            });
            extra = new VBox(Theme.SPACE_2,
                createDetailLabel("Attendee", ticket.getAttendeeName()),
                createDetailLabel("Event", ticket.getEventName()),
                createDetailLabel("Ticket Type", ticket.getTicketType()),
                createDetailLabel("Booking Ref", ticket.getBookingRef()),
                markUsedButton
            );
        } else if (ticket != null) {
            extra = new VBox(Theme.SPACE_2,
                createDetailLabel("Attendee", ticket.getAttendeeName()),
                createDetailLabel("Event", ticket.getEventName()),
                createDetailLabel("Ticket Type", ticket.getTicketType()),
                createDetailLabel("Booking Ref", ticket.getBookingRef())
            );
        }
        return switch (result.getStatus()) {
            case "VALID" -> createResultCard("✅", "Ticket Valid", Theme.SUCCESS, extra);
            case "ALREADY USED" -> createResultCard("⚠️", "Already Scanned\n" + result.getMessage(), Theme.WARNING, extra);
            default -> createResultCard("❌", "Invalid Ticket\n" + result.getMessage(), Theme.ERROR, extra);
        };
    }

    /**
     * This method creates a result summary card.
     */
    private VBox createResultCard(String icon, String message, javafx.scene.paint.Color color, VBox extraContent) {
        VBox card = new VBox(Theme.SPACE_3);
        card.setPadding(new Insets(Theme.SPACE_4));
        card.setBackground(new Background(new BackgroundFill(color.deriveColor(0, 1, 1, 0.12), new CornerRadii(14), Insets.EMPTY)));
        card.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, new CornerRadii(14), new BorderWidths(1))));

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 26px;");
        Label messageLabel = new Label(message);
        Theme.styleText(messageLabel, Theme.heading3(), color);
        messageLabel.setWrapText(true);

        card.getChildren().addAll(iconLabel, messageLabel);
        if (extraContent != null) {
            card.getChildren().add(extraContent);
        }
        return card;
    }

    /**
     * This method creates a small detail label.
     */
    private Label createDetailLabel(String title, String value) {
        Label label = new Label(title + ": " + value);
        Theme.styleText(label, Theme.body(), Theme.TEXT_PRIMARY);
        return label;
    }

    /**
     * This method refreshes table and bottom stats.
     */
    private void refreshValidationData() {
        if (tableView != null) {
            tableView.getItems().setAll(ticketService.getRecentValidations());
        }
        totalValidatedLabel.setText("Total Validated Today: " + ticketService.getTotalValidatedToday());
        validLabel.setText("Valid: " + ticketService.getCountForStatusToday("VALID"));
        invalidLabel.setText("Invalid: " + ticketService.getCountForStatusToday("INVALID"));
        usedLabel.setText("Already Used: " + ticketService.getCountForStatusToday("ALREADY USED"));
    }

    /**
     * This method creates a label for the bottom stats bar.
     */
    private Label createStatLabel(String text, javafx.scene.paint.Color color) {
        Label label = new Label(text);
        Theme.styleText(label, Theme.small(), color);
        return label;
    }

    /**
     * This method creates a normal string table column.
     */
    private TableColumn<TicketService.ValidationResult, String> createColumn(String title,
                                                                             java.util.function.Function<TicketService.ValidationResult, String> mapper) {
        TableColumn<TicketService.ValidationResult, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(mapper.apply(data.getValue())));
        return column;
    }

    /**
     * This method creates the validation status badge column.
     */
    private TableColumn<TicketService.ValidationResult, String> createStatusColumn() {
        TableColumn<TicketService.ValidationResult, String> column = new TableColumn<>("Status");
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(Theme.createBadge(item, Theme.statusColor(item).deriveColor(0, 1, 1, 0.15), Theme.statusColor(item)));
                }
            }
        });
        return column;
    }
}
