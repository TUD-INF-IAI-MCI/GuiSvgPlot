package application.controller.wizard.functions;

import application.GuiSvgPlott;
import application.Wizard.StageController;
import application.model.GuiSvgOptions;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionWizardFrameController implements StageController {


    // WIZARD
    @FXML
    private Button button_Back;
    @FXML
    private BorderPane borderPane_Wizard;
    @FXML
    private Button button_Next;
    @FXML
    private Button button_Cancel;
    @FXML
    private Label label_Headline;

    @FXML
    private TabPane tabPane_ContentHolder;

    //STAGE 1
    @FXML
    private AnchorPane stage1;

    @FXML
    private TextField textField_Title;
    @FXML
    private ComboBox<String> comboBox_OutputDevice;
    @FXML
    private TextField textField_OutputPath;
    @FXML
    private Button button_OutputPath;


    //STAGE 2
    @FXML
    private AnchorPane stage2;

    @FXML
    private TextField textField_Integral;
    @FXML
    private CheckBox checkBox_Pi;
    @FXML
    private ComboBox comboBox_DiagramType;
    @FXML
    private CheckBox checkBox_OriginalPoints;
    @FXML
    private Button button_Sort;
    @FXML
    private ComboBox comboBox_Sort;
    @FXML
    private TextField textField_UnitX;
    @FXML
    private TextField textField_UnitY;


    // STAGE 3
    @FXML
    private AnchorPane stage3;

    @FXML
    private ComboBox comboBox_CsvType;
    @FXML
    private RadioButton radio_Horizontal;
    @FXML
    private RadioButton radio_Vertical;
    @FXML
    private TextField textField_CsvPath;
    @FXML
    private Button button_CsvPath;

    // STAGE 4
    @FXML
    private AnchorPane stage4;


    @FXML
    private Button button_OutputPath1;
    @FXML
    private TextField textField_OutputPath1;
    @FXML
    private ComboBox comboBox_OutputDevice1;
    @FXML
    private TextField textField_Title1;


    /// Options
    private File userDir;
    private IntegerProperty currentStage;

    private ArrayList<AnchorPane> stages;
    private BooleanProperty isExtended;

    GuiSvgOptions svgOptions;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userDir = new File(System.getProperty("user.home"));
        textField_OutputPath.setText(userDir.getPath());

        svgOptions = new GuiSvgOptions(new SvgPlotOptions());

        this.currentStage = new SimpleIntegerProperty();
        this.isExtended = new SimpleBooleanProperty();

        initListener();

        preProcessContent();

        initStage1(false);
        initStage2(false);
        initStage4(false);
        initStage3(false);


    }

    public void setExtended(boolean isExtended) {
        this.isExtended.set(isExtended);

    }


    private void preProcessContent() {
        stages = new ArrayList<>();
        tabPane_ContentHolder.getTabs().forEach(tab -> stages.add((AnchorPane) tab.getContent()));
        currentStage.set(0);
        borderPane_Wizard.setCenter(stages.get(0));
        button_Back.setDisable(true);
    }


    private void initStage1(Boolean extended) {

        textField_Title.setText("");

        List<String> outputDevices = (Stream.of(OutputDevice.values()).map(Enum::name).collect(Collectors.toList()));
        comboBox_OutputDevice.setDisable(!isExtended.get());
        comboBox_OutputDevice.getItems().clear();
        comboBox_OutputDevice.getItems().addAll(outputDevices);
        comboBox_OutputDevice.getSelectionModel().select(0);

        button_OutputPath.setDisable(!isExtended.get());
        textField_OutputPath.setDisable(!isExtended.get());


        button_OutputPath.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File(System.getProperty("user.home")));
            File f = fc.showSaveDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (f != null)
                textField_OutputPath.setText(f.getAbsolutePath());

        });


    }

    private void initStage2(Boolean extended) {
    }

    private void initStage3(Boolean extended) {
    }

    private void initStage4(Boolean extended) {
    }


    private void initListener() {

        currentStage.addListener((args, oldVal, newVal) -> {

            if (newVal.intValue() < 1) button_Back.setDisable(true);
            else button_Back.setDisable(false);

            if (newVal.intValue() >= stages.size() - 1) {
                currentStage.set(oldVal.intValue());
            }

            borderPane_Wizard.setCenter(stages.get(newVal.intValue()));
        });


        isExtended.addListener(invalid -> {
            System.out.println("shit");
        });

        isExtended.addListener((args, oldVal, newVal) -> {

            System.out.println("kek");
            initStage1(newVal);
            initStage2(newVal);
            initStage4(newVal);
            initStage3(newVal);

        });

        // PreProcess
        button_Next.setOnAction(event -> currentStage.set(currentStage.get() + 1));


        button_Back.setOnAction(event -> currentStage.set(currentStage.get() - 1));

        button_Cancel.setOnAction(event -> {
            GuiSvgPlott.getInstance().closeWizard();
        });

        button_CsvPath.setOnAction(event -> {

        });


        button_OutputPath.setOnAction(event -> {

        });

        button_OutputPath1.setOnAction(event -> {

        });


        button_Sort.setOnAction(event -> {

        });

    }


    @Override
    public void setSvgPlotOptions(SvgPlotOptions svgPlotOptions) {

    }

    @Override
    public SvgPlotOptions getSvgPlotOptions() {
        return null;
    }


}
