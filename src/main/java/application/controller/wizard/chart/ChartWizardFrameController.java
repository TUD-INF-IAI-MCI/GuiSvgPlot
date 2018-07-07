package application.controller.wizard.chart;

import application.GuiSvgPlott;
import application.Wizard.SVGWizardController;
import application.model.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.Color;
import tud.tangram.svgplot.styles.GridStyle;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ChartWizardFrameController extends SVGWizardController {

    private static final int AMOUNTOFSTAGES = 6;

    /*Begin: FXML Nodes*/

    /* stage 1 */
    @FXML
    private GridPane stage1;
    @FXML
    public ChoiceBox<DiagramType> choiceBox_DiagramType;
    @FXML
    public TextField textField_Title;
    @FXML
    public ChoiceBox<OutputDevice> choiceBox_outputDevice;
    @FXML
    private ChoiceBox<PageSize> choiceBox_size;
    @FXML
    private Label label_customSizeWidth;
    @FXML
    private TextField textField_customSizeWidth;
    @FXML
    private Label label_customSizeHeight;
    @FXML
    private TextField textField_customSizeHeight;
    /* stage 2*/
    @FXML
    private GridPane stage2;
    @FXML
    public TextField textField_CsvPath;
    @FXML
    private Button button_CsvPath;
    @FXML
    public ChoiceBox<CsvOrientation> choiceBox_csvOrientation;
    @FXML
    public ChoiceBox<CsvType> choiceBox_csvType;

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
    //    @FXML
//    public CheckBox checkbox_linepoints;
//    @FXML
//    public Label label_pointsborderless;
//    @FXML
//    public CheckBox checkbox_pointsborderless;
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
    @FXML
    public TextField textField_xunit;
    @FXML
    public TextField textField_yunit;
    @FXML
    public RadioButton radioBtn_autoscale;
    @FXML
    public RadioButton radioBtn_customScale;
    @FXML
    public TextField textField_xfrom;
    @FXML
    public Label label_xfrom;
    @FXML
    public TextField textField_xto;
    @FXML
    public Label label_xto;
    @FXML
    public TextField textField_yfrom;
    @FXML
    public Label label_yfrom;
    @FXML
    public TextField textField_yto;
    @FXML
    public Label label_yto;

    /* stage 5 */
    @FXML
    private GridPane stage5;
    @FXML
    public ChoiceBox<GridStyle> choicebox_gridStyle;
    @FXML
    public TextField textField_xlines;
    @FXML
    public TextField textField_ylines;
    @FXML
    public ChoiceBox<AxisStyle> choicebox_dblaxes;

    /* stage 6 */
    @FXML
    private GridPane stage6;
    @FXML
    public TextField textField_cssFile;
    @FXML
    public ChoiceBox<Color> choiceBox_color1;
    @FXML
    public ChoiceBox<Color> choiceBox_color2;

    private List<String> colors;
    private Point size;

    /*End: FXML Nodes*/
    private ObjectProperty<Range> xRange;
    private ObjectProperty<Range> yRange;

    public ChartWizardFrameController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES);
        this.xRange = new SimpleObjectProperty<>();
        this.yRange = new SimpleObjectProperty<>();
        this.size = new Point(PageSize.A4.getWidth(), PageSize.A4.getHeight());
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

//        this.label_Headline.setText("");

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

        // title
        this.textField_Title.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setTitle(newValue);
        });

        // output device
        ObservableList<OutputDevice> outputDevices = FXCollections.observableArrayList(OutputDevice.values());
        this.choiceBox_outputDevice.setItems(outputDevices);
        this.choiceBox_outputDevice.setConverter(this.svgOptionsUtil.getOutputDeviceStringConverter());
        this.choiceBox_outputDevice.getSelectionModel().select(0);
        this.choiceBox_outputDevice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setOutputDevice(newValue);
        });

        // size
        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        this.choiceBox_size.setItems(sortedPageSizes);
        this.choiceBox_size.setConverter(svgOptionsUtil.getPageSizeStringConverter());
        this.choiceBox_size.getSelectionModel().select(PageSize.A4);
        this.choiceBox_size.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != PageSize.CUSTOM) {
                this.size = new Point(newValue.getWidth(), newValue.getHeight());
                this.svgPlotOptions.setSize(this.size);

            }
            toggleCustomSize(newValue == PageSize.CUSTOM);
        });

        // custom size
        this.textField_customSizeWidth.setText(this.size.x());
        this.textField_customSizeWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            this.size.setX(Integer.parseInt(newValue));
            this.svgPlotOptions.setSize(this.size);
        });
        this.textField_customSizeHeight.setText(this.size.y());
        this.textField_customSizeHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            this.size.setY(Integer.parseInt(newValue));
            this.svgPlotOptions.setSize(this.size);
        });

    }


    /**
     * Will initiate the second stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage2() {
        // csv path
        this.textField_CsvPath.setDisable(false);
        this.textField_CsvPath.textProperty().addListener((observable, oldValue, newValue) -> {
            this.svgPlotOptions.setCsvPath(newValue);
        });
        this.button_CsvPath.setDisable(false);
        this.button_CsvPath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                this.textField_CsvPath.setText(file.getAbsolutePath());
            }
        });

        // csv orientation
        ObservableList<CsvOrientation> csvOrientationObservableList = FXCollections.observableArrayList(CsvOrientation.values());
        this.choiceBox_csvOrientation.setItems(csvOrientationObservableList);
        this.choiceBox_csvOrientation.setConverter(this.svgOptionsUtil.getCsvOrientationStringConverter());
        this.choiceBox_csvOrientation.getSelectionModel().select(CsvOrientation.HORIZONTAL);
        this.choiceBox_csvOrientation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CsvOrientation>() {
            @Override
            public void changed(ObservableValue<? extends CsvOrientation> observable, CsvOrientation oldValue, CsvOrientation newValue) {
                svgPlotOptions.setCsvOrientation(newValue);
            }
        });

        // csv type
        ObservableList<CsvType> csvTypeObservableList = FXCollections.observableArrayList(CsvType.values());
        this.choiceBox_csvType.setItems(csvTypeObservableList);
        this.choiceBox_csvType.setConverter(this.svgOptionsUtil.getCsvTypeStringConverter());
        this.choiceBox_csvType.getSelectionModel().select(CsvType.DOTS);
        this.choiceBox_csvType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CsvType>() {
            @Override
            public void changed(ObservableValue<? extends CsvType> observable, CsvType oldValue, CsvType newValue) {
                svgPlotOptions.setCsvType(newValue);
            }
        });
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

                setVisible(newValue != SortingType.None, label_sortOrder, choicebox_sortOrder);
                if ( newValue == SortingType.None) {
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
        //TODO: validate text input
        // x unit
        this.textField_xunit.setText(this.svgPlotOptions.getxUnit());
        this.textField_xunit.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setxUnit(newValue);
        });

        // y unit
        this.textField_yunit.setText(this.svgPlotOptions.getyUnit());
        this.textField_yunit.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setyUnit(newValue);
        });

        // autoscale
        this.radioBtn_autoscale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setAutoScale(newValue);
                toggleAxesRanges(!newValue);
            }
        });
        this.radioBtn_autoscale.setSelected(true);

        // xRange
        this.xRange.set(this.svgPlotOptions.getxRange());
        if (this.xRange.get() == null) {
            this.xRange.set(new Range(-8, 8));
        }
        this.textField_xfrom.setText("" + this.xRange.get().getFrom());
        this.textField_xto.setText("" + this.xRange.get().getTo());

        this.textField_xfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            xRange.get().setFrom(Double.parseDouble(newValue));
        });
        this.textField_xto.textProperty().addListener((observable, oldValue, newValue) -> {
            xRange.get().setTo(Double.parseDouble(newValue));
        });
        this.xRange.addListener((args, oldVal, newVal) -> {
            if (!this.svgPlotOptions.hasAutoScale()) {
                svgPlotOptions.setxRange(xRange.get());
            }
        });

        // yRange
        this.yRange.set(this.svgPlotOptions.getyRange());
        if (this.yRange.get() == null) {
            this.yRange.set(new Range(-8, 8));
        }
        this.textField_yfrom.setText("" + this.yRange.get().getFrom());
        this.textField_yto.setText("" + this.yRange.get().getTo());

        this.textField_yfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            yRange.get().setFrom(Double.parseDouble(newValue));
        });
        this.textField_yto.textProperty().addListener((observable, oldValue, newValue) -> {
            yRange.get().setTo(Double.parseDouble(newValue));
        });
        this.yRange.addListener((args, oldVal, newVal) -> {
            if (!this.svgPlotOptions.hasAutoScale()) {
                System.out.println("set y range");
                svgPlotOptions.setyRange(yRange.get());
            }
        });
    }

    /**
     * Will initiate the fifth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage5() {
        // grid
        ObservableList<GridStyle> gridStyleObservableList = FXCollections.observableArrayList(GridStyle.values());
        this.choicebox_gridStyle.setItems(gridStyleObservableList);
        this.choicebox_gridStyle.setConverter(this.svgOptionsUtil.getGridStyleStringConverter());
        this.choicebox_gridStyle.getSelectionModel().select(GridStyle.NONE);
        this.choicebox_gridStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case FULL:
                    svgPlotOptions.setShowHorizontalGrid("on");
                    svgPlotOptions.setShowVerticalGrid("on");
                    break;
                case VERTICAL:
                    svgPlotOptions.setShowHorizontalGrid("off");
                    svgPlotOptions.setShowVerticalGrid("on");
                    break;
                case HORIZONTAL:
                    svgPlotOptions.setShowHorizontalGrid("on");
                    svgPlotOptions.setShowVerticalGrid("off");
                    break;
                case NONE:
                    svgPlotOptions.setShowHorizontalGrid("off");
                    svgPlotOptions.setShowVerticalGrid("off");
                    break;
            }
        });


        // xlines
        this.textField_xlines.setText(this.svgPlotOptions.getxLines());
        this.textField_xlines.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setxLines(newValue);
        });

        // ylines
        this.textField_ylines.setText(this.svgPlotOptions.getyLines());
        this.textField_ylines.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setyLines(newValue);
        });

        // double axes
        String showDoubleAxes = this.svgPlotOptions.getShowDoubleAxes();
        ObservableList<AxisStyle> axisStyleObservableList = FXCollections.observableArrayList(AxisStyle.values());
        axisStyleObservableList.remove(AxisStyle.GRAPH);
        this.choicebox_dblaxes.setItems(axisStyleObservableList);
        this.choicebox_dblaxes.setConverter(this.svgOptionsUtil.getAxisStyleStringConverter());
        this.choicebox_dblaxes.getSelectionModel().select((showDoubleAxes != null && showDoubleAxes.equals("on")) ? AxisStyle.BOX : AxisStyle.EDGE);
        this.choicebox_dblaxes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == AxisStyle.EDGE) {
                this.svgPlotOptions.setShowDoubleAxes("off");
            } else {
                this.svgPlotOptions.setShowDoubleAxes("on");
            }
        });
    }

    /**
     * Will initiate the sixth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage6() {
        this.colors = new ArrayList<>();
        this.size = this.svgPlotOptions.getSize();

        // css file
        this.textField_cssFile.setText(this.svgPlotOptions.getCss());
        this.textField_cssFile.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setCss(newValue);
        });

        // colors
        ObservableList<Color> sortingTypeObservableList = FXCollections.observableArrayList(Color.values());
        this.choiceBox_color1.setItems(sortingTypeObservableList);
        this.choiceBox_color2.setItems(sortingTypeObservableList);
        if (this.svgPlotOptions.getCustomColors().size() == 2) {
            Color color1 = Color.fromString(this.svgPlotOptions.getCustomColors().get(0));
            this.colors.add(color1.getName());
            this.choiceBox_color1.setValue(color1);

            Color color2 = Color.fromString(this.svgPlotOptions.getCustomColors().get(1));
            this.colors.add(color2.getName());
            this.choiceBox_color2.setValue(color2);
        }
        this.choiceBox_color1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                colors.add(newValue.getName());
                svgPlotOptions.setCustomColors(colors);
            }
        });
        this.choiceBox_color2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                colors.add(newValue.getName());
                svgPlotOptions.setCustomColors(colors);
            }
        });

    }

    /**
     * Sets the visibility of given Label and given field to true.
     *
     * @param label the {@link Label}
     * @param field the field {@link Node}
     */
    private void show(Label label, Node field) {
        label.setVisible(true);
        field.setVisible(true);
    }

    /**
     * Sets the visibility of given Label and given field to false.
     *
     * @param label the {@link Label}
     * @param field the field {@link Node}
     */
    private void hide(Label label, Node field) {
        label.setVisible(false);
        field.setVisible(false);
    }

    /**
     * Sets the value and visibility of x- and y-{@link Range} and corresponding {@link Node}s.
     *
     * @param enabled whether fields should be visible
     */
    private void toggleAxesRanges(Boolean enabled) {
        if (!enabled) {
            this.xRange.set(null);
            this.yRange.set(null);
        } else {
            this.xRange.set(new Range(-8, 8));
            this.yRange.set(new Range(-8, 8));
        }


        setVisible(enabled, label_xfrom, textField_xfrom);
        setVisible(enabled, label_xto, textField_xto);
        setVisible(enabled, label_yfrom, textField_yfrom);
        setVisible(enabled, label_yto, textField_yto);
    }

    /**
     * Sets the visibility of given label and field to given value.
     *
     * @param visible whether field {@link Node} and {@link Label} should be visible.
     * @param label   the {@link Label}
     * @param field   the the field {@link Node}
     */
    private void setVisible(boolean visible, Label label, Node field) {
        if (visible) {
            show(label, field);
        } else {
            hide(label, field);
        }
    }


    /**
     * Show/Hides labels and input fields of BarChart Options.
     *
     * @param show if field and value should be visible.
     */
    private void toggleBarChartOptions(final boolean show) {
        setVisible(show, label_baraccumulation, choiceBox_baraccumulation);
        setVisible(show, label_sorting, choiceBox_sorting);
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
        setVisible(show, label_linepoints, choiceBox_linepoints);
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

    private void toggleCustomSize(final boolean show) {
        setVisible(show, label_customSizeWidth, textField_customSizeWidth);
        setVisible(show, label_customSizeHeight, textField_customSizeHeight);

        if (show){
            this.textField_customSizeWidth.setText(this.size.x());
            this.textField_customSizeWidth.requestFocus();
            this.textField_customSizeHeight.setText(this.size.y());
        }
    }
}
