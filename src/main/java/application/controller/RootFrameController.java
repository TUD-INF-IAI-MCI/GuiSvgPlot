package application.controller;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.model.Preset;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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



    @FXML
    private MenuItem menuItem_About;
    @FXML
    public Menu menu_Presets = new Menu();
    @FXML
    public MenuItem menuItem_Save_Preset;
    @FXML
    public MenuItem menuItem_Preset_Editor;

    @FXML
    public ScrollPane scrollPane_message;

    @FXML
    public VBox vBox_messages;

    private Node center;

    private ResourceBundle bundle;

    public SVGWizardController svgWizardController;

    public PresetsController presetsController = PresetsController.getInstance();

    public static String wizardPath = "none";


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
        menuItem_Save_Preset.setDisable(true);
        menuItem_Save_Preset.setOnAction(event -> {
            if(wizardPath.contains("Chart")){
                Preset savedPreset = new Preset(svgWizardController.getGuiSvgOptions(), "tempName", svgWizardController.getGuiSvgOptions().getDiagramType());
                TextInputDialog nameDialogue = new TextInputDialog();
                nameDialogue.setTitle(bundle.getString("prompt_preset_name_title"));
                nameDialogue.setHeaderText(bundle.getString("prompt_preset_name_header"));
                nameDialogue.setContentText(bundle.getString("prompt_preset_name_content"));
                Optional<String> result = nameDialogue.showAndWait();
                if(result.get().equals("")){
                    Alert alarm = new Alert(Alert.AlertType.ERROR);
                    alarm.setTitle(bundle.getString("alert_preset_empty_title"));
                    alarm.setHeaderText(bundle.getString("alert_preset_empty_header"));
                    alarm.setContentText(bundle.getString("alert_preset_empty_content"));
                    alarm.showAndWait();
                }else if (result.isPresent() && !presetsController.presets.contains(result.get())){
                    savedPreset.setName(result.get());
                    startPresetOverview();
                    presetsController.presets.add(savedPreset);
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(bundle.getString("alert_preset_duplicate_title"));
                    alert.setHeaderText(bundle.getString("alert_preset_duplicate_header1") + result.get() + bundle.getString("alert_preset_duplicate_header2"));
                    alert.setContentText(bundle.getString("alert_preset_duplicate_content"));
                    alert.showAndWait();
                }
            }else if(wizardPath.contains("Function")){
                Preset savedPreset = new Preset(svgWizardController.getGuiSvgOptions(), "tempName", svgWizardController.getGuiSvgOptions().getDiagramType());
            }

        });

        menuItem_About.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(bundle.getString("menu_help_about_title"));
            alert.setHeaderText(null);
            alert.setResizable(true);
            alert.getDialogPane().setMinSize(500, 250);
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
        startWizard(GuiSvgPlott.FunctionWizardFrame, isExtended);
    }

    private void startDiagram(ActionEvent event) {
        this.setSceneTitle("application_create_chart_title");
        this.label_Headline.setText(this.bundle.getString("headline_chart"));
        startWizard(GuiSvgPlott.ChartWizardFrame, false);
    }

    @FXML
    private void closeButtonAction(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(400);
        alert.setTitle(bundle.getString("alert_exit_title"));
        alert.setHeaderText(bundle.getString("alert_exit_header"));
        alert.setContentText(bundle.getString("alert_exit_content"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            GuiSvgPlott.getInstance().getPrimaryStage().close();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    @FXML
    private void startDiagramDefaultPreset() {
        this.setSceneTitle("application_create_chart_title");
        this.label_Headline.setText(this.bundle.getString("headline_chart"));
        startWizard(GuiSvgPlott.ChartWizardFrame, false);
    }

    @FXML
    private void startFunctionDefaultPreset() {
        this.setSceneTitle("application_create_function_title");
        this.label_Headline.setText(this.bundle.getString("headline_function"));
        startWizard(GuiSvgPlott.FunctionWizardFrame, false);
    }

    @FXML
    public void startPresetOverview() {
        // checks whether the PresetOverview window has been opened from within the Function/Chartwizard Frame -> fixes #24
        if(wizardPath.contains("Wizard")){
            GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
            GuiSvgPlott.getInstance().closeWizard();
        }
        menuItem_Preset_Editor.setDisable(true);
        this.setSceneTitle("application_preset_overview");
        this.label_Headline.setText(this.bundle.getString("headline_presets"));
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation((GuiSvgPlott.PresetOverviewFrame));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
            if(presetsController == null){
                presetsController = loader.getController();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void startPresetEditor() {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(GuiSvgPlott.PresetEditorFrame);
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
            //presetsController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startWizard(URL fxmlPath, boolean isExtended) {
        this.clearMessageLabel();
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation((fxmlPath));
        wizardPath = fxmlPath.getPath();
        menuItem_Save_Preset.setDisable(false);

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

    public void setSceneTitle(final String messageCode) {
        Stage scene = GuiSvgPlott.getInstance().getPrimaryStage();
        scene.titleProperty().set(this.bundle.getString(messageCode));
    }

    public Menu getMenu_Presets() {
        return menu_Presets;
    }
}
