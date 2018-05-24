package application.controller;

import application.Wizard.WizardField;
import application.Wizard.WizardStage;
import application.controller.wizard.WizardController;
import application.controller.wizard.tiles.ChoiceBoxController;
import application.controller.wizard.tiles.TextFieldController;
import application.service.ButtonService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;

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
        // TODO: verschlanken
        SvgPlotOptions svgPlotOptions = new SvgPlotOptions();
        FXMLLoader choiceBoxLoader = new FXMLLoader();
        choiceBoxLoader.setLocation(getClass().getResource("/fxml/wizard/content/tiles/choiceBox.fxml"));
        FXMLLoader choiceBoxLoader2 = new FXMLLoader();
        choiceBoxLoader2.setLocation(getClass().getResource("/fxml/wizard/content/tiles/choiceBox.fxml"));
        FXMLLoader textFieldLoader = new FXMLLoader();
        textFieldLoader.setLocation(getClass().getResource("/fxml/wizard/content/tiles/textField.fxml"));

        ObservableList<DiagramType> diagramTypeObservableList =  FXCollections.observableArrayList(DiagramType.values());
        ObservableList<OutputDevice> outputDeviceObservableList =  FXCollections.observableArrayList(OutputDevice.values());
        ChoiceBoxController<DiagramType> cbc = new ChoiceBoxController<>("Diagrammtyp", diagramTypeObservableList, svgPlotOptions.getDiagramType());
        ChoiceBoxController<OutputDevice> cbc2 = new ChoiceBoxController<>("Ausgabegerär", outputDeviceObservableList, svgPlotOptions.getOutputDevice());
        TextFieldController tfc = new TextFieldController("Titel", svgPlotOptions.getTitle());

        Map<Integer, WizardStage> stagesMap = new HashMap<>();
        WizardField typeField = new WizardField(cbc, choiceBoxLoader, svgPlotOptions.getDiagramType());
        WizardField titleField = new WizardField(tfc, textFieldLoader, svgPlotOptions.getTitle());
        WizardField deviceField = new WizardField(cbc2, choiceBoxLoader2, svgPlotOptions.getOutputDevice());
        stagesMap.put(1, new WizardStage("Grundlagen", "", Arrays.asList(titleField, deviceField, typeField)));

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/wizard/Wizard.fxml"));
        try {
            center = borderPane_Content.getCenter();
            borderPane_Content.setCenter(loader.load());
            WizardController wizardController = loader.getController();
            wizardController.setStages(stagesMap);
            wizardController.setSvgPlotOptions(svgPlotOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
