package application.controller.wizard.functions;

import application.GuiSvgPlott;
import application.Wizard.StageController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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


    //STAGE 2
    @FXML
    private AnchorPane stage2;


    // STAGE 3
    @FXML
    private AnchorPane stage3;

    // STAGE 4
    @FXML
    private AnchorPane stage4;


    @FXML
    private TextField textField_Title;
    @FXML
    private ComboBox comboBox_OutputDevice;
    @FXML
    private TextField textField_OutputPath;
    @FXML
    private Button button_OutputPath;
    @FXML
    private TextField textField_Integral;
    @FXML
    private Button button_Sort;
    @FXML
    private ComboBox comboBox_Sort;
    @FXML
    private CheckBox checkBox_Pi;
    @FXML
    private ComboBox comboBox_DiagramType;
    @FXML
    private CheckBox checkBox_OriginalPoints;
    @FXML
    private TextField textField_UnitX;
    @FXML
    private TextField textField_UnitY;
    @FXML
    private TextField textField_CsvPath;
    @FXML
    private Button button_CsvPath;
    @FXML
    private RadioButton radio_Horizontal;
    @FXML
    private RadioButton radio_Vertical;
    @FXML
    private ComboBox comboBox_CsvType;
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

    ArrayList<AnchorPane> stages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userDir = new File(System.getProperty("user.home"));
        textField_OutputPath.setText(userDir.getPath());
        currentStage = new SimpleIntegerProperty();
        initListener();


        preProcessContent();

        initStage1();
        initStage2();
        initStage3();
        initStage4();


    }


    private void preProcessContent() {
        stages = new ArrayList<>();
        tabPane_ContentHolder.getTabs().forEach(tab -> stages.add((AnchorPane) tab.getContent()));
        currentStage.set(0);


        borderPane_Wizard.setCenter(stages.get(currentStage.get()));


    }


    private void initStage1() {
    }

    private void initStage2() {
    }

    private void initStage3() {
    }

    private void initStage4() {
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

        // PreProcessww
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
