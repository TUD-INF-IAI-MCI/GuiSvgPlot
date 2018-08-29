package application.controller;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RootFrameController implements Initializable {

    @FXML
    public BorderPane borderPane_Content;

    @FXML
    private Label label_Headline;

    @FXML
    public Button button_StartDiagram;

    @FXML
    public Button button_StartFunction;

    @FXML
    public AnchorPane anchorPain_main;

    public Menu getMenu_Presets() {
        return menu_Presets;
    }

    @FXML
    private MenuItem menuItem_About;
    @FXML
    public Menu menu_Presets = new Menu();

    @FXML
    public ScrollPane scrollPane_message;

    @FXML
    public VBox vBox_messages;

    private Node center;

    private ResourceBundle bundle;

    public SVGWizardController svgWizardController;

    public PresetsController presetsController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }

    public void init() {
        button_StartDiagram.setOnAction(this::startDiagram);
        button_StartFunction.setOnAction(event -> {


            startFunction(true);


//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//
//            alert.setHeaderText("Function Wizard Mode");
//            alert.setTitle("Choose Function Wizard Mode");
//            alert.setContentText("Simple will be faster, Extended will be more sophisticated");
//
//            ButtonType buttonTypeSimple = new ButtonType("Simple");
//            ButtonType buttonTypeExtended = new ButtonType("Extended");
//            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//
//            alert.getButtonTypes().setAll(buttonTypeSimple, buttonTypeExtended, buttonTypeCancel);
//
//            Optional<ButtonType> result = alert.showAndWait();
//            if (result.get() == buttonTypeSimple) {
//                startFunction(true);
//            } else if (result.get() == buttonTypeExtended) {
//                startFunction(true);
//            } else {
//                alert.close();
//            }
        });
        menuItem_About.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("menu_help_about_title"));
            alert.setHeaderText(null);
            alert.setResizable(true);
            alert.getDialogPane().setMinSize(400, 250);

            alert.setContentText(bundle.getString("menu_help_about_content"));
            alert.showAndWait();
        });
    }

    public void closeWizard() {
        this.setSceneTitle("application_title");
        this.label_Headline.setText(this.bundle.getString("application_title"));
        borderPane_Content.setCenter(center);
    }


    private void startFunction(boolean isExtended) {
        this.setSceneTitle("application_create_function_title");
        this.label_Headline.setText(this.bundle.getString("headline_function"));
        startWizard("/fxml/wizard/content/functions/FunctionWizardFrame.fxml", isExtended);
    }

    private void startDiagram(ActionEvent event) {
        this.setSceneTitle("application_create_chart_title");
        this.label_Headline.setText(this.bundle.getString("headline_chart"));
        startWizard("/fxml/wizard/content/chart/ChartWizardFrame.fxml", false);
    }

    @FXML
    private void closeButtonAction() {
        System.exit(1);
    }

    @FXML
    private void startDiagramDefaultPreset() {
        this.setSceneTitle("application_create_chart_title");
        this.label_Headline.setText(this.bundle.getString("headline_chart"));
        startWizard("/fxml/wizard/content/chart/ChartWizardFrame.fxml", false);
    }

    @FXML
    private void startFunctionDefaultPreset() {
        this.setSceneTitle("application_create_function_title");
        this.label_Headline.setText(this.bundle.getString("headline_function"));
        startWizard("/fxml/wizard/content/functions/FunctionWizardFrame.fxml", false);
    }

    @FXML
    private void startPresetOverview() {
        this.setSceneTitle("application_preset_overview");
        this.label_Headline.setText(this.bundle.getString("headline_presets"));
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(getClass().getResource("/fxml/wizard/PresetOverviewFrame.fxml"));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
            //presetsController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void startPresetEditor(){
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(getClass().getResource("/fxml/wizard/PresetEditorFrame.fxml"));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
            //presetsController = loader.getController();
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

    public void setSceneTitle(final String messageCode){
        Stage scene = GuiSvgPlott.getInstance().getPrimaryStage();
        scene.titleProperty().set(this.bundle.getString(messageCode));
    }
}
