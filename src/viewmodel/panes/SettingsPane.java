package viewmodel.panes;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import viewmodel.AudioManager;
import viewmodel.Config;
import viewmodel.SceneManager;

/**
 * Represents the settings pane in the game
 */
public class SettingsPane extends BorderPane {
    private VBox leftContainer;
    private Button returnButton;
    private Button toggleSoundFXButton;
    private VBox centerContainer;
    private TextArea infoText;

    /**
     * Instantiate the member components and connect and style them. Also set the callbacks.
     * Hints:
     * Use 20 for the VBox spacing.
     * The text of {@link SettingsPane#toggleSoundFXButton} should depend on {@link AudioManager}
     * Use {@link Config#getAboutText()} for the infoText
     */
    public SettingsPane() {
        //TODO

        leftContainer = new VBox(20);
        returnButton = new Button("Return");
        toggleSoundFXButton = new Button("Disable Sound FX");
        centerContainer = new VBox(20);
        infoText = new TextArea(Config.getAboutText());

        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * Connects the components together (think adding them into another, setting their positions, etc).
     */
    private void connectComponents() {
        //TODO

        centerContainer.getChildren().add(infoText);
        leftContainer.getChildren().addAll(returnButton, toggleSoundFXButton);

        this.setLeft(leftContainer);
        this.setCenter(centerContainer);
    }

    /**
     * Apply CSS styling to components.
     * <p>
     * Also set the text area to not be editable, but allow text wrapping.
     */
    private void styleComponents() {
        //TODO

        leftContainer.getStyleClass().add("side-menu");
        returnButton.getStyleClass().add("big-button");
        toggleSoundFXButton.getStyleClass().add("big-button");
        centerContainer.getStyleClass().add("big-vbox");
        infoText.getStyleClass().add("text-area");
        infoText.editableProperty().setValue(false);
        infoText.wrapTextProperty().setValue(true);
        infoText.setPrefHeight(Config.HEIGHT);

    }

    /**
     * Set the event handler for the 2 buttons.
     * The return button should go to the main menu scene
     */
    private void setCallbacks() {
        //TODO

        returnButton.setOnAction(event -> SceneManager.getInstance().showMainMenuScene());
        toggleSoundFXButton.setOnAction(event -> {
                                                if (AudioManager.getInstance().isEnabled()) {
                                                    toggleSoundFXButton.setText("Enable Sound FX");
                                                    AudioManager.getInstance().setEnabled(false);
                                                }
                                                else {
                                                    toggleSoundFXButton.setText("Disable Sound FX");
                                                    AudioManager.getInstance().setEnabled(true);
                                                }});
    }
}
