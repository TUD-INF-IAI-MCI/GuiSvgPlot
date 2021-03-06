package application.controller.wizard.chart;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import application.model.OutputGenerator;
import application.util.dialog.AccessibleTextInputDialog;
import de.tudresden.inf.mci.brailleplot.util.GeneralResource;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.model.DataPoint;
import application.model.DataSet;
import application.model.Options.GuiAxisStyle;
import application.model.Options.LinePointsOption;
import application.model.Options.SortOrder;
import application.model.Options.TrendlineAlgorithm;
import application.model.Options.VisibilityOfDataPoints;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.line.LineStyle;
import tud.tangram.svgplot.plotting.point.PointSymbol;
import tud.tangram.svgplot.plotting.texture.Texture;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.Color;

/**
 * The controller for chart-wizard.
 *
 * @author Emma Müller
 */
public class ChartWizardFrameController extends SVGWizardController {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ChartWizardFrameController.class);

    private static final int AMOUNTOFSTAGES = 6;
    private static final int COLOR_ROW = 4;
    private static final int POINTSYMBOL_ROW = 4;

    // TODO: Implement like pointSymbols and colors + replace with readed number of
    // data and set initialValue to 0
    private ObjectProperty<Integer> amountOfLineStyleInput = new SimpleObjectProperty<>(3);

    // TODO: replace with readed number of data and set initialValue to 0
    private ObjectProperty<Integer> amountOfPointSymbolInputs = new SimpleObjectProperty<>(3);

    // TODO: replace with readed number of data and set initialValue to 0
    private ObjectProperty<Integer> amountOfColorInput = new SimpleObjectProperty<>(3);

    /* Begin: FXML Nodes */

    /* stage 1 */
    @FXML
    private GridPane stage1;
    @FXML
    public ChoiceBox<DiagramType> choiceBox_diagramType;

    /* stage 2 */
    @FXML
    private GridPane stage2;
    @FXML
    private Button button_AddDataSet;
    @FXML
    private Button button_RemoveDataSet;
    @FXML
    private Button button_addDataPoint;
    @FXML
    private VBox vBox_dataTable;
    @FXML
    private ChoiceBox<DataSet> choiceBox_DataSets;

    /* stage 3 */
    @FXML
    private Text text_stage3_not_supported;
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
    private Text text_stage5_not_supported;
    @FXML
    private GridPane stage5;
    @FXML
    private Label label_dblaxes;

    /* stage 6 */
    @FXML
    private Text text_stage6_not_supported;
    @FXML
    private GridPane stage6;
    /* End: FXML Nodes */

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

    private ObservableList<DataSet> dataSets = FXCollections.observableArrayList();

    public ChartWizardFrameController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES, null);
        this.initiateAllStages();
        this.initChartOptionListeners();

        if (super.presets == null || super.presets.isEmpty()) {
            super.presets = FXCollections.observableArrayList(super.presetService.getAllCharts());
        }
        super.initloadPreset();
        this.initFieldListenersForChartPreview();

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
     * Help function to switch visibility for multiple nodes.
     * @param nodes An array of nodes to perform the action on.
     * @param visible The new visibility state.
     */
    private void setVisibilityForNodes(Node[] nodes, boolean visible) {
        for (Node node : nodes) {
            node.setVisible(visible);
        }
    }

    /**
     * Help function to disable/enable multiple nodes.
     * @param nodes An array of nodes to perform the action on.
     * @param disable The new disable state.
     */
    private void setDisableForNodes(Node[] nodes, boolean disable) {
        for (Node node : nodes) {
            node.setDisable(disable);
        }
    }

    /**
     * Help function to update the visibility and status of different controls in the UI
     * which must be disabled / enabled based on the selected output generator
     */
    private void updateSupportedGeneratorFeatures() {

        System.out.println("Feature restriction");

        OutputGenerator gen = guiSvgOptions.getOutputGenerator();
        boolean useBraillePlot = gen.equals(OutputGenerator.BraillePlot);

        if (useBraillePlot) {
            // Set default before disabling
            choiceBox_outputDevice.getSelectionModel().select(0);
            choiceBox_baraccumulation.getSelectionModel().select(1);
            choiceBox_trendline.getSelectionModel().select(0);
            toggleGroup_autoScale.selectToggle(radioBtn_autoscale);
            choicebox_gridStyle.getSelectionModel().select(0);
            choicebox_dblaxes.getSelectionModel().select(1);
            choiceBox_cssType.getSelectionModel().select(0);
            choiceBox_sorting.getSelectionModel().select(3);
            textField_xlines.clear();
            textField_ylines.clear();
        }

        // Enable/Disable (un)supported features:
        setDisableForNodes(
                new Node[]{
                        choiceBox_outputDevice, choiceBox_baraccumulation, radioBtn_customScale,
                        choiceBox_linepoints, choiceBox_trendline, choiceBox_sorting,
                        choicebox_gridStyle, textField_xlines, textField_ylines,
                        choiceBox_cssType, hBox_cssPath
                },
                useBraillePlot
        );
        setVisibilityForNodes(
                new Node[]{
                        choiceBox_outputDevice, label_firstTexture, label_secondTexture, label_thirdTexture,
                        choiceBox_firstTexture, choiceBox_secondTexture, choiceBox_thirdTexture,
                        button_resetFirstTexture, button_resetSecondTexture, button_resetThirdTexture,
                        label_firstLineStyle, label_secondLineStyle, label_thirdLineStyle,
                        choiceBox_firstLineStyle, choiceBox_secondLineStyle, choiceBox_thirdLineStyle,
                        button_resetFirstLineStyle, button_resetSecondLineStyle, button_resetThirdLineStyle,
                        label_dblaxes, choicebox_dblaxes
                },
                !useBraillePlot
        );
        setVisibilityForNodes(
                new Node[]{
                        choiceBox_brlPlotConfig, label_brailleplot_actions, checkbox_brlplot_print,
                        checkbox_brlplot_svgexport, checkbox_brlplot_textdump,
                        text_stage3_not_supported, text_stage5_not_supported, text_stage6_not_supported
                },
                useBraillePlot
        );

        if (!Objects.isNull(pointSymbolInputs)) {
            // only do this after full initialization
            pointSymbolInputs.forEach((label, hBox) -> {
                label.setVisible(!useBraillePlot);
                hBox.setVisible(!useBraillePlot);
            });
        }

        if (!useBraillePlot) {
            // re-enable & update all specific options for selected type, in case of unrestricted feature support
            toggleBarChartOptions(guiSvgOptions.getDiagramType() == DiagramType.BarChart);
            toggleLineChartOptions(guiSvgOptions.getDiagramType() == DiagramType.LineChart);
            toggleScatterPlotOptions(guiSvgOptions.getDiagramType() == DiagramType.ScatterPlot);
        }
    }

    /**
     * Will initiate the first stage. Depending on {@code extended}, some parts will
     * be dis- or enabled.
     */
    private void initStage1() {

        super.initGeneralFieldListeners();

        /*
        TODO: Document Extensions to the ChartWizardFrameController in Stage 1
        - initialization for generator choice box (essentially the same as GuiSvgPlot already does it for the outputDevice choice box)
        - init for brlplot output mode
        */

        // output generator
        ObservableList<OutputGenerator> outputGenerators = FXCollections.observableArrayList(OutputGenerator.values());
        choiceBox_outputGenerator.setItems(outputGenerators);
        choiceBox_outputGenerator.setConverter(converter.getOutputGeneratorStringConverter()); // for lang accessibility
        choiceBox_outputGenerator.getSelectionModel().select(0); // set first entry (enum ordinal 0 = SvgPlot) as default
        choiceBox_outputGenerator.getSelectionModel().selectedItemProperty() // add listener
                .addListener((observable, oldValue, newValue) -> {
                    this.guiSvgOptions.setOutputGenerator(newValue);
                    updateSupportedGeneratorFeatures();
                });

        // brailleplot config
        ArrayList<File> fileList = new ArrayList<>();
        Path printerDirPath = Paths.get(System.getProperty("user.home") + "/svgPlot/braillePlot/printers");
        File printerDir = printerDirPath.toFile();
        printerDir.mkdirs();
        File[] filesFound;
        filesFound = Objects.requireNonNull(printerDir.listFiles((dir, name) -> name.endsWith(".properties")));
        if (filesFound.length < 1) {
            logger.info("Exporting printer configuration files.");
            try {
                if(printerDir.exists()) {
                    printerDir.delete();
                }
                Files.move(GeneralResource.getOrExportResourceFile("brlPlotDeploy/printers/").toPath(), printerDirPath);
            } catch (IOException e) {
                logger.error("Can not export printer configuration files.", e);
                e.printStackTrace();
            }
            filesFound = printerDir.listFiles((dir, name) -> name.endsWith(".properties"));
        }
        fileList.addAll(Arrays.asList(filesFound));
        ObservableList<File> configFiles = FXCollections.observableArrayList(fileList);
        choiceBox_brlPlotConfig.setItems(configFiles);
        choiceBox_brlPlotConfig.setConverter(converter.getFileStringConverter());
        guiSvgOptions.setBrlPlotConfig(fileList.get(0));
        choiceBox_brlPlotConfig.getSelectionModel().select(0);
        choiceBox_brlPlotConfig.getSelectionModel().selectedItemProperty() // add listener
                .addListener((observable, oldValue, newValue) -> this.guiSvgOptions.setBrlPlotConfig(newValue));

        // brailleplot actions
        checkbox_brlplot_print.setSelected(true);
        checkbox_brlplot_print.selectedProperty().addListener((observableValue, oldValue, newValue) -> guiSvgOptions.setBrlPlotPrint(newValue));
        checkbox_brlplot_textdump.setSelected(false);
        checkbox_brlplot_textdump.selectedProperty().addListener((observableValue, oldValue, newValue) -> guiSvgOptions.setBrlPlotTextDump(newValue));
        checkbox_brlplot_svgexport.setSelected(false);
        checkbox_brlplot_svgexport.selectedProperty().addListener((observableValue, oldValue, newValue) -> guiSvgOptions.setBrlPlotSvgExport(newValue));

        /*
        // brailleplot mode
        ObservableList<BrlPlotMode> brlPlotModes = FXCollections.observableArrayList(BrlPlotMode.values());
        choiceBox_brlPlotOutputMode.setItems(brlPlotModes);
        choiceBox_brlPlotOutputMode.setConverter(converter.getBrlPlotModeStringConverter()); // for lang accessibility
        choiceBox_brlPlotOutputMode.getSelectionModel().select(0); // set first entry (enum ordinal 0 = DefaultPrinter) as default
        choiceBox_brlPlotOutputMode.getSelectionModel().selectedItemProperty() // add listener
                .addListener((observable, oldValue, newValue) -> this.guiSvgOptions.setBrlPlotMode(newValue));
         */

        // automatically apply feature restriction on update
        guiSvgOptions.onUpdate((options) -> this.updateSupportedGeneratorFeatures());

        // diagram type
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        diagramTypeObservableList.remove(DiagramType.FunctionPlot);
        this.choiceBox_diagramType.setItems(diagramTypeObservableList);
        // i18n
        this.choiceBox_diagramType.setConverter(this.converter.getDiagramTypeStringConverter());
        this.choiceBox_diagramType.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        guiSvgOptions.setDiagramType(newValue);
                        guiSvgOptions.setCsvPath("");
                        dataSets.clear();
                        textField_csvPath.clear();
                        toggleBarChartOptions(newValue == DiagramType.BarChart);
                        toggleLineChartOptions(newValue == DiagramType.LineChart);
                        toggleScatterPlotOptions(newValue == DiagramType.ScatterPlot);

                        // Need to recheck feature support that may have been toggled in the step before
                        updateSupportedGeneratorFeatures();
                    }
                });
        this.choiceBox_diagramType.getSelectionModel().select(0);
    }

    /**
     * Will initiate the second stage. Depending on {@code extended}, some parts
     * will be dis- or enabled.
     */
    private void initStage2() {
        super.initCsvFieldListeners();

        button_addDataPoint.setDisable(true);

        getResultFileProp().addListener(inval -> {
            try {
                parseCSV();
            } catch (Exception e) {
                Alert a = dialogUtil.alert(Alert.AlertType.ERROR, "csv_parse_error_title", "csv_parse_error_header",
                        "csv_parse_error");
                a.getDialogPane().getStyleClass().add("h-100");
                a.showAndWait();
            }
            if (!dataSets.isEmpty()) {
                choiceBox_DataSets.getSelectionModel().select(0);
            }
        });

        choiceBox_DataSets.setConverter(this.converter.getDataSetStringConverter(dataSets));
        choiceBox_DataSets.setItems(dataSets);

        button_AddDataSet.setOnAction(event -> {

            AccessibleTextInputDialog dialog = dialogUtil.textInputDialog("dataset_entername_title", "dataset_entername_header",
                    "dataset_entername_content");
            dialog.showAndWait();

            if (dialog.getResult() != null && !dialog.getResult().isEmpty()) {
                DataSet set = new DataSet(choiceBox_diagramType.getValue(), dialog.getResult());
                dataSets.add(set);
                choiceBox_DataSets.getSelectionModel().select(set);
            }
        });

        button_RemoveDataSet.setOnAction(event -> {
            if (dataSets.size() > 0) {
                int index = Math.max(0, choiceBox_DataSets.getSelectionModel().getSelectedIndex() - 1);
                dataSets.remove(choiceBox_DataSets.getValue());
                choiceBox_DataSets.getSelectionModel().select(index);
                setCSVOptions();

                if (dataSets.isEmpty())
                    button_addDataPoint.setDisable(true);
            }

        });

        button_RemoveDataSet.disableProperty().bind(button_addDataPoint.disableProperty());

        choiceBox_DataSets.setOnAction(event -> {
            renderTable(choiceBox_DataSets.getValue());
            if (choiceBox_DataSets.getValue() != null)
                button_addDataPoint.setDisable(false);
        });

        button_addDataPoint.setOnAction(event -> {
            if (choiceBox_DataSets.getValue() != null) {
                DataPoint p = new DataPoint("", "");
                choiceBox_DataSets.getValue().addPoint(p);

                vBox_dataTable.getChildren().add(generateTableEntry(p));
            }
            setCSVOptions();
        });

    }

    /**
     * Will initiate the third stage. Depending on {@code extended}, some parts will
     * be dis- or enabled.
     */
    private void initStage3() {
        // baraccumulation
        ObservableList<BarAccumulationStyle> csvOrientationObservableList = FXCollections
                .observableArrayList(BarAccumulationStyle.values());
        this.choiceBox_baraccumulation.setItems(csvOrientationObservableList);
        this.choiceBox_baraccumulation.setConverter(this.converter.getBarAccumulationStyleStringConverter());
        this.choiceBox_baraccumulation.setValue(this.guiSvgOptions.getBarAccumulationStyle());
        this.choiceBox_baraccumulation.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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
        ObservableList<Texture> textureObservableListSecondTexture = FXCollections
                .observableArrayList(Texture.values());
        ObservableList<Texture> textureObservableListThirdTexture = FXCollections.observableArrayList(Texture.values());
        this.choiceBox_firstTexture.setItems(textureObservableListFirstTexture);
        this.choiceBox_firstTexture.setConverter(this.converter.getTextureStringConverter());
        this.choiceBoxUtil.addNotEmptyValidationListener(this.choiceBox_firstTexture, this.label_firstTexture);
        this.choiceBox_firstTexture.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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
        this.choiceBox_secondTexture.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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
        this.choiceBox_thirdTexture.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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
        this.choiceBox_sorting.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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
        this.choicebox_sortOrder.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        guiSvgOptions.setSortOrder(newValue);
                    }
                });

        // line points
        ObservableList<LinePointsOption> linePointsOptionObservableList = FXCollections
                .observableArrayList(LinePointsOption.values());
        this.choiceBox_linepoints.setItems(linePointsOptionObservableList);
        this.choiceBox_linepoints.setConverter(converter.getLinePointsOptionStringConverter());
        this.choiceBox_linepoints.getSelectionModel().select(this.guiSvgOptions.getLinePointsOption());
        this.choiceBox_linepoints.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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
            this.drawPointSymbolField(pointSymbolInputs, this.customPointSymbols, i, false);
        }
        selectedPointSymbols.forEach((label, pointSymbol) -> {
            HBox hBox_scatter = pointSymbolInputs.get(label);
            if (hBox_scatter != null) {
                ChoiceBox<PointSymbol> pointSymbolChoiceBox = (ChoiceBox<PointSymbol>) hBox_scatter.getChildren()
                        .get(0);
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
        this.choiceBox_firstLineStyle.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    boolean oldValueInPossibleStyles = LineStyle.getListByOutputDevice(guiSvgOptions.getOutputDevice())
                            .contains(oldValue);
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
        this.choiceBox_secondLineStyle.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    try {
                        lineStyles.set(1, newValue);
                    } catch (Exception e) {
                        lineStyles.add(1, newValue);
                    }

                    boolean oldValueInPossibleStyles = LineStyle.getListByOutputDevice(guiSvgOptions.getOutputDevice())
                            .contains(oldValue);
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
        this.choiceBox_thirdLineStyle.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    try {
                        lineStyles.set(2, newValue);
                    } catch (Exception e) {
                        lineStyles.add(2, newValue);
                    }
                    boolean oldValueInPossibleStyles = LineStyle.getListByOutputDevice(guiSvgOptions.getOutputDevice())
                            .contains(oldValue);
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
        this.textFieldUtil.addDoubleValidationWithMinimumAndMaximum(this.textField_trendline_alpha,
                this.label_trendline_alpha, 0, 1);
        this.textField_trendline_alpha.textProperty().addListener((observable, oldValue, newValue) -> {
            setValueToIndex(trendline, 1, newValue);
        });
        this.textFieldUtil.addDoubleValidationWithMinimum(this.textField_trendline_forecast,
                this.label_trendline_forecast, 0);
        this.textField_trendline_forecast.textProperty().addListener((observable, oldValue, newValue) -> {
            setValueToIndex(trendline, 2, newValue);
        });
        ObservableList<VisibilityOfDataPoints> visibilityOfDataPointsObservableList = FXCollections
                .observableArrayList(VisibilityOfDataPoints.values());
        this.choiceBox_originalPoints.setItems(visibilityOfDataPointsObservableList);
        this.choiceBox_originalPoints.setConverter(this.converter.getVisibilityOfDataPointsStringConverter());
        this.choiceBox_originalPoints.getSelectionModel().select(VisibilityOfDataPoints.SHOW);
        this.choiceBox_originalPoints.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        this.guiSvgOptions.setHideOriginalPoints(newValue);
                        pointSymbolInputs.forEach((label,
                                                   hBox) -> toggleVisibility(newValue.equals(VisibilityOfDataPoints.SHOW), label, hBox));
                    }

                });
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections
                .observableArrayList(TrendlineAlgorithm.values());
        this.choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        this.choiceBox_trendline.setConverter(this.converter.getTrendlineAlgorithmStringConverter());
        this.choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
        this.choiceBox_trendline.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

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
                                pointSymbolInputs.forEach((label, hBox) -> toggleVisibility(guiSvgOptions.getHideOriginalPoints().equals(VisibilityOfDataPoints.SHOW), label, hBox));
                                hide(label_trendline_alpha, textField_trendline_alpha);
                                hide(label_trendline_forecast, textField_trendline_forecast);

                                trendline.setAll(newValue.toString(), n);
                                textField_trendline_n.setText(n);
                                break;
                            case BrownLES:
                                show(label_trendline_alpha, textField_trendline_alpha);
                                show(label_trendline_forecast, textField_trendline_forecast);
                                show(label_originalPoints, choiceBox_originalPoints);
                                pointSymbolInputs.forEach((label, hBox) -> toggleVisibility(guiSvgOptions.getHideOriginalPoints().equals(VisibilityOfDataPoints.SHOW), label, hBox));
                                hide(label_trendline_n, textField_trendline_n);

                                trendline.setAll(newValue.toString(), alpha, forecast);

                                textField_trendline_alpha.setText(alpha);
                                textField_trendline_forecast.setText(forecast);
                                break;
                            case ExponentialSmoothing:
                                show(label_trendline_alpha, textField_trendline_alpha);
                                show(label_originalPoints, choiceBox_originalPoints);
                                pointSymbolInputs.forEach((label, hBox) -> toggleVisibility(guiSvgOptions.getHideOriginalPoints().equals(VisibilityOfDataPoints.SHOW), label, hBox));
                                hide(label_trendline_forecast, textField_trendline_forecast);
                                hide(label_trendline_n, textField_trendline_n);

                                trendline.setAll(newValue.toString(), alpha);
                                textField_trendline_alpha.setText(alpha);
                                break;
                            case LinearRegression:
                                show(label_originalPoints, choiceBox_originalPoints);
                                pointSymbolInputs.forEach((label, hBox) -> toggleVisibility(guiSvgOptions.getHideOriginalPoints().equals(VisibilityOfDataPoints.SHOW), label, hBox));
                                hide(label_trendline_alpha, textField_trendline_alpha);
                                hide(label_trendline_forecast, textField_trendline_forecast);
                                hide(label_trendline_n, textField_trendline_n);
                                trendline.setAll(newValue.toString());
                                break;
                            default:
                                hide(label_trendline_alpha, textField_trendline_alpha);
                                hide(label_trendline_forecast, textField_trendline_forecast);
                                hide(label_trendline_n, textField_trendline_n);
                                hide(label_originalPoints, choiceBox_originalPoints);
                                pointSymbolInputs.forEach((label, hBox) -> hide(label, hBox));
                                trendline.clear();
                                break;
                        }
                    }
                });
    }

    /**
     * Will initiate the fourth stage. Depending on {@code extended}, some parts
     * will be dis- or enabled.
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
        this.choicebox_dblaxes.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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
     * Will initiate the fifth stage. Depending on {@code extended}, some parts will
     * be dis- or enabled.
     */
    private void initStage5() {
        super.initSpecialFieldListeners();
    }

    /**
     * Will initiate the sixth stage. Depending on {@code extended}, some parts will
     * be dis- or enabled.
     */
    private void initStage6() {
        super.initStylingFieldListeners();
        this.colorChoiceBoxes = new ArrayList<>();
        this.amountOfColorInput.addListener((observable, oldValue, newValue) -> {
            if (newValue > oldValue) {
                for (int i = oldValue; i < newValue; i++) {
                    this.drawColorField(colorInputs, this.customColors, i,
                            this.guiSvgOptions.getOutputDevice().equals(OutputDevice.ScreenColor));
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
            this.drawColorField(colorInputs, this.customColors, i,
                    this.guiSvgOptions.getOutputDevice().equals(OutputDevice.ScreenColor));
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
                        pointSymbolChoiceBoxes.get(i).getSelectionModel()
                                .select(guiSvgOptions.getPointSymbols().get(i));
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

                    // when selecting the first item of the first choicebox, it will be removed from
                    // the second and third choice box
                    choiceBox_firstLineStyle.setItems(lineStyleFirstObservableList);
                    choiceBox_firstLineStyle.getSelectionModel().select(0);
                    // therefore, you have to select the (new) first item of the second, which will
                    // then be removed from the first an third
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
                colorInputs.forEach(
                        (label, hBox) -> toggleVisibility(newValue.equals(OutputDevice.ScreenColor), label, hBox));
            }
        });

        this.guiSvgOptions.getTrendLine().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(final Change<? extends String> c) {
                if (guiSvgOptions.getTrendLine().size() > 0) {
                    TrendlineAlgorithm trendlineAlgorithm = TrendlineAlgorithm
                            .fromString(guiSvgOptions.getTrendLine().get(0));
                    choiceBox_trendline.getSelectionModel().select(trendlineAlgorithm);
                }
            }
        });

        this.guiSvgOptions.linePointsOptionProperty().addListener((observable, oldValue, newValue) -> {
            this.choiceBox_linepoints.getSelectionModel().select(newValue);
        });

        this.guiSvgOptions.csvPathProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!dataSets.isEmpty() && !dataSets.get(0).getAllPoints().isEmpty()) {
                        System.out.println("Calling build preview");
                        svgOptionsService.buildPreviewSVG(guiSvgOptions, webView_svg);
                        System.out.println("Build preview finished");
                    }
                });

        this.guiSvgOptions.hideOriginalPointsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.choiceBox_originalPoints.getSelectionModel().select(newValue);
            }
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
        /*
        TODO: Document extension of listener: preview update on generator or config change
         */
        this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions, choiceBox_outputGenerator, choiceBox_brlPlotConfig,
                this.choiceBox_diagramType, this.choiceBox_baraccumulation, this.choiceBox_firstTexture,
                this.choiceBox_secondTexture, this.choiceBox_thirdTexture, this.choicebox_dblaxes,
                this.choiceBox_firstLineStyle, this.choiceBox_secondLineStyle, this.choiceBox_thirdLineStyle,
                this.choiceBox_linepoints, this.choiceBox_originalPoints, this.choiceBox_sorting,
                this.choicebox_sortOrder, this.choiceBox_trendline, this.choiceBox_csvType);
        this.textFieldUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions, this.textField_xunit,
                this.textField_yunit, this.textField_trendline_n, this.textField_trendline_forecast,
                this.textField_trendline_alpha);

        this.pointSymbolInputs.forEach((label, hBox) -> {
            ChoiceBox<PointSymbol> pointSymbolChoiceBox = (ChoiceBox<PointSymbol>) hBox.getChildren().get(0);
            this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions,
                    pointSymbolChoiceBox);
        });
        this.colorInputs.forEach((label, hBox) -> {
            ChoiceBox<Color> colorChoiceBox = (ChoiceBox<Color>) hBox.getChildren().get(0);
            this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions, colorChoiceBox);
        });
        this.radioBtn_autoscale.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);
        });
    }

    private void drawPointSymbolField(final Map<Label, HBox> map, ObservableList<PointSymbol> observableList,
                                      final int index, final boolean visible) {

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
        ObservableList<PointSymbol> pointSymbolObservableList = FXCollections
                .observableArrayList(PointSymbol.getOrdered());
        pointSymbolChoiceBox.setItems(pointSymbolObservableList);
        pointSymbolChoiceBox.setConverter(this.converter.getPointSymbolStringConverter());

        pointSymbolChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
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

    private void drawColorField(final Map<Label, HBox> map, ObservableList<Color> observableList, final int index,
                                final boolean visible) {

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

    private void changeColors(final Map<Label, HBox> map, ObservableList<Color> observableList, final int index,
                              Label label, final Color newValue, final Color oldValue) {
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

    private void changePointSymbols(final Map<Label, HBox> map, ObservableList<PointSymbol> observableList,
                                    final int index, Label label, final PointSymbol newValue, final PointSymbol oldValue) {
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
        switch (this.guiSvgOptions.getDiagramType()) {
            case BarChart: {
                parseBarChart();
                break;
            }
            case LineChart: {
                // hackyTheHack says, its the same like scatterPlot
                parseScatterPlot();
                break;
            }
            case ScatterPlot: {
                parseScatterPlot();
                break;
            }
            default: {

            }

        }
    }

    private void parseBarChart() {

        IntegerProperty columnNumber = new SimpleIntegerProperty(0);
        StringProperty firstLine = new SimpleStringProperty("");

        try {
            Files.readAllLines(getResultFileProp().get()).forEach(line -> {
                columnNumber.set(0);
                // get all Names of the Datasets in row 0
                if (firstLine.isEmpty().get()) {
                    firstLine.set(line.replaceFirst(",", "").trim());
                    return;
                }
                String[] xValues = firstLine.get().split((","));

                String[] data = (line + ",").split("");
                DataSet set = null;
                boolean escaped = false;
                String actualValue = "";

                // just programmer magic parsing
                for (String aData : data) {
                    if (aData.equals("\"")) {
                        escaped = !escaped;
                        continue;
                    }
                    if (aData.equals(",") && !escaped) {
                        if (set == null) {
                            set = new DataSet(DiagramType.BarChart, actualValue);
                        } else {
                            set.addPoint(new DataPoint(xValues[columnNumber.get()], actualValue));
                            columnNumber.set(columnNumber.get() + 1);
                        }
                        actualValue = "";
                        continue;
                    }
                    actualValue += aData;
                }
                if (set != null)
                    dataSets.add(set);
                columnNumber.set(-1);

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseScatterPlot() {

        ObjectProperty<DataSet> dataSet = new SimpleObjectProperty<>(new DataSet(null, ""));

        try {
            Files.readAllLines(getResultFileProp().get()).forEach(line -> {

                line = line.trim();

                if (!line.startsWith(",")) {
                    // title and X-Values
                    dataSet.set(new DataSet(DiagramType.ScatterPlot, line.split(",")[0].trim()));
                    dataSets.add(dataSet.get());
                    String[] data = (line.substring(line.indexOf(",") + 1) + ",").split("");
                    boolean escaped = false;
                    String actualValue = "";

                    for (String aData : data) {
                        if (aData.equals("\"")) {
                            escaped = !escaped;
                            continue;
                        }
                        if (aData.equals(",") && !escaped) {
                            dataSet.get().addPoint(new DataPoint(actualValue, ""));
                            actualValue = "";
                            continue;
                        }
                        actualValue += aData;
                    }

                } else {
                    String[] data = (line.substring(1) + ",").split("");
                    boolean escaped = false;
                    String actualValue = "";
                    int counter = 0;
                    for (String aData : data) {
                        if (aData.equals("\"")) {
                            escaped = !escaped;
                            continue;
                        }
                        if (aData.equals(",") && !escaped) {
                            if (dataSet.get().getAllPoints().size() >= counter)
                                dataSet.get().getAllPoints().get(counter).setValue(actualValue);
                            actualValue = "";
                            counter++;
                            continue;
                        }
                        actualValue += aData;
                    }

                }

            });
        } catch (IOException e) {
            e.printStackTrace();

        }
        dataSets.forEach(item -> System.out.println(item.getAllPoints()));

    }

    private void renderTable(DataSet set) {
        vBox_dataTable.getChildren().clear();
        if (set != null)
            set.getAllPoints().forEach(point -> vBox_dataTable.getChildren().add(generateTableEntry(point)));
        setCSVOptions();
    }

    private HBox generateTableEntry(DataPoint point) {
        HBox row = new HBox();

        row.getStyleClass().add("data-row");
        row.getStyleClass().add("edit");
        row.setUserData(point);

        TextField keyField = new TextField(point.getKey().replaceAll(",", "."));
        keyField.getStyleClass().add("data-cell");
        keyField.getStyleClass().add("data-cell-x");

        if (choiceBox_diagramType.getValue().equals(DiagramType.BarChart)) {
            keyField.textProperty().addListener((args, oldVal, newVal) -> {
                point.setKey(newVal);
                setCSVOptions();
            });

        } else {
            keyField.textProperty().addListener((args, oldVal, newVal) -> {

                if (newVal.isEmpty() || newVal.matches("\\-{0,1}[0-9]+\\.{0,1}[0-9]*")) {
                    point.setKey(newVal.replaceAll("\\.", "\\,"));
                } else {
                    keyField.setText(oldVal);
                }
                setCSVOptions();
            });
        }

        keyField.setPromptText(
                this.bundle.getString(guiSvgOptions.getDiagramType().name().toLowerCase() + "_keyField"));
        keyField.setTooltip(
                new Tooltip(this.bundle.getString(guiSvgOptions.getDiagramType().name().toLowerCase() + "_keyField")));

        TextField valueField = new TextField(point.getValue().replaceAll(",", "."));
        valueField.getStyleClass().add("data-cell");
        valueField.getStyleClass().add("data-cell-y");
        valueField.setPromptText(
                this.bundle.getString(guiSvgOptions.getDiagramType().name().toLowerCase() + "_valueField"));
        valueField.setTooltip(new Tooltip(
                this.bundle.getString(guiSvgOptions.getDiagramType().name().toLowerCase() + "_valueField")));

        Glyph closeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.TRASH);
        Button removeButton = new Button();
        removeButton.setTooltip(new Tooltip(this.bundle.getString("datapoint_remove")));

        removeButton.setGraphic(closeGlyph);
        removeButton.getStyleClass().add("data-cell-button");
        removeButton.getStyleClass().add("btn-upload");

        removeButton.setOnAction(event -> {
            int idx = Math.max(0, vBox_dataTable.getChildren().indexOf(row));
            vBox_dataTable.getChildren().get(idx).requestFocus();

            vBox_dataTable.getChildren().remove(row);
            choiceBox_DataSets.getValue().getAllPoints().remove(point);
            renderTable(choiceBox_DataSets.getValue());
            svgOptionsService.buildPreviewSVG(guiSvgOptions, webView_svg);
        });

        valueField.textProperty().addListener((args, oldVal, newVal) -> {

            if (newVal.isEmpty() || newVal.matches("\\-{0,1}[0-9]+\\.{0,1}[0-9]*")) {
                point.setValue(newVal.replaceAll("\\.", ","));
                setCSVOptions();
            } else {
                valueField.setText(oldVal);
            }
        });

        row.getChildren().addAll(keyField, valueField, removeButton);
        row.setFocusTraversable(true);
        row.setAccessibleRole(AccessibleRole.TABLE_ROW);
        row.setAccessibleText(this.bundle.getString("chart_row") + (vBox_dataTable.getChildren().size() + 1));

        HBox.setHgrow(valueField, Priority.ALWAYS);
        HBox.setHgrow(keyField, Priority.ALWAYS);
        HBox.setHgrow(removeButton, Priority.NEVER);

        return row;
    }

    private void setCSVOptions() {

        HashSet<String> barChartNames = new HashSet<>();

        String title = choiceBox_diagramType.getValue() + "_" + textField_title.getText();

//        File f = new File("i:\\" + title + ".csv");

        try {
            final Path f = Files.createTempFile(title, ".csv");
            GuiSvgPlott.possibleTempFiles.add(f);

            if (f == null) {
                return;
            }

            try {
                Files.write(f, ("").getBytes(Charset.defaultCharset()), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (dataSets.isEmpty())
                return;

            if (choiceBox_diagramType.getValue().equals(DiagramType.BarChart)) {

                dataSets.forEach(set -> set.getAllPoints().forEach(point -> barChartNames.add(point.getKey())));

                StringProperty firstLine = new SimpleStringProperty("");
                // writeHeader
                for (String name : barChartNames) {
                    firstLine.set(firstLine.get() + name + ",");
                }

                if (firstLine.get().length() > 0)
                    try {
                        Files.write(f, ("," + firstLine.get().substring(0, firstLine.get().length() - 1) + "\n")
                                .getBytes(Charset.defaultCharset()), StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                dataSets.forEach(set -> {
                    if (set.getAllPoints().isEmpty())
                        return;
                    String line = set.getName() + ",";

                    for (String name : barChartNames) {
                        boolean found = false;
                        for (DataPoint point : set.getAllPoints()) {
                            if (!point.getKey().isEmpty() && !point.getValue().isEmpty())
                                if (point.getKey().equals(name)) {
                                    if (!found) {
                                        line += "\"" + point.getValue() + "\"" + ",";
                                        found = true;
                                    }
                                }
                        }
                        if (!found)
                            line += "0,";
                    }
                    line = line.substring(0, line.length() - 1);

                    try {
                        Files.write(f, (line + "\n").getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });


            } else {

                dataSets.forEach(item -> {
                    String line1 = item.getName() + ",";
                    String line2 = ",";

                    for (DataPoint point : item.getAllPoints()) {
                        if (!point.getKey().isEmpty() && !point.getValue().isEmpty()) {
                            line1 += "\"" + point.getKey() + "\"" + ",";
                            line2 += "\"" + point.getValue() + "\"" + ",";
                        }
                    }
                    if (item.getAllPoints().size() > 0) {
                        int index = 0;
                        if (choiceBox_diagramType.getValue().equals(DiagramType.BarChart)) {
                            index = 1;
                        }
                        line1 = line1.substring(index, line1.length() - 1) + "\n";
                        line2 = line2.substring(index, line2.length() - 1) + "\n";

                    }
                    try {
                        Files.write(f, (line1).getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
                        Files.write(f, (line2).getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

            }
            this.guiSvgOptions.setCsvPath(f.toAbsolutePath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}