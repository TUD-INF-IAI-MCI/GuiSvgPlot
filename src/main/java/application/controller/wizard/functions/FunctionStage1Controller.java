package application.controller.wizard.functions;

import application.GuiSvgPlott;
import application.Wizard.StageController;
import application.model.SvgStage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

public class FunctionStage1Controller implements StageController {


    @FXML
    private TextField textField_Title;
    @FXML
    private Button button_OutputPath;
    @FXML
    private TextField textField_OutputPath;
    @FXML
    private ComboBox<String> comboBox_OutputDevice;

    private SvgStage stage;
    private File userDir;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDir = new File(System.getProperty("user.home"));
        textField_OutputPath.setText(userDir.getPath());
        initContent();
    }


    @Override
    public void setSvgPlotOptions(SvgPlotOptions svgPlotOptions) {

    }

    @Override
    public SvgPlotOptions getSvgPlotOptions() {
        return null;
    }

    private void initContent() {

        List<String> outputDevices = (Stream.of(OutputDevice.values()).map(OutputDevice::name).collect(Collectors.toList()));
        comboBox_OutputDevice.getItems().addAll(outputDevices);

        button_OutputPath.setOnAction(event -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory(userDir);

            File f = dc.showDialog(GuiSvgPlott.getInstance().getPrimaryStage());

            if (f != null) {
                textField_OutputPath.setText(f.getPath());
            }


        });

    }
}
