package application.controller.wizard.functions;

import application.controller.wizard.SVGWizardController;
import application.model.Options.IntegralOption;
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

/**
 * @author Robert Schlegel, Emma Müller
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

    //    @FXML
//    private ScrollPane scrollPane_dataTable;
    @FXML
    private VBox vBox_dataTable;


    /* stage 3 */
    @FXML
    private GridPane stage3;

    @FXML
    private ChoiceBox<IntegralOption> choiceBox_integralOption;

    @FXML
    private Label label_function1;
    public ChoiceBox<Function> choiceBox_function1;

    @FXML
    private Label label_function2;
    public ChoiceBox<Function> choiceBox_function2;

    @FXML
    private TextField textField_rangeFrom;

    @FXML
    private TextField textField_rangeTo;


    @FXML
    private Label label_RangeFrom;

    @FXML
    private Label label_RangeTo;

    @FXML
    private Label label_integralName;
    @FXML
    private TextField textField_integralName;

    @FXML
    private RadioButton radioButton_valueRangeCustom;

    @FXML
    private RadioButton radioButton_ValueRangeDefault;


    /* stage 4 */
    @FXML
    private GridPane stage4;

    @FXML
    private RadioButton radioButton_scalingDecimal;

    @FXML
    private RadioButton radioButton_scalingPi;


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
    private IntegralPlotSettings integralPlotSettings;

    private ObservableList<Function> functionList;


    public FunctionWizardFrameController() {
        integral1 = new SimpleStringProperty("");
        integral2 = new SimpleStringProperty("");
        rangeFrom = new SimpleDoubleProperty(100);
        rangeTo = new SimpleDoubleProperty(-100);
        integralName = new SimpleStringProperty("");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES, DiagramType.FunctionPlot);
        this.initiateAllStages();
        this.initFunctionOptionListeners();
        textField_rangeTo.setText(10 + "");
        textField_rangeFrom.setText(-10 + "");

        this.guiSvgOptions.setDiagramType(DiagramType.FunctionPlot);
        if (super.presets == null || super.presets.isEmpty()) {
            super.presets = FXCollections.observableArrayList(super.presetService.getAllFunctions());
        }
        super.initloadPreset();
        this.initFieldListenersForFunctionPreview();
    }

    @Override
    protected void initiateAllStages() {
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
        vBox_dataTable.getStyleClass().add("data-table");
        vBox_dataTable.setFillWidth(true);

//        scrollPane_dataTable.setFitToWidth(true);

        this.functionList = FXCollections.observableArrayList();

        super.initCsvFieldListeners();


        button_EditDataSet.setOnAction(event -> {
            HBox row = generateTableEntry(new Function("", ""));
            vBox_dataTable.getChildren().add(row);
        });

        getResultFileProp().addListener(inval -> {
            try {
                Files.readAllLines(getResultFileProp().get()).forEach(item -> {
                    HBox row = generateTableEntry(new Function(item.substring(0, item.indexOf(",")).trim(), item.substring(item.indexOf(",") + 1).trim()));
                    vBox_dataTable.getChildren().add(row);
                });
            } catch (IOException e) {
            }


        });


        vBox_dataTable.getChildren().addListener((ListChangeListener.Change<? extends Node> nodes) -> {
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

        ObservableList<IntegralOption> integralOptionObservableList = FXCollections.observableArrayList(IntegralOption.values());
        this.choiceBox_integralOption.setItems(integralOptionObservableList);
        this.choiceBox_integralOption.getSelectionModel().select(IntegralOption.NONE);
        this.choiceBox_integralOption.setConverter(super.converter.getIntegralOptionStringConverter());
        this.choiceBox_integralOption.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            toggleVisibility(newValue.isShowIntegral(), label_integralName, textField_integralName);
            toggleVisibility(newValue.isShowIntegral(), label_function1, choiceBox_function1);
            toggleVisibility(newValue.isShowIntegral(), label_RangeFrom, textField_rangeFrom);
            toggleVisibility(newValue.isShowIntegral(), label_RangeTo, textField_rangeTo);
            toggleVisibility(newValue.equals(IntegralOption.FUNCTION), label_function2, choiceBox_function2);

            if (newValue.equals(IntegralOption.XAXIS)) integral2.set("y=0");
            if (newValue.equals(IntegralOption.FUNCTION)) integral2.set("");

            integralOptionBuilder();
        });

        this.rangeFrom.addListener((observable, oldValue, newValue) -> {
            this.textField_rangeFrom.setText(newValue + "");
        });
        this.rangeTo.addListener((observable, oldValue, newValue) -> {
            this.textField_rangeTo.setText(newValue + "");
        });

        choiceBox_function1.setItems(functionList);
        choiceBox_function2.setItems(functionList);


        StringConverter<Function> converter = new StringConverter<Function>() {
            Function f;

            @Override
            public String toString(Function function) {
                f = function;
                return function.toString();
            }

            @Override
            public Function fromString(String string) {
                return f;
            }
        };

        choiceBox_function1.setConverter(converter);
        choiceBox_function2.setConverter(converter);

        choiceBox_function1.setOnAction(event -> {
            try {
                integral1.set(choiceBox_function1.getSelectionModel().getSelectedItem().getFunction());
            } catch (NullPointerException e) {

            }
            integralOptionBuilder();

        });


        choiceBox_function2.setOnAction(event -> {
            integral2.set(choiceBox_function2.getSelectionModel().getSelectedItem().getFunction());
            integralOptionBuilder();
        });


        textField_rangeFrom.focusedProperty().addListener(inv -> {
            integralOptionBuilder();
        });
        textField_rangeTo.focusedProperty().addListener(inv -> {
            integralOptionBuilder();
        });


        integralName.bind(textField_integralName.textProperty());
    }

    /**
     * Will initiate the fourth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage4() {
        radioButton_scalingPi.selectedProperty().addListener(args -> {
            integralOptionBuilder();
        });
        radioButton_scalingDecimal.selectedProperty().addListener(args -> {
            integralOptionBuilder();
        });

        super.initAxisFieldListeners();
        super.toggleAxesRanges(true);

//        guiSvgOptions.piProperty().bindBidirectional(radioButton_scalingPi.selectedProperty());
        radioButton_scalingPi.selectedProperty().addListener((observable, oldValue, newValue) -> {
            guiSvgOptions.setPi(newValue);
        });

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

    private void initFieldListenersForFunctionPreview() {
        super.initFieldListenersForPreview();
        this.textFieldUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions,
                this.textField_integralName, this.textField_rangeFrom, this.textField_rangeTo);
        this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions,
                this.choiceBox_function2, this.choiceBox_function1);
    }

    private void integralOptionBuilder() {
        int f1 = choiceBox_function1.getSelectionModel().getSelectedIndex();
        int f2 = choiceBox_integralOption.getSelectionModel().getSelectedItem().equals(IntegralOption.FUNCTION) ? choiceBox_function2.getSelectionModel().getSelectedIndex() : -1;
        IntegralOption integralOption = choiceBox_integralOption.getSelectionModel().getSelectedItem();
        double from = integralOption.getDefaultXRange().getFrom();
        double to = integralOption.getDefaultXRange().getTo();

        if (integralOption.isShowIntegral()) {
            if (!textField_rangeFrom.getText().isEmpty())
                from = Double.parseDouble(textField_rangeFrom.getText().replaceAll(",", "."));
            if (!textField_rangeTo.getText().isEmpty())
                to = Double.parseDouble(textField_rangeTo.getText().replaceAll(",", "."));
        }

        String name = textField_integralName.getText();


        ObservableList<Function> functions = FXCollections.observableArrayList();

        functionList.forEach(func -> functions.add(func));
        guiSvgOptions.getFunctions().clear();

        guiSvgOptions.getFunctions().addAll(functions);

        if (!functions.isEmpty()) {
            this.integralPlotSettings = new IntegralPlotSettings(f1, f2, name, new Range(from, to));
            guiSvgOptions.setIntegral(this.integralPlotSettings);
            this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);
        }
    }

    public ObservableList<Function> getFunctionList() {
        return functionList;
    }


    private HBox generateTableEntry(Function function) {

        Function f = new Function(function.getTitle(), function.getFunction());

        HBox row = new HBox();

        row.getStyleClass().add("data-row");
        row.setSpacing(5);
        row.setUserData(f);

        if ((f.getTitle().isEmpty() && f.getFunction().isEmpty()))
            functionList.remove(row.getUserData());

        TextField titleField = new TextField(function.getTitle());
        titleField.getStyleClass().add("data-cell");
        titleField.getStyleClass().add("data-cell-x");

        titleField.setPromptText(this.bundle.getString("function_add_function_title"));

        TextField functionField = new TextField(function.getFunction());
        functionField.getStyleClass().add("data-cell");
        functionField.getStyleClass().add("data-cell-y");
        functionField.setPromptText(this.bundle.getString("function_add_function_prompt_text"));

        InvalidationListener invalidationListener = args -> {
            functionList.remove(row.getUserData());

            if (!functionField.getText().trim().isEmpty()) {
                Function func = new Function(titleField.getText(), functionField.getText());
                row.setUserData(func);
                functionList.add(func);
                renderFunctions();
                integralOptionBuilder();
            }
        };

        titleField.focusedProperty().addListener(invalidationListener);
        functionField.focusedProperty().addListener(invalidationListener);

        Glyph closeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CLOSE);
        Button removeButton = new Button();
        removeButton.setTooltip(new Tooltip(this.bundle.getString("function_remove_function")));

        removeButton.setGraphic(closeGlyph);
        removeButton.getStyleClass().add("data-cell-button");
        removeButton.setOnAction(event -> {
            vBox_dataTable.getChildren().remove(row);
            renderFunctions();
        });

        row.getChildren().addAll(titleField, functionField, removeButton);
        row.setFocusTraversable(true);
        row.setAccessibleRole(AccessibleRole.TABLE_ROW);
        row.setAccessibleText(this.bundle.getString("function_row") + (vBox_dataTable.getChildren().size() + 1));


        HBox.setHgrow(functionField, Priority.ALWAYS);
        HBox.setHgrow(titleField, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.NEVER);

        return row;
    }

    private void renderFunctions() {
        choiceBox_function1.setItems(functionList);
        if (!functionList.isEmpty())
            choiceBox_function1.getSelectionModel().select(functionList.stream().findFirst().get());
        choiceBox_function2.setItems(functionList);
    }


    /**
     * @author Emma Müller
     */
    private void initFunctionOptionListeners() {
        super.initOptionListeners();
        this.guiSvgOptions.piProperty().addListener((observable, oldValue, newValue) -> {
            this.radioButton_scalingPi.selectedProperty().set(newValue);
        });
        this.guiSvgOptions.getFunctions().addListener(new ListChangeListener<Function>() {
            @Override
            public void onChanged(final Change<? extends Function> c) {
                if (!functionList.containsAll(guiSvgOptions.getFunctions())) {
                    vBox_dataTable.getChildren().clear();
                    guiSvgOptions.getFunctions().forEach(function -> {
                        HBox row = generateTableEntry(function);
                        vBox_dataTable.getChildren().add(row);
                    });
                }
            }
        });
        this.guiSvgOptions.integralProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(integralPlotSettings)) {
                IntegralOption integralOption = IntegralOption.NONE;
                if (!newValue.xRange.equals(IntegralOption.NONE.getDefaultXRange())) {
                    choiceBox_function1.getSelectionModel().select(functionList.get(newValue.function1));
                    if (newValue.function2 == -1) {
                        integralOption = IntegralOption.XAXIS;
                    } else {
                        integralOption = IntegralOption.FUNCTION;
                    }
                }
                choiceBox_integralOption.getSelectionModel().select(integralOption);
                if (integralOption.equals(IntegralOption.FUNCTION)) {
                    choiceBox_function2.getSelectionModel().select(functionList.get(newValue.function2));
                }

                rangeFrom.set(newValue.xRange.getFrom());
                rangeTo.set(newValue.xRange.getTo());
            }
        });
    }
}
