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
import java.util.ArrayList;
import java.util.List;

/**
 * This class builds the organizer dashboard with event creation and sales tools.
 */
public class OrganizerScreen implements Navigator.Screen {
    private final EventService eventService;
    private final TicketService ticketService;
    private final UserService userService;
    private final User organizerUser;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private FlowPane eventsGrid;
    private TextField eventSearchField;
    private String activeEventFilter = "All";
    private Label totalEventsValue;
    private Label totalTicketsValue;
    private Label totalRevenueValue;
    private Label upcomingEventsValue;
    private TableView<Event> salesTable;
    private VBox previewCard;

    /**
     * This constructor stores the services used by the organizer screen.
     */
    public OrganizerScreen(EventService eventService, TicketService ticketService, UserService userService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.organizerUser = userService.getOrganizerUser();
    }

    /**
     * This method creates the organizer screen root.
     */
    @Override
    public Parent createView() {
        BorderPane root = new BorderPane();
        Theme.stylePage(root);

        Button backButton = Theme.createSecondaryButton("← Back");
        backButton.setOnAction(event -> Navigator.getInstance().goBack());

        Label welcomeLabel = new Label("Welcome, " + organizerUser.getName());
        Theme.styleText(welcomeLabel, Theme.body(), Theme.TEXT_SECONDARY);

        root.setTop(new Navbar(backButton, "Organizer Dashboard", welcomeLabel));

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: " + Theme.toRgbString(Theme.BACKGROUND) + ";");
        tabPane.getTabs().addAll(
            createTab("Create Event", createEventTab()),
            createTab("My Events", createMyEventsTab()),
            createTab("Sales Dashboard", createSalesDashboardTab())
        );

        BorderPane.setMargin(tabPane, new Insets(Theme.SPACE_6));
        root.setCenter(tabPane);
        refreshOrganizerData();
        return root;
    }

    /**
     * This method creates one dashboard tab.
     */
    private Tab createTab(String title, Parent content) {
        Tab tab = new Tab(title, content);
        tab.setClosable(false);
        return tab;
    }

    /**
     * This method creates the create event tab.
     */
    private Parent createEventTab() {
        TextField nameField = Theme.createTextField("Enter event name");
        TextArea descriptionArea = Theme.createTextArea("Describe the event experience", 3);
        TextField dateField = Theme.createTextField("DD/MM/YYYY");
        TextField timeField = Theme.createTextField("HH:MM AM/PM");
        TextField venueField = Theme.createTextField("Venue name");
        TextField venueAddressField = Theme.createTextField("Venue address");
        ComboBox<String> categoryBox = Theme.createComboBox();
        categoryBox.getItems().addAll("Concert", "Sports", "Tech", "Cultural", "Other");
        categoryBox.setPromptText("Select category");
        TextField capacityField = Theme.createTextField("Total capacity");

        TextField typeNameField = Theme.createTextField("VIP / General");
        TextField priceField = Theme.createTextField("Price in rupees");
        TextField quantityField = Theme.createTextField("Quantity");

        Label formErrorLabel = new Label();
        Theme.styleText(formErrorLabel, Theme.small(), Theme.ERROR);

        Label ticketErrorLabel = new Label();
        Theme.styleText(ticketErrorLabel, Theme.small(), Theme.ERROR);

        List<Event.TicketType> draftTypes = new ArrayList<>();
        FlowPane chipPane = new FlowPane();
        chipPane.setHgap(Theme.SPACE_2);
        chipPane.setVgap(Theme.SPACE_2);

        previewCard = new VBox(Theme.SPACE_3);
        previewCard.setPadding(new Insets(Theme.SPACE_6));
        Theme.styleCard(previewCard);

        Runnable previewUpdater = () -> updatePreview(nameField.getText(), descriptionArea.getText(), dateField.getText(), timeField.getText(),
            venueField.getText(), venueAddressField.getText(), categoryBox.getValue(), draftTypes);

        nameField.textProperty().addListener((obs, oldValue, newValue) -> previewUpdater.run());
        descriptionArea.textProperty().addListener((obs, oldValue, newValue) -> previewUpdater.run());
        dateField.textProperty().addListener((obs, oldValue, newValue) -> previewUpdater.run());
        timeField.textProperty().addListener((obs, oldValue, newValue) -> previewUpdater.run());
        venueField.textProperty().addListener((obs, oldValue, newValue) -> previewUpdater.run());
        venueAddressField.textProperty().addListener((obs, oldValue, newValue) -> previewUpdater.run());
        categoryBox.valueProperty().addListener((obs, oldValue, newValue) -> previewUpdater.run());

        Button addTicketButton = Theme.createSecondaryButton("+ Add Ticket Type");
        addTicketButton.setOnAction(event -> {
            ticketErrorLabel.setText("");
            if (typeNameField.getText().isBlank()) {
                Theme.markError(typeNameField);
                ticketErrorLabel.setText("Ticket type name is required.");
                return;
            }
            if (!isPositiveNumber(priceField.getText())) {
                Theme.markError(priceField);
                ticketErrorLabel.setText("Enter a valid ticket price.");
                return;
            }
            if (!isPositiveWholeNumber(quantityField.getText())) {
                Theme.markError(quantityField);
                ticketErrorLabel.setText("Enter a valid ticket quantity.");
                return;
            }
            Event.TicketType ticketType = new Event.TicketType(
                typeNameField.getText().trim(),
                Double.parseDouble(priceField.getText().trim()),
                Integer.parseInt(quantityField.getText().trim()),
                0
            );
            draftTypes.add(ticketType);
            chipPane.getChildren().add(createTicketChip(ticketType, draftTypes, chipPane, previewUpdater));
            typeNameField.clear();
            priceField.clear();
            quantityField.clear();
            previewUpdater.run();
        });

        VBox leftPanel = new VBox(Theme.SPACE_4);
        leftPanel.setPadding(new Insets(Theme.SPACE_6));
        Theme.styleCard(leftPanel);
        leftPanel.getChildren().addAll(
            Theme.createSectionTitle("Event Details"),
            createFieldBlock("Event Name", nameField),
            createFieldBlock("Description", descriptionArea),
            createFieldBlock("Date", dateField),
            createFieldBlock("Time", timeField),
            createFieldBlock("Venue Name", venueField),
            createFieldBlock("Venue Address", venueAddressField),
            createFieldBlock("Category", categoryBox),
            createFieldBlock("Max Capacity", capacityField),
            createDivider(),
            Theme.createSectionTitle("Ticket Types"),
            createFieldBlock("Type Name", typeNameField),
            createFieldBlock("Price ₹", priceField),
            createFieldBlock("Quantity", quantityField),
            addTicketButton,
            ticketErrorLabel,
            chipPane,
            formErrorLabel
        );

        Button createEventButton = Theme.createPrimaryButton("Create Event");
        createEventButton.setMaxWidth(Double.MAX_VALUE);
        createEventButton.setOnAction(event -> {
            formErrorLabel.setText("");
            if (!validateEventForm(nameField, descriptionArea, dateField, timeField, venueField, venueAddressField, categoryBox, capacityField, draftTypes, formErrorLabel)) {
                return;
            }
            Event newEvent = new Event();
            newEvent.setName(nameField.getText().trim());
            newEvent.setDescription(descriptionArea.getText().trim());
            newEvent.setDate(dateField.getText().trim());
            newEvent.setTime(timeField.getText().trim());
            newEvent.setVenueName(venueField.getText().trim());
            newEvent.setVenueAddress(venueAddressField.getText().trim());
            newEvent.setCategory(categoryBox.getValue());
            newEvent.setMaxCapacity(Integer.parseInt(capacityField.getText().trim()));
            newEvent.setTicketTypes(draftTypes.stream().map(Event.TicketType::copy).toList());
            createEventButton.setDisable(true);
            formErrorLabel.setText("Creating event...");
            PauseTransition pause = new PauseTransition(Duration.millis(500));
            pause.setOnFinished(done -> {
                eventService.createEvent(newEvent);
                createEventButton.setDisable(false);
                showInfoDialog("Event Created", "Your event has been added successfully.");
                clearEventForm(nameField, descriptionArea, dateField, timeField, venueField, venueAddressField, categoryBox, capacityField,
                    typeNameField, priceField, quantityField, chipPane, draftTypes, formErrorLabel, ticketErrorLabel);
                previewUpdater.run();
                refreshOrganizerData();
            });
            pause.play();
        });
        leftPanel.getChildren().add(createEventButton);

        VBox rightPanel = new VBox(Theme.SPACE_4);
        rightPanel.setPadding(new Insets(Theme.SPACE_6));
        Theme.styleCard(rightPanel);
        rightPanel.getChildren().addAll(Theme.createSectionTitle("Event Preview"), previewCard);
        updatePreview("", "", "", "", "", "", null, draftTypes);

        HBox content = new HBox(Theme.SPACE_6, leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        leftPanel.prefWidthProperty().bind(content.widthProperty().multiply(0.4));
        rightPanel.prefWidthProperty().bind(content.widthProperty().multiply(0.6));
        return Theme.createScrollPane(content);
    }

    /**
     * This method creates the my events tab.
     */
    private Parent createMyEventsTab() {
        eventSearchField = Theme.createTextField("Search by event name");
        eventSearchField.textProperty().addListener((obs, oldValue, newValue) -> refreshMyEventsGrid());

        HBox filters = new HBox(Theme.SPACE_2);
        filters.getChildren().addAll(
            createFilterButton("All"),
            createFilterButton("Upcoming"),
            createFilterButton("Past")
        );

        eventsGrid = new FlowPane();
        eventsGrid.setHgap(Theme.SPACE_4);
        eventsGrid.setVgap(Theme.SPACE_4);
        eventsGrid.setPrefWrapLength(920);

        VBox container = new VBox(Theme.SPACE_4);
        container.getChildren().addAll(eventSearchField, filters, eventsGrid);
        return Theme.createScrollPane(container);
    }

    /**
     * This method creates the sales dashboard tab.
     */
    private Parent createSalesDashboardTab() {
        HBox statsRow = new HBox(Theme.SPACE_4);
        totalEventsValue = new Label("0");
        totalTicketsValue = new Label("0");
        totalRevenueValue = new Label("₹0");
        upcomingEventsValue = new Label("0");
        statsRow.getChildren().addAll(
            createStatCard("Total Events Created", totalEventsValue),
            createStatCard("Total Tickets Sold", totalTicketsValue),
            createStatCard("Total Revenue ₹", totalRevenueValue),
            createStatCard("Upcoming Events", upcomingEventsValue)
        );

        salesTable = new TableView<>();
        Theme.styleTable(salesTable);
        Theme.styleTableHeaders(salesTable);
        salesTable.getColumns().addAll(
            createColumn("Event Name", Event::getName),
            createColumn("Date", Event::getDate),
            createColumn("Tickets Sold", event -> event.getTotalTicketsSold() + " / " + event.getTotalTicketQuantity()),
            createColumn("Revenue", event -> "₹" + formatAmount(event.getRevenue())),
            createStatusColumn()
        );

        VBox container = new VBox(Theme.SPACE_4, statsRow, salesTable);
        VBox.setVgrow(salesTable, Priority.ALWAYS);
        return container;
    }

    /**
     * This method refreshes organizer widgets with latest service data.
     */
    private void refreshOrganizerData() {
        refreshMyEventsGrid();
        refreshSalesDashboard();
    }

    /**
     * This method refreshes the event grid based on search and filters.
     */
    private void refreshMyEventsGrid() {
        if (eventsGrid == null) {
            return;
        }
        String query = eventSearchField == null ? "" : eventSearchField.getText().trim().toLowerCase();
        List<Event> filtered = eventService.getAllEvents().stream()
            .filter(event -> query.isBlank() || event.getName().toLowerCase().contains(query))
            .filter(event -> switch (activeEventFilter) {
                case "Upcoming" -> "UPCOMING".equalsIgnoreCase(event.getStatus()) || "ONGOING".equalsIgnoreCase(event.getStatus());
                case "Past" -> "COMPLETED".equalsIgnoreCase(event.getStatus());
                default -> true;
            })
            .toList();
        eventsGrid.getChildren().clear();
        if (filtered.isEmpty()) {
            VBox emptyState = new VBox(Theme.SPACE_3);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setPadding(new Insets(Theme.SPACE_12));
            Theme.styleCard(emptyState);
            emptyState.getChildren().addAll(
                new Label("🗂"),
                Theme.createSectionTitle("No events yet."),
                Theme.createMutedLabel("Create your first event!")
            );
            eventsGrid.getChildren().add(emptyState);
            return;
        }
        for (Event event : filtered) {
            eventsGrid.getChildren().add(createOrganizerEventCard(event));
        }
    }

    /**
     * This method refreshes the sales stats and table.
     */
    private void refreshSalesDashboard() {
        if (salesTable == null) {
            return;
        }
        List<Event> events = eventService.getAllEvents();
        int totalEvents = events.size();
        int totalTickets = events.stream().mapToInt(Event::getTotalTicketsSold).sum();
        double totalRevenue = events.stream().mapToDouble(Event::getRevenue).sum();
        long upcoming = events.stream().filter(event -> "UPCOMING".equalsIgnoreCase(event.getStatus()) || "ONGOING".equalsIgnoreCase(event.getStatus())).count();

        totalEventsValue.setText(String.valueOf(totalEvents));
        totalTicketsValue.setText(String.valueOf(totalTickets));
        totalRevenueValue.setText("₹" + formatAmount(totalRevenue));
        upcomingEventsValue.setText(String.valueOf(upcoming));
        salesTable.getItems().setAll(events);
    }

    /**
     * This method creates the event form preview content.
     */
    private void updatePreview(String name, String description, String date, String time, String venueName,
                               String venueAddress, String category, List<Event.TicketType> ticketTypes) {
        if (previewCard == null) {
            return;
        }
        previewCard.getChildren().clear();

        Label nameLabel = new Label(name == null || name.isBlank() ? "Event name will appear here" : name);
        Theme.styleText(nameLabel, Theme.heading2(), name == null || name.isBlank() ? Theme.TEXT_SECONDARY : Theme.TEXT_PRIMARY);

        Label dateTimeLabel = new Label((date == null || date.isBlank() ? "DD/MM/YYYY" : date) + " • " + (time == null || time.isBlank() ? "HH:MM AM/PM" : time));
        Theme.styleText(dateTimeLabel, Theme.body(), Theme.TEXT_SECONDARY);

        Label venueLabel = new Label((venueName == null || venueName.isBlank() ? "Venue name" : venueName) +
            ", " + (venueAddress == null || venueAddress.isBlank() ? "Venue address" : venueAddress));
        Theme.styleText(venueLabel, Theme.body(), Theme.TEXT_SECONDARY);
        venueLabel.setWrapText(true);

        Label descriptionLabel = new Label(description == null || description.isBlank() ? "Event description will appear in the preview." : description);
        Theme.styleText(descriptionLabel, Theme.body(), description == null || description.isBlank() ? Theme.TEXT_SECONDARY : Theme.TEXT_PRIMARY);
        descriptionLabel.setWrapText(true);

        HBox headerBadges = new HBox(Theme.SPACE_2);
        headerBadges.getChildren().add(Theme.createBadge(category == null ? "Category" : category,
            Theme.categoryColor(category).deriveColor(0, 1, 1, 0.12), Theme.categoryColor(category)));

        VBox ticketList = new VBox(Theme.SPACE_2);
        Label ticketHeading = new Label("Ticket Types");
        Theme.styleText(ticketHeading, Theme.heading3(), Theme.TEXT_PRIMARY);
        ticketList.getChildren().add(ticketHeading);
        if (ticketTypes.isEmpty()) {
            ticketList.getChildren().add(Theme.createMutedLabel("Added ticket types will appear here."));
        } else {
            for (Event.TicketType type : ticketTypes) {
                Label line = new Label(type.getTypeName() + " • ₹" + formatAmount(type.getPrice()) + " • " + type.getTotalQuantity() + " seats");
                Theme.styleText(line, Theme.body(), Theme.TEXT_PRIMARY);
                ticketList.getChildren().add(line);
            }
        }

        previewCard.getChildren().addAll(nameLabel, dateTimeLabel, venueLabel, headerBadges, descriptionLabel, createDivider(), ticketList);
    }

    /**
     * This method validates the event form values.
     */
    private boolean validateEventForm(TextField nameField, TextArea descriptionArea, TextField dateField, TextField timeField,
                                      TextField venueField, TextField venueAddressField, ComboBox<String> categoryBox,
                                      TextField capacityField, List<Event.TicketType> draftTypes, Label formErrorLabel) {
        clearFieldStyles(nameField, descriptionArea, dateField, timeField, venueField, venueAddressField, categoryBox, capacityField);
        if (nameField.getText().isBlank() || descriptionArea.getText().isBlank() || dateField.getText().isBlank()
            || timeField.getText().isBlank() || venueField.getText().isBlank() || venueAddressField.getText().isBlank()
            || categoryBox.getValue() == null || capacityField.getText().isBlank()) {
            formErrorLabel.setText("Please complete all event details.");
            markBlankFields(nameField, descriptionArea, dateField, timeField, venueField, venueAddressField, categoryBox, capacityField);
            return false;
        }
        if (!isValidDate(dateField.getText().trim())) {
            Theme.markError(dateField);
            formErrorLabel.setText("Date must use DD/MM/YYYY format.");
            return false;
        }
        if (!timeField.getText().trim().matches("(?i)^(0?[1-9]|1[0-2]):[0-5][0-9]\\s?(AM|PM)$")) {
            Theme.markError(timeField);
            formErrorLabel.setText("Time must use HH:MM AM/PM format.");
            return false;
        }
        if (!isPositiveWholeNumber(capacityField.getText().trim())) {
            Theme.markError(capacityField);
            formErrorLabel.setText("Capacity must be a whole number.");
            return false;
        }
        if (draftTypes.isEmpty()) {
            formErrorLabel.setText("Add at least one ticket type.");
            return false;
        }
        return true;
    }

    /**
     * This method clears all form values after a successful create.
     */
    private void clearEventForm(TextField nameField, TextArea descriptionArea, TextField dateField, TextField timeField,
                                TextField venueField, TextField venueAddressField, ComboBox<String> categoryBox, TextField capacityField,
                                TextField typeNameField, TextField priceField, TextField quantityField, FlowPane chipPane,
                                List<Event.TicketType> draftTypes, Label formErrorLabel, Label ticketErrorLabel) {
        nameField.clear();
        descriptionArea.clear();
        dateField.clear();
        timeField.clear();
        venueField.clear();
        venueAddressField.clear();
        categoryBox.getSelectionModel().clearSelection();
        capacityField.clear();
        typeNameField.clear();
        priceField.clear();
        quantityField.clear();
        chipPane.getChildren().clear();
        draftTypes.clear();
        formErrorLabel.setText("");
        ticketErrorLabel.setText("");
    }

    /**
     * This method creates a removable chip for a ticket type.
     */
    private HBox createTicketChip(Event.TicketType ticketType, List<Event.TicketType> draftTypes, FlowPane chipPane, Runnable previewUpdater) {
        HBox chip = new HBox(Theme.SPACE_2);
        chip.setAlignment(Pos.CENTER);
        chip.setPadding(new Insets(6, 10, 6, 10));
        chip.setBackground(new Background(new BackgroundFill(Theme.PRIMARY_LIGHT, new CornerRadii(999), Insets.EMPTY)));

        Label label = new Label(ticketType.getTypeName() + " • ₹" + formatAmount(ticketType.getPrice()) + " • " + ticketType.getTotalQuantity());
        Theme.styleText(label, Theme.small(), Theme.PRIMARY);

        Button removeButton = Theme.createSecondaryButton("×");
        removeButton.setMinWidth(28);
        removeButton.setPrefWidth(28);
        removeButton.setOnAction(event -> {
            draftTypes.remove(ticketType);
            chipPane.getChildren().remove(chip);
            previewUpdater.run();
        });

        chip.getChildren().addAll(label, removeButton);
        return chip;
    }

    /**
     * This method creates one organizer event card.
     */
    private EventCard createOrganizerEventCard(Event event) {
        EventCard card = new EventCard(event, false);
        card.setPrefWidth(440);

        ProgressBar progressBar = new ProgressBar(event.getTotalTicketQuantity() == 0 ? 0 : (double) event.getTotalTicketsSold() / event.getTotalTicketQuantity());
        progressBar.setPrefWidth(220);
        progressBar.setStyle("-fx-accent: " + Theme.toRgbString(Theme.PRIMARY) + ";");

        Label salesLabel = new Label(event.getTotalTicketsSold() + " / " + event.getTotalTicketQuantity() + " sold");
        Theme.styleText(salesLabel, Theme.body(), Theme.TEXT_PRIMARY);

        Label revenueLabel = new Label("Revenue: ₹" + formatAmount(event.getRevenue()));
        Theme.styleText(revenueLabel, Theme.body(), Theme.SUCCESS);

        Button detailsButton = Theme.createSecondaryButton("View Details");
        detailsButton.setOnAction(actionEvent -> showEventDetails(event));

        Button deleteButton = Theme.createDangerButton("Delete");
        deleteButton.setOnAction(actionEvent -> {
            eventService.deleteEvent(event.getId());
            refreshOrganizerData();
        });

        HBox actions = new HBox(Theme.SPACE_2, detailsButton, deleteButton);
        VBox progressBox = new VBox(Theme.SPACE_2, salesLabel, progressBar, revenueLabel, actions);
        card.getDetailsBox().getChildren().add(progressBox);
        return card;
    }

    /**
     * This method opens the event details dialog.
     */
    private void showEventDetails(Event event) {
        VBox content = new VBox(Theme.SPACE_4);
        content.setPadding(new Insets(Theme.SPACE_6));
        Theme.stylePage(content);

        Label title = new Label(event.getName());
        Theme.styleText(title, Theme.heading2(), Theme.TEXT_PRIMARY);

        VBox info = new VBox(Theme.SPACE_2,
            createInfoLabel("Date", event.getDate() + " • " + event.getTime()),
            createInfoLabel("Venue", event.getVenueName() + ", " + event.getVenueAddress()),
            createInfoLabel("Category", event.getCategory()),
            createInfoLabel("Status", event.getStatus()),
            createInfoLabel("Description", event.getDescription()),
            createInfoLabel("Revenue", "₹" + formatAmount(event.getRevenue()))
        );

        VBox ticketBox = new VBox(Theme.SPACE_2);
        for (Event.TicketType type : event.getTicketTypes()) {
            ticketBox.getChildren().add(createInfoLabel("Ticket", type.getTypeName() + " • ₹" + formatAmount(type.getPrice())
                + " • " + type.getSoldQuantity() + "/" + type.getTotalQuantity() + " sold"));
        }

        Button closeButton = Theme.createPrimaryButton("Close");
        closeButton.setOnAction(dialogEvent -> ((Stage) closeButton.getScene().getWindow()).close());

        content.getChildren().addAll(title, info, createDivider(), ticketBox, closeButton);
        Stage dialog = createDialogStage("Event Details", content, 540, 520);
        dialog.showAndWait();
    }

    /**
     * This method creates a styled label row for dialogs.
     */
    private Label createInfoLabel(String title, String value) {
        Label label = new Label(title + ": " + value);
        Theme.styleText(label, Theme.body(), Theme.TEXT_PRIMARY);
        label.setWrapText(true);
        return label;
    }

    /**
     * This method creates a stat card for the sales dashboard.
     */
    private VBox createStatCard(String title, Label valueLabel) {
        VBox card = new VBox(Theme.SPACE_2);
        card.setPadding(new Insets(Theme.SPACE_4));
        card.setPrefWidth(220);
        Theme.styleCard(card);

        Label titleLabel = new Label(title);
        Theme.styleText(titleLabel, Theme.body(), Theme.TEXT_SECONDARY);
        Theme.styleText(valueLabel, Theme.heading2(), Theme.PRIMARY);
        card.getChildren().addAll(titleLabel, valueLabel);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    /**
     * This method creates a table column from an event property.
     */
    private TableColumn<Event, String> createColumn(String title, java.util.function.Function<Event, String> mapper) {
        TableColumn<Event, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(mapper.apply(data.getValue())));
        return column;
    }

    /**
     * This method creates the status badge column.
     */
    private TableColumn<Event, String> createStatusColumn() {
        TableColumn<Event, String> column = new TableColumn<>("Status");
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
     * This method creates one filter button for my events.
     */
    private Button createFilterButton(String label) {
        Button button = Theme.createSecondaryButton(label);
        if (label.equals(activeEventFilter)) {
            applySelectedFilterStyle(button);
        }
        button.setOnAction(event -> {
            activeEventFilter = label;
            HBox parent = (HBox) button.getParent();
            for (javafx.scene.Node node : parent.getChildren()) {
                if (node instanceof Button filterButton) {
                    resetFilterStyle(filterButton);
                }
            }
            applySelectedFilterStyle(button);
            refreshMyEventsGrid();
        });
        return button;
    }

    /**
     * This method applies the active style to a filter button.
     */
    private void applySelectedFilterStyle(Button button) {
        button.setTextFill(Theme.SURFACE);
        button.setBackground(new Background(new BackgroundFill(Theme.PRIMARY, new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(Border.EMPTY);
    }

    /**
     * This method resets the normal style for a filter button.
     */
    private void resetFilterStyle(Button button) {
        button.setTextFill(Theme.PRIMARY);
        button.setBackground(new Background(new BackgroundFill(Theme.SURFACE, new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(Theme.PRIMARY, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1.2))));
    }

    /**
     * This method creates a label and field block.
     */
    private VBox createFieldBlock(String labelText, javafx.scene.Node field) {
        VBox box = new VBox(Theme.SPACE_2);
        Label label = new Label(labelText);
        Theme.styleText(label, Theme.body(), Theme.TEXT_PRIMARY);
        box.getChildren().addAll(label, field);
        return box;
    }

    /**
     * This method creates a horizontal divider.
     */
    private Separator createDivider() {
        return new Separator();
    }

    /**
     * This method checks if text is a positive number.
     */
    private boolean isPositiveNumber(String value) {
        try {
            return Double.parseDouble(value.trim()) >= 0;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * This method checks if text is a positive whole number.
     */
    private boolean isPositiveWholeNumber(String value) {
        try {
            return Integer.parseInt(value.trim()) > 0;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * This method checks if the date format is valid.
     */
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, dateFormatter);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * This method clears the input error styles.
     */
    private void clearFieldStyles(Control... controls) {
        for (Control control : controls) {
            Theme.clearError(control);
        }
    }

    /**
     * This method marks blank fields with error styling.
     */
    private void markBlankFields(Control... controls) {
        for (Control control : controls) {
            boolean blank = false;
            if (control instanceof TextInputControl textInputControl) {
                blank = textInputControl.getText().isBlank();
            } else if (control instanceof ComboBox<?> comboBox) {
                blank = comboBox.getValue() == null;
            }
            if (blank) {
                Theme.markError(control);
            }
        }
    }

    /**
     * This method formats currency values nicely.
     */
    private String formatAmount(double amount) {
        return amount == Math.rint(amount) ? String.valueOf((int) amount) : String.format("%.2f", amount);
    }

    /**
     * This method opens a simple information dialog.
     */
    private void showInfoDialog(String title, String message) {
        VBox content = new VBox(Theme.SPACE_4);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(Theme.SPACE_6));
        Theme.stylePage(content);

        Label titleLabel = new Label(title);
        Theme.styleText(titleLabel, Theme.heading2(), Theme.TEXT_PRIMARY);
        Label messageLabel = new Label(message);
        Theme.styleText(messageLabel, Theme.body(), Theme.TEXT_SECONDARY);
        messageLabel.setWrapText(true);

        Button closeButton = Theme.createPrimaryButton("Close");
        closeButton.setOnAction(event -> ((Stage) closeButton.getScene().getWindow()).close());
        content.getChildren().addAll(titleLabel, messageLabel, closeButton);

        Stage dialog = createDialogStage(title, content, 420, 220);
        dialog.showAndWait();
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
