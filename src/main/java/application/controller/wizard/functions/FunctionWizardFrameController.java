package application.controller.wizard.functions;

import application.controller.wizard.SVGWizardController;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author Robert Schlegel
 */
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

    @FXML
    private ScrollPane scrollPane_dataTable;
    @FXML
    private VBox vBox_DataTable;


    /* stage 3 */
    @FXML
    private GridPane stage3;

    public ChoiceBox<Function> comboBox_function2;

    public ChoiceBox<Function> comboBox_function1;


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
    private RadioButton radioButton_ValueRangeCustom;

    @FXML
    private RadioButton radioButton_ValueRangeDefault;


    /* stage 4 */
    @FXML
    private GridPane stage4;

    @FXML
    private RadioButton radioButton_ScalingDecimal;

    @FXML
    private RadioButton radioButton_ScalingPi;


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
        textField_rangeTo.setText(-10 + "");
        textField_rangeFrom.setText(10 + "");


        this.guiSvgOptions.setDiagramType(DiagramType.FunctionPlot);
        super.initloadPreset();
    }

    @Override
    protected void initiateAllStages() {
        initStage1();
        initStage2();
        initStage3();
        initStage4();
        initStage5();
        initStage6();
        super.initOptionListeners();
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
        vBox_DataTable.getStyleClass().add("data-table");
        vBox_DataTable.setFillWidth(true);

        scrollPane_dataTable.setFitToWidth(true);

        this.functionList = FXCollections.observableArrayList();

        super.initCsvFieldListeners();


        button_EditDataSet.setOnAction(event -> {
            HBox row = generateTableEntry(new Function("", ""));
            vBox_DataTable.getChildren().add(row);
        });

        getResultFileProp().addListener(inval -> {
            try {
                Files.readAllLines(getResultFileProp().get()).forEach(item -> {
                    HBox row = generateTableEntry(new Function(item.substring(0, item.indexOf(",")).trim(), item.substring(item.indexOf(",") + 1).trim()));
                    vBox_DataTable.getChildren().add(row);
                });
            } catch (IOException e) {
            }


        });


        vBox_DataTable.getChildren().addListener((ListChangeListener.Change<? extends Node> nodes) -> {
            while (nodes.next()) {

                nodes.getAddedSubList().forEach(row -> {
                    if (row instanceof HBox) {
                        Function f = (Function) ((HBox) row).getUserData();
                        if (!(f.getFunction().isEmpty() && f.getTitle().isEmpty()))
                            functionList.add((Function) row.getUserData());
                    }
                });

                nodes.getRemoved().forEach(row -> {
                    if (row instanceof HBox) {
                        functionList.remove(row.getUserData());
                    }
                });
            }
        });

    }


    /**
     * Will initiate the third stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage3() {

        comboBox_function1.setItems(functionList);
        comboBox_function2.setItems(functionList);


        StringConverter<Function> converter = new StringConverter<Function>() {
            Function f;

            @Override
            public String toString(Function function) {
                f = function;
                return function.toString();
//                return "";
            }

            @Override
            public Function fromString(String string) {
                return f;
            }
        };

        comboBox_function1.setConverter(converter);
        comboBox_function2.setConverter(converter);


        comboBox_function2.visibleProperty().bind(radioButton_Function2.selectedProperty());


        comboBox_function1.setOnAction(event -> {
            try {
                integral1.set(comboBox_function1.getSelectionModel().getSelectedItem().getFunction());
            } catch (NullPointerException e) {

            }
            integralOptionBuilder();

        });


        comboBox_function2.setOnAction(event -> {
            integral1.set(comboBox_function2.getSelectionModel().getSelectedItem().getFunction());
            integralOptionBuilder();

        });

        radioButton_Function2.selectedProperty().addListener((args, oldVal, newVal) -> {
            if (!newVal)
                integral2.set("y=0");
            integralOptionBuilder();
            this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);
        });


        textField_rangeFrom.visibleProperty().bind(radioButton_ValueRangeDefault.selectedProperty());
        textField_rangeTo.visibleProperty().bind(radioButton_ValueRangeDefault.selectedProperty());
        label_RangeFrom.visibleProperty().bind(radioButton_ValueRangeDefault.selectedProperty());
        label_RangeTo.visibleProperty().bind(radioButton_ValueRangeDefault.selectedProperty());


        textField_rangeFrom.textProperty().addListener(inv -> {
            integralOptionBuilder();
        });
        textField_rangeTo.textProperty().addListener(inv -> {
            integralOptionBuilder();
        });
        radioButton_ValueRangeCustom.selectedProperty().addListener(args -> {
            integralOptionBuilder();
        });
        radioButton_ValueRangeDefault.selectedProperty().addListener(args -> {
            integralOptionBuilder();
        });
        radioButton_ScalingPi.selectedProperty().addListener(args -> {
            integralOptionBuilder();
        });
        radioButton_ScalingDecimal.selectedProperty().addListener(args -> {
            integralOptionBuilder();
        });


        integralName.bind(textField_IntegralName.textProperty());
    }

    /**
     * Will initiate the forth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage4() {


        textField_rangeFrom.setText("-10");
        textField_rangeTo.setText("10");

        super.initAxisFieldListeners();
        super.toggleAxesRanges(true);

        guiSvgOptions.piProperty().bind(radioButton_ScalingPi.selectedProperty());


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
        int f2 = radioButton_Function2.isSelected() ? comboBox_function2.getSelectionModel().getSelectedIndex() : -1;
        double from = -10;
        double to = 10;

        if (radioButton_ValueRangeCustom.isSelected()) {
            if (!textField_rangeFrom.getText().isEmpty())
                from = Double.parseDouble(textField_rangeFrom.getText().replaceAll(",", "."));
            if (!textField_rangeTo.getText().isEmpty())
                to = Double.parseDouble(textField_rangeTo.getText().replaceAll(",", "."));
        }

        String name = textField_IntegralName.getText();


        ObservableList<Function> f = FXCollections.observableArrayList();

        functionList.forEach(func -> f.add(func));
        guiSvgOptions.getFunctions().clear();


        guiSvgOptions.getFunctions().addAll(f);

        if (!f.isEmpty()) {
            IntegralPlotSettings integalSettings = new IntegralPlotSettings(f1, f2, name, new Range(from, to));
            guiSvgOptions.setIntegral(integalSettings);
            this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);
        }
    }

    public ObservableList<Function> getFunctionList() {
        return functionList;
    }


    private HBox generateTableEntry(Function function) {

        Function f = new Function(function.getTitle(), function.getFunction());
        System.out.println(f);

        HBox row = new HBox();


        row.getStyleClass().add("data-row");
        row.setSpacing(5);
        row.setUserData(f);

        if ((f.getTitle().isEmpty() && f.getFunction().isEmpty()))
            functionList.remove(row.getUserData());

        TextField titleField = new TextField(function.getTitle());
        titleField.getStyleClass().add("data-cell-x");


        titleField.setPromptText("Funktionsname");

        TextField functionField = new TextField(function.getFunction());
        functionField.getStyleClass().add("data-cell-y");
        functionField.setPromptText("Funktion");

        InvalidationListener invalidationListener = args -> {
            functionList.remove(row.getUserData());

            if (!(titleField.getText().trim().isEmpty() && functionField.getText().trim().isEmpty())) {
                Function func = new Function(titleField.getText(), functionField.getText());
                row.setUserData(func);
                functionList.add(func);
                renderFunctions();
            }
        };

        titleField.textProperty().addListener(invalidationListener);
        functionField.textProperty().addListener(invalidationListener);

        Glyph closeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CLOSE);
        Button removeButton = new Button();
        removeButton.setAccessibleText("Reihe löschen");
        removeButton.setTooltip(new Tooltip("Reihe löschen"));

        removeButton.setGraphic(closeGlyph);
        removeButton.getStyleClass().add("data-cell-button");
        removeButton.setOnAction(event -> {
            vBox_DataTable.getChildren().remove(row);
            renderFunctions();
        });

        row.getChildren().addAll(titleField, functionField, removeButton);
        row.setFocusTraversable(true);
        row.setAccessibleRole(AccessibleRole.TABLE_ROW);
        row.setAccessibleText("Reihe " + (vBox_DataTable.getChildren().size() + 1));


        HBox.setHgrow(functionField, Priority.ALWAYS);
        HBox.setHgrow(titleField, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.NEVER);

        return row;
    }

    private void renderFunctions() {
        comboBox_function1.setItems(functionList);
        comboBox_function2.setItems(functionList);
    }


}
