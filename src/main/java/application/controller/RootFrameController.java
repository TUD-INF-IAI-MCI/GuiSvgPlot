package application.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RootFrameController implements Initializable {

    @FXML
    public BorderPane borderPane_Content;

    @FXML
    public Button button_StartWIzard;


    private Stage wizardStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void init() {

        button_StartWIzard.setOnAction(event -> {

            FXMLLoader loader = new FXMLLoader();


            loader.setLocation(getClass().getResource("/fxml/wizard/Wizard.fxml"));

            try {
                AnchorPane introPane = loader.load();


                Scene scene = new Scene(introPane);

                wizardStage = new Stage();
                wizardStage.setScene(scene);

                wizardStage.showAndWait();


            } catch (IOException e) {
                e.printStackTrace();
            }


        });

    }

    public void closeWizard() {

        if (wizardStage != null)
            wizardStage.close();

    }
}
