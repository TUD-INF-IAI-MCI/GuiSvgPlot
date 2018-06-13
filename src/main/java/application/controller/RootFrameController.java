package application.controller;

import application.controller.wizard.WizardController;
import application.controller.wizard.chart.ChartController;
import application.controller.wizard.functions.FunctionFrameController;
import application.model.SvgStage;
import application.service.ButtonService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

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
        button_StartFunction.setOnAction(event -> startFunction());
    }

    public void closeWizard() {
        borderPane_Content.setCenter(center);
    }


    private void startFunction() {

        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(getClass().getResource("/fxml/wizard/content/functions/FunctionWizardFrame.fxml"));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDiagram(ActionEvent event) {
        ResourceBundle bundle = ResourceBundle.getBundle("langBundle");
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(getClass().getResource("/fxml/wizard/Wizard.fxml"));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
            WizardController wizardController = loader.getController();
            wizardController.setSvgWizardController(new ChartController());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
