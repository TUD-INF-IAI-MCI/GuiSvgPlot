package application.controller.wizard.tiles;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class TextFieldController implements Initializable {

    @FXML
    private TextField textField;

    @FXML
    private Label textFieldLabel;

    private String title;

    private Object value;

    public TextFieldController(){
    }

    public TextFieldController(String title, Object value){
        this.title = title;
        this.value = value;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.textFieldLabel.setText(this.title);
        this.textField.setAccessibleText(this.title);
        this.textField.setText(this.value.toString());

        this.textField.textProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("textfield changed from " + oldValue + " to " + newValue);
        });
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.textFieldLabel.setText(this.title);
        this.textField.setAccessibleText(this.title);
    }

}
