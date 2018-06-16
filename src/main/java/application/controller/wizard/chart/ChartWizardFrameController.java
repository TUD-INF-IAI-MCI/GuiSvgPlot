package application.controller.wizard.chart;

import application.GuiSvgPlott;
import application.Wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.service.SvgOptionsHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.Color;
import tud.tangram.svgplot.svgcreator.SvgCreator;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChartWizardFrameController implements SVGWizardController {

    private static final int AMOUNTOFSTAGES = 6;

    /*Begin: FXML Nodes*/
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
    public TextField textField_xunit;
    @FXML
    public TextField textField_yunit;
    @FXML
    public Label label_baraccumulation;
    @FXML
    public ChoiceBox<BarAccumulationStyle> choiceBox_baraccumulation;
    @FXML
    public Label label_linepoints;
    @FXML
    public CheckBox checkbox_linepoints;

    /* stage 4 */
    @FXML
    private GridPane stage4;
    @FXML
    public CheckBox checkbox_Autoscale;
    @FXML
    public TextField textField_xfrom;
    @FXML
    public TextField textField_xTo;
    @FXML
    public TextField textField_yfrom;
    @FXML
    public TextField textField_yto;

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
    @FXML
    public TextField textField_sizeWidth;
    @FXML
    public TextField textField_sizeHeight;
    @FXML
    public ChoiceBox<Color> choiceBox_color1;
    @FXML
    public ChoiceBox<Color> choiceBox_color2;

    /*End: FXML Nodes*/

    private ResourceBundle bundle;
    private File userDir;
    private IntegerProperty currentStage;

    private ArrayList<GridPane> stages;
    private BooleanProperty isExtended;

    private GuiSvgOptions svgOptions;
    private SvgPlotOptions svgPlotOptions;
    private SvgOptionsHelper svgOptionsHelper = SvgOptionsHelper.getInstance();
    private Range xRange;
    private Range yRange;

    public ChartWizardFrameController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.userDir = new File(System.getProperty("user.home"));
        this.textField_outputPath.setText(userDir.getPath());

        this.svgPlotOptions = new SvgPlotOptions();
        this.svgOptions = new GuiSvgOptions(svgPlotOptions);

        this.currentStage = new SimpleIntegerProperty();
        this.isExtended = new SimpleBooleanProperty(false);

        initListener();

        preProcessContent();

        initiateAllStages();

    }

    /**
     * sets the {@code isExtended} value for the wizard
     *
     * @param isExtended {@link Boolean}-value for the isExtendedProperty
     */
    public void setExtended(boolean isExtended) {
        this.isExtended.set(isExtended);

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
     * content-preprocessing. Will "hide" the content-tabPane and shows the first stage
     */
    private void preProcessContent() {
        stages = new ArrayList<>();
        tabPane_ContentHolder.getTabs().forEach(tab -> stages.add((GridPane) tab.getContent()));
        currentStage.set(0);
        borderPane_Wizard.setCenter(stages.get(0));
        button_Back.setDisable(true);
    }


    /**
     * initiates all listeners for properties and elements
     */
    private void initListener() {
        // indicator for current stage. changes will automatically render the chosen stage
        currentStage.addListener((args, oldVal, newVal) -> {
            if (newVal.intValue() < 1) button_Back.setDisable(true);
            else button_Back.setDisable(false);

            if(newVal.intValue() == stages.size() - 1) {
                button_Next.setText(bundle.getString("create"));
            }else {
                button_Next.setText(bundle.getString("next"));
            }
            if (newVal.intValue() > stages.size() - 1) {
                currentStage.set(oldVal.intValue());
                this.buildSVG();

            }
            borderPane_Wizard.setCenter(stages.get(currentStage.get()));
        });

        // increment the currentStage counter. Will trigger its changeListener
        button_Next.setOnAction(event -> currentStage.set(currentStage.get() + 1));

        // decrement the currentStage counter. Will trigger its changeListener
        button_Back.setOnAction(event -> currentStage.set(currentStage.get() - 1));

        // closes the wizard
        button_Cancel.setOnAction(event -> {
            GuiSvgPlott.getInstance().closeWizard();
        });
    }


    /**
     * Will initiate the first stage. Depending on {@code extended}, some parts will dis- or enabled
     */
    private void initStage1() {
        this.label_Headline.setText("Diagramm erstellen");

        // diagram type
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        diagramTypeObservableList.remove(DiagramType.FunctionPlot);
        this.choiceBox_DiagramType.setItems(diagramTypeObservableList);
        // i18n
        this.choiceBox_DiagramType.setConverter(svgOptionsHelper.getDiagramTypConverter(this.bundle));
        this.choiceBox_DiagramType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DiagramType>() {
            @Override
            public void changed(ObservableValue<? extends DiagramType> observable, DiagramType oldValue, DiagramType newValue) {
                svgPlotOptions.setDiagramType(newValue);
                if (newValue != null) {
                    switch (newValue) {
                        case BarChart:
                            show(label_baraccumulation, choiceBox_baraccumulation);
                            hide(label_linepoints, checkbox_linepoints);
                            break;
                        case LineChart:
                            show(label_linepoints, checkbox_linepoints);
                            hide(label_baraccumulation, choiceBox_baraccumulation);
                            break;
                        default:
                            hide(label_baraccumulation, choiceBox_baraccumulation);
                            hide(label_linepoints, checkbox_linepoints);
                            break; // TODO: trendlinien bei Scatterplot
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
        this.choiceBox_outputDevice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OutputDevice>() {
            @Override
            public void changed(ObservableValue<? extends OutputDevice> observable, OutputDevice oldValue, OutputDevice newValue) {
                svgPlotOptions.setOutputDevice(newValue);
            }
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
    }
    /**
     * Will initiate the second stage. Depending on {@code extended}, some parts will dis- or enabled
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

    private void initStage3() {
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
    }

    private void initStage4() {
        //TODO: validate text input
        // xRange
        this.xRange = this.svgPlotOptions.getxRange();
        if (this.xRange == null) {
            this.xRange = new Range(-8, 8);
        }
        this.textField_xfrom.setText("" + this.xRange.getFrom());
        this.textField_xTo.setText("" + this.xRange.getTo());

        this.textField_xfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            xRange.setFrom(Double.parseDouble(newValue));
            svgPlotOptions.setxRange(xRange);
        });
        this.textField_xTo.textProperty().addListener((observable, oldValue, newValue) -> {
            xRange.setTo(Double.parseDouble(newValue));
            svgPlotOptions.setxRange(xRange);
        });

        // yRange
        this.yRange = this.svgPlotOptions.getyRange();
        if (this.yRange == null) {
            this.yRange = new Range(-8, 8);
        }
        this.textField_yfrom.setText("" + this.yRange.getFrom());
        this.textField_yto.setText("" + this.yRange.getTo());

        this.textField_yfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            yRange.setFrom(Double.parseDouble(newValue));
            svgPlotOptions.setxRange(yRange);
        });
        this.textField_yto.textProperty().addListener((observable, oldValue, newValue) -> {
            yRange.setTo(Double.parseDouble(newValue));
            svgPlotOptions.setxRange(yRange);
        });

        // autoscale
        this.checkbox_Autoscale.setSelected(this.svgPlotOptions.hasAutoScale());
        this.checkbox_Autoscale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setAutoScale(newValue);
            }
        });
    }

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

    private List<String> colors;
    private Point size;
    private void initStage6() {
        this.colors = new ArrayList<>();
        this.size = this.svgPlotOptions.getSize();

        // size
        this.textField_sizeWidth.setText(this.svgPlotOptions.getSize().x());
        this.textField_sizeWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            size.setX(Double.parseDouble(newValue));
            svgPlotOptions.setSize(size);
        });
        this.textField_sizeHeight.setText(this.svgPlotOptions.getSize().y());
        this.textField_sizeHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            size.setY(Double.parseDouble(newValue));
            svgPlotOptions.setSize(size);
        });

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

    private void show(Label label, Node field) {
        label.setVisible(true);
        field.setVisible(true);
    }
    private void hide(Label label, Node field) {
        label.setVisible(false);
        field.setVisible(false);
    }


    private void buildSVG(){
        this.svgPlotOptions.finalizeOptions();

        SvgCreator creator = this.svgPlotOptions.getDiagramType().getInstance(this.svgPlotOptions);
        try {
            creator.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        GuiSvgPlott.getInstance().closeWizard();
    }
}
