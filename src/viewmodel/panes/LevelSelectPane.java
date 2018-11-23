package viewmodel.panes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.Exceptions.InvalidMapException;
import model.GameLevel;
import model.LevelManager;
import model.Map.Cell;
import model.Map.Map;
import viewmodel.Config;
import viewmodel.MapRenderer;
import viewmodel.SceneManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Represents the main menu in the game
 */
public class LevelSelectPane extends BorderPane {
    private VBox leftContainer;
    private Button returnButton;
    private Button playButton;
    private Button chooseMapDirButton;
    private ListView<String> levelsListView;
    private VBox centerContainer;
    private Canvas levelPreview;
    /**
     * Instantiate the member components and connect and style them. Also set the callbacks.
     * Use 20 for VBox spacing
     */
    public LevelSelectPane() {
        //TODO
        leftContainer = new VBox();
        returnButton = new Button("Return");
        playButton = new Button("Play");
        chooseMapDirButton = new Button("Choose map directory");
        levelsListView = new ListView<>();
        centerContainer = new VBox();
        levelPreview = new Canvas();

        connectComponents();
        styleComponents();
        setCallbacks();

    }

    /**
     * Connects the components together (think adding them into another, setting their positions, etc). Reference
     * the other classes in the {@link javafx.scene.layout.Pane} package.
     */
    private void connectComponents() {
        //TODO

        leftContainer.getChildren().addAll(returnButton, chooseMapDirButton, levelsListView, playButton);
        centerContainer.getChildren().addAll(levelPreview);

        this.setLeft(leftContainer);
        this.setCenter(centerContainer);

    }

    /**
     * Apply CSS styling to components. Also sets the {@link LevelSelectPane#playButton} to be disabled.
     */
    private void styleComponents() {
        //TODO

        leftContainer.getStyleClass().addAll("big-vbox", "side-menu");
        returnButton.getStyleClass().add("big-button");
        playButton.getStyleClass().add("big-button");
        playButton.setDisable(true);
        chooseMapDirButton.getStyleClass().add("big-button");
        //levelsListView.getStyleClass().add("list-cell");
        levelsListView.setPrefHeight(Config.LIST_CELL_HEIGHT * 11);
        centerContainer.getStyleClass().add("big-vbox");
    }

    /**
     * Set the event handlers for the 3 buttons and listview.
     * <p>
     * Hints:
     * The return button should show the main menu scene
     * The chooseMapDir button should prompt the user to choose the map directory, and load the levels
     * The play button should set the current level based on the current level name (see LevelManager), show
     * the gameplay scene, and start the level timer.
     * The listview, based on which item was clicked, should set the current level (see LevelManager), render the
     * preview (see {@link MapRenderer#render(Canvas, Cell[][])}}, and set the play button to enabled.
     */
    private void setCallbacks() {
        //TODO

        setOnMouseMoved(event -> refreshLevelListView());
        returnButton.setOnAction(event -> SceneManager.getInstance().showMainMenuScene());
        chooseMapDirButton.setOnAction(event -> this.promptUserForMapDirectory());
        playButton.setOnAction(
                event -> {
                    try {
                        LevelManager.getInstance().setLevel(levelsListView.getSelectionModel().getSelectedItem());
                    } catch (InvalidMapException e) {
                        return;
                    } catch (FileNotFoundException e) {
                        return;
                    }

                    SceneManager.getInstance().showGamePlayScene();
                    LevelManager.getInstance().startLevelTimer();
                }
        );
        levelsListView.getSelectionModel().selectedItemProperty().addListener(
                event -> {
                    try {
                        LevelManager.getInstance().setLevel(levelsListView.getSelectionModel().getSelectedItem());
                    } catch (InvalidMapException e) {
                        Alert popup = new Alert(Alert.AlertType.WARNING);
                        popup.setTitle("Invalid map");
                        popup.setHeaderText("The chosen map is invalid");
                        popup.show();

                        levelsListView.getItems().remove(levelsListView.getSelectionModel().getSelectedItem());
                        levelsListView.refresh();
                        return;
                    } catch (FileNotFoundException e) {
                        return;
                    }
                }
        );
        levelsListView.getSelectionModel().selectedItemProperty().addListener(
                event -> {
                    try {
                        MapRenderer.render(levelPreview, LevelManager.getInstance().getGameLevel().getMap().getCells());
                    } catch (NullPointerException e) {
                        return;
                    }
                }
        );
        levelsListView.getSelectionModel().selectedItemProperty().addListener(event -> playButton.setDisable(false));

    }

    /**
     * Popup a DirectoryChooser window to ask the user where the map folder is stored.
     * Update the LevelManager's map directory afterwards, and potentially
     * load the levels from disk using LevelManager (if the user didn't cancel out the window)
     */
    private void promptUserForMapDirectory() {
        //TODO

        var chooser = new DirectoryChooser();

        chooser.setTitle("Load map directory");
        chooser.setInitialDirectory(new File(".")); //for saving in root/desktop
        File destFolder = chooser.showDialog(new Stage());
        if (destFolder == null)
            return;

        ObservableList<String> fileList = FXCollections.observableArrayList();
        int numOfMap = destFolder.listFiles().length;

        for (int i = 0; i < numOfMap; i++)
            fileList.add(destFolder.listFiles()[i].getName());

        levelsListView.setItems(fileList);
        LevelManager.getInstance().setMapDirectory(destFolder.getPath());
        LevelManager.getInstance().loadLevelNamesFromDisk();
    }

    public void refreshLevelListView() {
        if (LevelManager.getInstance().getLevelNames() != levelsListView.getItems())
            levelsListView.getItems().clear();
            levelsListView.setItems(LevelManager.getInstance().getLevelNames());
    }
}
