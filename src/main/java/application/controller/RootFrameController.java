package application.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RootFrameController implements Initializable {

    @FXML
    public BorderPane borderPane_Content;

    @FXML
    public Button button_StartDiagram;

    @FXML
    public Button button_StartFunction;

    @FXML
    public AnchorPane anchorPain_main;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void init() {
        button_StartDiagram.setOnAction(this::startDiagram);
        // fire Action Event on Enter
        button_StartDiagram.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                button_StartDiagram.fire();
                ev.consume();
            }
        });
    }

    public void closeWizard() {
        anchorPain_main.getChildren().remove(anchorPain_main.getChildren().size() - 1 );
        button_StartFunction.setVisible(true);
        button_StartDiagram.setVisible(true);
    }

    private void startDiagram(ActionEvent event){
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/fxml/wizard/Wizard.fxml"));

        try {
            button_StartDiagram.setVisible(false);
            button_StartFunction.setVisible(false);
            anchorPain_main.getChildren().add(loader.load());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
