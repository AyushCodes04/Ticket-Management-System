package ui;

import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Event;
import model.Ticket;
import model.User;
import service.EventService;
import service.TicketService;
import service.UserService;
import ui.components.EventCard;
import ui.components.Navbar;
import util.Navigator;
import util.Theme;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class builds the attendee browsing and ticket purchase experience.
 */
public class AttendeeScreen implements Navigator.Screen {
    private final EventService eventService;
    private final TicketService ticketService;
    private final UserService userService;
    private final User attendeeUser;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private VBox eventsList;
    private TextField searchField;
    private VBox purchasePanel;
    private Event selectedEvent;
    private String activeCategoryFilter = "All";
    private String activeDateFilter = "All";
    private final Map<String, Integer> selectedQuantities = new HashMap<>();

    /**
     * This constructor stores the services used by the attendee screen.
     */
    public AttendeeScreen(EventService eventService, TicketService ticketService, UserService userService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.attendeeUser = userService.getAttendeeUser();
    }

    /**
     * This method creates the attendee screen root.
     */
    @Override
    public Parent createView() {
        BorderPane root = new BorderPane();
        Theme.stylePage(root);

        Button backButton = Theme.createSecondaryButton("← Back");
        backButton.setOnAction(event -> Navigator.getInstance().goBack());

        Button myTicketsButton = Theme.createSecondaryButton("My Tickets");
        myTicketsButton.setOnAction(event -> showMyTicketsDialog());

        root.setTop(new Navbar(backButton, "Browse Events", myTicketsButton));

        searchField = Theme.createTextField("Search events by name or venue...");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> refreshEventList());

        HBox categoryFilters = new HBox(Theme.SPACE_2,
            createCategoryButton("All"),
            createCategoryButton("Concert"),
            createCategoryButton("Sports"),
            createCategoryButton("Tech"),
            createCategoryButton("Cultural")
        );

        HBox dateFilters = new HBox(Theme.SPACE_2,
            createDateButton("All"),
            createDateButton("Today"),
            createDateButton("This Week"),
            createDateButton("This Month")
        );

        eventsList = new VBox(Theme.SPACE_4);
        VBox leftPanel = new VBox(Theme.SPACE_4, searchField, categoryFilters, dateFilters, eventsList);
        ScrollPane leftScroll = Theme.createScrollPane(leftPanel);
        leftScroll.setFitToWidth(true);

        purchasePanel = new VBox(Theme.SPACE_4);
        purchasePanel.setPadding(new Insets(Theme.SPACE_6));
        Theme.styleCard(purchasePanel);
        showDefaultPurchaseState();

        HBox content = new HBox(Theme.SPACE_6, leftScroll, purchasePanel);
        HBox.setHgrow(leftScroll, Priority.ALWAYS);
        HBox.setHgrow(purchasePanel, Priority.ALWAYS);
        leftScroll.prefWidthProperty().bind(content.widthProperty().multiply(0.6));
        purchasePanel.prefWidthProperty().bind(content.widthProperty().multiply(0.4));

        BorderPane.setMargin(content, new Insets(Theme.SPACE_6));
        root.setCenter(content);
        refreshEventList();
        return root;
    }

    /**
     * This method refreshes the attendee event list.
     */
    private void refreshEventList() {
        if (eventsList == null) {
            return;
        }
        String query = searchField.getText().trim().toLowerCase();
        List<Event> filteredEvents = eventService.getAllEvents().stream()
            .filter(event -> query.isBlank() || event.getName().toLowerCase().contains(query) || event.getVenueName().toLowerCase().contains(query))
            .filter(event -> "All".equals(activeCategoryFilter) || event.getCategory().equalsIgnoreCase(activeCategoryFilter))
            .filter(this::matchesDateFilter)
            .toList();

        eventsList.getChildren().clear();
        if (filteredEvents.isEmpty()) {
            VBox emptyState = new VBox(Theme.SPACE_3);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(Theme.SPACE_12));
            Theme.styleCard(emptyState);
            emptyState.getChildren().addAll(
                new Label("🎟"),
                Theme.createSectionTitle("No matching events"),
                Theme.createMutedLabel("Try adjusting the search or filter options.")
            );
            eventsList.getChildren().add(emptyState);
            return;
        }
        for (Event event : filteredEvents) {
            eventsList.getChildren().add(createAttendeeEventCard(event));
        }
    }

    /**
     * This method creates one event card for the attendee list.
     */
    private EventCard createAttendeeEventCard(Event event) {
        EventCard card = new EventCard(event, true);
        Label priceLabel = new Label("₹" + formatAmount(event.getMinimumPrice()) + " onwards");
        Theme.styleText(priceLabel, Theme.heading3(), Theme.PRIMARY);

        Button buyButton = Theme.createPrimaryButton("View & Buy");
        buyButton.setOnAction(actionEvent -> selectEvent(event));

        card.getSideBox().getChildren().addAll(priceLabel, buyButton);
        return card;
    }

    /**
     * This method handles event selection.
     */
    private void selectEvent(Event event) {
        selectedEvent = eventService.getEventById(event.getId());
        selectedQuantities.clear();
        for (Event.TicketType ticketType : selectedEvent.getTicketTypes()) {
            selectedQuantities.put(ticketType.getTypeName(), 0);
        }
        showPurchasePanel();
    }

    /**
     * This method shows the purchase panel for the selected event.
     */
    private void showPurchasePanel() {
        purchasePanel.getChildren().clear();
        purchasePanel.setAlignment(Pos.TOP_LEFT);
        if (selectedEvent == null) {
            showDefaultPurchaseState();
            return;
        }

        Label titleLabel = new Label(selectedEvent.getName());
        Theme.styleText(titleLabel, Theme.heading2(), Theme.TEXT_PRIMARY);

        Label detailsLabel = new Label(selectedEvent.getDate() + " • " + selectedEvent.getTime() + "\n"
            + selectedEvent.getVenueName() + ", " + selectedEvent.getVenueAddress());
        Theme.styleText(detailsLabel, Theme.body(), Theme.TEXT_SECONDARY);
        detailsLabel.setWrapText(true);

        Label descriptionLabel = new Label(selectedEvent.getDescription());
        Theme.styleText(descriptionLabel, Theme.body(), Theme.TEXT_PRIMARY);
        descriptionLabel.setWrapText(true);

        Label totalLabel = new Label("₹0");
        Theme.styleText(totalLabel, Theme.heading2(), Theme.PRIMARY);

        VBox ticketRows = new VBox(Theme.SPACE_3);
        for (Event.TicketType ticketType : selectedEvent.getTicketTypes()) {
            ticketRows.getChildren().add(createTicketRow(ticketType, totalLabel));
        }

        TextField nameField = Theme.createTextField("Attendee name");
        nameField.setText(attendeeUser.getName());
        TextField emailField = Theme.createTextField("Attendee email");
        emailField.setText(attendeeUser.getEmail());

        Label errorLabel = new Label();
        Theme.styleText(errorLabel, Theme.small(), Theme.ERROR);

        Button purchaseButton = Theme.createPrimaryButton("Purchase Tickets");
        purchaseButton.setMaxWidth(Double.MAX_VALUE);
        purchaseButton.setOnAction(event -> handlePurchase(nameField, emailField, totalLabel, errorLabel, purchaseButton));

        purchasePanel.getChildren().addAll(
            titleLabel,
            detailsLabel,
            descriptionLabel,
            new Separator(),
            Theme.createSectionTitle("Select Tickets"),
            ticketRows,
            new Separator(),
            createTotalBlock(totalLabel),
            createFieldBlock("Attendee Name", nameField),
            createFieldBlock("Attendee Email", emailField),
            errorLabel,
            purchaseButton
        );
    }

    /**
     * This method creates one row for ticket quantity selection.
     */
    private HBox createTicketRow(Event.TicketType ticketType, Label totalLabel) {
        HBox row = new HBox(Theme.SPACE_4);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox infoBox = new VBox(Theme.SPACE_1);
        Label typeLabel = new Label(ticketType.getTypeName() + " • ₹" + formatAmount(ticketType.getPrice()));
        Theme.styleText(typeLabel, Theme.body(), Theme.TEXT_PRIMARY);
        Label remainingLabel = new Label((ticketType.getTotalQuantity() - ticketType.getSoldQuantity()) + " seats left");
        Theme.styleText(remainingLabel, Theme.small(), Theme.TEXT_SECONDARY);
        infoBox.getChildren().addAll(typeLabel, remainingLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Button minusButton = Theme.createSecondaryButton("-");
        minusButton.setMinWidth(36);
        Button plusButton = Theme.createSecondaryButton("+");
        plusButton.setMinWidth(36);

        Label quantityLabel = new Label("0");
        Theme.styleText(quantityLabel, Theme.heading3(), Theme.TEXT_PRIMARY);

        Label subtotalLabel = new Label("₹0");
        Theme.styleText(subtotalLabel, Theme.body(), Theme.TEXT_SECONDARY);
        subtotalLabel.setMinWidth(80);

        minusButton.setOnAction(event -> updateTicketQuantity(ticketType, -1, quantityLabel, subtotalLabel, totalLabel));
        plusButton.setOnAction(event -> updateTicketQuantity(ticketType, 1, quantityLabel, subtotalLabel, totalLabel));

        row.getChildren().addAll(infoBox, minusButton, quantityLabel, plusButton, subtotalLabel);
        return row;
    }

    /**
     * This method updates one ticket quantity and total.
     */
    private void updateTicketQuantity(Event.TicketType ticketType, int delta, Label quantityLabel, Label subtotalLabel, Label totalLabel) {
        int current = selectedQuantities.getOrDefault(ticketType.getTypeName(), 0);
        int maxAllowed = ticketType.getTotalQuantity() - ticketType.getSoldQuantity();
        int updated = Math.max(0, Math.min(maxAllowed, current + delta));
        selectedQuantities.put(ticketType.getTypeName(), updated);
        quantityLabel.setText(String.valueOf(updated));
        subtotalLabel.setText("₹" + formatAmount(ticketType.getPrice() * updated));
        totalLabel.setText("₹" + formatAmount(calculateTotal()));
    }

    /**
     * This method handles the purchase action.
     */
    private void handlePurchase(TextField nameField, TextField emailField, Label totalLabel, Label errorLabel, Button purchaseButton) {
        Theme.clearError(nameField);
        Theme.clearError(emailField);
        errorLabel.setText("");
        if (nameField.getText().isBlank()) {
            Theme.markError(nameField);
            errorLabel.setText("Attendee name is required.");
            return;
        }
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            Theme.markError(emailField);
            errorLabel.setText("Enter a valid email address.");
            return;
        }
        if (calculateTotal() <= 0) {
            errorLabel.setText("Select at least one ticket.");
            return;
        }
        purchaseButton.setDisable(true);
        errorLabel.setText("Processing payment...");
        PauseTransition pause = new PauseTransition(Duration.millis(650));
        pause.setOnFinished(done -> {
            try {
                String bookingRef = ticketService.purchaseTickets(selectedEvent.getId(), selectedQuantities, nameField.getText().trim(), emailField.getText().trim());
                showBookingSuccessDialog(bookingRef, totalLabel.getText());
                selectEvent(selectedEvent);
                refreshEventList();
            } catch (IllegalArgumentException exception) {
                errorLabel.setText(exception.getMessage());
            } finally {
                purchaseButton.setDisable(false);
            }
        });
        pause.play();
    }

    /**
     * This method shows the default right-side purchase state.
     */
    private void showDefaultPurchaseState() {
        purchasePanel.setAlignment(Pos.CENTER);
        purchasePanel.getChildren().setAll(
            new Label("🎟"),
            Theme.createSectionTitle("Select an event to purchase tickets"),
            Theme.createMutedLabel("Choose any event from the list to view pricing and complete your booking.")
        );
    }

    /**
     * This method shows the booking success dialog.
     */
    private void showBookingSuccessDialog(String bookingRef, String totalAmount) {
        VBox content = new VBox(Theme.SPACE_4);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(Theme.SPACE_6));
        Theme.stylePage(content);

        Label heading = new Label("Booking Confirmed! 🎉");
        Theme.styleText(heading, Theme.heading2(), Theme.SUCCESS);

        Label bookingLabel = new Label("Booking Reference: " + bookingRef);
        Theme.styleText(bookingLabel, Theme.heading3(), Theme.TEXT_PRIMARY);

        Label summaryLabel = new Label("Event: " + selectedEvent.getName() + "\nDate: " + selectedEvent.getDate() + " • " + selectedEvent.getTime()
            + "\nVenue: " + selectedEvent.getVenueName() + ", " + selectedEvent.getVenueAddress() + "\nAmount: " + totalAmount);
        Theme.styleText(summaryLabel, Theme.body(), Theme.TEXT_SECONDARY);
        summaryLabel.setWrapText(true);

        Button closeButton = Theme.createPrimaryButton("Close");
        closeButton.setOnAction(event -> ((Stage) closeButton.getScene().getWindow()).close());

        content.getChildren().addAll(heading, bookingLabel, summaryLabel, closeButton);
        Stage dialog = createDialogStage("Booking Confirmed", content, 480, 320);
        dialog.showAndWait();
    }

    /**
     * This method opens the attendee ticket history dialog.
     */
    private void showMyTicketsDialog() {
        TableView<Ticket> tableView = new TableView<>();
        Theme.styleTable(tableView);
        Theme.styleTableHeaders(tableView);
        tableView.getColumns().addAll(
            createTicketColumn("Event", Ticket::getEventName),
            createTicketColumn("Date", ticket -> {
                Event event = eventService.getEventById(ticket.getEventId());
                return event == null ? "-" : event.getDate();
            }),
            createTicketColumn("Ticket Type", Ticket::getTicketType),
            createTicketColumn("Qty", ticket -> String.valueOf(ticket.getQuantity())),
            createTicketColumn("Amount", ticket -> "₹" + formatAmount(ticket.getTotalAmount())),
            createTicketColumn("Booking Ref", Ticket::getBookingRef),
            createTicketStatusColumn()
        );
        tableView.getItems().setAll(ticketService.getTicketsByAttendee(attendeeUser.getEmail()));

        VBox content = new VBox(Theme.SPACE_4);
        content.setPadding(new Insets(Theme.SPACE_6));
        Theme.stylePage(content);
        Label title = new Label("My Tickets");
        Theme.styleText(title, Theme.heading2(), Theme.TEXT_PRIMARY);
        content.getChildren().addAll(title, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        if (tableView.getItems().isEmpty()) {
            content.getChildren().add(Theme.createMutedLabel("No tickets purchased yet"));
        }

        Stage dialog = createDialogStage("My Tickets", content, 860, 420);
        dialog.showAndWait();
    }

    /**
     * This method creates a ticket history table column.
     */
    private TableColumn<Ticket, String> createTicketColumn(String title, java.util.function.Function<Ticket, String> mapper) {
        TableColumn<Ticket, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(mapper.apply(data.getValue())));
        return column;
    }

    /**
     * This method creates the ticket status badge column.
     */
    private TableColumn<Ticket, String> createTicketStatusColumn() {
        TableColumn<Ticket, String> column = new TableColumn<>("Status");
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

    /**
     * This method creates the category filter buttons.
     */
    private Button createCategoryButton(String category) {
        Button button = Theme.createSecondaryButton(category);
        if (category.equals(activeCategoryFilter)) {
            applySelectedFilterStyle(button);
        }
        button.setOnAction(event -> {
            activeCategoryFilter = category;
            resetSiblingButtons(button);
            applySelectedFilterStyle(button);
            refreshEventList();
        });
        return button;
    }

    /**
     * This method creates the date filter buttons.
     */
    private Button createDateButton(String dateFilter) {
        Button button = Theme.createSecondaryButton(dateFilter);
        if (dateFilter.equals(activeDateFilter)) {
            applySelectedFilterStyle(button);
        }
        button.setOnAction(event -> {
            activeDateFilter = dateFilter;
            resetSiblingButtons(button);
            applySelectedFilterStyle(button);
            refreshEventList();
        });
        return button;
    }

    /**
     * This method resets sibling filter button styles.
     */
    private void resetSiblingButtons(Button selectedButton) {
        HBox parent = (HBox) selectedButton.getParent();
        for (javafx.scene.Node node : parent.getChildren()) {
            if (node instanceof Button button) {
                button.setTextFill(Theme.PRIMARY);
                button.setBackground(new Background(new BackgroundFill(Theme.SURFACE, new CornerRadii(8), Insets.EMPTY)));
                button.setBorder(new Border(new BorderStroke(Theme.PRIMARY, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1.2))));
            }
        }
    }

    /**
     * This method applies the selected filter style.
     */
    private void applySelectedFilterStyle(Button button) {
        button.setTextFill(Theme.SURFACE);
        button.setBackground(new Background(new BackgroundFill(Theme.PRIMARY, new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(Border.EMPTY);
    }

    /**
     * This method checks if an event matches the active date filter.
     */
    private boolean matchesDateFilter(Event event) {
        if ("All".equals(activeDateFilter)) {
            return true;
        }
        try {
            LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
            return switch (activeDateFilter) {
                case "Today" -> eventDate.isEqual(LocalDate.now());
                case "This Week" -> !eventDate.isBefore(LocalDate.now()) && !eventDate.isAfter(LocalDate.now().plusDays(7));
                case "This Month" -> eventDate.getMonth() == LocalDate.now().getMonth() && eventDate.getYear() == LocalDate.now().getYear();
                default -> true;
            };
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * This method calculates the current ticket total.
     */
    private double calculateTotal() {
        if (selectedEvent == null) {
            return 0;
        }
        return selectedEvent.getTicketTypes().stream()
            .mapToDouble(type -> type.getPrice() * selectedQuantities.getOrDefault(type.getTypeName(), 0))
            .sum();
    }

    /**
     * This method creates the total summary block.
     */
    private HBox createTotalBlock(Label totalLabel) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        Label totalText = new Label("Total Amount");
        Theme.styleText(totalText, Theme.heading3(), Theme.TEXT_PRIMARY);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().addAll(totalText, spacer, totalLabel);
        return box;
    }

    /**
     * This method creates a label and field block.
     */
    private VBox createFieldBlock(String title, Control field) {
        VBox box = new VBox(Theme.SPACE_2);
        Label label = new Label(title);
        Theme.styleText(label, Theme.body(), Theme.TEXT_PRIMARY);
        box.getChildren().addAll(label, field);
        return box;
    }

    /**
     * This method formats currency values nicely.
     */
    private String formatAmount(double amount) {
        return amount == Math.rint(amount) ? String.valueOf((int) amount) : String.format("%.2f", amount);
    }

    /**
     * This method creates a themed dialog stage.
     */
    private Stage createDialogStage(String title, Parent content, double width, double height) {
        Stage dialog = new Stage();
        dialog.initOwner(Navigator.getInstance().getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        dialog.setScene(new Scene(content, width, height));
        return dialog;
    }
}
