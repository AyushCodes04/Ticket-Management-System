package util;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This singleton class manages screen navigation and history.
 */
public class Navigator {
    private static final Navigator INSTANCE = new Navigator();

    private final Deque<Screen> history = new ArrayDeque<>();
    private Stage stage;
    private Scene scene;
    private Screen currentScreen;

    /**
     * This constructor keeps the class singleton-only.
     */
    private Navigator() {
    }

    /**
     * This method returns the single navigator instance.
     */
    public static Navigator getInstance() {
        return INSTANCE;
    }

    /**
     * This method prepares navigation with the main stage and first screen.
     */
    public void initialize(Stage primaryStage, Screen initialScreen) {
        this.stage = primaryStage;
        this.currentScreen = initialScreen;
        Parent root = initialScreen.createView();
        this.scene = new Scene(root, 1280, 820);
        stage.setScene(scene);
        stage.show();
        playFade(root);
    }

    /**
     * This method returns the active scene.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * This method navigates to a new screen and stores history.
     */
    public void navigateTo(Screen screen) {
        if (currentScreen != null) {
            history.push(currentScreen);
        }
        currentScreen = screen;
        Parent root = screen.createView();
        scene.setRoot(root);
        playFade(root);
    }

    /**
     * This method goes back to the previous screen if available.
     */
    public void goBack() {
        if (history.isEmpty()) {
            return;
        }
        currentScreen = history.pop();
        Parent root = currentScreen.createView();
        scene.setRoot(root);
        playFade(root);
    }

    /**
     * This method applies a small fade transition between screens.
     */
    private void playFade(Parent root) {
        root.setOpacity(0);
        FadeTransition transition = new FadeTransition(Duration.millis(300), root);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();
    }

    /**
     * This interface describes one navigable screen.
     */
    public interface Screen {
        /**
         * This method creates the root node for the screen.
         */
        Parent createView();
    }
}
