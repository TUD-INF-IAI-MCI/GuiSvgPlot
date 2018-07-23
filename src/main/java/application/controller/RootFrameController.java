package application.controller;

import application.controller.wizard.SVGWizardController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

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
    public Menu menu_Presets = new Menu();

    @FXML
    public ScrollPane scrollPane_message;

    @FXML
    public VBox vBox_messages;

    private Node center;

    private ResourceBundle bundle;

    public SVGWizardController svgWizardController;

    public SettingsController settingsController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        //TODO: fill menu with previously locally saved presets
        // dummy preset
        MenuItem add = new MenuItem("Preset 1");
        menu_Presets.getItems().add(1, add);
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
        startWizard("/fxml/wizard/content/chart/ChartWizardFrame.fxml", false);
    }

    @FXML
    private void closeButtonAction() {
        System.exit(1);
    }

    @FXML
    private void startPreset(ActionEvent event) {
        //TODO: will handle predefined preset functionality
    }

    @FXML
    private void startPresetEditor() {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(getClass().getResource("/fxml/wizard/SettingsFrame.fxml"));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
            settingsController = loader.getController();
            //System.out.println(settingsController);
        } catch (IOException e) {
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
            svgWizardController = ((SVGWizardController) loader.getController());
            svgWizardController.setExtended(isExtended);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void clearMessageLabel() {
//        this.scrollPane_message.setContent(new Text());
        this.scrollPane_message.setVisible(false);
        this.scrollPane_message.getStyleClass().clear();
        this.vBox_messages.getChildren().clear();

        try {
            this.svgWizardController.vBox_infos.getChildren().clear();
            this.svgWizardController.vBox_warnings.getChildren().clear();
            this.svgWizardController.button_Warnings.setGraphic(this.svgWizardController.getWarnIcon());
            this.svgWizardController.button_Warnings.setDisable(true);
            this.svgWizardController.button_Infos.setGraphic(this.svgWizardController.getInfoIcon());
            this.svgWizardController.button_Infos.setDisable(true);
        } catch (Exception e) {
        }
    }
}
