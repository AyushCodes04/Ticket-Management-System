package util;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * This class stores the full design system for the application.
 */
public final class Theme {

    public static final Color BACKGROUND = Color.web("#F8F9FF");
    public static final Color SURFACE = Color.web("#FFFFFF");
    public static final Color PRIMARY = Color.web("#6366F1");
    public static final Color PRIMARY_DARK = Color.web("#4F46E5");
    public static final Color PRIMARY_LIGHT = Color.web("#EEF2FF");
    public static final Color SECONDARY = Color.web("#06B6D4");
    public static final Color SUCCESS = Color.web("#10B981");
    public static final Color WARNING = Color.web("#F59E0B");
    public static final Color ERROR = Color.web("#EF4444");
    public static final Color TEXT_PRIMARY = Color.web("#1E1B4B");
    public static final Color TEXT_SECONDARY = Color.web("#6B7280");
    public static final Color BORDER = Color.web("#E5E7EB");
    public static final Color CARD_SHADOW = Color.web("#E0E7FF");

    public static final double SPACE_1 = 4;
    public static final double SPACE_2 = 8;
    public static final double SPACE_3 = 12;
    public static final double SPACE_4 = 16;
    public static final double SPACE_6 = 24;
    public static final double SPACE_8 = 32;
    public static final double SPACE_12 = 48;

    private Theme() {
    }

    /**
     * This method returns the main heading font.
     */
    public static Font heading1() {
        return Font.font("SansSerif", FontWeight.BOLD, 32);
    }

    /**
     * This method returns the section heading font.
     */
    public static Font heading2() {
        return Font.font("SansSerif", FontWeight.BOLD, 24);
    }

    /**
     * This method returns the card heading font.
     */
    public static Font heading3() {
        return Font.font("SansSerif", FontWeight.BOLD, 18);
    }

    /**
     * This method returns the default body font.
     */
    public static Font body() {
        return Font.font("SansSerif", FontWeight.NORMAL, 14);
    }

    /**
     * This method returns the small helper font.
     */
    public static Font small() {
        return Font.font("SansSerif", FontWeight.NORMAL, 12);
    }

    /**
     * This method returns the button font.
     */
    public static Font button() {
        return Font.font("SansSerif", FontWeight.SEMI_BOLD, 13);
    }

    /**
     * This method applies the application background to a region.
     */
    public static void stylePage(Region region) {
        region.setBackground(new Background(new BackgroundFill(BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * This method applies the surface card style to a region.
     */
    public static void styleCard(Region region) {
        region.setBackground(new Background(new BackgroundFill(SURFACE, new CornerRadii(16), Insets.EMPTY)));
        region.setBorder(new Border(
            new BorderStroke(CARD_SHADOW, BorderStrokeStyle.SOLID, new CornerRadii(16), new BorderWidths(1)),
            new BorderStroke(BORDER, BorderStrokeStyle.SOLID, new CornerRadii(16), new BorderWidths(0, 0, 2, 0))
        ));
        region.setEffect(new DropShadow(10, 0, 3, CARD_SHADOW.deriveColor(0, 1, 1, 0.35)));
    }

    /**
     * This method applies a stronger card style for highlighted panels.
     */
    public static void styleRaisedCard(Region region) {
        styleCard(region);
        region.setBorder(new Border(
            new BorderStroke(CARD_SHADOW, BorderStrokeStyle.SOLID, new CornerRadii(18), new BorderWidths(1.5)),
            new BorderStroke(BORDER, BorderStrokeStyle.SOLID, new CornerRadii(18), new BorderWidths(0, 0, 2, 0))
        ));
    }

    /**
     * This method creates a styled primary button.
     */
    public static Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setFont(button());
        button.setCursor(Cursor.HAND);
        button.setTextFill(SURFACE);
        button.setBackground(new Background(new BackgroundFill(PRIMARY, new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(Border.EMPTY);
        button.setMinHeight(36);
        button.setPrefHeight(36);
        button.setPadding(new Insets(8, 16, 8, 16));
        installButtonHover(button, PRIMARY, PRIMARY_DARK, SURFACE);
        installDisabledStyle(button);
        return button;
    }

    /**
     * This method creates a styled secondary button.
     */
    public static Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setFont(button());
        button.setCursor(Cursor.HAND);
        button.setTextFill(PRIMARY);
        button.setBackground(new Background(new BackgroundFill(SURFACE, new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(PRIMARY, BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1.2))));
        button.setMinHeight(36);
        button.setPrefHeight(36);
        button.setPadding(new Insets(8, 16, 8, 16));
        button.hoverProperty().addListener((obs, oldValue, isHovering) -> {
            if (!button.isDisabled()) {
                button.setBackground(new Background(new BackgroundFill(isHovering ? PRIMARY_LIGHT : SURFACE, new CornerRadii(8), Insets.EMPTY)));
            }
        });
        installDisabledStyle(button);
        return button;
    }

    /**
     * This method creates a styled danger button.
     */
    public static Button createDangerButton(String text) {
        Button button = new Button(text);
        button.setFont(button());
        button.setCursor(Cursor.HAND);
        button.setTextFill(SURFACE);
        button.setBackground(new Background(new BackgroundFill(ERROR, new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(Border.EMPTY);
        button.setMinHeight(36);
        button.setPrefHeight(36);
        button.setPadding(new Insets(8, 16, 8, 16));
        installButtonHover(button, ERROR, ERROR.deriveColor(0, 1, 0.9, 1), SURFACE);
        installDisabledStyle(button);
        return button;
    }

    /**
     * This method creates a styled text field.
     */
    public static TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        styleInput(textField);
        return textField;
    }

    /**
     * This method creates a styled text area.
     */
    public static TextArea createTextArea(String promptText, int rows) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(rows);
        styleInput(textArea);
        return textArea;
    }

    /**
     * This method creates a styled combo box.
     */
    public static <T> ComboBox<T> createComboBox() {
        ComboBox<T> comboBox = new ComboBox<>();
        styleInput(comboBox);
        return comboBox;
    }

    /**
     * This method applies the common input style to controls.
     */
    public static void styleInput(Control control) {
        control.setStyle(
            "-fx-background-color: " + toRgbString(SURFACE) + ";" +
                "-fx-border-color: " + toRgbString(BORDER) + ";" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 4;" +
                "-fx-padding: 8 10 8 10;" +
                "-fx-text-fill: " + toRgbString(TEXT_PRIMARY) + ";" +
                "-fx-prompt-text-fill: " + toRgbString(TEXT_SECONDARY) + ";" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
        );
        control.focusedProperty().addListener((obs, oldValue, focused) -> {
            if (!focused) {
                clearError(control);
            } else {
                control.setStyle(
                    "-fx-background-color: " + toRgbString(SURFACE) + ";" +
                        "-fx-border-color: " + toRgbString(PRIMARY) + ";" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8 10 8 10;" +
                        "-fx-text-fill: " + toRgbString(TEXT_PRIMARY) + ";" +
                        "-fx-prompt-text-fill: " + toRgbString(TEXT_SECONDARY) + ";" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
                );
            }
        });
    }

    /**
     * This method marks an input control as invalid.
     */
    public static void markError(Control control) {
        control.setStyle(
            "-fx-background-color: " + toRgbString(SURFACE) + ";" +
                "-fx-border-color: " + toRgbString(ERROR) + ";" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 4;" +
                "-fx-padding: 8 10 8 10;" +
                "-fx-text-fill: " + toRgbString(TEXT_PRIMARY) + ";" +
                "-fx-prompt-text-fill: " + toRgbString(TEXT_SECONDARY) + ";" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
        );
    }

    /**
     * This method clears the error style from an input control.
     */
    public static void clearError(Control control) {
        if (control.isFocused()) {
            control.setStyle(
                "-fx-background-color: " + toRgbString(SURFACE) + ";" +
                    "-fx-border-color: " + toRgbString(PRIMARY) + ";" +
                    "-fx-border-radius: 4;" +
                    "-fx-background-radius: 4;" +
                    "-fx-padding: 8 10 8 10;" +
                    "-fx-text-fill: " + toRgbString(TEXT_PRIMARY) + ";" +
                    "-fx-prompt-text-fill: " + toRgbString(TEXT_SECONDARY) + ";" +
                    "-fx-focus-color: transparent;" +
                    "-fx-faint-focus-color: transparent;"
            );
        } else {
            styleInput(control);
        }
    }

    /**
     * This method creates a colored badge label.
     */
    public static javafx.scene.control.Label createBadge(String text, Color background, Color textColor) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setFont(small());
        label.setTextFill(textColor);
        label.setPadding(new Insets(4, 10, 4, 10));
        label.setBackground(new Background(new BackgroundFill(background, new CornerRadii(999), Insets.EMPTY)));
        return label;
    }

    /**
     * This method applies hover elevation to a card node.
     */
    public static void installCardHover(Node node, Region region) {
        node.setOnMouseEntered(event -> {
            region.setTranslateY(-4);
            region.setScaleX(1.01);
            region.setScaleY(1.01);
        });
        node.setOnMouseExited(event -> {
            region.setTranslateY(0);
            region.setScaleX(1);
            region.setScaleY(1);
        });
    }

    /**
     * This method styles the application tables.
     */
    public static <T> void styleTable(javafx.scene.control.TableView<T> tableView) {
        tableView.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.setPlaceholder(createEmptyLabel("No data available"));
        tableView.setStyle(
            "-fx-background-color: " + toRgbString(SURFACE) + ";" +
                "-fx-control-inner-background: " + toRgbString(SURFACE) + ";" +
                "-fx-table-cell-border-color: " + toRgbString(BORDER) + ";" +
                "-fx-border-color: " + toRgbString(BORDER) + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;"
        );
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: " + toRgbString(PRIMARY_LIGHT) + ";");
                } else if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color: " + toRgbString(SURFACE) + ";");
                } else {
                    setStyle("-fx-background-color: " + toRgbString(BACKGROUND) + ";");
                }
            }
        });
    }

    /**
     * This method styles table headers after the skin is ready.
     */
    public static void styleTableHeaders(javafx.scene.control.TableView<?> tableView) {
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Node header = tableView.lookup("TableHeaderRow");
            if (header instanceof Region region) {
                region.setStyle("-fx-background-color: " + toRgbString(PRIMARY_LIGHT) + ";");
            }
        });
    }

    /**
     * This method creates a small muted label for helper states.
     */
    public static javafx.scene.control.Label createMutedLabel(String text) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setTextFill(TEXT_SECONDARY);
        label.setFont(body());
        label.setWrapText(true);
        return label;
    }

    /**
     * This method creates an empty state label.
     */
    public static javafx.scene.control.Label createEmptyLabel(String text) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setTextFill(TEXT_SECONDARY);
        label.setFont(body());
        label.setWrapText(true);
        return label;
    }

    /**
     * This method creates a section title label.
     */
    public static javafx.scene.control.Label createSectionTitle(String text) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setFont(heading2());
        label.setTextFill(TEXT_PRIMARY);
        return label;
    }

    /**
     * This method applies common text color and font to a labeled node.
     */
    public static void styleText(Labeled labeled, Font font, Color color) {
        labeled.setFont(font);
        labeled.setTextFill(color);
    }

    /**
     * This method returns the color for a category label.
     */
    public static Color categoryColor(String category) {
        return switch (category == null ? "" : category.toUpperCase()) {
            case "CONCERT" -> PRIMARY;
            case "SPORTS" -> SECONDARY;
            case "TECH" -> SUCCESS;
            case "CULTURAL" -> WARNING;
            default -> TEXT_SECONDARY;
        };
    }

    /**
     * This method returns the color for a status label.
     */
    public static Color statusColor(String status) {
        return switch (status == null ? "" : status.toUpperCase()) {
            case "UPCOMING", "CONFIRMED", "VALID" -> PRIMARY;
            case "ONGOING", "USED" -> SUCCESS;
            case "COMPLETED" -> TEXT_SECONDARY;
            case "INVALID", "CANCELLED" -> ERROR;
            case "ALREADY USED" -> WARNING;
            default -> TEXT_SECONDARY;
        };
    }

    /**
     * This method creates a styled scroll pane.
     */
    public static ScrollPane createScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: " + toRgbString(BACKGROUND) + ";" +
                "-fx-background-color: " + toRgbString(BACKGROUND) + ";"
        );
        return scrollPane;
    }

    /**
     * This method converts a color to a CSS rgb string.
     */
    public static String toRgbString(Color color) {
        return String.format(
            "rgba(%d, %d, %d, %.3f)",
            (int) Math.round(color.getRed() * 255),
            (int) Math.round(color.getGreen() * 255),
            (int) Math.round(color.getBlue() * 255),
            color.getOpacity()
        );
    }

    /**
     * This method creates rounded insets.
     */
    public static Insets insets(double all) {
        return new Insets(all);
    }

    /**
     * This method installs hover styling for a button.
     */
    private static void installButtonHover(Button button, Color normalColor, Color hoverColor, Color textColor) {
        button.hoverProperty().addListener((obs, oldValue, isHovering) -> {
            if (!button.isDisabled()) {
                button.setBackground(new Background(new BackgroundFill(isHovering ? hoverColor : normalColor, new CornerRadii(8), Insets.EMPTY)));
                button.setTextFill(textColor);
            }
        });
    }

    /**
     * This method styles the disabled state of buttons.
     */
    private static void installDisabledStyle(Button button) {
        button.disabledProperty().addListener((obs, oldValue, disabled) -> {
            if (disabled) {
                button.setCursor(Cursor.DEFAULT);
                button.setBackground(new Background(new BackgroundFill(BORDER, new CornerRadii(8), Insets.EMPTY)));
                button.setTextFill(TEXT_SECONDARY);
            } else {
                button.setCursor(Cursor.HAND);
            }
        });
    }
}
