package application.controller.wizard.functions;

import application.Wizard.StageController;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.net.URL;
import java.util.ResourceBundle;

public class FunctionStage3Controller implements StageController {


    public RadioButton radio_Vertical;
    public RadioButton radio_Horizontal;
    public ComboBox comboBox_CsvType;
    public Button button_CsvPath;
    public TextField textField_CsvPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    @Override
    public void setSvgPlotOptions(SvgPlotOptions svgPlotOptions) {

    }

    @Override
    public SvgPlotOptions getSvgPlotOptions() {
        return null;
    }

}
