package application.controller.wizard.chart;

import application.controller.wizard.SVGWizardController;
import application.model.LinePointsOption;
import application.model.SortOrder;
import application.model.TrendlineAlgorithm;
import application.model.VisibilityOfDataPoints;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.styles.BarAccumulationStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class ChartWizardFrameController extends SVGWizardController {

    private static final int AMOUNTOFSTAGES = 6;

    /*Begin: FXML Nodes*/

    /* stage 1 */
    @FXML
    private GridPane stage1;
    @FXML
    public ChoiceBox<DiagramType> choiceBox_DiagramType;

    /* stage 2*/
    @FXML
    private GridPane stage2;

    /* stage 3 */
    @FXML
    private GridPane stage3;
    @FXML
    public Label label_baraccumulation;
    @FXML
    public ChoiceBox<BarAccumulationStyle> choiceBox_baraccumulation;
    @FXML
    public Label label_sorting;
    @FXML
    public ChoiceBox<SortingType> choiceBox_sorting;
    @FXML
    public Label label_sortOrder;
    @FXML
    public ChoiceBox<SortOrder> choicebox_sortOrder;
    @FXML
    public Label label_linepoints;
    @FXML
    public ChoiceBox<LinePointsOption> choiceBox_linepoints;
    @FXML
    private Label label_trendline;
    @FXML
    private ChoiceBox<TrendlineAlgorithm> choiceBox_trendline;
    @FXML
    public Label label_trendline_alpha;
    @FXML
    public TextField textField_trendline_alpha;
    @FXML
    public Label label_trendline_forecast;
    @FXML
    public TextField textField_trendline_forecast;
    @FXML
    public Label label_trendline_n;
    @FXML
    public TextField textField_trendline_n;
    @FXML
    public Label label_originalPoints;
    @FXML
    public ChoiceBox<VisibilityOfDataPoints> choiceBox_originalPoints;

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

    public ChartWizardFrameController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES);
        this.initiateAllStages();
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
        this.choiceBox_DiagramType.setItems(diagramTypeObservableList);
        // i18n
        this.choiceBox_DiagramType.setConverter(svgOptionsUtil.getDiagramTypeStringConverter());
        this.choiceBox_DiagramType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DiagramType>() {

            @Override
            public void changed(ObservableValue<? extends DiagramType> observable, DiagramType oldValue, DiagramType newValue) {
                svgPlotOptions.setDiagramType(newValue);
                toggleBarChartOptions(newValue == DiagramType.BarChart);
                toggleLineChartOptions(newValue == DiagramType.LineChart);
                toggleScatterPlotOptions(newValue == DiagramType.ScatterPlot);
            }

        });
        this.choiceBox_DiagramType.getSelectionModel().select(0);
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
        this.choiceBox_baraccumulation.setValue(this.svgPlotOptions.getBarAccumulationStyle());
        this.choiceBox_baraccumulation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BarAccumulationStyle>() {
            @Override
            public void changed(ObservableValue<? extends BarAccumulationStyle> observable, BarAccumulationStyle oldValue, BarAccumulationStyle newValue) {
                svgPlotOptions.setBarAccumulationStyle(newValue);
            }
        });
        // sorting type
        ObservableList<SortingType> sortingTypeObservableList = FXCollections.observableArrayList(SortingType.values());
        this.choiceBox_sorting.setItems(sortingTypeObservableList);
        this.choiceBox_sorting.setConverter(this.svgOptionsUtil.getSortingTypeStringConverter());
        this.choiceBox_sorting.getSelectionModel().select(SortingType.None);
        this.choiceBox_sorting.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SortingType>() {
            @Override
            public void changed(ObservableValue<? extends SortingType> observable, SortingType oldValue, SortingType newValue) {
                svgPlotOptions.setSortingType(newValue);

                toggleVisibility(newValue != SortingType.None, label_sortOrder, choicebox_sortOrder);
                if (newValue == SortingType.None) {
                    choicebox_sortOrder.getSelectionModel().select(SortOrder.ASC);
                }
            }
        });

        // sort desc
        ObservableList<SortOrder> sortOrderObservableList = FXCollections.observableArrayList(SortOrder.values());
        this.choicebox_sortOrder.setItems(sortOrderObservableList);
        this.choicebox_sortOrder.setConverter(this.svgOptionsUtil.getSortOrderStringConverter());
        this.choicebox_sortOrder.getSelectionModel().select(SortOrder.ASC);
        this.choicebox_sortOrder.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SortOrder>() {
            @Override
            public void changed(ObservableValue<? extends SortOrder> observable, SortOrder oldValue, SortOrder newValue) {
                svgPlotOptions.setSortDescending(newValue.equals(SortOrder.DESC));
            }
        });

        // line points
        String showLinePoints = this.svgPlotOptions.getShowLinePoints();
        ObservableList<LinePointsOption> linePointsOptionObservableList = FXCollections.observableArrayList(LinePointsOption.values());
        this.choiceBox_linepoints.setItems(linePointsOptionObservableList);
        this.choiceBox_linepoints.setConverter(svgOptionsUtil.getLinePointsOptionStringConverter());
        LinePointsOption selected = showLinePoints != null && showLinePoints.equals("on") ? LinePointsOption.ShowWithBorder : LinePointsOption.Hide;
        if (this.svgPlotOptions.isPointsBorderless()) {
            selected = LinePointsOption.ShowBorderless;
        }
        this.choiceBox_linepoints.getSelectionModel().select(selected);
        this.choiceBox_linepoints.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setPointsBorderless(newValue.isPointsborderless());
            svgPlotOptions.setShowLinePoints(newValue.getShowLinePoints());
        });

        // trendline and hide original points
        ObservableList<String> trendline = FXCollections.observableArrayList();
        this.textField_trendline_n.textProperty().addListener((observable, oldValue, newValue) -> {
            trendline.add(1, newValue);
            svgPlotOptions.setTrendLine(trendline);
        });
        this.textField_trendline_alpha.textProperty().addListener((observable, oldValue, newValue) -> {
            trendline.add(1, newValue);
            svgPlotOptions.setTrendLine(trendline);
        });
        this.textField_trendline_forecast.textProperty().addListener((observable, oldValue, newValue) -> {
            trendline.add(2, newValue);
            svgPlotOptions.setTrendLine(trendline);
        });
        ObservableList<VisibilityOfDataPoints> visibilityOfDataPointsObservableList = FXCollections.observableArrayList(VisibilityOfDataPoints.values());
        this.choiceBox_originalPoints.setItems(visibilityOfDataPointsObservableList);
        this.choiceBox_originalPoints.setConverter(this.svgOptionsUtil.getVisibilityOfDataPointsStringConverter());
        this.choiceBox_originalPoints.getSelectionModel().select(VisibilityOfDataPoints.SHOW);
        this.choiceBox_originalPoints.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.svgPlotOptions.setHideOriginalPoints(newValue == VisibilityOfDataPoints.HIDE);
        });
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections.observableArrayList(TrendlineAlgorithm.values());
        this.choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        this.choiceBox_trendline.setConverter(this.svgOptionsUtil.getTrendlineAlgorithmStringConverter());
        this.choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
        this.choiceBox_trendline.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TrendlineAlgorithm>() {
            @Override
            public void changed(ObservableValue<? extends TrendlineAlgorithm> observable, TrendlineAlgorithm oldValue, TrendlineAlgorithm newValue) {
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
                svgPlotOptions.setTrendLine(trendline);

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
        toggleVisibility(show, label_baraccumulation, choiceBox_baraccumulation);
        toggleVisibility(show, label_sorting, choiceBox_sorting);
        if (!show) {
            hide(label_sortOrder, choicebox_sortOrder);
        }
    }

    /**
     * Show/Hides labels and input fields of LineChart Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleLineChartOptions(final boolean show) {
        toggleVisibility(show, label_linepoints, choiceBox_linepoints);
    }

    /**
     * Show/Hides labels and input fields of ScatterPlot Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleScatterPlotOptions(final boolean show) {
        if (show) {
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
}
