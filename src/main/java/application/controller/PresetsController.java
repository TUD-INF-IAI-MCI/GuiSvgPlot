package application.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.model.Preset;
import application.model.Options.CssType;
import application.model.Options.GuiAxisStyle;
import application.model.Options.IntegralOption;
import application.model.Options.LinePointsOption;
import application.model.Options.PageSize;
import application.model.Options.SortOrder;
import application.model.Options.TrendlineAlgorithm;
import application.model.Options.VisibilityOfDataPoints;
import application.service.PresetService;
import application.util.Converter;
import application.util.DiagramTypeUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.line.LineStyle;
import tud.tangram.svgplot.plotting.point.PointSymbol;
import tud.tangram.svgplot.plotting.texture.Texture;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.GridStyle;

/**
 * @author Constantin Amend
 */
public class PresetsController extends SVGWizardController implements Initializable {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(PresetsController.class);
    private ResourceBundle bundle;

    private static PresetsController presetsController;

    private Converter converter = Converter.getInstance();
    private Preset currentPreset;
    private GuiSvgOptions currentOptions = new GuiSvgOptions(new SvgPlotOptions());
    private final ToggleGroup scaleGroup = new ToggleGroup();
    //	private final ToggleGroup valueRangeGroup = new ToggleGroup();
//	private final ToggleGroup integralToggle = new ToggleGroup();
    @FXML
    private final ToggleGroup pageOrientationTG = new ToggleGroup();
    private final DiagramTypeUtil diagramTypeUtil = DiagramTypeUtil.getInstance();
    private PresetService presetService = PresetService.getInstance();
//	private ObservableList<Texture> textures;
//	private ObservableList<PointSymbol> customPointSymbols_scatterPlott;

    // glyphs
    private Glyph abortGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CLOSE);
    private Glyph closeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CLOSE);
    private Glyph backGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CHEVRON_LEFT);
    private Glyph saveGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.SAVE);
    private Glyph deleteGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.TRASH);

    // start of fxml variable declaration

    // h and vboxes
    @FXML
    public VBox vBox_Preset_DataTable;
    @FXML
    private HBox hBox_firstLineStyle;
    @FXML
    private HBox hBox_secondLineStyle;
    @FXML
    private HBox hBox_thirdLineStyle;
    @FXML
    private HBox hBox_firstTexture;
    @FXML
    private HBox hBox_secondTexture;
    @FXML
    private HBox hBox_thirdTexture;
    @FXML
    private HBox hBox_cssPath;

    // labels
    @FXML
    private Label label_diagramType;
    @FXML
    private Label label_firstTexture;
    @FXML
    private Label label_secondTexture;
    @FXML
    private Label label_thirdTexture;
    @FXML
    private Label label_sortOrder;
    @FXML
    private Label label_sorting;
    @FXML
    private Label label_baraccumulation;
    @FXML
    private Label label_customSizeWidth;
    @FXML
    private Label label_customSizeHeight;
    @FXML
    private Label label_pointSymbols_lineChart;
    @FXML
    private Label label_pointSymbols_scatterPlot;
    @FXML
    private Label label_secondLineStyle;
    @FXML
    private Label label_thirdLineStyle;
    @FXML
    private Label label_firstLineStyle;
    @FXML
    private Label label_linepoints;
    @FXML
    private Label label_trendline;
    @FXML
    private Label label_trendline_n;
    @FXML
    private Label label_x_from;
    @FXML
    private Label label_x_to;
    @FXML
    private Label label_y_from;
    @FXML
    private Label label_y_to;
    @FXML
    private Label label_cssCustom;
    @FXML
    private Label label_cssPath;
    @FXML
    private Label label_trendline_forecast;
    @FXML
    private Label label_trendline_alpha;
    @FXML
    private Label label_hide_original_points;
    @FXML
    private Label label_rangeFrom;
    @FXML
    private Label label_rangeTo;
    @FXML
    private Label label_integral;
    @FXML
    private Label label_integral_name;
    @FXML
    private Label label_integral_function1;
    @FXML
    private Label label_integral_function2;
    @FXML
    private Label label_pi;
    @FXML
    private Label label_scale_data;

    // choiceboxes
    @FXML
    private ChoiceBox<SortingType> choiceBox_sorting;
    @FXML
    private ChoiceBox<Texture> choiceBox_firstTexture;
    @FXML
    private ChoiceBox<Texture> choiceBox_secondTexture;
    @FXML
    private ChoiceBox<Texture> choiceBox_thirdTexture;
    @FXML
    private ChoiceBox<TrendlineAlgorithm> choiceBox_trendline;
    @FXML
    private ChoiceBox<SortOrder> choiceBox_sortOrder;
    @FXML
    private ChoiceBox<CsvType> choiceBox_csvType;
    @FXML
    private ChoiceBox<GridStyle> choiceBox_gridStyle;
    @FXML
    private ChoiceBox<GuiAxisStyle> choiceBox_dblaxes;
    @FXML
    private ChoiceBox<CssType> choiceBox_cssType;
    @FXML
    private ChoiceBox<LinePointsOption> choiceBox_linepoints;
    @FXML
    private ChoiceBox<LineStyle> choiceBox_secondLineStyle;
    @FXML
    private ChoiceBox<VisibilityOfDataPoints> choiceBox_hide_original_points;
    @FXML
    private ChoiceBox<PointSymbol> choiceBox_pointSymbols_lineChart;
    @FXML
    private ChoiceBox<PointSymbol> choiceBox_pointSymbols_scatterPlot;
    @FXML
    private ChoiceBox<LineStyle> choiceBox_thirdLineStyle;
    @FXML
    private ChoiceBox<LineStyle> choiceBox_firstLineStyle;
    @FXML
    private ChoiceBox<BarAccumulationStyle> choiceBox_baraccumulation;
    @FXML
    private ChoiceBox<IntegralOption> choiceBox_integralOption;
    @FXML
    private ChoiceBox<Function> choiceBox_function1;
    @FXML
    private ChoiceBox<Function> choiceBox_function2;

    // buttons
    @FXML
    private Button button_csvPath;
    @FXML
    private Button button_cssPath;
    @FXML
    private Button button_resetFirstTexture;
    @FXML
    private Button button_resetSecondTexture;
    @FXML
    private Button button_resetThirdTexture;
    @FXML
    private Button button_resetFirstLineStyle;
    @FXML
    private Button button_resetSecondLineStyle;
    @FXML
    private Button button_resetThirdLineStyle;
    @FXML
    private Button button_cancel;
    @FXML
    private Button button_cancel_editor;
    @FXML
    private Button button_back;
    @FXML
    private Button button_savePreset;
    @FXML
    private Button button_deletePreset;
    @FXML
    private Button button_sortByName;
    @FXML
    private Button button_sortByDate;
    @FXML
    private Button button_sortByType;

    // textfields
    @FXML
    private TextField textField_Title;
    @FXML
    private TextField textField_trendline_alpha;
    @FXML
    private TextField textField_csvpath;
    @FXML
    private TextField textField_xAxisTitle;
    @FXML
    private TextField textField_yAxisTitle;
    @FXML
    private TextField textField_trendline_forecast;
    @FXML
    private TextField textField_trendline_n;
    @FXML
    private TextField textField_customSizeWidth;
    @FXML
    private TextField textField_customSizeHeight;
    @FXML
    private TextField textField_x_from;
    @FXML
    private TextField textField_x_to;
    @FXML
    private TextField textField_y_from;
    @FXML
    private TextField textField_y_to;
    @FXML
    private TextArea textArea_cssCustom;
    @FXML
    private TextField textField_integralName;
    @FXML
    private TextField textField_helpLinesX;
    @FXML
    private TextField textField_helpLinesY;
    @FXML
    private TextField textField_presetName;
    @FXML
    private TextField textField_rangeFrom;
    @FXML
    private TextField textField_rangeTo;

    // radiobuttons
    @FXML
    private RadioButton radioBtn_scale_to_data;
    @FXML
    private RadioButton radioBtn_customScale;
    @FXML
    private RadioButton radioBtn_portrait;
    @FXML
    private RadioButton radioBtn_landscape;
    @FXML
    private RadioButton radioButton_scalingDecimal;
    @FXML
    private RadioButton radioButton_scalingPi;

    // borderpanes
    @FXML
    private BorderPane overViewBorderPane;
    @FXML
    private BorderPane borderPane_editor;

    // observablelists
    @FXML
    private ObservableList<String> chartTypeObservableList = FXCollections.observableArrayList();

    // end of fxml variable declaration

    private String sortBy;
    private boolean sortAsc;
    // protected TextFieldUtil textFieldUtil = TextFieldUtil.getInstance();

    public PresetsController() {
        presetsController = this;
    }

    public static PresetsController getInstance() {
        return presetsController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.converter = Converter.getInstance();
        this.converter.setBundle(resources);
        super.dialogUtil.setBundle(resources);
        this.diagramTypeUtil.setBundle(resources);
        chartTypeObservableList.add(resources.getString("combo_diagram"));
        chartTypeObservableList.add(resources.getString("combo_function"));
        overViewBorderPane.setVisible(true);
        this.sortBy = "";
        this.sortAsc = true;

        this.presetService.setBundle(bundle);

        this.initSortingBtns();
        if (super.presets == null || super.presets.isEmpty()) {
            presets = FXCollections.observableArrayList(this.presetService.getAll());
            loadTable();
        }
        super.presets.addListener(new ListChangeListener<Preset>() {
            @Override
            public void onChanged(Change<? extends Preset> c) {
                loadTable();
            }
        });
        button_cancel.setGraphic(abortGlyph);
        button_cancel_editor.setGraphic(closeGlyph);
        button_back.setGraphic(backGlyph);
        button_savePreset.setGraphic(saveGlyph);
        button_deletePreset.setGraphic(deleteGlyph);
    }

    /**
     * resets the visibilty of {@link TextField}'s, {@link ChoiceBox}es,
     * {@link HBox}es from the diagram/function specific part of the editor
     */
    private void resetSpecifics() {
        toggleVisibility(false, label_secondLineStyle, hBox_secondLineStyle);
        toggleVisibility(false, label_thirdLineStyle, hBox_thirdLineStyle);
        toggleVisibility(false, label_firstLineStyle, hBox_firstLineStyle);
        toggleVisibility(false, label_linepoints, choiceBox_linepoints);
        toggleVisibility(false, label_baraccumulation, choiceBox_baraccumulation);
        toggleVisibility(false, label_firstTexture, hBox_firstTexture);
        toggleVisibility(false, label_secondTexture, hBox_secondTexture);
        toggleVisibility(false, label_thirdTexture, hBox_thirdTexture);
        toggleVisibility(false, label_sorting, choiceBox_sorting);
        toggleVisibility(false, label_integral, choiceBox_integralOption);
        toggleVisibility(false, label_integral_name, textField_integralName);
        toggleVisibility(false, label_integral_function1, choiceBox_function1);
        toggleVisibility(false, label_integral_function2, choiceBox_function2);
        toggleVisibility(false, label_trendline, choiceBox_trendline);
        toggleVisibility(false, label_trendline_alpha, textField_trendline_alpha);
        toggleVisibility(false, label_trendline_forecast, textField_trendline_forecast);
        toggleVisibility(false, label_trendline_n, textField_trendline_n);
        toggleVisibility(false, label_hide_original_points, choiceBox_hide_original_points);
        toggleVisibility(false, label_pi, radioButton_scalingDecimal);
        toggleVisibility(false, label_pi, radioButton_scalingPi);

    }

    /**
     * initiates the general options in the preset editor. Depending on
     * {@code extended}, some parts will be dis- or enabled.
     */
    private void initEditor() {
        // diagramtype label
        label_diagramType
                .setText(bundle.getString("diagramType_" + currentPreset.getDiagramTypeString().toLowerCase()));

        // outputdevice
        choiceBox_outputDevice.setItems(FXCollections.observableArrayList(OutputDevice.values()));
        choiceBox_outputDevice.setConverter(converter.getOutputDeviceStringConverter());

        // pagesize
        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList
                .sorted(Comparator.comparing(PageSize::getName));
        choiceBox_size.setItems(sortedPageSizes);
        choiceBox_size.setConverter(converter.getPageSizeStringConverter());

        choiceBox_size.getSelectionModel().selectedIndexProperty().addListener((args, oldValue, newValue) -> {
            if (newValue.equals(sortedPageSizes.size() - 1)) {
                label_customSizeHeight.setVisible(true);
                label_customSizeWidth.setVisible(true);
                textField_customSizeHeight.setVisible(true);
                textField_customSizeWidth.setVisible(true);
            } else {
                label_customSizeHeight.setVisible(false);
                label_customSizeWidth.setVisible(false);
                textField_customSizeHeight.setVisible(false);
                textField_customSizeWidth.setVisible(false);
            }
        });

        // CSV
        button_csvPath.setDisable(false);
        button_csvPath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                textField_csvpath.setText(file.getAbsolutePath());
            }
        });

        choiceBox_csvType.setItems(FXCollections.observableArrayList(CsvType.values()));
        choiceBox_csvType.setConverter(converter.getCsvTypeStringConverter());

        // axis
        toggleVisibility(true, label_scale_data, radioBtn_scale_to_data);
        toggleVisibility(true, label_scale_data, radioBtn_customScale);
        // textFieldUtil.addDoubleValidation(textField_x_from, label_x_from);
        // textFieldUtil.addDoubleValidation(textField_x_to, label_x_to);
        // textFieldUtil.addNotEqualValidation(textField_x_from, label_x_from,
        // textField_x_to, label_x_to);
        // textFieldUtil.addFirstNotGreaterThanSecondValidationListener(textField_x_from,
        // label_xfrom, textField_x_to, label_x_to);

        // gridstyle
        choiceBox_gridStyle.setItems(FXCollections.observableArrayList(GridStyle.values()));
        choiceBox_gridStyle.setConverter(converter.getGridStyleStringConverter());

        // doubleaxis
        choiceBox_dblaxes.setItems(FXCollections.observableArrayList(GuiAxisStyle.values()));
        choiceBox_dblaxes.setConverter(converter.getAxisStyleStringConverter());

        // CSS
        choiceBox_cssType.setItems(FXCollections.observableArrayList(CssType.values()));
        choiceBox_cssType.setConverter(converter.getCssTypeStringConverter());
        choiceBox_cssType.getSelectionModel().select(CssType.NONE);

        choiceBox_cssType.getSelectionModel().selectedIndexProperty().addListener((args, oldValue, newValue) -> {
            switch (newValue.intValue()) {
                case 0:
                    cssCustomHider();
                    break;
                case 1:
                    cssCustomHider();
                    label_cssCustom.setVisible(true);
                    textArea_cssCustom.setVisible(true);
                    break;
                case 2:
                    cssCustomHider();
                    label_cssPath.setVisible(true);
                    hBox_cssPath.setVisible(true);
                    break;
            }

        });

        this.button_cssPath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(this.userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSS files (*.css)", "*.css");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                this.textField_cssPath.setText(file.getAbsolutePath());
            }
        });
        radioBtn_scale_to_data.setToggleGroup(scaleGroup);
        radioBtn_scale_to_data.setSelected(true);
        radioBtn_customScale.setToggleGroup(scaleGroup);

        radioBtn_customScale.selectedProperty().addListener((args, oldVal, newVal) -> {

            label_x_from.setVisible(newVal);
            textField_x_from.setVisible(newVal);
            label_x_to.setVisible(newVal);
            textField_x_to.setVisible(newVal);
            label_y_from.setVisible(newVal);
            textField_y_from.setVisible(newVal);
            label_y_to.setVisible(newVal);
            textField_y_to.setVisible(newVal);

            if (currentPreset.getOptions().getxRange() == null || currentPreset.getOptions().getyRange() == null) {
                textField_x_from.setText("-8.0");
                textField_x_to.setText("8.0");
                textField_y_from.setText("-8.0");
                textField_y_to.setText("8.0");
            }
        });

    }

    /**
     * hides the css custom labels and such
     */
    private void cssCustomHider() {
        toggleVisibility(false, label_cssCustom, textArea_cssCustom);
        toggleVisibility(false, label_cssPath, hBox_cssPath);
    }

    private void functionRadioButtonHider() {
        toggleVisibility(false, label_pi, radioButton_scalingDecimal);
        toggleVisibility(false, label_pi, radioButton_scalingPi);
    }

    /**
     * initiates the linechart specific options in the preset editor. Depending on
     * {@code extended}, some parts will be dis- or enabled.
     */
    private void initLineChart() {
        textField_presetName.setText(currentPreset.getName());
        toggleVisibility(true, label_secondLineStyle, hBox_secondLineStyle);
        toggleVisibility(true, label_thirdLineStyle, hBox_thirdLineStyle);
        toggleVisibility(true, label_firstLineStyle, hBox_firstLineStyle);
        toggleVisibility(true, label_linepoints, choiceBox_linepoints);
        ObservableList<LineStyle> secondLineStyleObservableList = FXCollections.observableArrayList(
                LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_secondLineStyle.setItems(secondLineStyleObservableList);
        choiceBox_secondLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        ObservableList<LineStyle> thirdLineStyleObservableList = FXCollections.observableArrayList(
                LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_thirdLineStyle.setItems(thirdLineStyleObservableList);
        choiceBox_thirdLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        ObservableList<LineStyle> firstLineStyleObservableList = FXCollections.observableArrayList(
                LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_firstLineStyle.setItems(firstLineStyleObservableList);
        choiceBox_firstLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        ObservableList<LinePointsOption> linePointsOptionObservableList = FXCollections
                .observableArrayList(LinePointsOption.values());
        choiceBox_linepoints.setItems(linePointsOptionObservableList);
        choiceBox_linepoints.setConverter(converter.getLinePointsOptionStringConverter());
        choiceBox_linepoints.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    // TODO linepointsymbols
//            switch (newValue.toString()){
//                case "ShowWithBorder":
//                    toggleVisibility(true, );
//                    toggleVisibility(true, );
//                    toggleVisibility(true, );
//                    break;
//                case "ShowBorderless":
//                    toggleVisibility(true, );
//                    toggleVisibility(true, );
//                    toggleVisibility(true, );
//                    break;
//                default:
//                    toggleVisibility(false, );
//                    toggleVisibility(false, );
//                    toggleVisibility(false, );
//                    break;
//            }
                });

        button_resetFirstLineStyle.setOnAction(event -> {
            choiceBox_firstLineStyle.getSelectionModel().select(null);
        });
        button_resetFirstLineStyle.getStyleClass().add("btn-reset");
        button_resetSecondLineStyle.setOnAction(event -> {
            choiceBox_secondLineStyle.getSelectionModel().select(null);
        });
        button_resetSecondLineStyle.getStyleClass().add("btn-reset");
        button_resetThirdLineStyle.setOnAction(event -> {
            choiceBox_thirdLineStyle.getSelectionModel().select(null);
        });
        button_resetThirdLineStyle.getStyleClass().add("btn-reset");
    }

    /**
     * initiates the scatterplot specific options in the preset editor. Depending on
     * {@code extended}, some parts will be dis- or enabled.
     */
    private void initScatterPlot() {
        textField_presetName.setText(currentPreset.getName());
        toggleVisibility(true, label_trendline, choiceBox_trendline);
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections
                .observableArrayList(TrendlineAlgorithm.values());
        choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        choiceBox_trendline.setConverter(converter.getTrendlineAlgorithmStringConverter());
        choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
        choiceBox_trendline.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch ((TrendlineAlgorithm) newValue) {
                    case MovingAverage:
                        toggleVisibility(true, label_trendline_n, textField_trendline_n);
                        toggleVisibility(true, label_hide_original_points, choiceBox_hide_original_points);
                        toggleVisibility(false, label_trendline_alpha, textField_trendline_alpha);
                        toggleVisibility(false, label_trendline_forecast, textField_trendline_forecast);
                        break;
                    case ExponentialSmoothing:
                        toggleVisibility(true, label_trendline_alpha, textField_trendline_alpha);
                        toggleVisibility(true, label_hide_original_points, choiceBox_hide_original_points);
                        toggleVisibility(false, label_trendline_forecast, textField_trendline_forecast);
                        toggleVisibility(false, label_trendline_n, textField_trendline_n);
                        break;
                    case BrownLES:
                        toggleVisibility(true, label_trendline_alpha, textField_trendline_alpha);
                        toggleVisibility(true, label_trendline_forecast, textField_trendline_forecast);
                        toggleVisibility(true, label_hide_original_points, choiceBox_hide_original_points);
                        toggleVisibility(false, label_trendline_n, textField_trendline_n);
                        break;
                    case LinearRegression:
                        toggleVisibility(true, label_hide_original_points, choiceBox_hide_original_points);
                        toggleVisibility(false, label_trendline_forecast, textField_trendline_forecast);
                        toggleVisibility(false, label_trendline_n, textField_trendline_n);
                        toggleVisibility(false, label_trendline_alpha, textField_trendline_alpha);
                        break;
                    default:
                        toggleVisibility(false, label_trendline_alpha, textField_trendline_alpha);
                        toggleVisibility(false, label_trendline_forecast, textField_trendline_forecast);
                        toggleVisibility(false, label_trendline_n, textField_trendline_n);
                        toggleVisibility(false, label_hide_original_points, choiceBox_hide_original_points);
                }
            }
        });

        ObservableList<VisibilityOfDataPoints> dataPoints = FXCollections
                .observableArrayList(VisibilityOfDataPoints.values());
        choiceBox_hide_original_points.setItems(dataPoints);
        choiceBox_hide_original_points.setConverter(converter.getVisibilityOfDataPointsStringConverter());
        choiceBox_hide_original_points.getSelectionModel().select(VisibilityOfDataPoints.HIDE);
        // TODO Pointsymbols
        /*
         * ObservableList<PointSymbol> pointSymbolObservableList =
         * FXCollections.observableArrayList(PointSymbol.getOrdered());
         * this.customPointSymbols_scatterPlott = guiSvgOptions.getPointSymbols();
         * choiceBox_pointSymbols_scatterPlot.setVisible(true);
         * choiceBox_pointSymbols_scatterPlot.getItems().addAll(
         * pointSymbolObservableList);
         * choiceBox_pointSymbols_scatterPlot.setConverter(this.converter.
         * getPointSymbolStringConverter());
         * choiceBox_pointSymbols_scatterPlot.getCheckModel().getCheckedItems().
         * addListener(new ListChangeListener<PointSymbol>() {
         *
         * public void onChanged(ListChangeListener.Change<? extends PointSymbol> ps) {
         * changePointSymbols(customPointSymbols_scatterPlott,
         * choiceBox_pointSymbols_scatterPlot, pointSymbolObservableList); } });
         */
    }

    /**
     * initiates the barchart specific options in the preset editor. Depending on
     * {@code extended}, some parts will be dis- or enabled.
     */
    private void initBarChart() {
        textField_presetName.setText(currentPreset.getName());
        toggleVisibility(true, label_baraccumulation, choiceBox_baraccumulation);
        toggleVisibility(true, label_firstTexture, hBox_firstTexture);
        toggleVisibility(true, label_secondTexture, hBox_secondTexture);
        toggleVisibility(true, label_thirdTexture, hBox_thirdTexture);
        toggleVisibility(true, label_sorting, choiceBox_sorting);

        List<Button> resetButtons = new ArrayList<>();
        resetButtons.add(button_resetFirstTexture);
        resetButtons.add(button_resetSecondTexture);
        resetButtons.add(button_resetThirdTexture);

        for (Button b : resetButtons) {
            b.setOnAction(event -> {
                String buttonName = b.getId();
                switch (buttonName) {
                    case "button_resetFirstTexture":
                        choiceBox_firstTexture.getSelectionModel().select(null);
                        break;
                    case "button_resetSecondTexture":
                        choiceBox_secondTexture.getSelectionModel().select(null);
                        break;
                    case "button_resetThirdTexture":
                        choiceBox_thirdTexture.getSelectionModel().select(null);
                        break;
                }
                b.getStyleClass().add("btn-reset");
            });
        }
        ObservableList<BarAccumulationStyle> barAccumulationStyleObservableList = FXCollections
                .observableArrayList(BarAccumulationStyle.values());
        choiceBox_baraccumulation.setItems(barAccumulationStyleObservableList);
        choiceBox_baraccumulation.setConverter(converter.getBarAccumulationStyleStringConverter());

        ObservableList<Texture> textureObservableListFirstTexture = FXCollections.observableArrayList(Texture.values());
        ObservableList<Texture> textureObservableListSecondTexture = FXCollections
                .observableArrayList(Texture.values());
        ObservableList<Texture> textureObservableListThirdTexture = FXCollections.observableArrayList(Texture.values());
        choiceBox_firstTexture.setItems(textureObservableListFirstTexture);
        choiceBox_secondTexture.setItems(textureObservableListSecondTexture);
        choiceBox_thirdTexture.setItems(textureObservableListThirdTexture);
        choiceBox_firstTexture.setConverter(converter.getTextureStringConverter());
        choiceBox_secondTexture.setConverter(converter.getTextureStringConverter());
        choiceBox_thirdTexture.setConverter(converter.getTextureStringConverter());

        ObservableList<SortingType> sortingTypeObservableList = FXCollections.observableArrayList(SortingType.values());
        choiceBox_sorting.setItems(sortingTypeObservableList);
        choiceBox_sorting.setConverter(converter.getSortingTypeStringConverter());
        choiceBox_sorting.getSelectionModel().select(SortingType.None);

        choiceBox_sorting.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.toString().equals("None")) {
                    choiceBox_sortOrder.setVisible(true);
                    label_sortOrder.setVisible(true);
                } else {
                    choiceBox_sortOrder.setVisible(false);
                    label_sortOrder.setVisible(false);
                }
            }
        }));
        toggleVisibility(false, label_sortOrder, choiceBox_sortOrder);
        ObservableList<SortOrder> sortOrderObservableList = FXCollections.observableArrayList(SortOrder.values());
        choiceBox_sortOrder.setItems(sortOrderObservableList);
        choiceBox_sortOrder.setConverter(converter.getSortOrderStringConverter());
        choiceBox_sortOrder.getSelectionModel().select(SortOrder.ASC);

    }

    /**
     * initiates the function specific options in the preset editor. Depending on
     * {@code extended}, some parts will be dis- or enabled.
     */
    private void initFunction() {
        textField_presetName.setText(currentPreset.getName());
        toggleVisibility(false, label_scale_data, radioBtn_customScale);
        toggleVisibility(false, label_scale_data, radioBtn_scale_to_data);
        toggleVisibility(true, label_integral, choiceBox_integralOption);
        toggleVisibility(true, label_pi, radioButton_scalingDecimal);
        toggleVisibility(true, label_pi, radioButton_scalingPi);
        ObservableList<IntegralOption> integralOptionObservableList = FXCollections
                .observableArrayList(IntegralOption.values());
        choiceBox_integralOption.setItems(integralOptionObservableList);
        choiceBox_integralOption.setConverter(super.converter.getIntegralOptionStringConverter());
        choiceBox_integralOption.getSelectionModel().select(IntegralOption.NONE);

        choiceBox_integralOption.getSelectionModel().selectedItemProperty().addListener((args, oldVal, newValue) -> {

            if (newValue == IntegralOption.XAXIS) {
                toggleVisibility(true, label_integral_name, textField_integralName);
                toggleVisibility(true, label_integral_function1, choiceBox_function1);
                toggleVisibility(true, label_rangeFrom, textField_rangeFrom);
                toggleVisibility(true, label_rangeTo, textField_rangeTo);
            } else if (newValue == IntegralOption.FUNCTION) {
                toggleVisibility(true, label_integral_name, textField_integralName);
                toggleVisibility(true, label_integral_function1, choiceBox_function1);
                toggleVisibility(true, label_integral_function2, choiceBox_function2);
                toggleVisibility(true, label_rangeFrom, textField_rangeFrom);
                toggleVisibility(true, label_rangeTo, textField_rangeTo);
            } else {
                toggleVisibility(false, label_integral_name, textField_integralName);
                toggleVisibility(false, label_integral_function1, choiceBox_function1);
                toggleVisibility(false, label_integral_function2, choiceBox_function2);
                toggleVisibility(false, label_rangeFrom, textField_rangeFrom);
                toggleVisibility(false, label_rangeTo, textField_rangeTo);
            }

        });

    }

//	private void changePointSymbols(ObservableList<PointSymbol> checkedPointSymbols,
//			CheckComboBox<PointSymbol> checkComboBox, ObservableList<PointSymbol> allPointSymbols) {
//
//		checkedPointSymbols.clear();
//		ObservableList<PointSymbol> checkedItems = checkComboBox.getCheckModel().getCheckedItems();
//		ObservableList<PointSymbol> newItems = checkedItems
//				.filtered(pointSymbol -> !checkedPointSymbols.contains(pointSymbol));
//		ObservableList<PointSymbol> oldItems = allPointSymbols.filtered(
//				pointSymbol -> !checkedItems.contains(pointSymbol) && checkedPointSymbols.contains(pointSymbol));
//		for (PointSymbol pointSymbol : newItems) {
//			checkedPointSymbols.add(pointSymbol);
//		}
//		for (PointSymbol pointSymbol : oldItems) {
//			checkedPointSymbols.remove(pointSymbol);
//		}
//		guiSvgOptions.setPointSymbols(checkedPointSymbols);
//	}

    /**
     * handle for the create new preset button from the fxml file. leads to the
     * diagramTypePrompt function
     */
    @FXML
    private void createNewPreset() {
        diagramTypePrompt();
    }

    /**
     * starts the prompt in which the user can choose the {@link DiagramType} leads
     * to the presetNamePrompt function with the chosen {@link DiagramType}
     *
     * @author Constantin Amend, Emma Müller
     */
    private void diagramTypePrompt() {
        DiagramType dt;
        List<String> choices = new ArrayList<>();
        for (DiagramType diagramType : DiagramType.values()) {
            choices.add(diagramTypeUtil.toString(diagramType));
        }
        ChoiceDialog<String> dialogue = new ChoiceDialog<>(diagramTypeUtil.toString(DiagramType.LineChart), choices);

        dialogue.setTitle(bundle.getString("prompt_diagramtype_title"));
        dialogue.setResizable(true);
        dialogue.setHeaderText(bundle.getString("prompt_diagramtype_header"));
        dialogue.setContentText(bundle.getString("prompt_diagramtype_content"));
        dialogUtil.styleDialog(dialogue);

        Optional<String> result = dialogue.showAndWait();
        if (result.isPresent()) {
            dt = diagramTypeUtil.fromString(result.get());
            presetNamePrompt(dt);
        }
    }

    /**
     * handles the preset name prompt. Checks whether there is already a preset with
     * that name present and rejects the call if it is.
     *
     * @param dt {@link DiagramType} in order to know which flag to set
     */
    private void presetNamePrompt(DiagramType dt) {
        TextInputDialog nameDialogue = dialogUtil.textInputDialog("prompt_preset_name_title",
                "prompt_preset_name_header", "prompt_preset_name_content");

        Optional<String> result = nameDialogue.showAndWait();
        if (result.isPresent() && result.get().equals("")) {
            emptyNameAlert();
        } else if (result.isPresent()
                && !super.presets.stream().map(p -> p.getName()).anyMatch(n -> n.equals(result.get()))) {
            currentPreset = new Preset(currentOptions, result.get(), dt);
            currentPreset.getOptions().setAutoScale(true);
            presetService.create(currentPreset);
            super.presets.add(currentPreset);

        } else if (result.isPresent()) {
            duplicateAlert(result.get());
        }
    }

    /**
     * refreshes the content of the table by iterating through the preset list and
     * adding each one to the vBox_Preset_DataTable
     */
    private void loadTable() {
        HBox header = (HBox) vBox_Preset_DataTable.getChildren().get(0);
        vBox_Preset_DataTable.getChildren().clear();
        vBox_Preset_DataTable.getChildren().add(header);
        for (Preset preset : super.presets) {
            vBox_Preset_DataTable.getChildren().add(generateTableEntry(preset));
        }
    }

    /**
     * initiates the sorting buttons for the overview pane
     */
    private void initSortingBtns() {
        Glyph chevronDown = new Glyph("FontAwesome", FontAwesome.Glyph.CHEVRON_DOWN);
        chevronDown.setColor(javafx.scene.paint.Color.valueOf("#fff"));
        Glyph chevronUp = new Glyph("FontAwesome", FontAwesome.Glyph.CHEVRON_UP);
        chevronUp.setColor(javafx.scene.paint.Color.valueOf("#fff"));
        this.button_sortByName.setOnAction(event -> {
            if (sortBy.equals("name")) {
                sortAsc = !sortAsc;
            } else {
                sortAsc = true;
            }
            if (sortAsc) {
                this.presets.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                button_sortByName.setGraphic(chevronDown);
            } else {
                this.presets.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
                button_sortByName.setGraphic(chevronUp);
            }
            sortBy = "name";
            button_sortByType.setGraphic(null);
            button_sortByDate.setGraphic(null);
            loadTable();
        });
        this.button_sortByDate.setOnAction(event -> {
            if (sortBy.equals("date")) {
                sortAsc = !sortAsc;
            } else {
                sortAsc = true;
            }
            if (sortAsc) {
                this.presets.sort((o1, o2) -> o1.getCreationDate().compareTo(o2.getCreationDate()));
                button_sortByDate.setGraphic(chevronDown);
            } else {
                this.presets.sort((o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()));
                button_sortByDate.setGraphic(chevronUp);
            }
            sortBy = "date";
            button_sortByName.setGraphic(null);
            button_sortByType.setGraphic(null);
            loadTable();
        });
        this.button_sortByType.setOnAction(event -> {
            if (sortBy.equals("type")) {
                sortAsc = !sortAsc;
            } else {
                sortAsc = true;
            }
            if (sortAsc) {
                this.presets.sort((o1, o2) -> {
                    String o1_diagramTypeName = bundle
                            .getString("diagramType_" + o1.getDiagramTypeString().toLowerCase());
                    String o2_diagramTypeName = bundle
                            .getString("diagramType_" + o2.getDiagramTypeString().toLowerCase());
                    return o1_diagramTypeName.compareTo(o2_diagramTypeName);
                });
                button_sortByType.setGraphic(chevronDown);
            } else {
                this.presets.sort((o1, o2) -> {
                    String o1_diagramTypeName = bundle
                            .getString("diagramType_" + o1.getDiagramTypeString().toLowerCase());
                    String o2_diagramTypeName = bundle
                            .getString("diagramType_" + o2.getDiagramTypeString().toLowerCase());
                    return o2_diagramTypeName.compareTo(o1_diagramTypeName);
                });
                button_sortByType.setGraphic(chevronUp);

            }
            sortBy = "type";
            button_sortByName.setGraphic(null);
            button_sortByDate.setGraphic(null);
            loadTable();
        });
    }

    /**
     * dynamically fills the HBox in which the presets are stored visually. adds
     * three columns (name, date, diagramtype) and another three for
     * edit/copy/delete buttons *
     *
     * @param preset
     * @return the {@link HBox}
     */
    private HBox generateTableEntry(final Preset preset) {
        int id = super.presets.indexOf(preset);
        String evenOrOddStyleClass = "even";
        if ((id & 1) != 0) {
            evenOrOddStyleClass = "odd";
        }
        HBox row = new HBox();
        row.setSpacing(5);
        row.getStyleClass().add("data-row");
        row.getStyleClass().add(evenOrOddStyleClass);
        row.setUserData(preset);
        row.setAccessibleRole(AccessibleRole.TABLE_ROW);
        row.setAccessibleText(this.bundle.getString("preset_row") + (vBox_Preset_DataTable.getChildren().size()));

        Label nameField = new Label(preset.getName());
        nameField.setFocusTraversable(true);
        nameField.getStyleClass().add("data-cell");
        nameField.getStyleClass().add("data-cell-x");
        nameField.getStyleClass().add("read-only");
        nameField.setMouseTransparent(true);
        nameField.setAccessibleText(this.bundle.getString("table_column_name_help") + preset.getName());
        nameField.setMaxWidth(1.7976931348623157E308);
        nameField.setMaxHeight(1.7976931348623157E308);

        Label creationDateField = new Label(preset.getFormattedCreationDate());
        creationDateField.setFocusTraversable(true);
        creationDateField.getStyleClass().add("data-cell");
        creationDateField.getStyleClass().add("data-cell-y");
        creationDateField.getStyleClass().add("read-only");
        creationDateField.setAccessibleText(bundle.getString("table_column_date") + preset.getFormattedCreationDate());
        creationDateField.setMouseTransparent(true);
        creationDateField.setMaxWidth(1.7976931348623157E308);
        creationDateField.setMaxHeight(1.7976931348623157E308);

        Label diagramTypeField = new Label(
                bundle.getString("diagramType_" + preset.getDiagramTypeString().toLowerCase()));
        diagramTypeField.setFocusTraversable(true);
        diagramTypeField.getStyleClass().add("data-cell");
        diagramTypeField.getStyleClass().add("data-cell-z");
        diagramTypeField.getStyleClass().add("read-only");
        diagramTypeField.setAccessibleText(bundle.getString("table_column_type")
                + bundle.getString("diagramType_" + preset.getDiagramTypeString().toLowerCase()));
        diagramTypeField.setMaxWidth(1.7976931348623157E308);
        diagramTypeField.setMaxHeight(1.7976931348623157E308);

        Button editButton = new Button();
        Glyph editGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.EDIT);
        editButton.setGraphic(editGlyph);
        editButton.setTooltip(new Tooltip(this.bundle.getString("preset_edit")));
        editButton.setOnAction(event -> {
            currentPreset = preset;
            overViewHider();
            initEditor();
            switch (currentPreset.getDiagramType()) {
                case FunctionPlot:
                    initFunction();
                    break;
                case LineChart:
                    initLineChart();
                    functionRadioButtonHider();
                    break;
                case ScatterPlot:
                    initScatterPlot();
                    functionRadioButtonHider();
                    break;
                case BarChart:
                    initBarChart();
                    functionRadioButtonHider();
                    break;
            }
            editorDisplayer();
            loadFlagsFromPreset(currentPreset);
        });

        Button copyButton = new Button();
        Glyph copyGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.COPY);
        copyButton.setGraphic(copyGlyph);
        copyButton.setTooltip(new Tooltip(this.bundle.getString("preset_copy")));
        copyButton.setOnAction(event -> {
            Preset copiedPreset = new Preset(preset);
            // presetname is a copy of another preset and ends with a digit
            if (copiedPreset.getName().contains(bundle.getString("preset_copy"))
                    && Character.isDigit(copiedPreset.getName().charAt(copiedPreset.getName().length() - 1))) {
                String lastDigit = copiedPreset.getName().substring(copiedPreset.getName().length() - 1);
                int amountOfCopies = Integer.valueOf(lastDigit);
                amountOfCopies++;
                String newName = copiedPreset.getName().substring(0, copiedPreset.getName().length() - 1)
                        + amountOfCopies;

                copiedPreset.setName(newName);
            } else {
                int amountOfCopies = 1;
                String newName = copiedPreset.getName() + " " + bundle.getString("preset_copy") + amountOfCopies;
                while (!this.presetService.findByName(newName).isEmpty()) {
                    amountOfCopies++;
                    newName = copiedPreset.getName() + " " + bundle.getString("preset_copy") + amountOfCopies;
                }
                copiedPreset.setName(newName);
            }
            this.presetService.create(copiedPreset);
            this.presets.add(copiedPreset);
        });

        Button removeButton = new Button();
        Glyph removeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.TRASH);
        removeButton.setGraphic(removeGlyph);
        removeButton.setTooltip(new Tooltip(this.bundle.getString("preset_remove")));
        removeButton.setOnAction(event -> {
            deleteConfirmationAlert(preset);
        });

        row.getChildren().addAll(nameField, creationDateField, diagramTypeField, editButton, copyButton, removeButton);
        row.setFocusTraversable(true);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        HBox.setHgrow(diagramTypeField, Priority.ALWAYS);
        HBox.setHgrow(creationDateField, Priority.ALWAYS);
        HBox.setHgrow(editButton, Priority.NEVER);
        HBox.setHgrow(copyButton, Priority.NEVER);
        HBox.setHgrow(removeButton, Priority.NEVER);
        return row;
    }

    /**
     * loads the saved options of the current editor frame and sets the
     * corresponding option into the current preset
     */
    private void saveFlagsToPreset() {

        PageSize.PageOrientation page_orientation;
        GuiSvgOptions editor_options = currentPreset.getOptions();
        editor_options.setDiagramType(currentPreset.getDiagramType());

        // Stage 1: basics
        editor_options.setOutputDevice(choiceBox_outputDevice.getSelectionModel().getSelectedItem());

        if (radioBtn_portrait.isSelected()) {
            page_orientation = PageSize.PageOrientation.PORTRAIT;
        } else {
            page_orientation = PageSize.PageOrientation.LANDSCAPE;
        }
        if (choiceBox_size.getSelectionModel().getSelectedItem().equals(PageSize.CUSTOM)) {
            double custom_height = Double.parseDouble(textField_customSizeHeight.getText());
            double custom_width = Double.parseDouble(textField_customSizeWidth.getText());
            System.out.println(custom_width);
            System.out.println(custom_height);
            editor_options.setSize(new Point(custom_height, custom_width));
        } else {
            editor_options.setSize(
                    choiceBox_size.getSelectionModel().getSelectedItem().getPageSizeWithOrientation(page_orientation));
        }

        // Stage 2: data
        if (textField_csvpath.getText() != null) {
            editor_options.setCsvPath(textField_csvpath.getText());
            editor_options.setCsvType((CsvType) choiceBox_csvType.getSelectionModel().getSelectedItem());
        }

        // Stage 3: specific
        switch (currentPreset.getDiagramType()) {
            case FunctionPlot:
                // TODO
                // currentPreset.getOptions().setIntegral();

                break;
            case BarChart:
                editor_options.setBarAccumulationStyle(
                        (BarAccumulationStyle) choiceBox_baraccumulation.getSelectionModel().getSelectedItem());
                editor_options.setSortingType((SortingType) choiceBox_sorting.getSelectionModel().getSelectedItem());
                editor_options.setSortOrder((SortOrder) choiceBox_sortOrder.getSelectionModel().getSelectedItem());
                break;
            case LineChart:
                ObservableList<LineStyle> line_styles = FXCollections.observableArrayList();
                line_styles.add((LineStyle) choiceBox_secondLineStyle.getSelectionModel().getSelectedItem());
                line_styles.add((LineStyle) choiceBox_thirdLineStyle.getSelectionModel().getSelectedItem());
                line_styles.add((LineStyle) choiceBox_firstLineStyle.getSelectionModel().getSelectedItem());
                editor_options.setLineStyles(line_styles);
                break;
            case ScatterPlot:
                if (!choiceBox_trendline.getSelectionModel().getSelectedItem().equals(TrendlineAlgorithm.None)) {
                    ObservableList<String> trendline_list = FXCollections.observableArrayList();
                    TrendlineAlgorithm trendline_type = choiceBox_trendline.getSelectionModel().getSelectedItem();
                    trendline_list.add(trendline_type.toString());
                    switch (trendline_type) {
                        case MovingAverage:
                            trendline_list.add(textField_trendline_n.getText());
                            break;
                        case ExponentialSmoothing:
                            trendline_list.add(textField_trendline_alpha.getText());
                            break;
                        case BrownLES:
                            trendline_list.add(textField_trendline_alpha.getText());
                            trendline_list.add(textField_trendline_forecast.getText());
                            break;
                        case LinearRegression:
                            break;
                        default:
                            break;
                    }
                    editor_options.setTrendLine(trendline_list);
                    editor_options.setHideOriginalPoints(
                            (VisibilityOfDataPoints) choiceBox_hide_original_points.getSelectionModel().getSelectedItem());
                    break;
                }
        }

        // Stage 4: axis
        editor_options.setxUnit(textField_xAxisTitle.getText());
        editor_options.setyUnit(textField_yAxisTitle.getText());
        if (radioBtn_scale_to_data.isSelected()) {
            editor_options.setAutoScale(true);
        } else {
            editor_options.setAutoScale(false);
        }

        try {
            if (textField_x_from.getText() != null && textField_x_to.getText() != null) {
                double xFromToDouble = Double.parseDouble(textField_x_from.getText());
                double xToToDouble = Double.parseDouble(textField_x_to.getText());
                editor_options.setxRange(new Range(xFromToDouble, xToToDouble));
            }
            if (textField_y_from.getText() != null && textField_y_to.getText() != null) {
                double yFromToDouble = Double.parseDouble(textField_y_from.getText());
                double yToToDouble = Double.parseDouble(textField_y_to.getText());
                editor_options.setyRange(new Range(yFromToDouble, yToToDouble));
            }
        } catch (NumberFormatException e) {

        }

        // Stage 5: special
        editor_options.setGridStyle((GridStyle) choiceBox_gridStyle.getSelectionModel().getSelectedItem());
        if (textField_helpLinesX.getText() != null && !textField_helpLinesX.getText().isEmpty()) {
            editor_options.setxLines(textField_helpLinesX.getText());
        }
        if (textField_helpLinesY.getText() != null && !textField_helpLinesY.getText().isEmpty()) {
            editor_options.setyLines(textField_helpLinesY.getText());
        }
        editor_options.setAxisStyle((GuiAxisStyle) choiceBox_dblaxes.getSelectionModel().getSelectedItem());

        // Stage 6: display
        if (choiceBox_cssType.getSelectionModel().getSelectedItem().equals(CssType.CUSTOM)) {
            editor_options.setCss(textArea_cssCustom.getText());
        } else if (choiceBox_cssType.getSelectionModel().getSelectedItem().equals(CssType.FILE)) {
            editor_options.setCss(textField_cssPath.getText());
        }
    }

    /**
     * sets the flags from the {@link Preset} into the corresponding
     * {@link TextField}'s, {@link ChoiceBox}es etc.
     *
     * @param p the {@link Preset}, that the flags are being taken out of
     */
    private void loadFlagsFromPreset(Preset p) {
        GuiSvgOptions options = p.getOptions();
        Point point = options.getOptions().getSize();
        PageSize pageSize = PageSize.getByPoint(point);
        PageSize.PageOrientation pageOrientation = PageSize.PageOrientation.getByPoint(options.getOptions().getSize());

        // Stage 1: basics
        choiceBox_outputDevice.getSelectionModel().select(options.getOutputDevice());
        switch (pageOrientation) {
            case LANDSCAPE:
                radioBtn_landscape.setSelected(true);
                break;
            case PORTRAIT:
                radioBtn_portrait.setSelected(true);
                break;
        }
        choiceBox_size.getSelectionModel().select(PageSize.getByPoint(point));
        if (pageSize.equals(PageSize.CUSTOM)) {
            if (pageOrientation.equals(PageSize.PageOrientation.PORTRAIT)) {
                textField_customSizeWidth.setText("" + point.x());
                textField_customSizeHeight.setText("" + point.y());
            } else {
                textField_customSizeWidth.setText("" + point.y());
                textField_customSizeHeight.setText("" + point.x());
            }
        }

        // Stage 2: data
        textField_csvpath.setText(options.getCsvPath());
        choiceBox_csvType.getSelectionModel().select(options.getCsvType());

        // Stage 3: specific
        switch (currentPreset.getDiagramType()) {
            case FunctionPlot:
                if (!options.getFunctions().isEmpty()) {
                    System.out.println(options.getFunctions());
                    System.out.println(options.getFunctionsAsList());
                } else {
                    choiceBox_integralOption.getSelectionModel().select(IntegralOption.NONE);
                }
                break;
            case BarChart:
                if (options.getTexturesAsList().size() != 0) {
                    choiceBox_firstTexture.getSelectionModel().select(options.getTextures().get(0));
                    choiceBox_secondTexture.getSelectionModel().select(options.getTextures().get(1));
                    choiceBox_thirdTexture.getSelectionModel().select(options.getTextures().get(2));
                } else {
                    choiceBox_firstTexture.getSelectionModel().select(0);
                    choiceBox_secondTexture.getSelectionModel().select(1);
                    choiceBox_thirdTexture.getSelectionModel().select(2);
                }
                choiceBox_baraccumulation.getSelectionModel().select(options.getBarAccumulationStyle());
                choiceBox_sorting.getSelectionModel().select(options.getSortingType());
                choiceBox_sortOrder.getSelectionModel().select(options.getSortOrder());
                break;
            case LineChart:
                if (options.getLineStylesAsList().size() != 0) {
                    choiceBox_firstLineStyle.getSelectionModel().select(options.getLineStyles().get(0));
                    choiceBox_secondLineStyle.getSelectionModel().select(options.getLineStyles().get(1));
                    choiceBox_thirdLineStyle.getSelectionModel().select(options.getLineStyles().get(2));
                } else {
                    // defaults
                    choiceBox_firstLineStyle.getSelectionModel().select(1);
                    choiceBox_secondLineStyle.getSelectionModel().select(2);
                    choiceBox_thirdLineStyle.getSelectionModel().select(0);
                }
                if (options.getHideOriginalPoints() == VisibilityOfDataPoints.HIDE) {
                    choiceBox_hide_original_points.getSelectionModel().select(0);
                } else {
                    // TODO
                }
                break;
            case ScatterPlot:
                if (!options.getTrendLine().isEmpty()) {
                    switch (options.getTrendLine().get(0)) {
                        case "MovingAverage":
                            choiceBox_trendline.getSelectionModel().select(1);
                            textField_trendline_n.setText(options.getTrendLine().get(1));
                            break;
                        case "ExponentialSmoothing":
                            choiceBox_trendline.getSelectionModel().select(2);
                            textField_trendline_alpha.setText(options.getTrendLine().get(1));
                            break;
                        case "BrownLES":
                            choiceBox_trendline.getSelectionModel().select(3);
                            textField_trendline_alpha.setText(options.getTrendLine().get(1));
                            textField_trendline_forecast.setText(options.getTrendLine().get(2));
                            break;
                        case "LinearRegression":
                            choiceBox_trendline.getSelectionModel().select(4);
                            break;
                    }
                } else {
                    choiceBox_trendline.getSelectionModel().select(0);
                }
                choiceBox_hide_original_points.getSelectionModel().select(options.getHideOriginalPoints());
                break;

        }

        // Stage 4: axis
        textField_xAxisTitle.setText(options.getxUnit());
        textField_yAxisTitle.setText(options.getyUnit());
        if (options.isAutoScale()) {
            scaleGroup.selectToggle(radioBtn_scale_to_data);
        } else {
            scaleGroup.selectToggle(radioBtn_customScale);
            if (options.getxRange() != null && options.getyRange() != null) {
                textField_x_from.setText(String.valueOf(options.getxRange().getFrom()));
                textField_x_to.setText(String.valueOf(options.getxRange().getTo()));
                textField_y_from.setText(String.valueOf(options.getyRange().getFrom()));
                textField_y_to.setText(String.valueOf(options.getyRange().getTo()));
            } else {
                textField_x_from.setText("-8.0");
                textField_x_to.setText("8.0");
                textField_y_from.setText("-8.0");
                textField_y_to.setText("8.0");
            }
        }

        // Stage 5: special
        if (options.getGridStyle() != null) {
            choiceBox_gridStyle.getSelectionModel().select(options.getGridStyle());
        } else {
            choiceBox_gridStyle.getSelectionModel().select(0);
        }
        textField_helpLinesX.setText(options.getxLines());
        textField_helpLinesY.setText(options.getyLines());
        choiceBox_dblaxes.getSelectionModel().select(options.getAxisStyle());
        // Stage 6: display
        if (options.getCss() != null && !options.getCss().isEmpty()) {
            // checks (rather foolishly) whether the saved css type is a path or rather a
            // custom string
            if (options.getCss().contains("/")) {
                choiceBox_cssType.getSelectionModel().select(CssType.FILE);
                textField_cssPath.setText(options.getCss());
            } else {
                choiceBox_cssType.getSelectionModel().select(CssType.CUSTOM);
                textArea_cssCustom.setText(options.getCss());
            }
        }
    }

    private void overviewDisplayer() {
        loadTable();
        overViewBorderPane.setVisible(true);
    }

    private void overViewHider() {
        overViewBorderPane.setVisible(false);
    }

    private void editorDisplayer() {
        borderPane_editor.setVisible(true);
    }

    private void editorHider() {
        borderPane_editor.setVisible(false);
    }

    /**
     * handles the delete button onclick and prompts an deleteConfirmationAlert
     */
    @FXML
    private void deletePreset() {
        deleteConfirmationAlert(currentPreset);
    }

    private void hideAllEditors() {
        editorHider();
    }

    /**
     * handles the back button onclick and toggles visibility for editors and
     * overview pane. also resets the specific visibility booleans
     */
    @FXML
    private void backToOverview() {
        hideAllEditors();
        overviewDisplayer();
        resetSpecifics();
    }

    /**
     * handle for the save Preset Button. Gets all the options via the
     * saveFlagsToPreset function and saves it via the presetServer.save function an
     * empty name will cause an alert to be triggered
     */
    @FXML
    private void savePreset() {
        if (!textField_presetName.getText().trim().isEmpty()) {
            currentPreset.setName(textField_presetName.getText());
            saveFlagsToPreset();
            hideAllEditors();
            presetService.save(currentPreset);
            overviewDisplayer();
        } else {
            emptyNameAlert();
        }
    }

    /**
     * checks whether the {@link TextField} of the presetName is empty
     *
     * @return the status
     */
    public boolean isPresetNameEmpty() {
        return textField_presetName.getText().isEmpty();
    }

    /**
     * prompts for confirmation of the user if the {@link Preset} shall really be
     * deleted
     *
     * @param p the current preset
     */
    public void deleteConfirmationAlert(Preset p) {
        Alert alert = dialogUtil.alert(Alert.AlertType.CONFIRMATION, "alert_preset_delete_title",
                "alert_preset_delete_header", "alert_preset_delete_content1");
        alert.setContentText(bundle.getString("alert_preset_delete_content1") + p.getName()
                + bundle.getString("alert_preset_delete_content2"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            this.presetService.delete(p);
            super.presets.remove(p);
            hideAllEditors();
            overviewDisplayer();
        } else {
            // ... user chose CANCEL or closed the dialog, hence do nothing
        }
    }

    /**
     * prompts for confirmation of the user if the {@link Preset} shall really be
     * deleted
     *
     * @param duplicate the name of the preset that is a duplicate as a String
     */
    public void duplicateAlert(String duplicate) {
        Alert alert = dialogUtil.alert(Alert.AlertType.ERROR, "alert_preset_duplicate_title",
                "alert_preset_duplicate_header1", "alert_preset_duplicate_content");
        alert.setHeaderText(bundle.getString("alert_preset_duplicate_header1") + duplicate
                + bundle.getString("alert_preset_duplicate_header2"));
        alert.showAndWait();
    }

    /**
     * handles the alert prompt when the name of the preset is empty
     */
    public void emptyNameAlert() {
        Alert alarm = dialogUtil.alert(Alert.AlertType.ERROR, "alert_preset_empty_title", "alert_preset_empty_header",
                "alert_preset_empty_content");
        alarm.showAndWait();
    }

    /**
     * handles the cancel button onclick call and resets the specific options in the
     * editor pane
     */
    @FXML
    private void quitToMainMenu() {
        GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
        GuiSvgPlott.getInstance().getRootFrameController().menuItem_Preset_Editor.setDisable(false);
        resetSpecifics();
        GuiSvgPlott.getInstance().closeWizard(true);
    }

}