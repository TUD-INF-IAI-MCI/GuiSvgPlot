package application.controller.wizard.chart;

import application.Wizard.StageController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for third chart stage.
 * Options: size, css, colors
 * TODO: direct css command, more colors, validate input size inputs
 */
public class Stage7Controller implements StageController {

    @FXML
    public ChoiceBox<OutputDevice> choiceBox_Device;
    @FXML
    public TextField textField_output;

    private SvgPlotOptions svgPlotOptions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initDeviceItems();
    }

    @Override
    public SvgPlotOptions getSvgPlotOptions() {
        return svgPlotOptions;
    }

    @Override
    public void setSvgPlotOptions(SvgPlotOptions svgPlotOptions) {
        this.svgPlotOptions = svgPlotOptions;
        this.initFields();
        this.initListeners();
    }

    private void initDeviceItems(){
        ObservableList<OutputDevice> csvOrientationObservableList = FXCollections.observableArrayList(OutputDevice.values());
        this.choiceBox_Device.setItems(csvOrientationObservableList);

    }
    private void initFields() {
        this.choiceBox_Device.setValue(this.svgPlotOptions.getOutputDevice());

        if (this.svgPlotOptions.getOutput() != null){
            this.textField_output.setText(this.svgPlotOptions.getOutput().getPath());
        }
    }

    /**
     * Initializes {@link ChangeListener} on fields.
     */
    private void initListeners() {
        this.textField_output.textProperty().addListener((observable, oldValue, newValue) -> {
            File file = new File(newValue);
            svgPlotOptions.setOutput(file);
        });

        this.choiceBox_Device.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OutputDevice>() {
            @Override
            public void changed(ObservableValue<? extends OutputDevice> observable, OutputDevice oldValue, OutputDevice newValue) {
                svgPlotOptions.setOutputDevice(newValue);
            }
        });
    }


}
