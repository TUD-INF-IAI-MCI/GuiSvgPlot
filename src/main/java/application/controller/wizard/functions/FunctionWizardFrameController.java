package application.controller.wizard.functions;

import application.controller.wizard.SVGWizardController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

import java.net.URL;
import java.util.ResourceBundle;

public class FunctionWizardFrameController extends SVGWizardController {

    private static final int AMOUNTOFSTAGES = 6;


    /*Begin: FXML Nodes*/

    /* stage 1 */
    @FXML
    private GridPane stage1;

    /* stage 2*/
    @FXML
    private GridPane stage2;

    /* stage 3 */
    @FXML
    private GridPane stage3;

    @FXML
    private TextField textField_IntegralFunction1;

    @FXML
    private TextField textField_IntegralFunction2;

    @FXML
    private RadioButton radioButton_Function2;

    @FXML
    private TextField textField_rangeFrom;

    @FXML
    private TextField textField_rangeTo;

    @FXML
    private RadioButton radioButton_integralRange;

    @FXML
    private Label label_RangeFrom;

    @FXML
    private Label label_RangeTo;

    @FXML
    private TextField textField_IntegralName;


    /* stage 4 */
    @FXML
    private GridPane stage4;


    /* stage 5 */
    @FXML
    private GridPane stage5;

    /* stage 6 */
    @FXML
    private GridPane stage6;

    /*End: FXML Nodes*/

    private SimpleStringProperty integral1;
    private SimpleStringProperty integral2;
    private SimpleDoubleProperty rangeFrom;
    private SimpleDoubleProperty rangeTo;
    private SimpleStringProperty integralName;

    public FunctionWizardFrameController() {
        integral1 = new SimpleStringProperty("");
        integral2 = new SimpleStringProperty("");
        rangeFrom = new SimpleDoubleProperty(-100);
        rangeTo = new SimpleDoubleProperty(100);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES);
        this.initiateAllStages();

        this.guiSvgOptions.setDiagramType(DiagramType.FunctionPlot);

    }


    private void initiateAllStages() {
        initStage1();
        initStage2();
        initStage3();
        initStage4();
        initStage5();
        initStage6();
    }


    /**
     * Will initiate the first stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage1() {

        super.initGeneralFieldListeners();

    }


    /**
     * Will initiate the second stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage2() {
        super.initCsvFieldListeners();
    }

    /**
     * Will initiate the third stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage3() {


        textField_IntegralFunction2.visibleProperty().bind(radioButton_Function2.selectedProperty());

        textField_IntegralFunction1.textProperty().addListener((args, oldVal, newVal) -> {
            integral1.set(newVal);
            integralOptionBuilder();
        });

        textField_IntegralFunction2.textProperty().addListener((args, oldVal, newVal) -> {
            if (textField_IntegralFunction2.isVisible())
                integral2.set(newVal);
            integralOptionBuilder();
        });

        radioButton_Function2.visibleProperty().addListener(args -> {
            integral2.set("y=0");
            integralOptionBuilder();
        });


        textField_rangeFrom.visibleProperty().bind(radioButton_integralRange.selectedProperty());
        textField_rangeTo.visibleProperty().bind(radioButton_integralRange.selectedProperty());
        label_RangeFrom.visibleProperty().bind(radioButton_integralRange.selectedProperty());
        label_RangeTo.visibleProperty().bind(radioButton_integralRange.selectedProperty());

        integralName.bind(textField_IntegralName.textProperty());

    }

    /**
     * Will initiate the forth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage4() {
        super.initAxisFieldListeners();
    }

    /**
     * Will initiate the fifth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage5() {
        super.initSpecialFieldListeners();
    }

    /**
     * Will initiate the sixth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage6() {
        super.initStylingFieldListeners();
    }

    private void integralOptionBuilder() {


        //FIXME
        int f1 = 1;
        int f2 = 2;
        double from = -100;
        double to = 100;
        String name = "integral";

        IntegralPlotSettings integalSettings = new IntegralPlotSettings(f1, f2, name, new Range(from, to));
        guiSvgOptions.setIntegral(integalSettings);

    }


}
