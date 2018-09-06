package application.controller.wizard.functions;

import application.controller.wizard.SVGWizardController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FunctionWizardFrameController extends SVGWizardController {

    private static final int AMOUNTOFSTAGES = 6;



    /*Begin: FXML Nodes*/

    /* stage 1 */
    @FXML
    private GridPane stage1;

    /* stage 2*/
    @FXML
    private GridPane stage2;
    @FXML
    private Button button_EditDataSet;


    /* stage 3 */
    @FXML
    private GridPane stage3;

    public ComboBox<Function> comboBox_function2;

    public ComboBox<Function> comboBox_function1;


    @FXML
    private RadioButton radioButton_Function2;

    @FXML
    private TextField textField_rangeFrom;

    @FXML
    private TextField textField_rangeTo;


    @FXML
    private Label label_RangeFrom;

    @FXML
    private Label label_RangeTo;

    @FXML
    private TextField textField_IntegralName;

    @FXML
    private CheckBox checkBox_ValueRange;


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

    private ObservableList<Function> functionList;


    public FunctionWizardFrameController() {
        integral1 = new SimpleStringProperty("");
        integral2 = new SimpleStringProperty("");
        rangeFrom = new SimpleDoubleProperty(-100);
        rangeTo = new SimpleDoubleProperty(100);
        integralName = new SimpleStringProperty("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES, DiagramType.FunctionPlot);
        this.initiateAllStages();


        this.guiSvgOptions.setDiagramType(DiagramType.FunctionPlot);
        super.initSaveAsPreset();
    }


    private void initiateAllStages() {
        initStage1();
        initStage2();
        initStage3();
        initStage4();
        initStage5();
        initStage6();

        Function f1 = new Function("F1", "x^2");
        Function f2 = new Function("F2", "-(x^2)");
        Function f3 = new Function("F3", "2x");
        Function f4 = new Function("F4", "3");


        functionList.addAll(f1, f2, f3, f4);


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

        this.functionList = FXCollections.observableArrayList();

        super.initCsvFieldListeners();


    }


    /**
     * Will initiate the third stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage3() {

        comboBox_function1.setItems(functionList);
        comboBox_function2.setItems(functionList);

        Callback<ListView<Function>, ListCell<Function>> comboBoxCB = new Callback<ListView<Function>, ListCell<Function>>() {
            @Override
            public ListCell<Function> call(ListView<Function> p) {
                return new ListCell<Function>() {
                    @Override
                    protected void updateItem(Function item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getFunction());
                        }
                    }
                };
            }
        };

        comboBox_function1.setCellFactory(comboBoxCB);
        comboBox_function2.setCellFactory(comboBoxCB);


        comboBox_function2.visibleProperty().bind(radioButton_Function2.selectedProperty());


        comboBox_function1.setOnAction(event -> {
            integral1.set(comboBox_function1.getSelectionModel().getSelectedItem().getFunction());
            integralOptionBuilder();

        });


        comboBox_function2.setOnAction(event -> {
            integral1.set(comboBox_function2.getSelectionModel().getSelectedItem().getFunction());
            integralOptionBuilder();

        });

        radioButton_Function2.visibleProperty().addListener(args -> {
            integral2.set("y=0");
            integralOptionBuilder();
        });


        textField_rangeFrom.visibleProperty().bind(checkBox_ValueRange.selectedProperty());
        textField_rangeTo.visibleProperty().bind(checkBox_ValueRange.selectedProperty());
        label_RangeFrom.visibleProperty().bind(checkBox_ValueRange.selectedProperty());
        label_RangeTo.visibleProperty().bind(checkBox_ValueRange.selectedProperty());

        integralName.bind(textField_IntegralName.textProperty());
    }

    /**
     * Will initiate the forth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage4() {
        super.initAxisFieldListeners();
        super.toggleAxesRanges(true);
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

        int f1 = comboBox_function1.getSelectionModel().getSelectedIndex();
        int f2 = radioButton_Function2.isVisible() ? comboBox_function2.getSelectionModel().getSelectedIndex() : -1;
        double from = -10;
        double to = 10;

        if (checkBox_ValueRange.isSelected()) {
            from = Double.parseDouble(textField_rangeFrom.getText());
            to = Double.parseDouble(textField_rangeTo.getText());
        }
        String name = textField_IntegralName.getText();


        ObservableList<Function> f = FXCollections.observableArrayList();

        functionList.forEach(func -> f.add(func));
        guiSvgOptions.getFunctions().clear();


//        if (f2 == -1) {
//            f.add(new Function("0x"));
//            f2 = f.size();
//        }
        guiSvgOptions.getFunctions().addAll(f);
        IntegralPlotSettings integalSettings = new IntegralPlotSettings(f1, f2, name, new Range(from, to));
        guiSvgOptions.setIntegral(integalSettings);

        this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);

    }

    public ObservableList<Function> getFunctionList() {
        return functionList;
    }


}
