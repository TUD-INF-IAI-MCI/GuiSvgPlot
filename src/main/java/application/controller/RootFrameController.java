package application.controller;

import application.controller.wizard.chart.ChartController;
import application.controller.wizard.WizardController;
import application.service.ButtonService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init() {
        button_StartDiagram.setOnAction(this::startDiagram);
        // fire Action Event on Enter
        buttonService.addEnterEventHandler(button_StartDiagram);
    }

    public void closeWizard() {
        borderPane_Content.setCenter(center);
    }

    private void startDiagram(ActionEvent event){
        FXMLLoader loader = new FXMLLoader();
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
