package application.controller.wizard.chart;

import application.Wizard.StageController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for first chart stage.
 * Options: diagramtype, title
 */
public class Stage1Controller implements StageController {

    @FXML
    public ChoiceBox<DiagramType> choiceBox_DiagramType;

    @FXML
    public TextField textField_Title;

    private SvgPlotOptions svgPlotOptions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDiagramTypeItems();
        initListeners();
    }

    @Override
    public SvgPlotOptions getSvgPlotOptions() {
        return svgPlotOptions;
    }

    @Override
    public void setSvgPlotOptions(SvgPlotOptions svgPlotOptions) {
        this.svgPlotOptions = svgPlotOptions;
        this.initFields();
    }

    private void initDiagramTypeItems() {
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        diagramTypeObservableList.remove(DiagramType.FunctionPlot);
        this.choiceBox_DiagramType.setItems(diagramTypeObservableList);
    }

    private void initListeners() {
        this.choiceBox_DiagramType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DiagramType>() {
            @Override
            public void changed(ObservableValue<? extends DiagramType> observable, DiagramType oldValue, DiagramType newValue) {
              svgPlotOptions.setDiagramType(newValue);
            }
        });

        this.textField_Title.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setTitle(newValue);
        });
    }

    private void initFields(){
        this.choiceBox_DiagramType.setValue(this.svgPlotOptions.getDiagramType());
        this.textField_Title.setText(this.svgPlotOptions.getTitle());
    }
}
