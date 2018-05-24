package application.controller.wizard.tiles;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ChoiceBoxController<T> implements Initializable {

    @FXML
    private ChoiceBox choiceBox;

    @FXML
    private Label choiceBoxLabel;

    private String title;
    private ObservableList<T> items;
    private Object value;

    public ChoiceBoxController(){
    }

    public ChoiceBoxController(String title, ObservableList<T> items, T value){
        this.title = title;
        this.items = items;
        this.value = value;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.choiceBox.setId(this.choiceBox.getId() + "_" +this.title);
        this.choiceBoxLabel.setId(this.choiceBoxLabel.getId() + "_" +this.title);
        this.choiceBoxLabel.setText(this.title);
        this.choiceBox.setAccessibleText(this.title);
        this.choiceBox.setItems(this.items);
        this.choiceBox.setValue(this.value);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.choiceBoxLabel.setText(this.title);
        this.choiceBox.setAccessibleText(this.title);
    }

    public ObservableList<T> getItems() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items = items;
        this.choiceBox.setItems(this.items);
    }
}
