package application.controller.wizard.chart;

import application.controller.wizard.SVGWizardController;
import application.model.Options.*;
import application.util.KeyValuePair;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.line.LineStyle;
import tud.tangram.svgplot.plotting.point.PointSymbol;
import tud.tangram.svgplot.plotting.texture.Texture;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.Color;

import java.net.URL;
import java.util.*;

/**
 * The controller for chart-wizard.
 *
 * @author Emma Müller
 */
public class ChartWizardFrameController extends SVGWizardController {
    private static final Logger logger = LoggerFactory.getLogger(ChartWizardFrameController.class);

    private static final int AMOUNTOFSTAGES = 6;
    private static final int COLOR_ROW = 4;
    private static final int POINTSYMBOL_ROW = 4;

    // TODO: Implement like pointSymbols and colors + replace with readed number of data and set initialValue to 0
    private ObjectProperty<Integer> amountOfLineStyleInput = new SimpleObjectProperty<>(3);

    // TODO: replace with readed number of data and set initialValue to 0
    private ObjectProperty<Integer> amountOfPointSymbolInputs = new SimpleObjectProperty<>(3);

    // TODO: replace with readed number of data and set initialValue to 0
    private ObjectProperty<Integer> amountOfColorInput = new SimpleObjectProperty<>(3);


    /*Begin: FXML Nodes*/

    /* stage 1 */
    @FXML
    private GridPane stage1;
    @FXML
    public ChoiceBox<DiagramType> choiceBox_diagramType;

    /* stage 2*/
    @FXML
    private GridPane stage2;
    @FXML
    private Button button_EditDataSet;
    @FXML
    private Button button_addDataPoint;
    @FXML
    private ListView<String> listView_SetNames;


    /* stage 3 */
    @FXML
    private GridPane stage3;
    @FXML
    private Label label_baraccumulation;
    @FXML
    private ChoiceBox<BarAccumulationStyle> choiceBox_baraccumulation;
    @FXML
    private Label label_firstTexture;
    @FXML
    private ChoiceBox<Texture> choiceBox_firstTexture;
    @FXML
    private Button button_resetFirstTexture;
    @FXML
    private HBox hbox_firstTexture;
    @FXML
    private Label label_secondTexture;
    @FXML
    private ChoiceBox<Texture> choiceBox_secondTexture;
    @FXML
    private Button button_resetSecondTexture;
    @FXML
    private HBox hbox_secondTexture;
    @FXML
    private Label label_thirdTexture;
    @FXML
    private ChoiceBox<Texture> choiceBox_thirdTexture;
    @FXML
    private Button button_resetThirdTexture;
    @FXML
    private HBox hbox_thirdTexture;
    @FXML
    private Label label_sorting;
    @FXML
    private ChoiceBox<SortingType> choiceBox_sorting;
    @FXML
    private Label label_sortOrder;
    @FXML
    private ChoiceBox<SortOrder> choicebox_sortOrder;
    @FXML
    private Label label_linepoints;
    @FXML
    private ChoiceBox<LinePointsOption> choiceBox_linepoints;
    @FXML
    private Label label_firstLineStyle;
    @FXML
    private ChoiceBox<LineStyle> choiceBox_firstLineStyle;
    @FXML
    private Button button_resetFirstLineStyle;
    @FXML
    private HBox hbox_firstLineStyle;
    @FXML
    private Label label_secondLineStyle;
    @FXML
    private ChoiceBox<LineStyle> choiceBox_secondLineStyle;
    @FXML
    private Button button_resetSecondLineStyle;
    @FXML
    private HBox hbox_secondLineStyle;
    @FXML
    private Label label_thirdLineStyle;
    @FXML
    private ChoiceBox<LineStyle> choiceBox_thirdLineStyle;
    @FXML
    private Button button_resetThirdLineStyle;
    @FXML
    private HBox hbox_thirdLineStyle;
    @FXML
    private Label label_trendline;
    @FXML
    private ChoiceBox<TrendlineAlgorithm> choiceBox_trendline;
    @FXML
    private Label label_trendline_alpha;
    @FXML
    private TextField textField_trendline_alpha;
    @FXML
    private Label label_trendline_forecast;
    @FXML
    private TextField textField_trendline_forecast;
    @FXML
    private Label label_trendline_n;
    @FXML
    private TextField textField_trendline_n;
    @FXML
    private Label label_originalPoints;
    @FXML
    private ChoiceBox<VisibilityOfDataPoints> choiceBox_originalPoints;


    /* stage 4 */
    @FXML
    private GridPane stage4;
    @FXML
    public ChoiceBox<GuiAxisStyle> choicebox_dblaxes;
    @FXML
    protected TextField textField_xunit;
    @FXML
    protected TextField textField_yunit;
    @FXML
    protected ToggleGroup toggleGroup_autoScale;
    @FXML
    protected RadioButton radioBtn_autoscale;
    @FXML
    protected RadioButton radioBtn_customScale;

    /* stage 5 */
    @FXML
    private GridPane stage5;

    /* stage 6 */
    @FXML
    private GridPane stage6;
    /*End: FXML Nodes*/

    private ObservableList<Texture> textures;
    private ObservableList<LineStyle> lineStyles;

    private ObservableList<PointSymbol> customPointSymbols;
    private Map<Label, HBox> pointSymbolInputs;
    private Map<Label, PointSymbol> selectedPointSymbols;
    private List<ChoiceBox<PointSymbol>> pointSymbolChoiceBoxes;

    private ObservableList<LineStyle> lineStyleFirstObservableList;
    private ObservableList<LineStyle> lineStyleSecondObservableList;
    private ObservableList<LineStyle> lineStyleThirdObservableList;

    private ObservableList<Color> customColors;
    private Map<Label, HBox> colorInputs;
    private Map<Label, Color> selectedColors;
    private List<ChoiceBox<Color>> colorChoiceBoxes;

    private ObservableList<String> setNames;
    private HashMap<String, ArrayList<KeyValuePair>> keyMap;


    private boolean isInitial = true;

    public ChartWizardFrameController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES, null);
        this.initiateAllStages();
        this.initChartOptionListeners();
        super.initloadPreset();
        this.initFieldListenersForChartPreview();

        this.setNames = FXCollections.observableArrayList();
        this.keyMap = new HashMap<>();
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

        // diagram type
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        diagramTypeObservableList.remove(DiagramType.FunctionPlot);
        this.choiceBox_diagramType.setItems(diagramTypeObservableList);
        // i18n
        this.choiceBox_diagramType.setConverter(this.converter.getDiagramTypeStringConverter());
        this.choiceBox_diagramType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                guiSvgOptions.setDiagramType(newValue);
                toggleBarChartOptions(newValue == DiagramType.BarChart);
                toggleLineChartOptions(newValue == DiagramType.LineChart);
                toggleScatterPlotOptions(newValue == DiagramType.ScatterPlot);
            }
        });
        this.choiceBox_diagramType.getSelectionModel().select(0);
    }


    /**
     * Will initiate the second stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage2() {
        super.initCsvFieldListeners();


        button_EditDataSet.setOnAction(event -> {
            Dialog d = new TextInputDialog();
            d.setHeaderText("header");
            d.setContentText("content");
            d.initModality(Modality.APPLICATION_MODAL);
            d.showAndWait();
            if (d.getResult() != null) {
                String result = ((TextInputDialog) d).getResult();
                if (!result.isEmpty()) {
                    setNames.add(result);
                }
            }
        });
        listView_SetNames.setItems(setNames);

    }

    /**
     * Will initiate the third stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage3() {
        // baraccumulation
        ObservableList<BarAccumulationStyle> csvOrientationObservableList = FXCollections.observableArrayList(BarAccumulationStyle.values());
        this.choiceBox_baraccumulation.setItems(csvOrientationObservableList);
        this.choiceBox_baraccumulation.setConverter(this.converter.getBarAccumulationStyleStringConverter());
        this.choiceBox_baraccumulation.setValue(this.guiSvgOptions.getBarAccumulationStyle());
        this.choiceBox_baraccumulation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            guiSvgOptions.setBarAccumulationStyle(newValue);
        });

        this.textures = guiSvgOptions.getTextures();
        this.textures.addListener(new ListChangeListener<Texture>() {
            @Override
            public void onChanged(final Change<? extends Texture> c) {
                guiSvgOptions.setTextures(textures);
            }
        });
        ObservableList<Texture> textureObservableListFirstTexture = FXCollections.observableArrayList(Texture.values());
        ObservableList<Texture> textureObservableListSecondTexture = FXCollections.observableArrayList(Texture.values());
        ObservableList<Texture> textureObservableListThirdTexture = FXCollections.observableArrayList(Texture.values());
        this.choiceBox_firstTexture.setItems(textureObservableListFirstTexture);
        this.choiceBox_firstTexture.setConverter(this.converter.getTextureStringConverter());
        this.choiceBoxUtil.addNotEmptyValidationListener(this.choiceBox_firstTexture, this.label_firstTexture);
        this.choiceBox_firstTexture.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                textureObservableListSecondTexture.add(oldValue);
                textureObservableListThirdTexture.add(oldValue);
            }
            if (newValue != null) {
                textureObservableListSecondTexture.remove(newValue);
                textureObservableListThirdTexture.remove(newValue);
            }

            this.textures.set(0, newValue);
        });
        this.button_resetFirstTexture.setOnAction(event -> {
            this.textures.set(0, null);
        });
        this.choiceBox_secondTexture.setItems(textureObservableListSecondTexture);
        this.choiceBox_secondTexture.setConverter(this.converter.getTextureStringConverter());
        this.choiceBoxUtil.addNotEmptyValidationListener(this.choiceBox_secondTexture, this.label_secondTexture);
        this.choiceBox_secondTexture.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                textureObservableListFirstTexture.add(oldValue);
                textureObservableListThirdTexture.add(oldValue);
            }
            if (newValue != null) {
                textureObservableListFirstTexture.remove(newValue);
                textureObservableListThirdTexture.remove(newValue);
            }
            this.textures.set(1, newValue);
        });
        this.button_resetSecondTexture.setOnAction(event -> {
            this.textures.set(1, null);
        });
        this.choiceBox_thirdTexture.setItems(textureObservableListThirdTexture);
        this.choiceBox_thirdTexture.setConverter(this.converter.getTextureStringConverter());
        this.choiceBoxUtil.addNotEmptyValidationListener(this.choiceBox_thirdTexture, this.label_thirdTexture);
        this.choiceBox_thirdTexture.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                textureObservableListFirstTexture.add(oldValue);
                textureObservableListSecondTexture.add(oldValue);
            }
            if (newValue != null) {
                textureObservableListFirstTexture.remove(newValue);
                textureObservableListSecondTexture.remove(newValue);
            }
            this.textures.set(2, newValue);
        });
        this.button_resetThirdTexture.setOnAction(event -> {
            this.textures.set(2, null);
        });

        // sorting type
        ObservableList<SortingType> sortingTypeObservableList = FXCollections.observableArrayList(SortingType.values());
        this.choiceBox_sorting.setItems(sortingTypeObservableList);
        this.choiceBox_sorting.setConverter(this.converter.getSortingTypeStringConverter());
        this.choiceBox_sorting.getSelectionModel().select(SortingType.None);
        this.choiceBox_sorting.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            guiSvgOptions.setSortingType(newValue);

            toggleVisibility(newValue != SortingType.None, label_sortOrder, choicebox_sortOrder);
            if (newValue == SortingType.None) {
                choicebox_sortOrder.getSelectionModel().select(SortOrder.ASC);
            }
        });

        // sort desc
        ObservableList<SortOrder> sortOrderObservableList = FXCollections.observableArrayList(SortOrder.values());
        this.choicebox_sortOrder.setItems(sortOrderObservableList);
        this.choicebox_sortOrder.setConverter(this.converter.getSortOrderStringConverter());
        this.choicebox_sortOrder.getSelectionModel().select(SortOrder.ASC);
        this.choicebox_sortOrder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                guiSvgOptions.setSortOrder(newValue);
            }
        });

        // line points
        ObservableList<LinePointsOption> linePointsOptionObservableList = FXCollections.observableArrayList(LinePointsOption.values());
        this.choiceBox_linepoints.setItems(linePointsOptionObservableList);
        this.choiceBox_linepoints.setConverter(converter.getLinePointsOptionStringConverter());
        this.choiceBox_linepoints.getSelectionModel().select(this.guiSvgOptions.getLinePointsOption());
        this.choiceBox_linepoints.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                guiSvgOptions.setLinePointsOption(newValue);
                switch (newValue) {
                    case Hide:
                        pointSymbolInputs.forEach((label, hBox) -> hide(label, hBox));
                        break;
                    default:
                        pointSymbolInputs.forEach((label, hBox) -> show(label, hBox));
                        break;
                }
            }
        });
        this.selectedPointSymbols = new HashMap<>();
        this.customPointSymbols = guiSvgOptions.getPointSymbols();
        this.pointSymbolChoiceBoxes = new ArrayList<>();

        this.pointSymbolInputs = new HashMap<>();
        for (int i = 0; i < amountOfPointSymbolInputs.get(); i++) {
            this.drawPointSymbolField(pointSymbolInputs, this.customPointSymbols, i, true);
        }
        selectedPointSymbols.forEach((label, pointSymbol) -> {
            HBox hBox_scatter = pointSymbolInputs.get(label);
            if (hBox_scatter != null) {
                ChoiceBox<PointSymbol> pointSymbolChoiceBox = (ChoiceBox<PointSymbol>) hBox_scatter.getChildren().get(0);
                pointSymbolChoiceBox.getSelectionModel().select(pointSymbol);
            }
        });
        // line style
        List<LineStyle> lineStylesArray = LineStyle.getByOutputDeviceOrderedById(this.guiSvgOptions.getOutputDevice());
        lineStyleFirstObservableList = FXCollections.observableArrayList(lineStylesArray);
        lineStyleSecondObservableList = FXCollections.observableArrayList(lineStylesArray);
        lineStyleThirdObservableList = FXCollections.observableArrayList(lineStylesArray);
        lineStyles = FXCollections.observableArrayList(this.guiSvgOptions.getLineStyles());

        this.lineStyles.addListener(new ListChangeListener<LineStyle>() {
            @Override
            public void onChanged(final Change<? extends LineStyle> c) {
                if (lineStyles.size() == 3 && !lineStyles.equals(guiSvgOptions.getLineStyles())) {
                    guiSvgOptions.setLineStyles(lineStyles);
                }
            }
        });

        this.choiceBox_firstLineStyle.setItems(lineStyleFirstObservableList);
        this.choiceBox_firstLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        this.choiceBoxUtil.addNotEmptyValidationListener(this.choiceBox_firstLineStyle, this.label_firstLineStyle);
        this.choiceBox_firstLineStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean oldValueInPossibleStyles = LineStyle.getListByOutputDevice(guiSvgOptions.getOutputDevice()).contains(oldValue);
            if (oldValue != null && oldValueInPossibleStyles) {
                if (!this.lineStyleSecondObservableList.contains(oldValue))
                    this.lineStyleSecondObservableList.add(oldValue);
                if (!this.lineStyleThirdObservableList.contains(oldValue))
                    this.lineStyleThirdObservableList.add(oldValue);
            }
            if (newValue != null) {
                if (this.lineStyleSecondObservableList.contains(newValue))
                    this.lineStyleSecondObservableList.remove(newValue);
                if (this.lineStyleThirdObservableList.contains(newValue))
                    this.lineStyleThirdObservableList.remove(newValue);
            }
            try {
                lineStyles.set(0, newValue);
            } catch (IndexOutOfBoundsException e) {
                lineStyles.add(0, newValue);
            }
        });
        this.button_resetFirstLineStyle.setOnAction(event -> {
            this.choiceBox_firstLineStyle.getSelectionModel().select(null);
        });

        this.choiceBox_secondLineStyle.setItems(lineStyleSecondObservableList);
        this.choiceBox_secondLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        this.choiceBoxUtil.addNotEmptyValidationListener(this.choiceBox_secondLineStyle, this.label_secondLineStyle);
        this.choiceBox_secondLineStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                lineStyles.set(1, newValue);
            } catch (Exception e) {
                lineStyles.add(1, newValue);
            }

            boolean oldValueInPossibleStyles = LineStyle.getListByOutputDevice(guiSvgOptions.getOutputDevice()).contains(oldValue);
            if (oldValue != null && oldValueInPossibleStyles) {
                if (!this.lineStyleFirstObservableList.contains(oldValue))
                    this.lineStyleFirstObservableList.add(oldValue);
                if (!this.lineStyleThirdObservableList.contains(oldValue))
                    this.lineStyleThirdObservableList.add(oldValue);
            }
            if (newValue != null) {
                if (this.lineStyleFirstObservableList.contains(newValue))
                    this.lineStyleFirstObservableList.remove(newValue);
                if (this.lineStyleThirdObservableList.contains(newValue))
                    this.lineStyleThirdObservableList.remove(newValue);
            }
        });
        this.button_resetSecondLineStyle.setOnAction(event -> {
            this.choiceBox_secondLineStyle.getSelectionModel().select(null);
        });

        this.choiceBox_thirdLineStyle.setItems(lineStyleThirdObservableList);
        this.choiceBox_thirdLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        this.choiceBoxUtil.addNotEmptyValidationListener(this.choiceBox_thirdLineStyle, this.label_thirdLineStyle);
        this.choiceBox_thirdLineStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                lineStyles.set(2, newValue);
            } catch (Exception e) {
                lineStyles.add(2, newValue);
            }
            boolean oldValueInPossibleStyles = LineStyle.getListByOutputDevice(guiSvgOptions.getOutputDevice()).contains(oldValue);
            if (oldValue != null && oldValueInPossibleStyles) {
                if (!lineStyleFirstObservableList.contains(oldValue)) {
                    lineStyleFirstObservableList.add(oldValue);
                }
                if (!lineStyleSecondObservableList.contains(oldValue)) {
                    lineStyleSecondObservableList.add(oldValue);
                }
            }
            if (newValue != null) {
                if (lineStyleFirstObservableList.contains(newValue))
                    lineStyleFirstObservableList.remove(newValue);
                if (lineStyleSecondObservableList.contains(newValue))
                    lineStyleSecondObservableList.remove(newValue);
            }
        });
        this.button_resetThirdLineStyle.setOnAction(event -> {
            this.choiceBox_thirdLineStyle.getSelectionModel().select(null);
        });

        // trendline and hide original points
        ObservableList<String> trendline = FXCollections.observableArrayList(guiSvgOptions.getTrendLine());
        trendline.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(final Change<? extends String> c) {
                guiSvgOptions.setTrendLine(trendline);
            }
        });
        this.textFieldUtil.addIntegerValidationWithMinimum(this.textField_trendline_n, 1);
        this.textField_trendline_n.textProperty().addListener((observable, oldValue, newValue) -> {
            setValueToIndex(trendline, 1, newValue);
        });
        this.textFieldUtil.addDoubleValidationWithMinimumAndMaximum(this.textField_trendline_alpha, this.label_trendline_alpha, 0, 1);
        this.textField_trendline_alpha.textProperty().addListener((observable, oldValue, newValue) -> {
            setValueToIndex(trendline, 1, newValue);
        });
        this.textFieldUtil.addDoubleValidationWithMinimum(this.textField_trendline_forecast, this.label_trendline_forecast, 0);
        this.textField_trendline_forecast.textProperty().addListener((observable, oldValue, newValue) -> {
            setValueToIndex(trendline, 2, newValue);
        });
        ObservableList<VisibilityOfDataPoints> visibilityOfDataPointsObservableList = FXCollections.observableArrayList(VisibilityOfDataPoints.values());
        this.choiceBox_originalPoints.setItems(visibilityOfDataPointsObservableList);
        this.choiceBox_originalPoints.setConverter(this.converter.getVisibilityOfDataPointsStringConverter());
        this.choiceBox_originalPoints.getSelectionModel().select(VisibilityOfDataPoints.SHOW);
        this.choiceBox_originalPoints.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.guiSvgOptions.setHideOriginalPoints(newValue);
                pointSymbolInputs.forEach((label, hBox) -> toggleVisibility(newValue.equals(VisibilityOfDataPoints.SHOW), label, hBox));
            }

        });
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections.observableArrayList(TrendlineAlgorithm.values());
        this.choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        this.choiceBox_trendline.setConverter(this.converter.getTrendlineAlgorithmStringConverter());
        this.choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
        this.choiceBox_trendline.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                // default values
                String alpha = "0.0";
                String n = "1";
                String forecast = "1";
                if (guiSvgOptions.getTrendLine().size() > 1) {
                    alpha = guiSvgOptions.getTrendLine().get(1);
                    n = guiSvgOptions.getTrendLine().get(1);
                }
                if (guiSvgOptions.getTrendLine().size() > 2) {
                    forecast = guiSvgOptions.getTrendLine().get(2);
                }

                switch (newValue) {
                    case MovingAverage:
                        show(label_trendline_n, textField_trendline_n);
                        show(label_originalPoints, choiceBox_originalPoints);
                        hide(label_trendline_alpha, textField_trendline_alpha);
                        hide(label_trendline_forecast, textField_trendline_forecast);

                        trendline.setAll(newValue.toString(), n);
                        textField_trendline_n.setText(n);
                        break;
                    case BrownLES:
                        show(label_trendline_alpha, textField_trendline_alpha);
                        show(label_trendline_forecast, textField_trendline_forecast);
                        show(label_originalPoints, choiceBox_originalPoints);
                        hide(label_trendline_n, textField_trendline_n);

                        trendline.setAll(newValue.toString(), alpha, forecast);

                        textField_trendline_alpha.setText(alpha);
                        textField_trendline_forecast.setText(forecast);
                        break;
                    case ExponentialSmoothing:
                        show(label_trendline_alpha, textField_trendline_alpha);
                        show(label_originalPoints, choiceBox_originalPoints);
                        hide(label_trendline_forecast, textField_trendline_forecast);
                        hide(label_trendline_n, textField_trendline_n);

                        trendline.setAll(newValue.toString(), alpha);
                        textField_trendline_alpha.setText(alpha);
                        break;
                    case LinearRegression:
                        show(label_originalPoints, choiceBox_originalPoints);
                        hide(label_trendline_alpha, textField_trendline_alpha);
                        hide(label_trendline_forecast, textField_trendline_forecast);
                        hide(label_trendline_n, textField_trendline_n);
                        break;
                    default:
                        hide(label_trendline_alpha, textField_trendline_alpha);
                        hide(label_trendline_forecast, textField_trendline_forecast);
                        hide(label_trendline_n, textField_trendline_n);
                        hide(label_originalPoints, choiceBox_originalPoints);
                        trendline.clear();
                        choiceBox_originalPoints.getSelectionModel().select(VisibilityOfDataPoints.SHOW);
                        break;
                }
            }
        });
    }

    /**
     * Will initiate the fourth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage4() {
        super.initAxisFieldListeners();
        this.guiSvgOptions.setAutoScale(true);

        // x unit
        this.textField_xunit.textProperty().bindBidirectional(this.guiSvgOptions.xUnitProperty());

        // y unit
        this.textField_yunit.textProperty().bindBidirectional(this.guiSvgOptions.yUnitProperty());

        ObservableList<GuiAxisStyle> axisStyleObservableList = FXCollections.observableArrayList(GuiAxisStyle.values());
        axisStyleObservableList.remove(GuiAxisStyle.Barchart);
        this.choicebox_dblaxes.setItems(axisStyleObservableList);
        this.choicebox_dblaxes.setConverter(this.converter.getAxisStyleStringConverter());
        this.choicebox_dblaxes.getSelectionModel().select(this.guiSvgOptions.getAxisStyle());
        this.choicebox_dblaxes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.guiSvgOptions.setAxisStyle(newValue);

            }
        });


        // autoscale
        SimpleObjectProperty<Boolean> isCustomScale = new SimpleObjectProperty<>();
        this.radioBtn_autoscale.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setAutoScale(newValue);
            isCustomScale.set(!newValue);
            super.toggleAxesRanges(!newValue);
        });

        this.radioBtn_autoscale.selectedProperty().bindBidirectional(this.guiSvgOptions.autoScaleProperty());
        this.radioBtn_customScale.selectedProperty().bindBidirectional(isCustomScale);

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
        this.colorChoiceBoxes = new ArrayList<>();
        this.amountOfColorInput.addListener((observable, oldValue, newValue) -> {
            if (newValue > oldValue) {
                for (int i = oldValue; i < newValue; i++) {
                    this.drawColorField(colorInputs, this.customColors, i, this.guiSvgOptions.getOutputDevice().equals(OutputDevice.ScreenColor));
                }
            } else {
                this.stage6.getChildren().remove(4 + newValue * 2, 4 + oldValue * 2);
            }
        });

        // customColors
        this.selectedColors = new HashMap<>();
        this.customColors = guiSvgOptions.getColors();
        this.colorInputs = new HashMap<>();
        for (int i = 0; i < amountOfColorInput.get(); i++) {
            this.drawColorField(colorInputs, this.customColors, i, this.guiSvgOptions.getOutputDevice().equals(OutputDevice.ScreenColor));
        }

        selectedColors.forEach((label, color) -> {
            HBox hBox_scatter = colorInputs.get(label);
            if (hBox_scatter != null) {
                ChoiceBox<Color> colorChoiceBox = (ChoiceBox<Color>) hBox_scatter.getChildren().get(0);
                colorChoiceBox.getSelectionModel().select(color);
            }
        });

    }

    /**
     * Show/Hides labels and input fields of BarChart Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleBarChartOptions(final boolean show) {
        this.toggleVisibility(show, this.label_baraccumulation, this.choiceBox_baraccumulation);
        this.toggleVisibility(show, this.label_sorting, this.choiceBox_sorting);
        this.toggleVisibility(show, this.label_firstTexture, this.hbox_firstTexture);
        this.toggleVisibility(show, this.label_secondTexture, this.hbox_secondTexture);
        this.toggleVisibility(show, this.label_thirdTexture, this.hbox_thirdTexture);

        boolean showXRangeInputs = !this.guiSvgOptions.isAutoScale() && !show;
        this.toggleVisibility(showXRangeInputs, this.label_xfrom, this.textField_xfrom);
        this.toggleVisibility(showXRangeInputs, this.label_xto, this.textField_xto);
        if (!show) {
            this.choicebox_dblaxes.getItems().remove(GuiAxisStyle.Barchart);
            this.hide(this.label_sortOrder, this.choicebox_sortOrder);
        } else {
            this.choicebox_dblaxes.getItems().add(GuiAxisStyle.Barchart);
            if (pointSymbolInputs != null) {
                pointSymbolInputs.forEach((label, hBox) -> hide(label, hBox));
            }
        }
    }

    /**
     * Show/Hides labels and input fields of LineChart Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleLineChartOptions(final boolean show) {
        toggleVisibility(show, label_linepoints, choiceBox_linepoints);
        toggleVisibility(show, label_firstLineStyle, hbox_firstLineStyle);
        toggleVisibility(show, label_secondLineStyle, hbox_secondLineStyle);
        toggleVisibility(show, label_thirdLineStyle, hbox_thirdLineStyle);

        if (pointSymbolInputs != null) {
            if (!show || !this.guiSvgOptions.getLinePointsOption().isShowLinePoints()) {
                pointSymbolInputs.forEach((label, hBox) -> hide(label, hBox));
            } else if (this.guiSvgOptions.getLinePointsOption().isShowLinePoints()) {
                pointSymbolInputs.forEach((label, hBox) -> show(label, hBox));
            }
        }
    }

    /**
     * Show/Hides labels and input fields of ScatterPlot Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleScatterPlotOptions(final boolean show) {
        if (show) {
            if (pointSymbolInputs != null) {
                pointSymbolInputs.forEach((label, hBox) -> show(label, hBox));
            }
            show(label_trendline, choiceBox_trendline);
            // show corresponding other fields
            TrendlineAlgorithm trendlineAlgorithm = choiceBox_trendline.getValue();
            this.choiceBox_trendline.setValue(TrendlineAlgorithm.None);
            this.choiceBox_trendline.setValue(trendlineAlgorithm);
        } else {
            hide(label_trendline, choiceBox_trendline);
            hide(label_trendline_alpha, textField_trendline_alpha);
            hide(label_trendline_forecast, textField_trendline_forecast);
            hide(label_trendline_n, textField_trendline_n);
            hide(label_originalPoints, choiceBox_originalPoints);
        }

    }

    /**
     * Initiates Listeners in {@link application.model.GuiSvgOptions}
     */
    private void initChartOptionListeners() {
        super.initOptionListeners();
        this.guiSvgOptions.textureProperty().addListener(new ListChangeListener<Texture>() {
            @Override
            public void onChanged(final Change<? extends Texture> c) {
                if (guiSvgOptions.getTextures().size() == 3) {
                    choiceBox_firstTexture.getSelectionModel().select(guiSvgOptions.getTextures().get(0));
                    choiceBox_secondTexture.getSelectionModel().select(guiSvgOptions.getTextures().get(1));
                    choiceBox_thirdTexture.getSelectionModel().select(guiSvgOptions.getTextures().get(2));
                }
            }
        });

        this.guiSvgOptions.getPointSymbols().addListener(new ListChangeListener<PointSymbol>() {
            @Override
            public void onChanged(final Change<? extends PointSymbol> c) {
                for (int i = 0; i < amountOfPointSymbolInputs.get(); i++) {
                    if (i < pointSymbolChoiceBoxes.size() && i < guiSvgOptions.getPointSymbols().size()) {
                        pointSymbolChoiceBoxes.get(i).getSelectionModel().select(guiSvgOptions.getPointSymbols().get(i));
                    } else if (i >= pointSymbolChoiceBoxes.size()) {
                        drawPointSymbolField(pointSymbolInputs, customPointSymbols, i, true);
                    }
                }
            }
        });

        this.guiSvgOptions.axisStyleProperty().addListener((observable, oldValue, newValue) -> {
            this.choicebox_dblaxes.getSelectionModel().select(newValue);
        });

        this.guiSvgOptions.getLineStyles().addListener(new ListChangeListener<LineStyle>() {
            @Override
            public void onChanged(final Change<? extends LineStyle> c) {
                List<LineStyle> newLineStyles = guiSvgOptions.getLineStyles();
                if (!newLineStyles.contains(null) && !lineStyles.equals(newLineStyles)) {
                    lineStyleFirstObservableList = FXCollections.observableArrayList(newLineStyles);
                    lineStyleSecondObservableList = FXCollections.observableArrayList(newLineStyles);
                    lineStyleThirdObservableList = FXCollections.observableArrayList(newLineStyles);

                    // when selecting the first item of the first choicebox, it will be removed from the second and third choice box
                    choiceBox_firstLineStyle.setItems(lineStyleFirstObservableList);
                    choiceBox_firstLineStyle.getSelectionModel().select(0);
                    // therefore, you have to select the (new) first item of the second, which will then be removed from the first an third
                    choiceBox_secondLineStyle.setItems(lineStyleSecondObservableList);
                    choiceBox_secondLineStyle.getSelectionModel().select(0);
                    // equivalent here
                    choiceBox_thirdLineStyle.setItems(lineStyleThirdObservableList);
                    choiceBox_thirdLineStyle.getSelectionModel().select(0);
                }
            }
        });

        this.guiSvgOptions.getColors().addListener(new ListChangeListener<Color>() {
            @Override
            public void onChanged(Change<? extends Color> c) {
                for (int i = 0; i < amountOfColorInput.get(); i++) {
                    if (i < colorChoiceBoxes.size() && i < guiSvgOptions.getColors().size()) {
                        colorChoiceBoxes.get(i).getSelectionModel().select(guiSvgOptions.getColors().get(i));
                    } else if (i >= pointSymbolChoiceBoxes.size()) {
                        drawColorField(colorInputs, customColors, i, true);
                    }
                }
            }
        });

        this.guiSvgOptions.outputDeviceProperty().addListener((observable, oldValue, newValue) -> {
            if (colorInputs != null) {
                colorInputs.forEach((label, hBox) -> toggleVisibility(newValue.equals(OutputDevice.ScreenColor), label, hBox));
            }
        });

        this.guiSvgOptions.getTrendLine().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(final Change<? extends String> c) {
                if (guiSvgOptions.getTrendLine().size() > 0) {
                    TrendlineAlgorithm trendlineAlgorithm = TrendlineAlgorithm.fromString(guiSvgOptions.getTrendLine().get(0));
                    choiceBox_trendline.getSelectionModel().select(trendlineAlgorithm);
                }
            }
        });

        this.guiSvgOptions.linePointsOptionProperty().addListener((observable, oldValue, newValue) -> {
            this.choiceBox_linepoints.getSelectionModel().select(newValue);
        });
    }

    private void setValueToIndex(ObservableList<String> trendline, int index, String newValue) {
        if (trendline.size() - 1 >= index) {
            trendline.set(index, newValue);
        } else {
            trendline.add(index, newValue);
        }
    }

    private void initFieldListenersForChartPreview() {
        super.initFieldListenersForPreview();
        this.guiSvgOptions.diagramTypeProperty().addListener((observable, oldValue, newValue) -> {
            this.choiceBox_diagramType.getSelectionModel().select(newValue);
        });
        this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions,
                this.choiceBox_diagramType, this.choiceBox_baraccumulation, this.choiceBox_firstTexture,
                this.choiceBox_secondTexture, this.choiceBox_thirdTexture, this.choicebox_dblaxes, this.choiceBox_firstLineStyle,
                this.choiceBox_secondLineStyle, this.choiceBox_thirdLineStyle, this.choiceBox_linepoints, this.choiceBox_originalPoints,
                this.choiceBox_sorting, this.choicebox_sortOrder, this.choiceBox_trendline, this.choiceBox_csvType);
        this.textFieldUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions,
                this.textField_xunit, this.textField_yunit, this.textField_trendline_n,
                this.textField_trendline_forecast, this.textField_trendline_alpha);

        this.pointSymbolInputs.forEach((label, hBox) -> {
            ChoiceBox<PointSymbol> pointSymbolChoiceBox = (ChoiceBox<PointSymbol>) hBox.getChildren().get(0);
            this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions, pointSymbolChoiceBox);
        });
        this.colorInputs.forEach((label, hBox) -> {
            ChoiceBox<Color> colorChoiceBox = (ChoiceBox<Color>) hBox.getChildren().get(0);
            this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions, colorChoiceBox);
        });
        this.radioBtn_autoscale.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);
        });
    }

    private void drawPointSymbolField(final Map<Label, HBox> map, ObservableList<PointSymbol> observableList, final int index, final boolean visible) {

        String labelStr = (index + 1) + ". " + this.bundle.getString("label_pointSymbol");

        Label label = new Label(labelStr);
        label.setId("label_pointSymbol_" + (index + 1));
        label.setVisible(visible);

        HBox inputGroup = new HBox();
        inputGroup.setAlignment(Pos.CENTER_RIGHT);
        inputGroup.setSpacing(10.0);

        ChoiceBox<PointSymbol> pointSymbolChoiceBox = new ChoiceBox<>();
        pointSymbolChoiceBox.setId("choiceBox_pointSymbol_" + (index + 1));
        pointSymbolChoiceBox.setAccessibleHelp(labelStr);
        pointSymbolChoiceBox.setMinWidth(50.0);
        pointSymbolChoiceBox.setPrefHeight(25.0);
        pointSymbolChoiceBox.setMaxWidth(1.7976931348623157E308);
        ObservableList<PointSymbol> pointSymbolObservableList = FXCollections.observableArrayList(PointSymbol.getOrdered());
        pointSymbolChoiceBox.setItems(pointSymbolObservableList);
        pointSymbolChoiceBox.setConverter(this.converter.getPointSymbolStringConverter());

        pointSymbolChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changePointSymbols(map, observableList, index, label, newValue, oldValue);
        });
        choiceBoxUtil.addNotEmptyValidationListener(pointSymbolChoiceBox, label);

        Glyph closeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CLOSE);
        Button resetBtn = new Button();
        resetBtn.setTooltip(new Tooltip(bundle.getString("button_point_symbol_reset")));
        resetBtn.setGraphic(closeGlyph);
        resetBtn.getStyleClass().add("btn-reset");
        resetBtn.setOnAction(event -> {
            pointSymbolChoiceBox.getSelectionModel().select(null);
        });

        inputGroup.getChildren().addAll(pointSymbolChoiceBox, resetBtn);
        HBox.setHgrow(pointSymbolChoiceBox, Priority.ALWAYS);
        HBox.setHgrow(resetBtn, Priority.NEVER);
        inputGroup.setVisible(visible);
        stage3.add(label, 0, POINTSYMBOL_ROW + index);
        stage3.add(inputGroup, 1, POINTSYMBOL_ROW + index);
        map.put(label, inputGroup);
        selectedPointSymbols.put(label, guiSvgOptions.getPointSymbols().get(index));
        pointSymbolChoiceBoxes.add(pointSymbolChoiceBox);
    }


    private void drawColorField(final Map<Label, HBox> map, ObservableList<Color> observableList, final int index, final boolean visible) {

        String idStr = "label_color_" + (index + 1);

        String labelStr = (index + 1) + ". " + this.bundle.getString("color_select_text");

        Label label = new Label(labelStr);
        label.setPrefWidth(190);
        label.setId(idStr);
        label.setVisible(visible);

        HBox inputGoup = new HBox();
        inputGoup.setAlignment(Pos.CENTER_RIGHT);
        inputGoup.setSpacing(10.0);

        ChoiceBox<Color> colorChoiceBox = new ChoiceBox<>();
        colorChoiceBox.setId("choiceBox_color_" + (index + 1));
        colorChoiceBox.setAccessibleHelp(labelStr);
        colorChoiceBox.setMinWidth(50.0);
        colorChoiceBox.setPrefHeight(25.0);
        colorChoiceBox.setMaxWidth(1.7976931348623157E308);
        ObservableList<Color> colorObservableList = FXCollections.observableArrayList(Color.values());
        selectedColors.forEach((colorLabel, color) -> colorObservableList.remove(color));

        colorChoiceBox.setItems(colorObservableList);
        colorChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changeColors(map, observableList, index, label, newValue, oldValue);
        });
        choiceBoxUtil.addNotEmptyValidationListener(colorChoiceBox, label);

        Glyph closeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CLOSE);
        Button resetBtn = new Button();
        resetBtn.setTooltip(new Tooltip(bundle.getString("button_point_symbol_reset")));
        resetBtn.setGraphic(closeGlyph);
        resetBtn.getStyleClass().add("btn-reset");
        resetBtn.setOnAction(event -> {
            colorChoiceBox.getSelectionModel().select(null);
        });

        inputGoup.getChildren().addAll(colorChoiceBox, resetBtn);
        HBox.setHgrow(colorChoiceBox, Priority.ALWAYS);
        HBox.setHgrow(resetBtn, Priority.NEVER);
        inputGoup.setVisible(visible);

        stage6.add(label, 0, COLOR_ROW + index);
        stage6.add(inputGoup, 1, COLOR_ROW + index);
        map.put(label, inputGoup);

        selectedColors.put(label, Color.values()[index]);
        colorChoiceBoxes.add(colorChoiceBox);
    }

    private void changeColors(final Map<Label, HBox> map, ObservableList<Color> observableList, final int index, Label label, final Color newValue, final Color oldValue) {
        if (oldValue != null) {
            map.forEach((label1, hBox) -> {
                if (!label1.equals(label)) {
                    ChoiceBox<Color> pointSymbolChoiceBox = (ChoiceBox<Color>) hBox.getChildren().get(0);
                    pointSymbolChoiceBox.getItems().add(oldValue);
                }
            });
        }
        if (newValue != null) {
            map.forEach((label1, hBox) -> {
                if (!label1.equals(label)) {
                    ChoiceBox<Color> pointSymbolChoiceBox = (ChoiceBox<Color>) hBox.getChildren().get(0);
                    pointSymbolChoiceBox.getItems().remove(newValue);
                }
            });
        }
        if (index < observableList.size()) {
            observableList.set(index, newValue);
        }
    }

    private void changePointSymbols(final Map<Label, HBox> map, ObservableList<PointSymbol> observableList, final int index, Label label, final PointSymbol newValue, final PointSymbol oldValue) {
        if (oldValue != null) {
            map.forEach((label1, hBox) -> {
                if (!label1.equals(label)) {
                    ChoiceBox<PointSymbol> pointSymbolChoiceBox = (ChoiceBox<PointSymbol>) hBox.getChildren().get(0);
                    pointSymbolChoiceBox.getItems().add(oldValue);
                }
            });
        }
        if (newValue != null) {
            map.forEach((label1, hBox) -> {
                if (!label1.equals(label)) {
                    ChoiceBox<PointSymbol> pointSymbolChoiceBox = (ChoiceBox<PointSymbol>) hBox.getChildren().get(0);
                    pointSymbolChoiceBox.getItems().remove(newValue);
                }
            });
        }
        if (index < observableList.size()) {
            observableList.set(index, newValue);
        }
    }


    private void parseCSV() {

        switch (this.choiceBox_diagramType.getSelectionModel().getSelectedItem()) {

            case BarChart: {

                break;
            }
            default: {

            }

        }
    }


}
