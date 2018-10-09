package application.controller;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.model.Options.*;
import application.model.Preset;
import application.model.Settings;
import application.service.PresetService;
import application.util.Converter;
import application.util.DiagramTypeUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.plotting.line.LineStyle;
import tud.tangram.svgplot.plotting.point.PointSymbol;
import tud.tangram.svgplot.plotting.texture.Texture;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.GridStyle;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @author Constantin Amend
 */
public class PresetsController extends SVGWizardController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(PresetsController.class);
    private ResourceBundle bundle;

    private static PresetsController presetsController;


    public Converter converter = Converter.getInstance();
    private Preset currentPreset;
    private GuiSvgOptions currentOptions = new GuiSvgOptions(new SvgPlotOptions());
    private final ToggleGroup scaleGroup = new ToggleGroup();
    private final ToggleGroup valueRangeGroup = new ToggleGroup();
    private final ToggleGroup integralToggle = new ToggleGroup();
    @FXML
    private final ToggleGroup pageOrientationTG = new ToggleGroup();
    private final DiagramTypeUtil diagramTypeUtil = DiagramTypeUtil.getInstance();
    private PresetService presetService = PresetService.getInstance();
    private ObservableList<Texture> textures;
    private ObservableList<PointSymbol> customPointSymbols_scatterPlott;

    Glyph abortGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CLOSE);
    Glyph closeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CLOSE);
    Glyph backGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.CHEVRON_LEFT);
    Glyph saveGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.SAVE);
    Glyph deleteGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.TRASH);


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
    private HBox hBox_integral_integral;
    @FXML
    private ChoiceBox choiceBox_sorting;
    @FXML
    private ChoiceBox choiceBox_firstTexture;
    @FXML
    private ChoiceBox choiceBox_secondTexture;
    @FXML
    private ChoiceBox choiceBox_thirdTexture;
    @FXML
    private ChoiceBox choiceBox_trendline;
    @FXML
    private Label label_firstTexture;
    @FXML
    private Label label_secondTexture;
    @FXML
    private Label label_thirdTexture;

    @FXML
    private Label label_sortOrder;
    @FXML
    private ChoiceBox choiceBox_sortOrder;
    @FXML
    private Label label_sorting;

    @FXML
    private TableColumn table_column_name;
    @FXML
    private TableColumn table_column_date;
    @FXML
    private TableColumn table_column_diagram_type;
    @FXML
    private ObservableList<String> chartTypeObservableList = FXCollections.observableArrayList();

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
    private ChoiceBox choiceBox_csvType;
    @FXML
    private ChoiceBox choiceBox_gridStyle;
    @FXML
    private ChoiceBox choiceBox_dblaxes;
    @FXML
    private ChoiceBox<CssType> choiceBox_cssType;
    @FXML
    private ChoiceBox choiceBox_linepoints;
    @FXML
    private ChoiceBox choiceBox_secondLineStyle;
    @FXML
    private ChoiceBox<PointSymbol> choiceBox_pointSymbols_lineChart;
    @FXML
    private ChoiceBox<PointSymbol> choiceBox_pointSymbols_scatterPlot;
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
    private HBox hBox_cssPath;
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
    private TextField textField_helpLinesX;
    @FXML
    private TextField textField_helpLinesY;
    @FXML
    private TextField textField_presetName;
    @FXML
    private RadioButton radioBtn_scale_to_data;
    @FXML
    private RadioButton radioBtn_customScale;
    @FXML
    private RadioButton radioBtn_portrait;
    @FXML
    private RadioButton radioBtn_landscape;
    @FXML
    private RadioButton radioBtn_valueRange_default;
    @FXML
    private RadioButton radioBtn_valueRange_custom;
    @FXML
    private BorderPane overViewBorderPane;
    @FXML
    private BorderPane borderPane_editor;
    @FXML
    private ChoiceBox choiceBox_thirdLineStyle;
    @FXML
    private ChoiceBox choiceBox_firstLineStyle;
    @FXML
    private ChoiceBox choiceBox_baraccumulation;
    @FXML
    private Label label_hide_original_points;
    @FXML
    private ChoiceBox choiceBox_hide_original_points;
    @FXML
    private TextField textField_rangeFrom;
    @FXML
    private TextField textField_rangeTo;
    @FXML
    private Label label_rangeFrom;
    @FXML
    private Label label_rangeTo;
    @FXML
    private Label label_integral_name;
    @FXML
    private TextField textField_integralName;
    @FXML
    private Label label_integral_function1;
    @FXML
    private AnchorPane anchorPane_function1;
    @FXML
    private AnchorPane anchorPane_function2;
    @FXML
    private Label label_integral_measuring;
    @FXML
    private RadioButton radioBtn_x_axis;
    @FXML
    private RadioButton radioBtn_integral_func_1;
    @FXML
    private Label label_valueRange;
    @FXML
    private HBox hBox_valueRange;


    @FXML
    private Button button_sortByName;
    @FXML
    private Button button_sortByDate;
    @FXML
    private Button button_sortByType;

    private String sortBy;
    private boolean sortAsc;
    //protected TextFieldUtil textFieldUtil = TextFieldUtil.getInstance();


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
     * resets the visibilty of {@link TextField}'s, {@link ChoiceBox}es, {@link HBox}es etc.
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
        toggleVisibility(false, label_integral_name, textField_integralName);
        toggleVisibility(false, label_integral_function1, anchorPane_function1);
        toggleVisibility(false, label_integral_measuring, hBox_integral_integral);
        toggleVisibility(false, label_valueRange, hBox_valueRange);
        toggleVisibility(false, label_trendline, choiceBox_trendline);
        toggleVisibility(false, label_trendline_alpha, textField_trendline_alpha);
        toggleVisibility(false, label_trendline_forecast, textField_trendline_forecast);
        toggleVisibility(false, label_trendline_n, textField_trendline_n);
        toggleVisibility(false, label_hide_original_points, choiceBox_hide_original_points);
    }


    /**
     * initiates the general options in the preset editor. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initEditor() {
        //outputdevice
        choiceBox_outputDevice.setItems(FXCollections.observableArrayList(OutputDevice.values()));
        choiceBox_outputDevice.setConverter(converter.getOutputDeviceStringConverter());

        pageOrientationTG.setUserData(radioBtn_portrait);
        pageOrientationTG.setUserData(radioBtn_landscape);

        //pagesize
        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        choiceBox_size.setItems(sortedPageSizes);
        choiceBox_size.setConverter(converter.getPageSizeStringConverter());
        choiceBox_size.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
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
            }
        });


        //CSV
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

        //axis
        //textFieldUtil.addDoubleValidation(textField_x_from, label_x_from);
        //textFieldUtil.addDoubleValidation(textField_x_to, label_x_to);
        //textFieldUtil.addNotEqualValidation(textField_x_from, label_x_from, textField_x_to, label_x_to);
        //textFieldUtil.addFirstNotGreaterThanSecondValidationListener(textField_x_from, label_xfrom, textField_x_to, label_x_to);

        //gridstyle
        choiceBox_gridStyle.setItems(FXCollections.observableArrayList(GridStyle.values()));
        choiceBox_gridStyle.setConverter(converter.getGridStyleStringConverter());

        //doubleaxis
        choiceBox_dblaxes.setItems(FXCollections.observableArrayList(GuiAxisStyle.values()));
        choiceBox_dblaxes.setConverter(converter.getAxisStyleStringConverter());

        //CSS
        choiceBox_cssType.setItems(FXCollections.observableArrayList(CssType.values()));
        choiceBox_cssType.setConverter(converter.getCssTypeStringConverter());
        choiceBox_cssType.getSelectionModel().select(CssType.NONE);
        choiceBox_cssType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                switch (newValue.toString()) {
                    case "0":
                        cssCustomHider();
                        break;
                    case "1":
                        cssCustomHider();
                        label_cssCustom.setVisible(true);
                        textArea_cssCustom.setVisible(true);
                        break;
                    case "2":
                        cssCustomHider();
                        label_cssPath.setVisible(true);
                        hBox_cssPath.setVisible(true);
                        break;
                }
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
        radioBtn_customScale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    label_x_from.setVisible(true);
                    textField_x_from.setVisible(true);
                    label_x_to.setVisible(true);
                    textField_x_to.setVisible(true);
                    label_y_from.setVisible(true);
                    textField_y_from.setVisible(true);
                    label_y_to.setVisible(true);
                    textField_y_to.setVisible(true);
                }
                if (currentPreset.getOptions().getxRange() == null || currentPreset.getOptions().getyRange() == null) {
                    textField_x_from.setText("-8.0");
                    textField_x_to.setText("8.0");
                    textField_y_from.setText("-8.0");
                    textField_y_to.setText("8.0");
                }
            }
        });
        radioBtn_scale_to_data.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    label_x_from.setVisible(false);
                    textField_x_from.setVisible(false);
                    label_x_to.setVisible(false);
                    textField_x_to.setVisible(false);
                    label_y_from.setVisible(false);
                    textField_y_from.setVisible(false);
                    label_y_to.setVisible(false);
                    textField_y_to.setVisible(false);
                }
            }
        });

    }

    private void cssCustomHider() {
        toggleVisibility(false, label_cssCustom, textArea_cssCustom);
        toggleVisibility(false, label_cssPath, hBox_cssPath);
    }

    /**
     * initiates the linechart specific options in the preset editor. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initLineChart() {
        textField_presetName.setText(currentPreset.getName());
        toggleVisibility(true, label_secondLineStyle, hBox_secondLineStyle);
        toggleVisibility(true, label_thirdLineStyle, hBox_thirdLineStyle);
        toggleVisibility(true, label_firstLineStyle, hBox_firstLineStyle);
        toggleVisibility(true, label_linepoints, choiceBox_linepoints);
        ObservableList<LineStyle> secondLineStyleObservableList = FXCollections.observableArrayList(LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_secondLineStyle.setItems(secondLineStyleObservableList);
        choiceBox_secondLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        ObservableList<LineStyle> thirdLineStyleObservableList = FXCollections.observableArrayList(LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_thirdLineStyle.setItems(thirdLineStyleObservableList);
        choiceBox_thirdLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        ObservableList<LineStyle> firstLineStyleObservableList = FXCollections.observableArrayList(LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_firstLineStyle.setItems(firstLineStyleObservableList);
        choiceBox_firstLineStyle.setConverter(this.converter.getLineStyleStringConverter());
        ObservableList<LinePointsOption> linePointsOptionObservableList = FXCollections.observableArrayList(LinePointsOption.values());
        choiceBox_linepoints.setItems(linePointsOptionObservableList);
        choiceBox_linepoints.setConverter(converter.getLinePointsOptionStringConverter());

        choiceBox_linepoints.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //TODO linepointsymbols
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
     * initiates the scatterplot specific options in the preset editor. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initScatterPlot() {
        textField_presetName.setText(currentPreset.getName());
        toggleVisibility(true, label_trendline, choiceBox_trendline);
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections.observableArrayList(TrendlineAlgorithm.values());
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


        ObservableList<VisibilityOfDataPoints> dataPoints = FXCollections.observableArrayList(VisibilityOfDataPoints.values());
        choiceBox_hide_original_points.setItems(dataPoints);
        choiceBox_hide_original_points.setConverter(converter.getVisibilityOfDataPointsStringConverter());
        choiceBox_hide_original_points.getSelectionModel().select(VisibilityOfDataPoints.HIDE);
        //TODO Pointsymbols
        /*ObservableList<PointSymbol> pointSymbolObservableList = FXCollections.observableArrayList(PointSymbol.getOrdered());
        this.customPointSymbols_scatterPlott = guiSvgOptions.getPointSymbols();
        choiceBox_pointSymbols_scatterPlot.setVisible(true);
        choiceBox_pointSymbols_scatterPlot.getItems().addAll(pointSymbolObservableList);
        choiceBox_pointSymbols_scatterPlot.setConverter(this.converter.getPointSymbolStringConverter());
        choiceBox_pointSymbols_scatterPlot.getCheckModel().getCheckedItems().addListener(new ListChangeListener<PointSymbol>() {

            public void onChanged(ListChangeListener.Change<? extends PointSymbol> ps) {
                changePointSymbols(customPointSymbols_scatterPlott, choiceBox_pointSymbols_scatterPlot, pointSymbolObservableList);
            }
        });*/
    }


    /**
     * initiates the barchart specific options in the preset editor. Depending on {@code extended}, some parts will be dis- or enabled.
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
        ObservableList<BarAccumulationStyle> barAccumulationStyleObservableList = FXCollections.observableArrayList(BarAccumulationStyle.values());
        choiceBox_baraccumulation.setItems(barAccumulationStyleObservableList);
        choiceBox_baraccumulation.setConverter(converter.getBarAccumulationStyleStringConverter());

        ObservableList<Texture> textureObservableListFirstTexture = FXCollections.observableArrayList(Texture.values());
        ObservableList<Texture> textureObservableListSecondTexture = FXCollections.observableArrayList(Texture.values());
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
     * initiates the function specific options in the preset editor. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initFunction() {
        textField_presetName.setText(currentPreset.getName());
        toggleVisibility(true, label_integral_name, textField_integralName);
        toggleVisibility(true, label_integral_function1, anchorPane_function1);
        toggleVisibility(true, label_integral_measuring, hBox_integral_integral);
        toggleVisibility(true, label_valueRange, hBox_valueRange);

        radioBtn_x_axis.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    anchorPane_function2.setVisible(false);
                }
            }
        });

        radioBtn_integral_func_1.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    anchorPane_function2.setVisible(true);
                }
            }
        });

        radioBtn_valueRange_default.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    toggleVisibility(false, label_rangeFrom, textField_rangeFrom);
                    toggleVisibility(false, label_rangeTo, textField_rangeTo);
                }
            }
        });
        radioBtn_valueRange_custom.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    toggleVisibility(true, label_rangeFrom, textField_rangeFrom);
                    toggleVisibility(true, label_rangeTo, textField_rangeTo);
                }
            }
        });


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

    /**
     * handle for the create new preset button from the fxml file.
     * leads to the diagramTypePrompt function
     */
    @FXML
    private void createNewPreset() {
        diagramTypePrompt();
    }

    /**
     * starts the prompt in which the user can choose the {@link DiagramType}
     * leads to the presetNamePrompt function with the chosen {@link DiagramType}
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

        Stage stage = (Stage) dialogue.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Settings.getInstance().favicon);

        Optional<String> result = dialogue.showAndWait();
        if (result.isPresent()) {
            dt = diagramTypeUtil.fromString(result.get());
            presetNamePrompt(dt);
        }
    }

    /**
     * handles the preset name prompt. Checks whether there is already a preset with that name present and rejects the call if it is.
     *
     * @param dt {@link DiagramType} in order to know which flag to set
     */
    private void presetNamePrompt(DiagramType dt) {
        TextInputDialog nameDialogue = new TextInputDialog();
        nameDialogue.setTitle(bundle.getString("prompt_preset_name_title"));
        nameDialogue.setHeaderText(bundle.getString("prompt_preset_name_header"));
        nameDialogue.setContentText(bundle.getString("prompt_preset_name_content"));

        Stage stage = (Stage) nameDialogue.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Settings.getInstance().favicon);

        Optional<String> result = nameDialogue.showAndWait();
        if (result.get().equals("")) {
            emptyNameAlert();
        } else if (result.isPresent() && !super.presets.stream().map(p -> p.getName()).anyMatch(n -> n.equals(result.get()))) {
            currentPreset = new Preset(currentOptions, result.get(), dt);
            currentPreset.getOptions().setAutoScale(true);
            presetService.create(currentPreset);
            super.presets.add(currentPreset);

        } else {
            duplicateAlert(result);
        }
    }

    /**
     * refreshes the content of the table by iterating through the preset list and adding each one to the vBox_Preset_DataTable
     */
    private void loadTable() {
        HBox header = (HBox) vBox_Preset_DataTable.getChildren().get(0);
        vBox_Preset_DataTable.getChildren().clear();
        vBox_Preset_DataTable.getChildren().add(header);
        for (Preset preset : super.presets) {
            vBox_Preset_DataTable.getChildren().add(generateTableEntry(preset));
        }
    }

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
                    String o1_diagramTypeName = bundle.getString("diagramType_" + o1.getDiagramTypeString().toLowerCase());
                    String o2_diagramTypeName = bundle.getString("diagramType_" + o2.getDiagramTypeString().toLowerCase());
                    return o1_diagramTypeName.compareTo(o2_diagramTypeName);
                });
                button_sortByType.setGraphic(chevronDown);
            }else {
                this.presets.sort((o1, o2) -> {
                    String o1_diagramTypeName = bundle.getString("diagramType_" + o1.getDiagramTypeString().toLowerCase());
                    String o2_diagramTypeName = bundle.getString("diagramType_" + o2.getDiagramTypeString().toLowerCase());
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
     * dynamically fills the HBox in which the presets are stored visually.
     * adds three columns (name, date, diagramtype) and another three for edit/copy/delete buttons     *
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

        Label diagramTypeField = new Label(bundle.getString("diagramType_" + preset.getDiagramTypeString().toLowerCase()));
        diagramTypeField.setFocusTraversable(true);
        diagramTypeField.getStyleClass().add("data-cell");
        diagramTypeField.getStyleClass().add("data-cell-z");
        diagramTypeField.getStyleClass().add("read-only");
        diagramTypeField.setAccessibleText(bundle.getString("table_column_type") + bundle.getString("diagramType_" + preset.getDiagramTypeString().toLowerCase()));
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
                    break;
                case ScatterPlot:
                    initScatterPlot();
                    break;
                case BarChart:
                    initBarChart();
                    break;
            }
            editorDisplayer();
            flagSetter(currentPreset);
        });

        Button copyButton = new Button();
        Glyph copyGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.COPY);
        copyButton.setGraphic(copyGlyph);
        copyButton.setTooltip(new Tooltip(this.bundle.getString("preset_copy")));
        copyButton.setOnAction(event -> {
            //TODO handle incrementing numbers more efficiently
            Preset copiedPreset = new Preset(preset);
            // presetname is a copy of another preset and ends with a digit
            if (copiedPreset.getName().contains(bundle.getString("preset_copy")) && Character.isDigit(copiedPreset.getName().charAt(copiedPreset.getName().length() - 1))) {
                String lastDigit = copiedPreset.getName().substring(copiedPreset.getName().length() - 1);
                int amountOfCopies = Integer.valueOf(lastDigit);
                amountOfCopies++;
                String newName = copiedPreset.getName().substring(0, copiedPreset.getName().length() - 1) + amountOfCopies;
                copiedPreset.setName(newName);
            } else {
                copiedPreset.setName(copiedPreset.getName() + " " + bundle.getString("preset_copy") + " 1");
            }
            vBox_Preset_DataTable.getChildren().add(generateTableEntry(copiedPreset));
            this.presetService.create(preset);
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
     * loads the saved options of the current editor frame and sets the corresponding option into the current preset
     */
    private void flagGetter() {
        //Stage 1: basics
        currentPreset.getOptions().setOutputDevice(choiceBox_outputDevice.getSelectionModel().getSelectedItem());
        //TODO: page orientation
        //currentPreset.getOptions().set
        //TODO:
        currentOptions.setSize(currentPreset.getOptions().getSize());

        //Stage 2: data
        currentPreset.getOptions().setCsvPath(textField_csvpath.getText());
        currentPreset.getOptions().setCsvType((CsvType) choiceBox_csvType.getSelectionModel().getSelectedItem());

        //Stage 3: specific
        switch (currentPreset.getDiagramType()) {
            case FunctionPlot:
                //TODO
                //currentPreset.getOptions().setIntegral();

                break;
            case BarChart:
                //TODO
                currentPreset.getOptions().setBarAccumulationStyle((BarAccumulationStyle) choiceBox_baraccumulation.getSelectionModel().getSelectedItem());

                currentPreset.getOptions().setSortingType((SortingType) choiceBox_sorting.getSelectionModel().getSelectedItem());
                currentPreset.getOptions().setSortOrder((SortOrder) choiceBox_sortOrder.getSelectionModel().getSelectedItem());
                break;
            case LineChart:
                //TODO

                //currentPreset.getOptions().setLineStyles();
                //currentPreset.getOptions().setLinePointsOption();
                break;
            case ScatterPlot:
                //TODO
                //currentPreset.getOptions().setTrendLine();
                break;

        }
//        currentOptions.setTrendLine(choiceBox_trendline.getSelectionModel().getSelectedItem());

        //Stage 4: axis
        currentPreset.getOptions().setxUnit(textField_xAxisTitle.getText());
        currentPreset.getOptions().setyUnit(textField_yAxisTitle.getText());
        if (radioBtn_scale_to_data.isSelected()) {
            currentPreset.getOptions().setAutoScale(true);
        } else {
            currentPreset.getOptions().setAutoScale(false);
        }


        try {
            if (textField_x_from.getText() != null && textField_x_to.getText() != null) {
                double xFromToDouble = Double.parseDouble(textField_x_from.getText());
                double xToToDouble = Double.parseDouble(textField_x_to.getText());
                currentPreset.getOptions().setxRange(new Range(xFromToDouble, xToToDouble));
            }
            if (textField_y_from.getText() != null && textField_y_to.getText() != null) {
                double yFromToDouble = Double.parseDouble(textField_y_from.getText());
                double yToToDouble = Double.parseDouble(textField_y_to.getText());
                currentPreset.getOptions().setyRange(new Range(yFromToDouble, yToToDouble));
            }
        } catch (NumberFormatException e) {

        }

        //Stage 5: special
        currentPreset.getOptions().setGridStyle((GridStyle) choiceBox_gridStyle.getSelectionModel().getSelectedItem());
        if (textField_helpLinesX.getText() != null && !textField_helpLinesX.getText().isEmpty()) {
            currentPreset.getOptions().setxLines(textField_helpLinesX.getText());
        }
        if (textField_helpLinesY.getText() != null && !textField_helpLinesY.getText().isEmpty()) {
            currentPreset.getOptions().setyLines(textField_helpLinesY.getText());
        }
        currentPreset.getOptions().setAxisStyle((GuiAxisStyle) choiceBox_dblaxes.getSelectionModel().getSelectedItem());

        //System.out.println(choiceBox_cssType.getSelectionModel().getSelectedItem());
        //Stage 6: display
        if (choiceBox_cssType.getSelectionModel().getSelectedItem().equals(CssType.CUSTOM)) {
            currentPreset.getOptions().setCss(textArea_cssCustom.getText());
        } else if (choiceBox_cssType.getSelectionModel().getSelectedItem().equals(CssType.FILE)) {
            currentPreset.getOptions().setCss(textField_cssPath.getText());
        }
    }

    /**
     * sets the flags from the {@link Preset} into the corresponding {@link TextField}'s, {@link ChoiceBox}es etc.
     *
     * @param p the {@link Preset}, that the flags are being taken out of
     */
    private void flagSetter(Preset p) {
        GuiSvgOptions options = p.getOptions();

        //Stage 1: basics
        choiceBox_outputDevice.getSelectionModel().select(options.getOutputDevice());
        //TODO how to get page orientation from presets?

        //TODO Pagesize is not propagated properly
        PageSize pageSize = PageSize.getByPoint(options.getOptions().getSize());
        PageSize.PageOrientation pageOrientation = PageSize.PageOrientation.getByPoint(options.getOptions().getSize());
        switch (pageOrientation){
            case LANDSCAPE:
                pageOrientationTG.selectToggle(radioBtn_landscape);
                break;
            case PORTRAIT:
                pageOrientationTG.selectToggle(radioBtn_portrait);
                break;
        }
        System.out.println(pageOrientationTG.getToggles());
        System.out.println(pageOrientation);
        System.out.println(pageSize.getWidth());
        System.out.println(pageSize.getHeight());
        choiceBox_size.getSelectionModel().select(pageSize);
        if (pageSize.equals(PageSize.CUSTOM)) {
            textField_customSizeWidth.setText("" + pageSize.getWidth());
            textField_customSizeHeight.setText("" + pageSize.getHeight());
        }

        //Stage 2: data
        textField_csvpath.setText(options.getCsvPath());
        choiceBox_csvType.getSelectionModel().select(options.getCsvType());

        //Stage 3: specific
        switch (currentPreset.getDiagramType()) {
            case FunctionPlot:
                //TODO: Robert code goes here after usability overhaul #51

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
                    choiceBox_firstLineStyle.getSelectionModel().select(options.getLineStyles());
                    choiceBox_secondLineStyle.getSelectionModel().select(options.getLineStyles());
                    choiceBox_thirdLineStyle.getSelectionModel().select(options.getLineStyles());
                } else {
                    choiceBox_firstLineStyle.getSelectionModel().select(1);
                    choiceBox_secondLineStyle.getSelectionModel().select(2);
                    choiceBox_thirdLineStyle.getSelectionModel().select(0);
                }
                if (options.getHideOriginalPoints() == VisibilityOfDataPoints.HIDE) {
                    choiceBox_hide_original_points.getSelectionModel().select(0);
                } else {
                    //TODO
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

        //Stage 4: axis
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
        //Stage 5: special

        if (options.getGridStyle() != null) {
            choiceBox_gridStyle.getSelectionModel().select(options.getGridStyle());
        } else {
            choiceBox_gridStyle.getSelectionModel().select(0);
        }
        textField_helpLinesX.setText(options.getxLines());
        textField_helpLinesY.setText(options.getyLines());
        choiceBox_dblaxes.getSelectionModel().select(options.getAxisStyle());
        //Stage 6: display
        //TODO: remove following line before release
        //System.out.println("Der gespeicherte CSS Path lautet: " + options.getCss());

        if (options.getCss() != null && !options.getCss().isEmpty()) {
            //checks (rather foolishly) whether the saved css type is a path or rather a custom string
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
        Preset tobedeletedPreset = currentPreset;
        deleteConfirmationAlert(tobedeletedPreset);
    }

    private void hideAllEditors() {
        editorHider();
    }

    @FXML
    private void backToOverview() {
        hideAllEditors();
        overviewDisplayer();
        resetSpecifics();
    }

    /**
     * handle for the save Preset Button. Gets all the options via the flagGetter function and saves it via the presetServer.save function
     * an empty name will cause an alert to be triggered
     */
    @FXML
    private void savePreset() {
        if (!isPresetNameEmpty()) {
            currentPreset.setName(textField_presetName.getText());
            flagGetter();
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
        if (textField_presetName.getText().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * prompts for confirmation of the user if the {@link Preset} shall really be deleted
     *
     * @param p the current preset
     */
    public void deleteConfirmationAlert(Preset p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("alert_preset_delete_title"));
        alert.setHeaderText(bundle.getString("alert_preset_delete_header"));
        alert.setContentText(bundle.getString("alert_preset_delete_content1") + p.getName() + bundle.getString("alert_preset_delete_content2"));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Settings.getInstance().favicon);

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
     * prompts for confirmation of the user if the {@link Preset} shall really be deleted
     *
     * @param o the name of the preset that is a duplicate as a String
     */
    public void duplicateAlert(Optional o) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("alert_preset_duplicate_title"));
        alert.setHeaderText(bundle.getString("alert_preset_duplicate_header1") + o.get() + bundle.getString("alert_preset_duplicate_header2"));
        alert.setContentText(bundle.getString("alert_preset_duplicate_content"));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Settings.getInstance().favicon);
        alert.showAndWait();
    }

    /**
     * handles the alert prompt when the name of the preset is empty
     */
    public void emptyNameAlert() {
        Alert alarm = new Alert(Alert.AlertType.ERROR);
        alarm.setTitle(bundle.getString("alert_preset_empty_title"));
        alarm.setHeaderText(bundle.getString("alert_preset_empty_header"));
        alarm.setContentText(bundle.getString("alert_preset_empty_content"));
        Stage stage = (Stage) alarm.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Settings.getInstance().favicon);
        alarm.showAndWait();
    }

    /**
     * handles the cancel button onclick call and resets the specific options in the editor pane
     */
    @FXML
    private void quitToMainMenu() {
        GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
        GuiSvgPlott.getInstance().getRootFrameController().menuItem_Preset_Editor.setDisable(false);
        resetSpecifics();
        GuiSvgPlott.getInstance().closeWizard();
    }


}
