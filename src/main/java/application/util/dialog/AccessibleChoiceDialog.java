package application.util.dialog;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Collection;

/**
 * this is an extended copy of the {@link ChoiceDialog}.
 * @param <T>
 */
public class AccessibleChoiceDialog<T> extends Dialog<T> {

    private final GridPane grid;
    private final Label label;
    private final ChoiceBox<T> choiceBox;
    private final T defaultChoice;


    public AccessibleChoiceDialog(T defaultChoice, Collection<T> choices) {
        final DialogPane dialogPane = getDialogPane();

        // -- grid
        this.grid = new GridPane();
        this.grid.setHgap(10);
        this.grid.setMaxWidth(Double.MAX_VALUE);
        this.grid.setAlignment(Pos.CENTER_LEFT);

        // -- label
        label = createContentLabel(dialogPane.getContentText());
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.textProperty().bind(dialogPane.contentTextProperty());

        dialogPane.contentTextProperty().addListener(o -> updateGrid());

        setTitle(ControlResources.getString("Dialog.confirm.title"));
        dialogPane.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
        dialogPane.getStyleClass().add("choice-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setFocusTraversable(true);

        final double MIN_WIDTH = 150;

        choiceBox = new ChoiceBox<T>();
        choiceBox.setMinWidth(MIN_WIDTH);
        if (choices != null) {
            choiceBox.getItems().addAll(choices);
        }
        choiceBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(choiceBox, Priority.ALWAYS);
        GridPane.setFillWidth(choiceBox, true);

        this.defaultChoice = choiceBox.getItems().contains(defaultChoice) ? defaultChoice : null;

        if (defaultChoice == null) {
            choiceBox.getSelectionModel().selectFirst();
        } else {
            choiceBox.getSelectionModel().select(defaultChoice);
        }

        updateGrid();

        setResultConverter((dialogButton) -> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.OK_DONE ? getSelectedItem() : null;
        });
    }


    public ChoiceBox getChoiceBox() {
        return choiceBox;
    }

    private Label createContentLabel(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.getStyleClass().add("content");
        label.setWrapText(true);
        label.setPrefWidth(360);
        return label;
    }
/**************************************************************************
 *
 * Public API
 *
 **************************************************************************/

    /**
     * Returns the currently selected item in the dialog.
     */
    public final T getSelectedItem() {
        return choiceBox.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns the property representing the currently selected item in the dialog.
     */
    public final ReadOnlyObjectProperty<T> selectedItemProperty() {
        return choiceBox.getSelectionModel().selectedItemProperty();
    }

    /**
     * Sets the currently selected item in the dialog.
     * @param item The item to select in the dialog.
     */
    public final void setSelectedItem(T item) {
        choiceBox.getSelectionModel().select(item);
    }

    /**
     * Returns the list of all items that will be displayed to users. This list
     * can be modified by the developer to add, remove, or reorder the items
     * to present to the user.
     */
    public final ObservableList<T> getItems() {
        return choiceBox.getItems();
    }

    /**
     * Returns the default choice that was specified in the constructor.
     */
    public final T getDefaultChoice() {
        return defaultChoice;
    }



    /**************************************************************************
     *
     * Private Implementation
     *
     **************************************************************************/

    private void updateGrid() {
        grid.getChildren().clear();

        grid.add(label, 0, 0);
        grid.add(choiceBox, 1, 0);
        getDialogPane().setContent(grid);

        Platform.runLater(() -> choiceBox.requestFocus());
    }
}
