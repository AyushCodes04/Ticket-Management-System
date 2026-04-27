package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import util.Theme;

/**
 * This component renders a bottom status bar with flexible content.
 */
public class StatusBar extends HBox {

    private final Label messageLabel;
    private final HBox rightContent;

    /**
     * This constructor creates an empty status bar.
     */
    public StatusBar() {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(Theme.SPACE_4);
        setPadding(new Insets(Theme.SPACE_3, Theme.SPACE_6, Theme.SPACE_3, Theme.SPACE_6));
        setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(Theme.BORDER, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 0, 0))));

        messageLabel = new Label();
        Theme.styleText(messageLabel, Theme.small(), Theme.TEXT_SECONDARY);

        rightContent = new HBox(Theme.SPACE_4);
        rightContent.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(messageLabel, spacer, rightContent);
    }

    /**
     * This method updates the left side message.
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * This method replaces the right side nodes.
     */
    public void setRightContent(Node... nodes) {
        rightContent.getChildren().setAll(nodes);
    }
}
