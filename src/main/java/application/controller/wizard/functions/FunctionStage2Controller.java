package application.controller.wizard.functions;

import application.GuiSvgPlott;
import application.Wizard.StageController;
import application.model.SvgStage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionStage2Controller implements StageController {


    public TextField textField_Integral;
    public Button button_Sort;
    public ComboBox comboBox_Sort;
    public CheckBox checkBox_Pi;
    public ComboBox comboBox_DiagramType;
    public CheckBox checkBox_OriginalPoints;
    public TextField textField_UnitX;
    public TextField textField_UnitY;

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
