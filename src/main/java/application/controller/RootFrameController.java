package application.controller;

import application.Wizard.SVGWizardController;
import application.model.SvgStage;
import application.service.ButtonService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RootFrameController implements Initializable {

    @FXML
    public BorderPane borderPane_Content;

    @FXML
    public Button button_StartDiagram;

    @FXML
    public Button button_StartFunction;

    @FXML
    public Button button_ExitProgram;

    @FXML
    public AnchorPane anchorPain_main;

    private Node center;

    private ButtonService buttonService = ButtonService.getInstance();
    private ResourceBundle bundle;

    private SvgStage currentStage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }

    public void init() {
        button_StartDiagram.setOnAction(this::startDiagram);
        button_StartFunction.setOnAction(event -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setHeaderText("Function Wizard Mode");
            alert.setTitle("Choose Function Wizard Mode");
            alert.setContentText("Simple will be faster, Extended will be more sophisticated");

            ButtonType buttonTypeSimple = new ButtonType("Simple");
            ButtonType buttonTypeExtended = new ButtonType("Extended");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeSimple, buttonTypeExtended, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeSimple) {
                startFunction(false);
            } else if (result.get() == buttonTypeExtended) {
                startFunction(true);
            } else {
                alert.close();
            }
        });
    }

    public void closeWizard() {
        borderPane_Content.setCenter(center);
    }


    private void startFunction(boolean isExtended) {
        startWizard("/fxml/wizard/content/functions/FunctionWizardFrame.fxml", isExtended);
    }

    private void startDiagram(ActionEvent event) {
        startWizard("/fxml/wizard/content/chart/ChartWizardFrame.fxml", false );
    }

    @FXML
    private void closeButtonAction(){
        System.exit(1);
    }

    private void startWizard(String fxmlPath, boolean isExtended) {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(getClass().getResource(fxmlPath));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
            ((SVGWizardController) loader.getController()).setExtended(isExtended);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
