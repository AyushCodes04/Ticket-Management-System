package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import model.Event;
import util.Theme;

/**
 * This component shows event information in a reusable card layout.
 */
public class EventCard extends HBox {
    private final VBox detailsBox;
    private final VBox sideBox;

    /**
     * This constructor creates a reusable event card.
     */
    public EventCard(Event event, boolean showCategoryStrip) {
        setSpacing(0);
        setAlignment(Pos.TOP_LEFT);

        Region strip = new Region();
        strip.setPrefWidth(showCategoryStrip ? 4 : 0);
        strip.setMinWidth(showCategoryStrip ? 4 : 0);
        strip.setBackground(new Background(new BackgroundFill(Theme.categoryColor(event.getCategory()), CornerRadii.EMPTY, Insets.EMPTY)));

        HBox body = new HBox(Theme.SPACE_4);
        body.setPadding(new Insets(Theme.SPACE_4));
        body.setAlignment(Pos.TOP_LEFT);
        Theme.styleCard(body);

        detailsBox = new VBox(Theme.SPACE_2);
        HBox.setHgrow(detailsBox, Priority.ALWAYS);

        Label titleLabel = new Label(event.getName());
        Theme.styleText(titleLabel, Theme.heading3(), Theme.TEXT_PRIMARY);

        Label dateLabel = new Label("📅 " + event.getDate() + "  •  " + event.getTime());
        Theme.styleText(dateLabel, Theme.body(), Theme.TEXT_SECONDARY);

        Label venueLabel = new Label("📍 " + event.getVenueName() + ", " + event.getVenueAddress());
        Theme.styleText(venueLabel, Theme.body(), Theme.TEXT_SECONDARY);
        venueLabel.setWrapText(true);

        HBox badges = new HBox(Theme.SPACE_2);
        badges.getChildren().add(Theme.createBadge(event.getCategory(), Theme.categoryColor(event.getCategory()).deriveColor(0, 1, 1, 0.14), Theme.categoryColor(event.getCategory())));

        detailsBox.getChildren().addAll(titleLabel, dateLabel, venueLabel, badges);

        sideBox = new VBox(Theme.SPACE_3);
        sideBox.setAlignment(Pos.CENTER_RIGHT);
        sideBox.setMinWidth(180);

        body.getChildren().addAll(detailsBox, sideBox);
        getChildren().addAll(strip, body);
        Theme.installCardHover(this, body);
    }

    /**
     * This method returns the main details box.
     */
    public VBox getDetailsBox() {
        return detailsBox;
    }

    /**
     * This method returns the side content box.
     */
    public VBox getSideBox() {
        return sideBox;
    }
}
