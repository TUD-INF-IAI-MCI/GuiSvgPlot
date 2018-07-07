package application.controller.wizard;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import application.model.PageSize;
import application.service.SvgOptionsService;
import application.util.SvgOptionsUtil;
import application.util.TextFieldUtil;
import com.sun.javafx.scene.control.skin.ScrollPaneSkin;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.GridStyle;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class SVGWizardController implements Initializable {
    // start logger
    private static final Logger logger = LoggerFactory.getLogger(SVGWizardController.class);

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
    protected TextField textField_Title;
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
    // grid and axis styling options
    @FXML
    public ChoiceBox<GridStyle> choicebox_gridStyle;
    @FXML
    public TextField textField_xlines;
    @FXML
    public TextField textField_ylines;
    @FXML
    public ChoiceBox<AxisStyle> choicebox_dblaxes;

    // general axis options
    @FXML
    protected TextField textField_xunit;
    @FXML
    protected TextField textField_yunit;
    @FXML
    protected RadioButton radioBtn_autoscale;
    @FXML
    protected RadioButton radioBtn_customScale;
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
    public TextField textField_cssFile;
    @FXML
    public ChoiceBox<tud.tangram.svgplot.styles.Color> choiceBox_color1;
    @FXML
    public ChoiceBox<tud.tangram.svgplot.styles.Color> choiceBox_color2;
    // csv fields
    @FXML
    public TextField textField_CsvPath;
    @FXML
    private Button button_CsvPath;
    @FXML
    public ChoiceBox<CsvOrientation> choiceBox_csvOrientation;
    @FXML
    public ChoiceBox<CsvType> choiceBox_csvType;

    private List<String> colors;

    public VBox vBox_warnings;
    private PopOver popOver_warnings;
    public VBox vBox_infos;
    private PopOver popOver_infos;
    private Glyph warnIcon;
    private Glyph infoIcon;

    private Point size;
    protected BooleanProperty isExtended;
    protected List<Button> stageBtns;
    protected List<Button> messageBtns;
    protected ResourceBundle bundle;
    protected IntegerProperty currentStage;
    protected File userDir;
    protected ArrayList<GridPane> stages;
    protected GuiSvgOptions svgOptions;
    protected SvgPlotOptions svgPlotOptions;
    protected SvgOptionsService svgOptionsService = SvgOptionsService.getInstance();
    protected SvgOptionsUtil svgOptionsUtil = SvgOptionsUtil.getInstance();
    protected TextFieldUtil textFieldUtil = TextFieldUtil.getInstance();
    private ObjectProperty<Range> xRange;
    private ObjectProperty<Range> yRange;


    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.svgOptionsService.setBundle(resources);
        this.svgOptionsUtil.setBundle(resources);
        this.textFieldUtil.setBundle(resources);
        this.currentStage = new SimpleIntegerProperty();
        this.isExtended = new SimpleBooleanProperty(false);
        this.bundle = resources;
        this.size = new Point(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        this.xRange = new SimpleObjectProperty<>();
        this.yRange = new SimpleObjectProperty<>();
        this.userDir = new File(System.getProperty("user.home"));
        this.svgPlotOptions = new SvgPlotOptions();
        this.svgOptions = new GuiSvgOptions(svgPlotOptions);
        this.webView_svg.setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        this.webView_svg.setAccessibleHelp(this.bundle.getString("preview"));

        initListener();
        preProcessContent();
    }

    public void setExtended(boolean isExtended) {
        this.isExtended.set(isExtended);
    }

    protected void initGeneralFieldListeners() {
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
        this.textFieldUtil.addMinimumIntegerValidationWithMinimum(this.textField_customSizeWidth, 1);
        this.textField_customSizeWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            this.size.setX(Integer.parseInt(newValue));
            this.svgPlotOptions.setSize(this.size);
        });
        this.textField_customSizeHeight.setText(this.size.y());
        this.textFieldUtil.addMinimumIntegerValidationWithMinimum(this.textField_customSizeHeight, 1);
        this.textField_customSizeHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            this.size.setY(Integer.parseInt(newValue));
            this.svgPlotOptions.setSize(this.size);
        });

    }

    protected void initAxisFieldListeners() {
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

        this.textFieldUtil.addDoubleValidation(this.textField_xfrom, this.label_xfrom);
        this.textField_xfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("-")){
                xRange.get().setFrom(Double.parseDouble(newValue));
            }
        });
        this.textFieldUtil.addDoubleValidation(this.textField_xto, this.label_xto);
        this.textField_xto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("-")) {
                xRange.get().setTo(Double.parseDouble(newValue));
            }
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

        this.textFieldUtil.addDoubleValidation(this.textField_yfrom, this.label_yfrom);
        this.textField_yfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("-")) {
                yRange.get().setFrom(Double.parseDouble(newValue));
            }
        });
        this.textFieldUtil.addDoubleValidation(this.textField_yto, this.label_yto);
        this.textField_yto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("-")) {
                yRange.get().setTo(Double.parseDouble(newValue));
            }
        });
        this.yRange.addListener((args, oldVal, newVal) -> {
            if (!this.svgPlotOptions.hasAutoScale()) {
                svgPlotOptions.setyRange(yRange.get());
            }
        });
    }

    protected void initStylingFieldListeners() {
        this.colors = new ArrayList<>();

        // css file
        this.textField_cssFile.setText(this.svgPlotOptions.getCss());
        this.textField_cssFile.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setCss(newValue);
        });

        // colors
        ObservableList<tud.tangram.svgplot.styles.Color> sortingTypeObservableList = FXCollections.observableArrayList(tud.tangram.svgplot.styles.Color.values());
        this.choiceBox_color1.setItems(sortingTypeObservableList);
        this.choiceBox_color2.setItems(sortingTypeObservableList);
        if (this.svgPlotOptions.getCustomColors().size() == 2) {
            tud.tangram.svgplot.styles.Color color1 = tud.tangram.svgplot.styles.Color.fromString(this.svgPlotOptions.getCustomColors().get(0));
            this.colors.add(color1.getName());
            this.choiceBox_color1.setValue(color1);

            tud.tangram.svgplot.styles.Color color2 = tud.tangram.svgplot.styles.Color.fromString(this.svgPlotOptions.getCustomColors().get(1));
            this.colors.add(color2.getName());
            this.choiceBox_color2.setValue(color2);
        }
        this.choiceBox_color1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<tud.tangram.svgplot.styles.Color>() {
            @Override
            public void changed(ObservableValue<? extends tud.tangram.svgplot.styles.Color> observable, tud.tangram.svgplot.styles.Color oldValue, tud.tangram.svgplot.styles.Color newValue) {
                colors.add(newValue.getName());
                svgPlotOptions.setCustomColors(colors);
            }
        });
        this.choiceBox_color2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<tud.tangram.svgplot.styles.Color>() {
            @Override
            public void changed(ObservableValue<? extends tud.tangram.svgplot.styles.Color> observable, tud.tangram.svgplot.styles.Color oldValue, tud.tangram.svgplot.styles.Color newValue) {
                colors.add(newValue.getName());
                svgPlotOptions.setCustomColors(colors);
            }
        });
    }

    protected void initCsvFieldListeners() {
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

    protected void initSpecialFieldListeners() {
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

    protected void initiatePagination(final HBox hBox_pagination, final int AMOUNTOFSTAGES) {
        this.stageBtns = new ArrayList<>();
        for (int stage = 0; stage < AMOUNTOFSTAGES; stage++) {
            Button stageBtn = new Button(bundle.getString("chart_stage" + stage));
//            stageBtn.wrapTextProperty().setValue(true);
            stageBtn.setTextAlignment(TextAlignment.CENTER);
            stageBtn.getStyleClass().add("stageBtn");
            if (this.currentStage.get() == stage) {
                stageBtn.getStyleClass().add("active");
            }

            final int stageNumber = stage;
            stageBtn.setOnAction(event -> {
                currentStage.set(stageNumber);
            });
            hBox_pagination.getChildren().add(stageBtn);
            hBox_pagination.setAccessibleRole(AccessibleRole.MENU);
            stageBtn.accessibleRoleProperty().set(AccessibleRole.MENU_ITEM);
            this.stageBtns.add(stageBtn);
        }

        this.messageBtns = new ArrayList<>();
        hBox_pagination.getChildren().remove(this.button_Warnings);
        this.warnIcon = new Glyph("FontAwesome", FontAwesome.Glyph.WARNING);
        this.warnIcon.setColor(Color.valueOf("#f0ad4e"));
        this.button_Warnings = new Button("", warnIcon);
        this.button_Warnings.getStyleClass().add("notification");
        this.button_Warnings.getStyleClass().add("notification-btn");
        this.button_Warnings.setId("btn_warnings");
        this.button_Warnings.setDisable(true);
        hBox_pagination.getChildren().add(this.button_Warnings);

        hBox_pagination.getChildren().remove(this.button_Infos);
        this.infoIcon = new Glyph("FontAwesome", FontAwesome.Glyph.INFO);
        this.infoIcon.setColor(Color.valueOf("#002557"));
        this.button_Infos = new Button("", infoIcon);
        this.button_Infos.getStyleClass().add("notification");
        this.button_Infos.getStyleClass().add("notification-btn");
        this.button_Infos.setId("btn_infos");
        this.button_Infos.setDisable(true);
        hBox_pagination.getChildren().add(this.button_Infos);
        this.messageBtns.add(this.button_Infos);

        popOver_warnings = new PopOver();
        popOver_infos = new PopOver();

        vBox_infos = new VBox();
        vBox_infos.getStyleClass().add("notification");
        vBox_infos.getStyleClass().add("info");

        vBox_warnings = new VBox();
        vBox_warnings.getStyleClass().add("notification");
        vBox_warnings.getStyleClass().add("warn");


        button_Infos.setOnAction(event -> {
            ScrollPane infoScrollPane = new ScrollPane(vBox_infos);
            infoScrollPane.getStyleClass().add("notification");
            infoScrollPane.getStyleClass().add("notification-scrollPane");
            infoScrollPane.getStyleClass().add("info");
            infoScrollPane.setMaxSize(340, 500);
            infoScrollPane.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
            infoScrollPane.setPadding(new Insets(0, 10, 0, 0));

            popOver_infos.setTitle("Informationen");
            popOver_warnings.getStyleClass().add("notification");
            popOver_warnings.getStyleClass().add("info");
            popOver_infos.setHeaderAlwaysVisible(true);
            popOver_infos.setContentNode(infoScrollPane);
            popOver_infos.show(button_Infos);
            fixBlurryText(infoScrollPane);
        });
        button_Warnings.setOnAction(event -> {
            ScrollPane warningScrollPane = new ScrollPane(vBox_warnings);
            warningScrollPane.getStyleClass().add("notification");
            warningScrollPane.getStyleClass().add("notification-scrollPane");
            warningScrollPane.getStyleClass().add("warn");
            warningScrollPane.setMaxSize(340, 500);
            warningScrollPane.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
            warningScrollPane.setPadding(new Insets(0, 10, 0, 0));

            popOver_warnings.setTitle("Warnungen");
            popOver_warnings.getStyleClass().add("notification");
            popOver_warnings.getStyleClass().add("warn");
            popOver_warnings.setHeaderAlwaysVisible(true);
            popOver_warnings.setContentNode(warningScrollPane);
            popOver_warnings.show(button_Warnings);
            fixBlurryText(warningScrollPane);
        });

    }

    /**
     * content-preprocessing. Will "hide" the content-tabPane and shows the first stage
     */
    protected void preProcessContent() {
        stages = new ArrayList<>();
        tabPane_ContentHolder.getTabs().forEach(tab -> stages.add((GridPane) tab.getContent()));
        currentStage.set(0);
        borderPane_WizardContent.setCenter(stages.get(0));
        button_Back.setDisable(true);
    }

    /**
     * initiates all listeners for properties and elements
     */
    protected void initListener() {
        // indicator for current stage. changes will automatically render the chosen stage
        currentStage.addListener((args, oldVal, newVal) -> {

            if (newVal.intValue() < 1) button_Back.setDisable(true);
            else button_Back.setDisable(false);

            if (newVal.intValue() == stages.size() - 1) {
                button_Next.setDisable(true);
            } else {
                button_Next.setDisable(false);
            }
            if (newVal.intValue() > stages.size() - 1) {
                currentStage.set(oldVal.intValue());
            }

            if (oldVal.intValue() < this.stageBtns.size()) {
                this.stageBtns.get(oldVal.intValue()).getStyleClass().remove("active");
                this.stageBtns.get(currentStage.get()).getStyleClass().add("active");
            }

            borderPane_WizardContent.setCenter(stages.get(currentStage.get()));
            this.svgOptionsService.buildPreviewSVG(this.svgPlotOptions, this.webView_svg);
        });

        // increment the currentStage counter. Will trigger its changeListener
        button_Next.setOnAction(event -> {
            currentStage.set(currentStage.get() + 1);
        });

        // decrement the currentStage counter. Will trigger its changeListener
        button_Back.setOnAction(event -> currentStage.set(currentStage.get() - 1));

        // closes the wizard
        button_Cancel.setOnAction(event -> {
            GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
            GuiSvgPlott.getInstance().closeWizard();
            popOver_warnings.hide();
            popOver_infos.hide();
        });

        // create chart
        button_Create.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Scalable Vector Graphics (SVG)", "*.svg");
            fileChooser.getExtensionFilters().add(extFilter);
            String title = this.svgPlotOptions.getTitle().isEmpty() ? "untitled" : this.svgPlotOptions.getTitle();
            fileChooser.setInitialFileName(title.toLowerCase() + ".svg");
            File file = fileChooser.showSaveDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                this.svgPlotOptions.setOutput(file);
                this.svgOptionsService.buildSVG(svgPlotOptions);
                popOver_warnings.hide();
                popOver_infos.hide();
                GuiSvgPlott.getInstance().closeWizard();
            }
        });

        // rerender preview
        button_rerenderPreview.setOnAction(event ->
                this.svgOptionsService.buildPreviewSVG(this.svgPlotOptions, this.webView_svg)
        );

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
    protected void setVisible(boolean visible, Label label, Node field) {
        if (visible) {
            show(label, field);
        } else {
            hide(label, field);
        }
    }

    /**
     * Sets the value and visibility custom size {@link TextField}s and corresponding {@link Label}s.
     *
     * @param show if field and label should be visible.
     */
    protected void toggleCustomSize(final boolean show) {
        setVisible(show, label_customSizeWidth, textField_customSizeWidth);
        setVisible(show, label_customSizeHeight, textField_customSizeHeight);

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
            this.xRange.set(new Range(-8, 8));
            this.yRange.set(new Range(-8, 8));
        }


        setVisible(enabled, label_xfrom, textField_xfrom);
        setVisible(enabled, label_xto, textField_xto);
        setVisible(enabled, label_yfrom, textField_yfrom);
        setVisible(enabled, label_yto, textField_yto);
    }

}
