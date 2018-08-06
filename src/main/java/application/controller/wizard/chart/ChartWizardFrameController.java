package application.controller.wizard.chart;

import application.controller.wizard.SVGWizardController;
import application.model.Options.LinePointsOption;
import application.model.Options.SortOrder;
import application.model.Options.TrendlineAlgorithm;
import application.model.Options.VisibilityOfDataPoints;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.plotting.line.LineStyle;
import tud.tangram.svgplot.plotting.point.PointSymbol;
import tud.tangram.svgplot.plotting.texture.Texture;
import tud.tangram.svgplot.styles.BarAccumulationStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class ChartWizardFrameController extends SVGWizardController {
    private static final Logger logger = LoggerFactory.getLogger(ChartWizardFrameController.class);

    private static final int AMOUNTOFSTAGES = 6;

    /*Begin: FXML Nodes*/

    /* stage 1 */
    @FXML
    private GridPane stage1;
    @FXML
    public ChoiceBox<DiagramType> choiceBox_diagramType;

    /* stage 2*/
    @FXML
    private GridPane stage2;

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
    private Label label_secondTexture;
    @FXML
    private ChoiceBox<Texture> choiceBox_secondTexture;
    @FXML
    private Label label_thirdTexture;
    @FXML
    private ChoiceBox<Texture> choiceBox_thirdTexture;
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
    private Label label_secondLineStyle;
    @FXML
    private ChoiceBox<LineStyle> choiceBox_secondLineStyle;
    @FXML
    private Label label_thirdLineStyle;
    @FXML
    private ChoiceBox<LineStyle> choiceBox_thirdLineStyle;
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
    @FXML
    private Label label_pointSymbols_lineChart;
    @FXML
    private CheckComboBox<PointSymbol> checkComboBox_pointSymbols_lineChart;
    @FXML
    private Label label_pointSymbols_scatterPlot;
    @FXML
    private CheckComboBox<PointSymbol> checkComboBox_pointSymbols_scatterPlot;


    /* stage 4 */
    @FXML
    private GridPane stage4;


    /* stage 5 */
    @FXML
    private GridPane stage5;

    /* stage 6 */
    @FXML
    private GridPane stage6;

    private ObservableList<Texture> textures;
    private ObservableList<LineStyle> lineStyles;
    private ObservableList<PointSymbol> customPointSymbols_scatterPlott;
    private ObservableList<PointSymbol> customPointSymbols_lineChart;
    /*End: FXML Nodes*/

    public ChartWizardFrameController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES);
        this.initiateAllStages();
        this.initOptionListeners();
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

        // diagram type
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        diagramTypeObservableList.remove(DiagramType.FunctionPlot);
        this.choiceBox_diagramType.setItems(diagramTypeObservableList);
        // i18n
        this.choiceBox_diagramType.setConverter(this.svgOptionsUtil.getDiagramTypeStringConverter());
        this.choiceBox_diagramType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            guiSvgOptions.setDiagramType(newValue);
            toggleBarChartOptions(newValue == DiagramType.BarChart);
            toggleLineChartOptions(newValue == DiagramType.LineChart);
            toggleScatterPlotOptions(newValue == DiagramType.ScatterPlot);
        });
        this.choiceBox_diagramType.getSelectionModel().select(0);
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
        // baraccumulation
        ObservableList<BarAccumulationStyle> csvOrientationObservableList = FXCollections.observableArrayList(BarAccumulationStyle.values());
        this.choiceBox_baraccumulation.setItems(csvOrientationObservableList);
        this.choiceBox_baraccumulation.setConverter(this.svgOptionsUtil.getBarAccumulationStyleStringConverter());
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
        ObservableList<Texture> textureObservableList = FXCollections.observableArrayList(Texture.values());
        this.choiceBox_firstTexture.setItems(textureObservableList);
        this.choiceBox_firstTexture.setConverter(this.svgOptionsUtil.getTextureStringConverter());
        this.choiceBox_firstTexture.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.textures.set(0, newValue);
        });
        this.choiceBox_secondTexture.setItems(textureObservableList);
        this.choiceBox_secondTexture.setConverter(this.svgOptionsUtil.getTextureStringConverter());
        this.choiceBox_secondTexture.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.textures.set(1, newValue);
        });
        this.choiceBox_thirdTexture.setItems(textureObservableList);
        this.choiceBox_thirdTexture.setConverter(this.svgOptionsUtil.getTextureStringConverter());
        this.choiceBox_thirdTexture.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.textures.set(2, newValue);
        });

        // sorting type
        ObservableList<SortingType> sortingTypeObservableList = FXCollections.observableArrayList(SortingType.values());
        this.choiceBox_sorting.setItems(sortingTypeObservableList);
        this.choiceBox_sorting.setConverter(this.svgOptionsUtil.getSortingTypeStringConverter());
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
        this.choicebox_sortOrder.setConverter(this.svgOptionsUtil.getSortOrderStringConverter());
        this.choicebox_sortOrder.getSelectionModel().select(SortOrder.ASC);
        this.choicebox_sortOrder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            guiSvgOptions.setSortOrder(newValue);
        });

        // line points
        ObservableList<LinePointsOption> linePointsOptionObservableList = FXCollections.observableArrayList(LinePointsOption.values());
        this.choiceBox_linepoints.setItems(linePointsOptionObservableList);
        this.choiceBox_linepoints.setConverter(svgOptionsUtil.getLinePointsOptionStringConverter());
        this.choiceBox_linepoints.getSelectionModel().select(this.guiSvgOptions.getLinePointsOption());
        this.choiceBox_linepoints.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            guiSvgOptions.setLinePointsOption(newValue);
            switch (newValue){
                case Hide:
                    hide(label_pointSymbols_lineChart, checkComboBox_pointSymbols_lineChart);
                    break;
                default:
                    show(label_pointSymbols_lineChart, checkComboBox_pointSymbols_lineChart);
                    break;
            }
        });

        this.customPointSymbols_lineChart = guiSvgOptions.getPointSymbols();
        ObservableList<PointSymbol> pointSymbolObservableList = FXCollections.observableArrayList(PointSymbol.getOrdered());
        this.checkComboBox_pointSymbols_lineChart.getItems().addAll(pointSymbolObservableList);
        this.checkComboBox_pointSymbols_lineChart.setConverter(this.svgOptionsUtil.getPointSymbolStringConverter());
        this.checkComboBox_pointSymbols_lineChart.getCheckModel().getCheckedItems().addListener(new ListChangeListener<PointSymbol>() {
            public void onChanged(ListChangeListener.Change<? extends PointSymbol> ps) {
                changePointSymbols(customPointSymbols_lineChart, checkComboBox_pointSymbols_lineChart, pointSymbolObservableList);
            }
        });

        this.lineStyles = guiSvgOptions.getLineStyles();
        this.lineStyles.addListener(new ListChangeListener<LineStyle>() {
            @Override
            public void onChanged(final Change<? extends LineStyle> c) {
                guiSvgOptions.setLineStyles(lineStyles);
            }
        });
        LineStyle[] lineStylesArray = LineStyle.getByOutputDeviceOrderedById(this.guiSvgOptions.getOutputDevice());
        ObservableList<LineStyle> lineStyleObservableList = FXCollections.observableArrayList(lineStylesArray);
        this.choiceBox_firstLineStyle.setItems(lineStyleObservableList);
        this.choiceBox_firstLineStyle.setConverter(this.svgOptionsUtil.getLineStyleStringConverter());
        this.choiceBox_firstLineStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (this.lineStyles.get(0) != newValue && newValue != null) {
                this.lineStyles.set(0, newValue);
            }
        });
        this.choiceBox_secondLineStyle.setItems(lineStyleObservableList);
        this.choiceBox_secondLineStyle.setConverter(this.svgOptionsUtil.getLineStyleStringConverter());
        this.choiceBox_secondLineStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (this.lineStyles.get(1) != newValue && newValue != null) {
                this.lineStyles.set(1, newValue);
            }
        });
        this.choiceBox_thirdLineStyle.setItems(lineStyleObservableList);
        this.choiceBox_thirdLineStyle.setConverter(this.svgOptionsUtil.getLineStyleStringConverter());
        this.choiceBox_thirdLineStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (this.lineStyles.get(2) != newValue && newValue != null){
                this.lineStyles.set(2, newValue);
            }
        });

        // trendline and hide original points
        ObservableList<String> trendline = FXCollections.observableArrayList();
        trendline.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(final Change<? extends String> c) {
                guiSvgOptions.setTrendLine(trendline);
            }
        });
        this.textField_trendline_n.textProperty().addListener((observable, oldValue, newValue) -> {
            setValueToIndex(trendline, 1, newValue);
        });
        this.textField_trendline_alpha.textProperty().addListener((observable, oldValue, newValue) -> {
            setValueToIndex(trendline, 1, newValue);
        });
        this.textField_trendline_forecast.textProperty().addListener((observable, oldValue, newValue) -> {
            setValueToIndex(trendline, 2, newValue);
        });
        ObservableList<VisibilityOfDataPoints> visibilityOfDataPointsObservableList = FXCollections.observableArrayList(VisibilityOfDataPoints.values());
        this.choiceBox_originalPoints.setItems(visibilityOfDataPointsObservableList);
        this.choiceBox_originalPoints.setConverter(this.svgOptionsUtil.getVisibilityOfDataPointsStringConverter());
        this.choiceBox_originalPoints.getSelectionModel().select(VisibilityOfDataPoints.SHOW);
        this.choiceBox_originalPoints.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setHideOriginalPoints(newValue);
        });
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections.observableArrayList(TrendlineAlgorithm.values());
        this.choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        this.choiceBox_trendline.setConverter(this.svgOptionsUtil.getTrendlineAlgorithmStringConverter());
        this.choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
        this.choiceBox_trendline.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            trendline.clear();
            trendline.add(0, newValue.toString());
            switch (newValue) {
                case MovingAverage:
                    show(label_trendline_n, textField_trendline_n);
                    show(label_originalPoints, choiceBox_originalPoints);
                    hide(label_trendline_alpha, textField_trendline_alpha);
                    hide(label_trendline_forecast, textField_trendline_forecast);
                    // default n
                    textField_trendline_n.setText("1");
                    break;
                case BrownLES:
                    show(label_trendline_alpha, textField_trendline_alpha);
                    show(label_trendline_forecast, textField_trendline_forecast);
                    show(label_originalPoints, choiceBox_originalPoints);
                    hide(label_trendline_n, textField_trendline_n);
                    // default alpha
                    textField_trendline_alpha.setText("0.0");
                    // default forecast
                    textField_trendline_forecast.setText("1");
                    break;
                case ExponentialSmoothing:
                    show(label_trendline_alpha, textField_trendline_alpha);
                    show(label_originalPoints, choiceBox_originalPoints);
                    hide(label_trendline_forecast, textField_trendline_forecast);
                    hide(label_trendline_n, textField_trendline_n);
                    // default alpha
                    textField_trendline_alpha.setText("0.0");
                    break;
                case LinearRegression:
                    show(label_originalPoints, choiceBox_originalPoints);
                    hide(label_trendline_alpha, textField_trendline_alpha);
                    hide(label_trendline_forecast, textField_trendline_forecast);
                    hide(label_trendline_n, textField_trendline_n);
                    break;
                case None:
                    hide(label_trendline_alpha, textField_trendline_alpha);
                    hide(label_trendline_forecast, textField_trendline_forecast);
                    hide(label_trendline_n, textField_trendline_n);
                    hide(label_originalPoints, choiceBox_originalPoints);
                    trendline.clear();
                    choiceBox_originalPoints.getSelectionModel().select(VisibilityOfDataPoints.SHOW);
                    break;
            }
        });


        this.customPointSymbols_scatterPlott = guiSvgOptions.getPointSymbols();
        this.checkComboBox_pointSymbols_scatterPlot.getItems().addAll(pointSymbolObservableList);
        this.checkComboBox_pointSymbols_scatterPlot.setConverter(this.svgOptionsUtil.getPointSymbolStringConverter());
        this.checkComboBox_pointSymbols_scatterPlot.getCheckModel().getCheckedItems().addListener(new ListChangeListener<PointSymbol>() {
            public void onChanged(ListChangeListener.Change<? extends PointSymbol> ps) {
                changePointSymbols(customPointSymbols_scatterPlott, checkComboBox_pointSymbols_scatterPlot, pointSymbolObservableList);
            }
        });
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

    /**
     * Show/Hides labels and input fields of BarChart Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleBarChartOptions(final boolean show) {
        this.toggleVisibility(show, this.label_baraccumulation, this.choiceBox_baraccumulation);
        this.toggleVisibility(show, this.label_sorting, this.choiceBox_sorting);
        this.toggleVisibility(show, this.label_firstTexture, this.choiceBox_firstTexture);
        this.toggleVisibility(show, this.label_secondTexture, this.choiceBox_secondTexture);
        this.toggleVisibility(show, this.label_thirdTexture, this.choiceBox_thirdTexture);
        if (!show) {
            this.hide(this.label_sortOrder, this.choicebox_sortOrder);
        }
    }

    /**
     * Show/Hides labels and input fields of LineChart Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleLineChartOptions(final boolean show) {
        toggleVisibility(show, label_linepoints, choiceBox_linepoints);
        toggleVisibility(show, label_firstLineStyle, choiceBox_firstLineStyle);
        toggleVisibility(show, label_secondLineStyle, choiceBox_secondLineStyle);
        toggleVisibility(show, label_thirdLineStyle, choiceBox_thirdLineStyle);
        if (!show){
            hide(label_pointSymbols_lineChart, checkComboBox_pointSymbols_lineChart);
        }else if (this.guiSvgOptions.getLinePointsOption().isShowLinePoints()){
            show(label_pointSymbols_lineChart, checkComboBox_pointSymbols_lineChart);
        }
    }

    /**
     * Show/Hides labels and input fields of ScatterPlot Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleScatterPlotOptions(final boolean show) {
        if (show) {
            show(label_pointSymbols_scatterPlot, checkComboBox_pointSymbols_scatterPlot);
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
            hide(label_pointSymbols_scatterPlot, checkComboBox_pointSymbols_scatterPlot);
        }
    }

    private void initOptionListeners() {
        this.guiSvgOptions.textureProperty().addListener(new ListChangeListener<Texture>() {
            @Override
            public void onChanged(final Change<? extends Texture> c) {
                choiceBox_firstTexture.getSelectionModel().select(guiSvgOptions.getTextures().get(0));
                choiceBox_secondTexture.getSelectionModel().select(guiSvgOptions.getTextures().get(1));
                choiceBox_thirdTexture.getSelectionModel().select(guiSvgOptions.getTextures().get(2));
            }
        });
        this.guiSvgOptions.getLineStyles().addListener(new ListChangeListener<LineStyle>() {
            @Override
            public void onChanged(final Change<? extends LineStyle> c) {
                if(!guiSvgOptions.getLineStyles().isEmpty() && guiSvgOptions.getLineStyles().size() >= 3) {
                    LineStyle[] lineStylesArray = LineStyle.getByOutputDeviceOrderedById(guiSvgOptions.getOutputDevice());
                    ObservableList<LineStyle> lineStyleObservableList = FXCollections.observableArrayList(lineStylesArray);

                    choiceBox_firstLineStyle.setItems(lineStyleObservableList);
                    choiceBox_firstLineStyle.getSelectionModel().select(guiSvgOptions.getLineStyles().get(0));
                    choiceBox_secondLineStyle.setItems(lineStyleObservableList);
                    choiceBox_secondLineStyle.getSelectionModel().select(guiSvgOptions.getLineStyles().get(1));
                    choiceBox_thirdLineStyle.setItems(lineStyleObservableList);
                    choiceBox_thirdLineStyle.getSelectionModel().select(guiSvgOptions.getLineStyles().get(2));
                }
            }
        });
        this.guiSvgOptions.getPointSymbols().addListener(new ListChangeListener<PointSymbol>() {
            @Override
            public void onChanged(final Change<? extends PointSymbol> c) {

            }
        });
    }


    private void setValueToIndex(ObservableList<String> trendline, int index, String newValue){
        if (trendline.size() > index){
            trendline.set(index, newValue);
        } else {
            trendline.add(index, newValue);
        }
    }

    private void changePointSymbols(ObservableList<PointSymbol> checkedPointSymbols, CheckComboBox<PointSymbol> checkComboBox, ObservableList<PointSymbol> allPointSymbols) {
        checkedPointSymbols.clear();
        ObservableList<PointSymbol> checkedItems = checkComboBox.getCheckModel().getCheckedItems();
        ObservableList<PointSymbol> newItems =
                checkedItems.filtered(pointSymbol -> !checkedPointSymbols.contains(pointSymbol));
        ObservableList<PointSymbol> oldItems =
                allPointSymbols.filtered(pointSymbol -> !checkedItems.contains(pointSymbol) && checkedPointSymbols.contains(pointSymbol));
        if (newItems.size() > 0) {
            for (PointSymbol pointSymbol : newItems) {
                checkedPointSymbols.add(pointSymbol);
            }
        }
        if (oldItems.size() > 0) {
            for (PointSymbol pointSymbol : oldItems) {
                checkedPointSymbols.remove(pointSymbol);
            }
        }
        guiSvgOptions.setPointSymbols(checkedPointSymbols);
    }

}
