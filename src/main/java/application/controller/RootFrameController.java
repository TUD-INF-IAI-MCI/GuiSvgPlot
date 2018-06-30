package application.controller;

import application.Wizard.SVGWizardController;
import application.model.SvgStage;
import application.service.ButtonService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

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
    public AnchorPane anchorPain_main;

    @FXML
    public Label label_message;

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

    @FXML
    private void startPreset(ActionEvent event){
        //TODO: will handle predefined preset functionality
    }

    @FXML
    private void startPresetEditor(){

        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(getClass().getResource("/fxml/wizard/SettingsFrame.fxml"));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
           // ((SettingsController) loader.getController()).testFunction();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void startWizard(String fxmlPath, boolean isExtended) {
        this.clearMessageLabel();
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


    public void clearMessageLabel(){
        this.label_message.setText("");
        this.label_message.setVisible(false);
        this.label_message.getStyleClass().clear();
    }
}
