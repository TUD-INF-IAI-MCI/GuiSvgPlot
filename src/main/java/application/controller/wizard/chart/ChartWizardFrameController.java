package application.controller.wizard.chart;

import application.GuiSvgPlott;
import application.Wizard.SVGWizardController;
import application.model.PageSize;
import application.model.TrendlineAlgorithm;
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
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.Color;

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
    public TextField textField_outputPath;
    @FXML
    private Button button_OutputPath;
    @FXML
    private ChoiceBox<PageSize> choiceBox_size;

    /* stage 2*/
    @FXML
    private GridPane stage2;
    @FXML
    public TextField textField_CsvPath;
    @FXML
    private Button button_CsvPath;
    @FXML
    public ChoiceBox<CsvOrientation> choiceBox_CsvOrientation;
    @FXML
    public ChoiceBox<CsvType> choiceBox_CsvType;
    @FXML
    public ChoiceBox<SortingType> choiceBox_Sorting;
    @FXML
    public CheckBox checkbox_SortDesc;

    /* stage 3 */
    @FXML
    private GridPane stage3;
    @FXML
    public Label label_baraccumulation;
    @FXML
    public ChoiceBox<BarAccumulationStyle> choiceBox_baraccumulation;
    @FXML
    public Label label_linepoints;
    @FXML
    public CheckBox checkbox_linepoints;
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
    public Label label_hideOriginalPoints;
    @FXML
    public CheckBox checkbox_hideOriginalPoints;

    /* stage 4 */
    @FXML
    private GridPane stage4;
    @FXML
    public TextField textField_xunit;
    @FXML
    public TextField textField_yunit;
    @FXML
    public CheckBox checkbox_Autoscale;
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
    public CheckBox checkbox_hgrid;
    @FXML
    public CheckBox checkbox_vgrid;
    @FXML
    public TextField textField_xlines;
    @FXML
    public TextField textField_ylines;
    @FXML
    public CheckBox checkbox_dblaxes;
    @FXML
    public CheckBox checkbox_pointsborderless;

    /* stage 6 */
    @FXML
    private GridPane stage6;
    @FXML
    public TextField textField_cssFile;
    //    @FXML
//    public TextField textField_sizeWidth;
//    @FXML
//    public TextField textField_sizeHeight;
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
        this.textField_outputPath.setText(userDir.getPath());
        this.xRange = new SimpleObjectProperty<>();
        this.yRange = new SimpleObjectProperty<>();
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

        this.label_Headline.setText("");

        // diagram type
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        diagramTypeObservableList.remove(DiagramType.FunctionPlot);
        this.choiceBox_DiagramType.setItems(diagramTypeObservableList);
        // i18n
        this.choiceBox_DiagramType.setConverter(svgOptionsService.getDiagramTypConverter());
        this.choiceBox_DiagramType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DiagramType>() {
            @Override
            public void changed(ObservableValue<? extends DiagramType> observable, DiagramType oldValue, DiagramType newValue) {
                svgPlotOptions.setDiagramType(newValue);
                if (newValue != null) {
                    switch (newValue) {
                        case BarChart:
                            show(label_baraccumulation, choiceBox_baraccumulation);
                            hide(label_linepoints, checkbox_linepoints);
                            hide(label_trendline, choiceBox_trendline);
                            break;
                        case LineChart:
                            show(label_linepoints, checkbox_linepoints);
                            hide(label_baraccumulation, choiceBox_baraccumulation);
                            hide(label_trendline, choiceBox_trendline);
                            break;
                        default:
                            show(label_trendline, choiceBox_trendline);
                            hide(label_baraccumulation, choiceBox_baraccumulation);
                            hide(label_linepoints, checkbox_linepoints);
                            break;
                    }
                } else {
                    show(label_baraccumulation, choiceBox_baraccumulation);
                    show(label_linepoints, checkbox_linepoints);
                }
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
        this.choiceBox_outputDevice.getSelectionModel().select(0);
        this.choiceBox_outputDevice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setOutputDevice(newValue);
        });

        // output path
        this.textField_outputPath.setDisable(false);
        this.textField_outputPath.textProperty().addListener((observable, oldValue, newValue) -> {
            this.svgPlotOptions.setOutput(new File(newValue));
        });
        this.button_OutputPath.setDisable(false);
        this.button_OutputPath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(userDir);
            File file = fileChooser.showSaveDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                this.textField_outputPath.setText(file.getAbsolutePath());
            }
        });

        // size
        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        this.choiceBox_size.setItems(sortedPageSizes);
        this.choiceBox_size.setConverter(svgOptionsService.getPageSizeConverter());
        this.choiceBox_size.getSelectionModel().select(PageSize.A4);
        this.choiceBox_size.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Point size = new Point(newValue.getWidth(), newValue.getHeight());
            this.svgPlotOptions.setSize(size);
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
        this.choiceBox_CsvOrientation.setItems(csvOrientationObservableList);
        this.choiceBox_CsvOrientation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CsvOrientation>() {
            @Override
            public void changed(ObservableValue<? extends CsvOrientation> observable, CsvOrientation oldValue, CsvOrientation newValue) {
                svgPlotOptions.setCsvOrientation(newValue);
            }
        });

        // csv type
        ObservableList<CsvType> csvTypeObservableList = FXCollections.observableArrayList(CsvType.values());
        this.choiceBox_CsvType.setItems(csvTypeObservableList);
        this.choiceBox_CsvType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CsvType>() {
            @Override
            public void changed(ObservableValue<? extends CsvType> observable, CsvType oldValue, CsvType newValue) {
                svgPlotOptions.setCsvType(newValue);
            }
        });

        // sorting type
        ObservableList<SortingType> sortingTypeObservableList = FXCollections.observableArrayList(SortingType.values());
        this.choiceBox_Sorting.setItems(sortingTypeObservableList);
        this.choiceBox_Sorting.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SortingType>() {
            @Override
            public void changed(ObservableValue<? extends SortingType> observable, SortingType oldValue, SortingType newValue) {
                svgPlotOptions.setSortingType(newValue);
            }
        });

        // sort desc
        this.checkbox_SortDesc.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setSortDescending(newValue);
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
        this.choiceBox_baraccumulation.setValue(this.svgPlotOptions.getBarAccumulationStyle());
        this.choiceBox_baraccumulation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BarAccumulationStyle>() {
            @Override
            public void changed(ObservableValue<? extends BarAccumulationStyle> observable, BarAccumulationStyle oldValue, BarAccumulationStyle newValue) {
                svgPlotOptions.setBarAccumulationStyle(newValue);
            }
        });
        // line points
        String showLinePoints = this.svgPlotOptions.getShowLinePoints();
        this.checkbox_linepoints.setSelected(showLinePoints != null && showLinePoints.equals("on"));
        this.checkbox_linepoints.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    svgPlotOptions.setShowLinePoints("on");
                } else {
                    svgPlotOptions.setShowLinePoints("off");
                }
            }
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
        this.checkbox_hideOriginalPoints.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setHideOriginalPoints(newValue);
            }
        });
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections.observableArrayList(TrendlineAlgorithm.values());
        this.choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        this.choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
        this.choiceBox_trendline.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TrendlineAlgorithm>() {
            @Override
            public void changed(ObservableValue<? extends TrendlineAlgorithm> observable, TrendlineAlgorithm oldValue, TrendlineAlgorithm newValue) {
                trendline.clear();
                trendline.add(0, newValue.toString());
                switch (newValue) {
                    case MovingAverage:
                        show(label_trendline_n, textField_trendline_n);
                        show(label_hideOriginalPoints, checkbox_hideOriginalPoints);
                        hide(label_trendline_alpha, textField_trendline_alpha);
                        hide(label_trendline_forecast, textField_trendline_forecast);
                        // default n
                        textField_trendline_n.setText("1");
                        break;
                    case BrownLES:
                        show(label_trendline_alpha, textField_trendline_alpha);
                        show(label_trendline_forecast, textField_trendline_forecast);
                        show(label_hideOriginalPoints, checkbox_hideOriginalPoints);
                        hide(label_trendline_n, textField_trendline_n);
                        // default alpha
                        textField_trendline_alpha.setText("0.0");
                        // default forecast
                        textField_trendline_forecast.setText("1");
                        break;
                    case ExponentialSmoothing:
                        show(label_trendline_alpha, textField_trendline_alpha);
                        show(label_hideOriginalPoints, checkbox_hideOriginalPoints);
                        hide(label_trendline_forecast, textField_trendline_forecast);
                        hide(label_trendline_n, textField_trendline_n);
                        // default alpha
                        textField_trendline_alpha.setText("0.0");
                        break;
                    case LinearRegression:
                        show(label_hideOriginalPoints, checkbox_hideOriginalPoints);
                        hide(label_trendline_alpha, textField_trendline_alpha);
                        hide(label_trendline_forecast, textField_trendline_forecast);
                        hide(label_trendline_n, textField_trendline_n);
                        break;
                    case None:
                        hide(label_trendline_alpha, textField_trendline_alpha);
                        hide(label_trendline_forecast, textField_trendline_forecast);
                        hide(label_trendline_n, textField_trendline_n);
                        hide(label_hideOriginalPoints, checkbox_hideOriginalPoints);
                        // TODO: this does not have any effect because SvgPlotOptions do not clear his private variable trendlineAlgorithm (SvgPlotOptions.parseTrendLine)
                        trendline.clear();
                        checkbox_hideOriginalPoints.setSelected(false);
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
        this.checkbox_Autoscale.setSelected(this.svgPlotOptions.hasAutoScale());
        this.checkbox_Autoscale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setAutoScale(newValue);
                toggleAxesRanges(!newValue);
            }
        });

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
            svgPlotOptions.setxRange(xRange.get());
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
            svgPlotOptions.setxRange(yRange.get());
        });
    }

    /**
     * Will initiate the fifth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage5() {
        // horizontal grid
        String showHorizontalGrid = this.svgPlotOptions.getShowHorizontalGrid();
        this.checkbox_hgrid.setSelected(showHorizontalGrid != null && showHorizontalGrid.equals("on"));
        this.checkbox_hgrid.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    svgPlotOptions.setShowHorizontalGrid("on");
                } else {
                    svgPlotOptions.setShowHorizontalGrid("off");
                }
            }
        });

        // vertical grid
        String showVerticalGrid = this.svgPlotOptions.getShowVerticalGrid();
        this.checkbox_vgrid.setSelected(showVerticalGrid != null && showVerticalGrid.equals("on"));
        this.checkbox_vgrid.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    svgPlotOptions.setShowVerticalGrid("on");
                } else {
                    svgPlotOptions.setShowVerticalGrid("off");
                }
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
        this.checkbox_dblaxes.setSelected(showDoubleAxes != null && showDoubleAxes.equals("on"));
        this.checkbox_dblaxes.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    svgPlotOptions.setShowDoubleAxes("on");
                } else {
                    svgPlotOptions.setShowDoubleAxes("off");
                }
            }
        });

        // points borderless
        this.checkbox_pointsborderless.setSelected(this.svgPlotOptions.isPointsBorderless());
        this.checkbox_pointsborderless.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setPointsBorderless(newValue);
            }
        });
    }

    /**
     * Will initiate the sixth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage6() {
        this.colors = new ArrayList<>();
        this.size = this.svgPlotOptions.getSize();

//        // size
//        this.textField_sizeWidth.setText(this.svgPlotOptions.getSize().x());
//        this.textField_sizeWidth.textProperty().addListener((observable, oldValue, newValue) -> {
//            size.setX(Double.parseDouble(newValue));
//            svgPlotOptions.setSize(size);
//        });
//        this.textField_sizeHeight.setText(this.svgPlotOptions.getSize().y());
//        this.textField_sizeHeight.textProperty().addListener((observable, oldValue, newValue) -> {
//            size.setY(Double.parseDouble(newValue));
//            svgPlotOptions.setSize(size);
//        });

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

}
