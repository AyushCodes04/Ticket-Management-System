package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import util.Theme;

/**
 * This component renders the top navigation bar used across screens.
 */
public class Navbar extends HBox {

    /**
     * This constructor creates a navigation bar with left and right content.
     */
    public Navbar(Node leftContent, String title, Node rightContent) {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(Theme.SPACE_4);
        setPadding(new Insets(Theme.SPACE_4, Theme.SPACE_6, Theme.SPACE_4, Theme.SPACE_6));
        setMinHeight(72);
        setBorder(new Border(new BorderStroke(Theme.BORDER, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = new Label(title);
        Theme.styleText(titleLabel, Theme.heading3(), Theme.TEXT_PRIMARY);

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        StackPane titleHolder = new StackPane(titleLabel);
        titleHolder.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleHolder, Priority.ALWAYS);

        getChildren().addAll(
            wrap(leftContent, Pos.CENTER_LEFT),
            leftSpacer,
            titleHolder,
            rightSpacer,
            wrap(rightContent, Pos.CENTER_RIGHT)
        );
    }

    /**
     * This method wraps nodes to keep alignment stable.
     */
    private StackPane wrap(Node node, Pos alignment) {
        StackPane pane = new StackPane(node);
        pane.setAlignment(alignment);
        pane.setMinWidth(170);
        return pane;
    }
}
