package application.controller.wizard;

import application.GuiSvgPlott;
import application.Wizard.WizardField;
import application.Wizard.WizardStage;
import application.service.ButtonService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    private Map<Integer, WizardStage> stages;

    private SvgPlotOptions svgPlotOptions;


    public WizardController() {
        this.stages = new HashMap<>();
        this.svgPlotOptions = new SvgPlotOptions();
    }

    public void setStages(Map<Integer, WizardStage> stages) {
        this.stages = stages;
    }

    public Map<Integer, WizardStage> getStages() {
        return this.stages;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initButtons();
        loadIntro();
    }

    private void loadIntro() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(GuiSvgPlott.class.getResource("/fxml/wizard/content/Intro.fxml"));

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
        WizardStage stage = this.stages.getOrDefault(currentStageIndex, null);
        System.out.println(stage);
        if (stage != null) {
            Pane stagePane = new AnchorPane();
            Label header = new Label(stage.getTitle());
            stagePane.getChildren().add(header);
            int index = 0;
            for (WizardField tile : stage.getInputFields()) {
                index++;
                try {
                    Node node = tile.getFxmlLoader().load();
                    node.setTranslateY(node.getLayoutY() + 100 * index);
                    stagePane.getChildren().add(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            borderPane_Wizard.setCenter(stagePane);
        }
    }


    public void setSvgPlotOptions(SvgPlotOptions svgPlotOptions) {
        this.svgPlotOptions = svgPlotOptions;
    }
}
