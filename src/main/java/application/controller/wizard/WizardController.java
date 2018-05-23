package application.controller.wizard;

import application.GuiSvgPlott;
import application.service.ButtonService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WizardController implements Initializable {

    @FXML
    public Button button_Next;

    @FXML
    public Button button_Cancel;

    @FXML
    public Button button_Close;

    @FXML
    public BorderPane borderPane_Wizard;

    private ButtonService buttonService = ButtonService.getInstance();

    private int currentStageIndex = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initButtons();
        loadIntro();
    }

    private void loadIntro() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/wizard/content/Intro.fxml"));

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

        buttonService.addEnterEventHandler(button_Cancel);
        button_Cancel.setOnAction(event -> {
            GuiSvgPlott.getInstance().closeWizard();
        });

    }

    private void loadWizardContentByIndex(int currentStageIndex) {
        System.out.println(currentStageIndex);
        borderPane_Wizard.setCenter(null);
    }


}
