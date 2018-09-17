package application.controller.wizard;

import application.GuiSvgPlott;
import application.controller.CsvEditorController;
import application.controller.PresetsController;
import application.controller.wizard.chart.ChartWizardFrameController;
import application.model.GuiSvgOptions;
import application.model.Options.CssType;
import application.model.Options.PageSize;
import application.model.Preset;
import application.service.SvgOptionsService;
import application.util.ChoiceBoxUtil;
import application.util.SvgOptionsUtil;
import application.util.TextFieldUtil;
import com.sun.javafx.scene.control.skin.ScrollPaneSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.Color;
import tud.tangram.svgplot.styles.GridStyle;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static application.controller.RootFrameController.wizardPath;

/**
 * The controller for wizards. Parent of {@link ChartWizardFrameController} and {@link application.controller.wizard.functions.FunctionWizardFrameController}.
 * @author Emma Müller, Robert Schlegel
 */
public class SVGWizardController implements Initializable {
    // start logger
    private static final Logger logger = LoggerFactory.getLogger(SVGWizardController.class);

    public PresetsController presetsController;
    @FXML
    protected Button button_Back;
    @FXML
    public BorderPane borderPane_WizardContent;
    @FXML
    protected HBox hBox_pagination;
    @FXML
    protected Button button_Next;
    @FXML
    protected Button button_Cancel;
    @FXML
    protected Button button_Create;
    @FXML
    protected Button button_Load;
    @FXML
    protected Button button_Edit_Preset;
    @FXML
    public Button button_rerenderPreview;
    //    @FXML
//    protected Label label_Headline;
    @FXML
    protected TabPane tabPane_ContentHolder;
    @FXML
    protected WebView webView_svg;

    @FXML
    public Button button_Warnings;
    @FXML
    public Button button_Infos;

    // generell Options
    @FXML
    protected TextField textField_title;
    @FXML
    protected ChoiceBox<OutputDevice> choiceBox_outputDevice;
    @FXML
    protected ChoiceBox<PageSize> choiceBox_size;
    @FXML
    protected Label label_customSizeWidth;
    @FXML
    protected TextField textField_customSizeWidth;
    @FXML
    protected Label label_customSizeHeight;
    @FXML
    protected TextField textField_customSizeHeight;
    @FXML
    private RadioButton radioBtn_portrait;
    @FXML
    private RadioButton radioBtn_landscape;
    @FXML
    private ToggleGroup toggleGroup_pageOrientation;

    // grid and axis styling options
    @FXML
    public ChoiceBox<GridStyle> choicebox_gridStyle;
    @FXML
    public TextField textField_xlines;
    @FXML
    public TextField textField_ylines;

    // general axis options
    @FXML
    protected TextField textField_xfrom;
    @FXML
    protected Label label_xfrom;
    @FXML
    protected TextField textField_xto;
    @FXML
    protected Label label_xto;
    @FXML
    protected TextField textField_yfrom;
    @FXML
    protected Label label_yfrom;
    @FXML
    protected TextField textField_yto;
    @FXML
    protected Label label_yto;
    // styling fields
    @FXML
    public ChoiceBox<CssType> choiceBox_cssType;
    @FXML
    public Label label_cssPath;
    @FXML
    public TextField textField_cssPath;
    @FXML
    private Button button_cssPath;
    @FXML
    private HBox hBox_cssPath;
    @FXML
    public Label label_cssCustom;
    @FXML
    public TextArea textArea_cssCustom;
    @FXML
    public Label label_colors;

    public CheckComboBox<Color> checkComboBox_color = new CheckComboBox<>();
    // csv fields
    @FXML
    public TextField textField_csvPath;
    @FXML
    private Button button_csvPath;


    @FXML
    public ChoiceBox<CsvOrientation> choiceBox_csvOrientation;
    @FXML
    public ChoiceBox<CsvType> choiceBox_csvType;

    public ObservableList<Preset> presets;

    private ObservableList<String> colors;

    public VBox vBox_warnings;
    private PopOver popOver_warnings;
    public VBox vBox_infos;
    private PopOver popOver_infos;
    private Glyph warnIcon;
    private Glyph infoIcon;

    private Point size;
    private SimpleObjectProperty<PageSize.PageOrientation> pageOrientation;
    protected BooleanProperty isExtended;
    protected List<Button> stageBtns;
    protected List<Button> messageBtns;
    protected ResourceBundle bundle;
    protected IntegerProperty currentStage;
    protected File userDir;
    protected ArrayList<AnchorPane> stages;
    protected GuiSvgOptions guiSvgOptions;
    protected SvgOptionsService svgOptionsService = SvgOptionsService.getInstance();
    protected SvgOptionsUtil svgOptionsUtil = SvgOptionsUtil.getInstance();
    protected TextFieldUtil textFieldUtil = TextFieldUtil.getInstance();
    protected ChoiceBoxUtil choiceBoxUtil = ChoiceBoxUtil.getInstance();
    protected ObjectProperty<Range> xRange;
    protected ObjectProperty<Range> yRange;

    protected File temporaryCSV;
    protected HashMap<DiagramType, File> pointMap;

    protected SimpleObjectProperty<File> currentDataSet;


    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.svgOptionsService.setBundle(resources);
        this.svgOptionsUtil.setBundle(resources);
        this.textFieldUtil.setBundle(resources);
        this.choiceBoxUtil.setBundle(resources);
        this.currentStage = new SimpleIntegerProperty();
        this.isExtended = new SimpleBooleanProperty(false);
        this.size = new Point(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        this.pageOrientation = new SimpleObjectProperty<>(PageSize.PageOrientation.PORTRAIT);
        this.xRange = new SimpleObjectProperty<>();
        this.yRange = new SimpleObjectProperty<>();
        this.userDir = new File(System.getProperty("user.home"));
        this.guiSvgOptions = new GuiSvgOptions(new SvgPlotOptions());
        this.webView_svg.setAccessibleRole(AccessibleRole.PAGE_ITEM);
        this.webView_svg.setAccessibleHelp(this.bundle.getString("preview"));
        this.currentDataSet = new SimpleObjectProperty<>();
        this.pointMap = new HashMap<>();

        this.initListener();
        this.initOptionListeners();
        this.preProcessContent();
        initloadPreset();
        if (presets == null) {
            presets = FXCollections.observableArrayList();
        }
    }

    public GuiSvgOptions getGuiSvgOptions() {
        return this.guiSvgOptions;
    }

    public void setExtended(boolean isExtended) {
        this.isExtended.set(isExtended);
    }

    protected void initGeneralFieldListeners() {
        // title

        focusInit();

        this.textField_title.textProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setTitle(newValue);
        });

        // output device
        ObservableList<OutputDevice> outputDevices = FXCollections.observableArrayList(OutputDevice.values());
        this.choiceBox_outputDevice.setItems(outputDevices);
        this.choiceBox_outputDevice.setConverter(this.svgOptionsUtil.getOutputDeviceStringConverter());
        this.choiceBox_outputDevice.getSelectionModel().select(0);
        this.choiceBox_outputDevice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setOutputDevice(newValue);
            boolean isColorDevice = newValue == OutputDevice.ScreenColor;
            this.toggleVisibility(isColorDevice, this.label_colors, this.checkComboBox_color);
        });

        // size
        this.pageOrientation.addListener((observable, oldValue, newValue) -> {
            this.size = new Point(this.size.getY(), this.size.getX());
            this.guiSvgOptions.setSize(this.size);

            this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);

            this.textField_customSizeWidth.setText(this.size.x());
            this.textField_customSizeHeight.setText(this.size.y());
        });

        this.radioBtn_landscape.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                pageOrientation.set(PageSize.PageOrientation.LANDSCAPE);
            } else {
                pageOrientation.set(PageSize.PageOrientation.PORTRAIT);
            }
        });
        this.radioBtn_portrait.setSelected(true);

        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        this.choiceBox_size.setItems(sortedPageSizes);
        this.choiceBox_size.setConverter(this.svgOptionsUtil.getPageSizeStringConverter());
        this.choiceBox_size.getSelectionModel().select(PageSize.A4);
        this.choiceBox_size.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != PageSize.CUSTOM) {
                this.size = newValue.getPageSizeWithOrientation(this.pageOrientation.get());
                this.guiSvgOptions.setSize(this.size);
            }
            this.toggleCustomSize(newValue == PageSize.CUSTOM);
        });

        // custom size
        this.textField_customSizeWidth.setText(this.size.x());
        this.textFieldUtil.addIntegerValidationWithMinimum(this.textField_customSizeWidth, 1);
        this.textFieldUtil.addMinimumIntegerValidation(this.textField_customSizeWidth, this.label_customSizeWidth, GuiSvgOptions.MINIMUM_PAGE_WIDTH);
        this.textField_customSizeWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            this.size.setX(Integer.parseInt(newValue));
            this.guiSvgOptions.setSize(this.size);
        });
        this.textField_customSizeHeight.setText(this.size.y());
        this.textFieldUtil.addIntegerValidationWithMinimum(this.textField_customSizeHeight, 1);
        this.textFieldUtil.addMinimumIntegerValidation(this.textField_customSizeHeight, this.label_customSizeHeight, GuiSvgOptions.MINIMUM_PAGE_HEIGHT);
        this.textField_customSizeHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            this.size.setY(Integer.parseInt(newValue));
            this.guiSvgOptions.setSize(this.size);
        });

    }

    protected void initAxisFieldListeners() {
        // xRange
        this.textFieldUtil.addNotEqualValidation(this.textField_xfrom, this.label_xfrom, this.textField_xto, this.label_xto);
        this.xRange.addListener((args, oldVal, newVal) -> {
            if (!this.guiSvgOptions.isAutoScale()) {
                this.guiSvgOptions.setxRange(xRange.get());
                if (!this.textField_xfrom.getText().equals("" + newVal.getFrom())) {
                    this.textField_xfrom.setText("" + newVal.getFrom());
                }
                if (!this.textField_xto.getText().equals("" + newVal.getTo())) {
                    this.textField_xto.setText("" + newVal.getTo());
                }
            }
        });
        this.xRange.set(this.guiSvgOptions.getxRange());
        if (this.xRange.get() == null) {
            this.xRange.set(new Range(-8, 8));
        }
        this.textField_xfrom.setText("" + this.xRange.get().getFrom());
        this.textField_xto.setText("" + this.xRange.get().getTo());

        this.textFieldUtil.addDoubleValidation(this.textField_xfrom, this.label_xfrom);
        this.textField_xfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("-")) {
                if (this.xRange.get() == null) {
                    this.xRange.set(new Range(Double.parseDouble(newValue), 8));
                } else {
                    this.xRange.get().setFrom(Double.parseDouble(newValue));
                }
            }
        });
        this.textFieldUtil.addDoubleValidation(this.textField_xto, this.label_xto);
        this.textField_xto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("-")) {
                if (this.xRange.get() == null) {
                    this.xRange.set(new Range(-8, Double.parseDouble(newValue)));
                } else {
                    this.xRange.get().setTo(Double.parseDouble(newValue));
                }
            }
        });


        // yRange
        this.textFieldUtil.addNotEqualValidation(this.textField_yfrom, this.label_yfrom, this.textField_yto, this.label_yto);
        this.yRange.addListener((args, oldVal, newVal) -> {
            if (!this.guiSvgOptions.isAutoScale()) {
                this.guiSvgOptions.setyRange(this.yRange.get());
                if (!this.textField_yfrom.getText().equals("" + newVal.getFrom())) {
                    this.textField_yfrom.setText("" + newVal.getFrom());
                }
                if (!this.textField_yto.getText().equals("" + newVal.getTo())) {
                    this.textField_yto.setText("" + newVal.getTo());
                }
            }
        });
        this.yRange.set(this.guiSvgOptions.getyRange());
        if (this.yRange.get() == null) {
            this.yRange.set(new Range(-8, 8));
        }
        this.textField_yfrom.setText("" + this.yRange.get().getFrom());
        this.textField_yto.setText("" + this.yRange.get().getTo());


        this.textFieldUtil.addDoubleValidation(this.textField_yfrom, this.label_yfrom);
        this.textField_yfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("-")) {
                if (this.yRange.get() == null) {
                    this.yRange.set(new Range(Double.parseDouble(newValue), 8));
                } else {
                    this.yRange.get().setFrom(Double.parseDouble(newValue));
                }
            }
        });
        this.textFieldUtil.addDoubleValidation(this.textField_yto, this.label_yto);
        this.textField_yto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("-")) {
                if (this.yRange.get() == null) {
                    this.yRange.set(new Range(-8, Double.parseDouble(newValue)));
                } else {
                    this.yRange.get().setTo(Double.parseDouble(newValue));
                }
            }
        });

    }

    protected void initStylingFieldListeners() {
        ObservableList<CssType> cssTypeObservableList = FXCollections.observableArrayList(CssType.values());
        this.choiceBox_cssType.setItems(cssTypeObservableList);
        this.choiceBox_cssType.setConverter(this.svgOptionsUtil.getCssTypeStringConverter());
        this.choiceBox_cssType.getSelectionModel().select(CssType.NONE);
        this.choiceBox_cssType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.toggleVisibility(newValue == CssType.FILE, this.label_cssPath, this.hBox_cssPath);
            this.toggleVisibility(newValue == CssType.CUSTOM, this.label_cssCustom, this.textArea_cssCustom);
            if (newValue == CssType.NONE) this.guiSvgOptions.setCss("");
        });

        // css file
        this.textField_cssPath.setText(this.guiSvgOptions.getCss());
        this.textField_cssPath.textProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setCss(newValue);
        });

        textField_cssPath.setOnDragOver(dragOverHandler(".css"));
        textField_cssPath.setOnDragDropped(event -> {

            String path = event.getDragboard().getFiles().get(0).getAbsolutePath();
            textField_cssPath.setText(path);

        });

        this.button_cssPath.setDisable(false);
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

        // custom css
        this.textArea_cssCustom.textProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setCss(newValue.replace("\n", ""));
        });


        // colors
        this.colors = guiSvgOptions.getColors();
        ObservableList<Color> colorsObservableList = FXCollections.observableArrayList(Color.values());
        this.checkComboBox_color.getItems().addAll(colorsObservableList);
        this.checkComboBox_color.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Color>() {
            public void onChanged(ListChangeListener.Change<? extends Color> c) {
//                colors.clear();
                ObservableList<Color> checkedItems = checkComboBox_color.getCheckModel().getCheckedItems();
                ObservableList<Color> newItems =
                        checkedItems.filtered(color -> !colors.contains(color.getName()));
                ObservableList<Color> oldItems =
                        colorsObservableList.filtered(color -> !checkedItems.contains(color) && colors.contains(color.getName()));
                if (newItems.size() > 0) {
                    for (Color color : newItems) {
                        colors.add(color.getName());
                    }
                }
                if (oldItems.size() > 0) {
                    for (Color color : oldItems) {
                        colors.remove(color.getName());
                    }
                }
                guiSvgOptions.setColors(colors);
            }
        });
    }

    protected void initCsvFieldListeners() {


        // csv path
        this.textField_csvPath.setDisable(false);
        this.textField_csvPath.textProperty().addListener((observable, oldValue, newValue) -> {

            guiSvgOptions.setCsvPath(generateCSV());
        });

        currentDataSet.addListener(item -> {
            guiSvgOptions.setCsvPath(generateCSV());
        });

        textField_csvPath.setOnDragOver(dragOverHandler(".csv"));
        textField_csvPath.setOnDragDropped(event -> {
            String path = event.getDragboard().getFiles().get(0).getAbsolutePath();
            textField_csvPath.setText(path);
        });


        this.button_csvPath.setDisable(false);

        this.button_csvPath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(this.userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                this.textField_csvPath.setText(file.getAbsolutePath());
            }
        });

        if (this instanceof ChartWizardFrameController) {


            // csv orientation
            ObservableList<CsvOrientation> csvOrientationObservableList = FXCollections.observableArrayList(CsvOrientation.values());
            this.choiceBox_csvOrientation.setItems(csvOrientationObservableList);
            this.choiceBox_csvOrientation.setConverter(this.svgOptionsUtil.getCsvOrientationStringConverter());
            this.choiceBox_csvOrientation.getSelectionModel().select(CsvOrientation.HORIZONTAL);
            this.choiceBox_csvOrientation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                this.guiSvgOptions.setCsvOrientation(newValue);
            });

            // csv type
            ObservableList<CsvType> csvTypeObservableList = FXCollections.observableArrayList(CsvType.values());
            this.choiceBox_csvType.setItems(csvTypeObservableList);
            this.choiceBox_csvType.setConverter(this.svgOptionsUtil.getCsvTypeStringConverter());
            this.choiceBox_csvType.getSelectionModel().select(CsvType.DOTS);
            this.choiceBox_csvType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                this.guiSvgOptions.setCsvType(newValue);
            });


        }
    }

    protected void initSpecialFieldListeners() {
        // grid
        ObservableList<GridStyle> gridStyleObservableList = FXCollections.observableArrayList(GridStyle.values());
        this.choicebox_gridStyle.setItems(gridStyleObservableList);
        this.choicebox_gridStyle.setConverter(this.svgOptionsUtil.getGridStyleStringConverter());
        this.choicebox_gridStyle.getSelectionModel().select(this.guiSvgOptions.getGridStyle());
        this.choicebox_gridStyle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setGridStyle(newValue);
        });


        // xlines
        this.textField_xlines.setText(this.guiSvgOptions.getxLines());
        this.textField_xlines.textProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setxLines(newValue);
        });

        // ylines
        this.textField_ylines.setText(this.guiSvgOptions.getyLines());
        this.textField_ylines.textProperty().addListener((observable, oldValue, newValue) -> {
            this.guiSvgOptions.setyLines(newValue);
        });
    }

    protected void initiatePagination(final HBox hBox_pagination, final int AMOUNTOFSTAGES, final DiagramType diagramType) {
        this.stageBtns = new ArrayList<>();
        for (int stage = 0; stage < AMOUNTOFSTAGES; stage++) {
            Button stageBtn = new Button(bundle.getString("chart_stage" + stage));

            if (stage == 2 && DiagramType.FunctionPlot.equals(diagramType)) {
                stageBtn = new Button(bundle.getString("chart_stage" + stage + "Func"));
            }

//            stageBtn.wrapTextProperty().setValue(true);
            stageBtn.setTextAlignment(TextAlignment.CENTER);
            stageBtn.getStyleClass().add("stageBtn");
            if (this.currentStage.get() == stage) {
                stageBtn.getStyleClass().add("active");
                stageBtn.setAccessibleHelp(bundle.getString("active_stage"));
            }

            final int stageNumber = stage;
            stageBtn.setOnAction(event -> {
                this.currentStage.set(stageNumber);
            });
            hBox_pagination.getChildren().add(stageBtn);
            hBox_pagination.setAccessibleRole(AccessibleRole.MENU);
            stageBtn.accessibleRoleProperty().set(AccessibleRole.MENU_ITEM);
            this.stageBtns.add(stageBtn);
        }

        this.messageBtns = new ArrayList<>();
        hBox_pagination.getChildren().remove(this.button_Warnings);
        this.warnIcon = new Glyph("FontAwesome", FontAwesome.Glyph.WARNING);
        this.warnIcon.setColor(javafx.scene.paint.Color.valueOf("#f0ad4e"));
        this.button_Warnings = new Button("", warnIcon);
        this.button_Warnings.getStyleClass().add("notification");
        this.button_Warnings.getStyleClass().add("notification-btn");
        this.button_Warnings.setAccessibleText(this.bundle.getString("warn_message_help"));
        this.button_Warnings.setAccessibleHelp(this.bundle.getString("warn_message_no_warnings"));
        this.button_Warnings.setTooltip(new Tooltip(this.bundle.getString("warn_message_help")));
        this.button_Warnings.setId("btn_warnings");
        this.button_Warnings.setFocusTraversable(true);
        hBox_pagination.getChildren().add(this.button_Warnings);

        hBox_pagination.getChildren().remove(this.button_Infos);
        this.infoIcon = new Glyph("FontAwesome", FontAwesome.Glyph.INFO);
        this.infoIcon.setColor(javafx.scene.paint.Color.valueOf("#002557"));
        this.button_Infos = new Button("", infoIcon);
        this.button_Infos.getStyleClass().add("notification");
        this.button_Infos.getStyleClass().add("notification-btn");
        this.button_Infos.setAccessibleText(this.bundle.getString("info_message_help"));
        this.button_Infos.setAccessibleHelp(this.bundle.getString("info_message_no_infos"));
        this.button_Infos.setTooltip(new Tooltip(this.bundle.getString("info_message_help")));
        this.button_Infos.setId("btn_infos");
        this.button_Infos.setFocusTraversable(true);
        hBox_pagination.getChildren().add(this.button_Infos);
        this.messageBtns.add(this.button_Infos);

        this.popOver_warnings = new PopOver();
        this.popOver_infos = new PopOver();

        this.vBox_infos = new VBox();
        this.vBox_infos.getStyleClass().add("notification");
        this.vBox_infos.getStyleClass().add("info");
        this.vBox_infos.setFocusTraversable(true);

        this.vBox_warnings = new VBox();
        this.vBox_warnings.getStyleClass().add("notification");
        this.vBox_warnings.getStyleClass().add("warn");
        this.vBox_warnings.setFocusTraversable(true);

        this.button_Infos.setOnAction(event -> {
            if (this.vBox_infos.getChildren().size() > 0) {
                ScrollPane infoScrollPane = new ScrollPane(this.vBox_infos);
                infoScrollPane.getStyleClass().add("notification");
                infoScrollPane.getStyleClass().add("notification-scrollPane");
                infoScrollPane.getStyleClass().add("info");
                infoScrollPane.setMaxSize(340, 500);
                infoScrollPane.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
                infoScrollPane.setPadding(new Insets(0, 10, 0, 0));

                this.popOver_infos.setTitle(this.bundle.getString("popup_info_title"));
                this.popOver_warnings.getStyleClass().add("notification");
                this.popOver_warnings.getStyleClass().add("info");
                this.popOver_infos.setHeaderAlwaysVisible(true);
                this.popOver_infos.setContentNode(infoScrollPane);
                this.popOver_infos.show(this.button_Infos);
                this.vBox_infos.getChildren().get(0).requestFocus();
                this.fixBlurryText(infoScrollPane);
            }
        });
        button_Warnings.setOnAction(event -> {
            if (this.vBox_warnings.getChildren().size() > 0) {
                ScrollPane warningScrollPane = new ScrollPane(this.vBox_warnings);
                warningScrollPane.getStyleClass().add("notification");
                warningScrollPane.getStyleClass().add("notification-scrollPane");
                warningScrollPane.getStyleClass().add("warn");
                warningScrollPane.setMaxSize(340, 500);
                warningScrollPane.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
                warningScrollPane.setPadding(new Insets(0, 10, 0, 0));

                this.popOver_warnings.setTitle(this.bundle.getString("popup_warn_title"));
                this.popOver_warnings.getStyleClass().add("notification");
                this.popOver_warnings.getStyleClass().add("warn");
                this.popOver_warnings.setHeaderAlwaysVisible(true);
                this.popOver_warnings.setContentNode(warningScrollPane);
                this.popOver_warnings.show(this.button_Warnings);
                this.vBox_warnings.getChildren().get(0).requestFocus();
                this.fixBlurryText(warningScrollPane);
            }
        });

    }

    /**
     * content-preprocessing. Will "hide" the content-tabPane and shows the first stage
     */
    protected void preProcessContent() {
        this.stages = new ArrayList<AnchorPane>();
        this.tabPane_ContentHolder.getTabs().forEach(tab -> this.stages.add((AnchorPane) tab.getContent()));
        this.currentStage.set(0);
        this.borderPane_WizardContent.setCenter(this.stages.get(0));
        this.stages.get(0).getChildren().get(0).requestFocus();
        this.button_Back.setDisable(true);
    }

    /**
     * initiates all listeners for properties and elements
     */
    protected void initListener() {
        // indicator for current stage. changes will automatically render the chosen stage
        this.currentStage.addListener((args, oldVal, newVal) -> {

            if (newVal.intValue() < 1) this.button_Back.setDisable(true);
            else this.button_Back.setDisable(false);

            if (newVal.intValue() == stages.size() - 1) {
                this.button_Next.setDisable(true);
            } else {
                this.button_Next.setDisable(false);
            }
            if (newVal.intValue() > stages.size() - 1) {
                this.currentStage.set(oldVal.intValue());
            }

            if (oldVal.intValue() < this.stageBtns.size()) {
                this.stageBtns.get(oldVal.intValue()).getStyleClass().remove("active");
                this.stageBtns.get(oldVal.intValue()).setAccessibleHelp("");
                this.stageBtns.get(this.currentStage.get()).getStyleClass().add("active");
                this.stageBtns.get(this.currentStage.get()).setAccessibleHelp(bundle.getString("active_stage"));
            }

            this.borderPane_WizardContent.setCenter(this.stages.get(this.currentStage.get()));
            this.borderPane_WizardContent.getCenter().requestFocus();
        });

        // increment the currentStage counter. Will trigger its changeListener
        this.button_Next.setOnAction(event -> {
            this.currentStage.set(this.currentStage.get() + 1);
        });

        // decrement the currentStage counter. Will trigger its changeListener
        this.button_Back.setOnAction(event -> this.currentStage.set(this.currentStage.get() - 1));

        // closes the wizard
        this.button_Cancel.setOnAction(event -> {
            GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
            GuiSvgPlott.getInstance().closeWizard();
            wizardPath = "none";
            if (GuiSvgPlott.getInstance().getRootFrameController().menuItem_Preset_Editor.isDisable()) {
                GuiSvgPlott.getInstance().getRootFrameController().menuItem_Preset_Editor.setDisable(false);
            }
            GuiSvgPlott.getInstance().getRootFrameController().menuItem_Save_Preset.setDisable(true);
            this.popOver_warnings.hide();
            this.popOver_infos.hide();
        });

        // create chart
        this.button_Create.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Scalable Vector Graphics (SVG)", "*.svg");
            fileChooser.getExtensionFilters().add(extFilter);
            String title = this.guiSvgOptions.getTitle().isEmpty() ? "untitled" : this.guiSvgOptions.getTitle();
            fileChooser.setInitialFileName(title.toLowerCase() + ".svg");
            File file = fileChooser.showSaveDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                try {
                    this.guiSvgOptions.setOutput(file.getAbsolutePath());
                    this.svgOptionsService.buildSVG(guiSvgOptions.getOptions());
                    this.popOver_infos.hide();
                    this.popOver_warnings.hide();
                    GuiSvgPlott.getInstance().closeWizard();
                } catch (ValidationException e) {
                    logger.error(this.bundle.getString("svg_creation_validation_error"));
                }
            }
        });

        // rerender preview
        this.button_rerenderPreview.graphicProperty().setValue(new Glyph("FontAwesome", '\uf021'));
        this.button_rerenderPreview.setOnAction(event ->
                this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg)
        );

        this.button_Load.graphicProperty().setValue(new Glyph("FontAwesome", FontAwesome.Glyph.FOLDER_OPEN));


    }

    protected void initloadPreset() {
        button_Load.setOnAction(event -> {
            //TODO
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NOT YET IMPLEMENTED");
            alert.setHeaderText("NOT YET IMPLEMENTED");
            alert.setContentText("NOT YET IMPLEMENTED");
            alert.showAndWait();
            /*List<String> choices = new ArrayList<>();
            for (Preset p : presets) {
                choices.add(p.toString());
            }
            ChoiceDialog<String> dialog = new ChoiceDialog<>("",choices);
            dialog.setTitle(bundle.getString("prompt_load_title"));
            dialog.setHeaderText(bundle.getString("prompt_load_header"));
            dialog.setContentText(bundle.getString("prompt_load_content"));
            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()){

            }*/
        });
    }

    public Glyph getWarnIcon() {
        return warnIcon;
    }

    public Glyph getInfoIcon() {
        return infoIcon;
    }

    /**
     * Fixes blurry text issue of {@link ScrollPane} inside a {@link PopOver}.
     *
     * @param node the node
     */
    private static void fixBlurryText(Node node) {
        try {
            Field field = ScrollPaneSkin.class.getDeclaredField("viewRect");
            field.setAccessible(true);

            ScrollPane scrollPane = (ScrollPane) node.lookup(".scroll-pane");

            StackPane stackPane = (StackPane) field.get(scrollPane.getSkin());
            stackPane.setCache(false);

        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sets the visibility of given Label and given field to true.
     *
     * @param label the {@link Label}
     * @param field the field {@link Node}
     */
    protected void show(Label label, Node field) {
        label.setVisible(true);
        field.setVisible(true);
    }

    /**
     * Sets the visibility of given Label and given field to false.
     *
     * @param label the {@link Label}
     * @param field the field {@link Node}
     */
    protected void hide(Label label, Node field) {
        label.setVisible(false);
        field.setVisible(false);
    }

    /**
     * Sets the visibility of given label and field to given value.
     *
     * @param visible whether field {@link Node} and {@link Label} should be visible.
     * @param label   the {@link Label}
     * @param field   the the field {@link Node}
     */
    protected void toggleVisibility(boolean visible, Label label, Node field) {
        if (visible) {
            this.show(label, field);
        } else {
            this.hide(label, field);
        }
    }

    /**
     * Sets the value and visibility custom size {@link TextField}s and corresponding {@link Label}s.
     *
     * @param show if field and label should be visible.
     */
    protected void toggleCustomSize(final boolean show) {
        this.toggleVisibility(show, this.label_customSizeWidth, this.textField_customSizeWidth);
        this.toggleVisibility(show, this.label_customSizeHeight, this.textField_customSizeHeight);

        if (show) {
            this.textField_customSizeWidth.setText(this.size.x());
            this.textField_customSizeWidth.requestFocus();
            this.textField_customSizeHeight.setText(this.size.y());
        }
    }


    /**
     * Sets the value and visibility of x- and y-{@link Range} and corresponding {@link Node}s.
     *
     * @param enabled whether fields should be visible
     */
    protected void toggleAxesRanges(Boolean enabled) {
        if (!enabled) {
            this.xRange.set(null);
            this.yRange.set(null);
        } else {
            this.xRange.set(this.guiSvgOptions.getxRange());
            this.yRange.set(this.guiSvgOptions.getyRange());
        }

        if (!DiagramType.BarChart.equals(this.guiSvgOptions.getDiagramType())) {
            this.toggleVisibility(enabled, this.label_xfrom, this.textField_xfrom);
            this.toggleVisibility(enabled, this.label_xto, this.textField_xto);
        }

        this.toggleVisibility(enabled, this.label_yfrom, this.textField_yfrom);
        this.toggleVisibility(enabled, this.label_yto, this.textField_yto);
    }


    /**
     * Initializes Listeners on {@link GuiSvgOptions}.
     */
    private void initOptionListeners() {
        this.guiSvgOptions.gridStyleProperty().addListener((observable, oldValue, newValue) -> {
            this.choicebox_gridStyle.getSelectionModel().select(newValue);
        });
        this.guiSvgOptions.csvTypeProperty().addListener((observable, oldValue, newValue) -> {
            this.choiceBox_csvType.getSelectionModel().select(newValue);
        });
    }


    /**
     * DragHandler for Path-TextFields. Only one dragged File is acceptable.
     *
     * @param fileType determines which kind of files can be used for dragging
     * @return the EventHandler
     */
    private EventHandler<? super DragEvent> dragOverHandler(String fileType) {

        EventHandler<? super DragEvent> dragOverHandler = (EventHandler<DragEvent>) event -> {

            Dragboard board = event.getDragboard();
            if (board.getFiles().size() == 1 && board.getFiles().get(0).getName().endsWith(fileType)) {
                event.acceptTransferModes(TransferMode.LINK);
            } else {
                event.acceptTransferModes(TransferMode.NONE);
            }
        };
        return dragOverHandler;
    }


    protected void openEditDataSetFrame() {

        FXMLLoader loader = new FXMLLoader();

        try {

            loader.setResources(bundle);
            loader.setLocation(GuiSvgPlott.CsvEditorFrame);

            BorderPane bp = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(bp);

            stage.setMinHeight(450);
            stage.setScene(scene);

            CsvEditorController csvController = loader.getController();


            csvController.button_Cancel.setOnAction(event -> {
                stage.close();
            });
            csvController.init(pointMap, textField_csvPath.getText(), guiSvgOptions, this);

            stage.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    protected String generateCSV() {

        if (currentDataSet == null || currentDataSet.get() == null)
            textField_csvPath.getText();

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("dataset", ".csv");
            mergeDataSet(textField_csvPath.getText(), tempFile);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return (tempFile != null) ? tempFile.toString() : "";
    }

    private void mergeDataSet(String originFile, Path resultFile) throws IOException {

        File f = resultFile.toFile();

        Files.write(resultFile, "".getBytes());

        Files.readAllLines(Paths.get(originFile)).forEach(line -> {

            try {
                Files.write(resultFile, (line + System.lineSeparator()).getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (currentDataSet != null && currentDataSet.get() != null)
            Files.readAllLines(Paths.get(currentDataSet.get().getAbsolutePath())).forEach(line -> {

                try {
                    Files.write(resultFile, (line + System.lineSeparator()).getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        f.deleteOnExit();
    }

    protected void initFieldListenersForPreview() {
        this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions,
                this.choiceBox_outputDevice, this.choiceBox_size, this.choicebox_gridStyle,
                this.choiceBox_csvOrientation, this.choiceBox_csvType, this.choiceBox_cssType);
        this.textFieldUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions,
                this.textField_title, this.textField_customSizeWidth, this.textField_customSizeHeight,
                this.textField_xfrom, this.textField_xto, this.textField_yfrom, this.textField_yto,
                this.textField_xlines, this.textField_ylines);
    }

    private void focusInit() {
        getAllNodes(tabPane_ContentHolder).forEach(node -> {
            if (node instanceof TextField)
                setFocusRenderPeviewProperty(node);
            if (node instanceof TextArea)
                setFocusRenderPeviewProperty(node);
            if (node instanceof ChoiceBox)
                setFocusRenderPeviewProperty(node);
            if (node instanceof CheckBox)
                setFocusRenderPeviewProperty(node);
            if (node instanceof RadioButton)
                setFocusRenderPeviewProperty(node);

        });

    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent) node, nodes);
        }
    }


    protected void setFocusRenderPeviewProperty(Node node) {

        if (node != null)
            node.focusedProperty().addListener((args, oldVal, newVal) -> {
                if (!newVal)
                    this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);
            });
    }

}
