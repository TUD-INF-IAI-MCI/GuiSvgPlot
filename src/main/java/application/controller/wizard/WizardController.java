package application.controller.wizard;

import application.GuiSvgPlott;
import application.Wizard.SVGWizardController;
import application.service.ButtonService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WizardController implements Initializable {

    @FXML
    public Button button_Next;

    @FXML
    public Button button_Back;

    @FXML
    public Button button_Cancel;

    @FXML
    public Button button_Close;

    @FXML
    public BorderPane borderPane_Wizard;

    @FXML
    public Label label_Headline;

    private ButtonService buttonService = ButtonService.getInstance();

    private int currentStageIndex = 0;

    private SVGWizardController svgWizardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initButtons();
        loadIntro();
    }

    private void loadIntro() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/wizard/content/Intro.fxml"));
        System.out.println(loader.getLocation());
        try {
            AnchorPane introPane = loader.load();
            borderPane_Wizard.setCenter(introPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initButtons() {
        buttonService.addEnterEventHandler(button_Next);
        button_Next.setOnAction(event -> {
            loadWizardContentByIndex(++currentStageIndex);
        });

        buttonService.addEnterEventHandler(button_Back);
        button_Back.setOnAction(event -> {
            loadWizardContentByIndex(--currentStageIndex);
        });

        buttonService.addEnterEventHandler(button_Cancel);
        button_Cancel.setOnAction(event -> {
            GuiSvgPlott.getInstance().closeWizard();
        });

    }

    private void loadWizardContentByIndex(int currentStageIndex) {
        System.out.println(currentStageIndex);
        if(svgWizardController != null) {
            svgWizardController.setCurrentStage(currentStageIndex);
        }
    }

    public SVGWizardController getSvgWizardController() {
        return svgWizardController;
    }

    public void setSvgWizardController(SVGWizardController svgWizardController) {
        this.svgWizardController = svgWizardController;
        this.label_Headline.setText(this.svgWizardController.getTitle() + " erstellen");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(this.svgWizardController.getFXMLLocation()));
        try {
            loader.setController(this.svgWizardController);
            borderPane_Wizard.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
