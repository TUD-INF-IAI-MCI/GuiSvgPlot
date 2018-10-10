package application.controller.wizard;

import application.GuiSvgPlott;
import application.controller.CsvEditorController;
import application.controller.wizard.chart.ChartWizardFrameController;
import application.model.GuiSvgOptions;
import application.model.Options.CssType;
import application.model.Options.PageSize;
import application.model.Preset;
import application.service.PresetService;
import application.service.SvgOptionsService;
import application.util.ChoiceBoxUtil;
import application.util.Converter;
import application.util.DialogUtil;
import application.util.TextFieldUtil;
import com.sun.javafx.scene.control.skin.ScrollPaneSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
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
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.GridStyle;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static application.controller.RootFrameController.wizardPath;

/**
 * The controller for wizards. Parent of {@link ChartWizardFrameController} and
 * {@link application.controller.wizard.functions.FunctionWizardFrameController}.
 *
 * @author Emma Müller, Robert Schlegel
 */
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
	protected Button button_Load;
	@FXML
	protected Button button_Edit_Preset;
	@FXML
	public Button button_rerenderPreview;
	// @FXML
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
	protected PresetService presetService = PresetService.getInstance();
	protected DialogUtil dialogUtil = DialogUtil.getInstance();

	public VBox vBox_warnings;
	private PopOver popOver_warnings;
	public VBox vBox_infos;
	private PopOver popOver_infos;
	private Glyph warnIcon;
	private Glyph infoIcon;

	private SimpleObjectProperty<Point> size;
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
	protected Converter converter = Converter.getInstance();
	protected TextFieldUtil textFieldUtil = TextFieldUtil.getInstance();
	protected ChoiceBoxUtil choiceBoxUtil = ChoiceBoxUtil.getInstance();
	protected ObjectProperty<Range> xRange;
	protected ObjectProperty<Range> yRange;

	protected File temporaryCSV;
	protected HashMap<DiagramType, File> pointMap;

	protected SimpleObjectProperty<File> currentDataSet;
	private ObjectProperty<Path> tempFileProp;

	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		this.svgOptionsService.setBundle(resources);
		this.converter.setBundle(resources);
		this.textFieldUtil.setBundle(resources);
		this.choiceBoxUtil.setBundle(resources);
		this.dialogUtil.setBundle(resources);
		this.currentStage = new SimpleIntegerProperty();
		this.isExtended = new SimpleBooleanProperty(false);
		this.size = new SimpleObjectProperty<>(new Point(PageSize.A4.getWidth(), PageSize.A4.getHeight()));
		this.pageOrientation = new SimpleObjectProperty<>(PageSize.PageOrientation.PORTRAIT);
		this.xRange = new SimpleObjectProperty<>();
		this.yRange = new SimpleObjectProperty<>();
		this.userDir = new File(System.getProperty("user.home"));
		this.guiSvgOptions = new GuiSvgOptions(new SvgPlotOptions());
		this.webView_svg.setAccessibleRole(AccessibleRole.PAGE_ITEM);
		this.webView_svg.setAccessibleHelp(this.bundle.getString("preview"));
		this.currentDataSet = new SimpleObjectProperty<>();
		this.pointMap = new HashMap<>();
		this.tempFileProp = new SimpleObjectProperty<>();

		this.initListener();
		// this.initOptionListeners();
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
		focusInit();

		// title
		this.textField_title.textProperty().bindBidirectional(this.guiSvgOptions.titleProperty());

		// output device
		ObservableList<OutputDevice> outputDevices = FXCollections.observableArrayList(OutputDevice.values());
		this.choiceBox_outputDevice.setItems(outputDevices);
		this.choiceBox_outputDevice.setConverter(this.converter.getOutputDeviceStringConverter());
		this.choiceBox_outputDevice.getSelectionModel().select(0);
		this.choiceBox_outputDevice.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					this.guiSvgOptions.setOutputDevice(newValue);
					boolean isColorDevice = newValue == OutputDevice.ScreenColor;
//            this.toggleVisibility(isColorDevice, this.label_color1, this.hbox_firstColor);
//            this.toggleVisibility(isColorDevice, this.label_color2, this.hbox_secondColor);
//            this.toggleVisibility(isColorDevice, this.label_color3, this.hbox_thirdColor);
				});

		// size
		this.guiSvgOptions.sizeProperty().bindBidirectional(this.size);
		SimpleObjectProperty<Boolean> isPageOrientationPortrait = new SimpleObjectProperty<>();
		SimpleObjectProperty<Boolean> isPageOrientationLandscape = new SimpleObjectProperty<>();
		this.radioBtn_portrait.selectedProperty().bindBidirectional(isPageOrientationPortrait);
		this.radioBtn_portrait.selectedProperty().set(true);
		this.radioBtn_landscape.selectedProperty().bindBidirectional(isPageOrientationLandscape);

		this.radioBtn_landscape.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				pageOrientation.set(PageSize.PageOrientation.LANDSCAPE);
			} else {
				pageOrientation.set(PageSize.PageOrientation.PORTRAIT);
			}
		});

		this.pageOrientation.addListener((observable, oldValue, newValue) -> {
			isPageOrientationPortrait.set(newValue.equals(PageSize.PageOrientation.PORTRAIT));
			isPageOrientationLandscape.set(newValue.equals(PageSize.PageOrientation.LANDSCAPE));
			if (!choiceBox_size.getSelectionModel().getSelectedItem().equals(PageSize.CUSTOM)) {
				this.size.set(new Point(this.size.get().getY(), this.size.get().getX()));

				this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg);

				this.textField_customSizeWidth.setText(this.size.get().x());
				this.textField_customSizeHeight.setText(this.size.get().y());
			}
		});

		ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
		ObservableList<PageSize> sortedPageSizes = pageSizeObservableList
				.sorted(Comparator.comparing(PageSize::getName));
		this.choiceBox_size.setItems(sortedPageSizes);
		this.choiceBox_size.setConverter(this.converter.getPageSizeStringConverter());
		this.choiceBox_size.getSelectionModel().select(PageSize.A4);
		this.choiceBox_size.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				if (newValue != PageSize.CUSTOM) {
					this.size.set(newValue.getPageSizeWithOrientation(this.pageOrientation.get()));
				}
				this.toggleCustomSize(newValue == PageSize.CUSTOM);
			}
		});

		// custom size
		this.textField_customSizeWidth.setText(this.size.get().x());
		this.textFieldUtil.addIntegerValidationWithMinimum(this.textField_customSizeWidth, 1);
		this.textFieldUtil.addMinimumIntegerValidation(this.textField_customSizeWidth, this.label_customSizeWidth,
				GuiSvgOptions.MINIMUM_PAGE_WIDTH);
		this.textField_customSizeWidth.textProperty().addListener((observable, oldValue, newValue) -> {
			this.size.get().setX(Integer.parseInt(newValue));
		});
		this.textField_customSizeHeight.setText(this.size.get().y());
		this.textFieldUtil.addIntegerValidationWithMinimum(this.textField_customSizeHeight, 1);
		this.textFieldUtil.addMinimumIntegerValidation(this.textField_customSizeHeight, this.label_customSizeHeight,
				GuiSvgOptions.MINIMUM_PAGE_HEIGHT);
		this.textField_customSizeHeight.textProperty().addListener((observable, oldValue, newValue) -> {
			this.size.get().setY(Integer.parseInt(newValue));
		});

	}

	protected void initAxisFieldListeners() {
		// xRange
		this.textFieldUtil.addNotEqualValidation(this.textField_xfrom, this.label_xfrom, this.textField_xto,
				this.label_xto);
		this.textFieldUtil.addFirstNotGreaterThanSecondValidationListener(this.textField_xfrom, this.label_xfrom,
				this.textField_xto, this.label_xto);
		this.xRange.addListener((args, oldVal, newVal) -> {
			if (!this.guiSvgOptions.isAutoScale() && newVal != null) {
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
		this.textFieldUtil.addNotEqualValidation(this.textField_yfrom, this.label_yfrom, this.textField_yto,
				this.label_yto);
		this.textFieldUtil.addFirstNotGreaterThanSecondValidationListener(this.textField_yfrom, this.label_yfrom,
				this.textField_yto, this.label_yto);
		this.yRange.addListener((args, oldVal, newVal) -> {
			if (!this.guiSvgOptions.isAutoScale() && newVal != null) {
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
		this.choiceBox_cssType.setConverter(this.converter.getCssTypeStringConverter());
		this.choiceBox_cssType.getSelectionModel().select(CssType.NONE);
		this.choiceBox_cssType.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					this.toggleVisibility(newValue == CssType.FILE, this.label_cssPath, this.hBox_cssPath);
					this.toggleVisibility(newValue == CssType.CUSTOM, this.label_cssCustom, this.textArea_cssCustom);
					if (newValue == CssType.NONE)
						this.guiSvgOptions.setCss("");
				});

		// css file
		this.textField_cssPath.textProperty().bindBidirectional(this.guiSvgOptions.cssProperty());
		this.textField_cssPath.setOnDragOver(dragOverHandler(".css"));
		this.textField_cssPath.setOnDragDropped(event -> {
			String path = event.getDragboard().getFiles().get(0).getAbsolutePath();
			this.textField_cssPath.setText(path);

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

	}

	protected void initCsvFieldListeners() {
		textField_csvPath.textProperty().bindBidirectional(this.guiSvgOptions.csvPathProperty());
		textField_csvPath.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				guiSvgOptions.setCsvPath(generateCSV());
			}
		});

		textField_csvPath.setOnDragOver(dragOverHandler(".csv"));

		textField_csvPath.setOnDragDropped(event -> {
			String path = event.getDragboard().getFiles().get(0).getAbsolutePath();
			textField_csvPath.setText(path);
		});

		this.button_csvPath.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(this.userDir);
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
			fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showOpenDialog(GuiSvgPlott.getInstance().getPrimaryStage());
			if (file != null) {
				textField_csvPath.setText(file.getAbsolutePath());
				guiSvgOptions.setCsvPath(generateCSV());

				Alert a = dialogUtil.alert(AlertType.INFORMATION, "csv_added_title", "csv_added_header", "csv_added");
				a.setContentText(file.getName() + " " + this.bundle.getString("csv_added"));
                a.showAndWait();
			}
		});

		if (this instanceof ChartWizardFrameController) {

//            // csv orientation
//            ObservableList<CsvOrientation> csvOrientationObservableList = FXCollections.observableArrayList(CsvOrientation.values());
//            this.choiceBox_csvOrientation.setItems(csvOrientationObservableList);
//            this.choiceBox_csvOrientation.setConverter(this.converter.getCsvOrientationStringConverter());
//            this.choiceBox_csvOrientation.getSelectionModel().select(CsvOrientation.HORIZONTAL);
//            this.choiceBox_csvOrientation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//                this.guiSvgOptions.setCsvOrientation(newValue);
//            });

			// csv type
			ObservableList<CsvType> csvTypeObservableList = FXCollections.observableArrayList(CsvType.values());
			this.choiceBox_csvType.setItems(csvTypeObservableList);
			this.choiceBox_csvType.setConverter(this.converter.getCsvTypeStringConverter());
			this.choiceBox_csvType.getSelectionModel().select(CsvType.DOTS);
			this.choiceBox_csvType.getSelectionModel().selectedItemProperty()
					.addListener((observable, oldValue, newValue) -> {
						this.guiSvgOptions.setCsvType(newValue);
					});

		}
	}

	protected void initSpecialFieldListeners() {
		// grid
		ObservableList<GridStyle> gridStyleObservableList = FXCollections.observableArrayList(GridStyle.values());
		this.choicebox_gridStyle.setItems(gridStyleObservableList);
		this.choicebox_gridStyle.setConverter(this.converter.getGridStyleStringConverter());
		this.choicebox_gridStyle.getSelectionModel().select(this.guiSvgOptions.getGridStyle());
		this.choicebox_gridStyle.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						this.guiSvgOptions.setGridStyle(newValue);
					}
				});

		// xlines
		this.textField_xlines.textProperty().bindBidirectional(this.guiSvgOptions.xLinesProperty());

		// ylines
		this.textField_ylines.textProperty().bindBidirectional(this.guiSvgOptions.yLinesProperty());
	}

	protected void initiatePagination(final HBox hBox_pagination, final int AMOUNTOFSTAGES,
			final DiagramType diagramType) {
		this.stageBtns = new ArrayList<>();
		for (int stage = 0; stage < AMOUNTOFSTAGES; stage++) {
			Button stageBtn = new Button(bundle.getString("chart_stage" + stage));
			stageBtn.setAccessibleText(bundle.getString("chart_stage" + stage + "_help"));
			if (stage == 2 && DiagramType.FunctionPlot.equals(diagramType)) {
				stageBtn = new Button(bundle.getString("chart_stage" + stage + "Func"));
				stageBtn.setAccessibleText(bundle.getString("chart_stage" + stage + "_help_Func"));
			}

			stageBtn.setTextAlignment(TextAlignment.CENTER);
			stageBtn.getStyleClass().add("stageBtn");
			stageBtn.setFocusTraversable(false);
			if (this.currentStage.get() == stage) {
				stageBtn.getStyleClass().add("active");
				stageBtn.requestFocus();
				stageBtn.setFocusTraversable(true);
//                stageBtn.setAccessibleHelp(bundle.getString("active_stage"));
			}

			final int stageNumber = stage;
			stageBtn.setOnAction(event -> {
				this.currentStage.set(stageNumber);
			});
			hBox_pagination.getChildren().add(stageBtn);
			hBox_pagination.setAccessibleRole(AccessibleRole.TAB_PANE);
			stageBtn.accessibleRoleProperty().set(AccessibleRole.TAB_ITEM);

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
		this.button_Warnings.setDisable(true);
		this.button_Warnings.setAccessibleRole(AccessibleRole.BUTTON);
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
		this.button_Infos.setDisable(true);
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
				infoScrollPane.setAccessibleText("Infos:");

				this.popOver_infos.setTitle(this.bundle.getString("popup_info_title"));
				this.popOver_warnings.getStyleClass().add("notification");
				this.popOver_warnings.getStyleClass().add("info");
				this.popOver_infos.setHeaderAlwaysVisible(true);
				this.popOver_infos.setContentNode(infoScrollPane);
				this.popOver_infos.show(this.button_Infos);
				String accessibleText = this.vBox_infos.getChildren().size() + " "
						+ this.bundle.getString("info_message") + " "
						+ this.vBox_infos.getChildren().get(0).getAccessibleText();
				this.vBox_infos.getChildren().get(0).setAccessibleText(accessibleText);
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
				String accessibleText = this.vBox_warnings.getChildren().size() + " "
						+ this.bundle.getString("warn_message") + " "
						+ this.vBox_warnings.getChildren().get(0).getAccessibleText();
				this.vBox_warnings.getChildren().get(0).setAccessibleText(accessibleText);
				this.vBox_warnings.getChildren().get(0).requestFocus();
				this.fixBlurryText(warningScrollPane);
			}
		});

	}

	/**
	 * content-preprocessing. Will "hide" the content-tabPane and shows the first
	 * stage
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

		// indicator for current stage. changes will automatically render the chosen
		// stage
		this.currentStage.addListener((args, oldVal, newVal) -> {

			if (newVal.intValue() < 1)
				this.button_Back.setDisable(true);
			else
				this.button_Back.setDisable(false);

			if (newVal.intValue() == stages.size() - 1) {
				this.button_Next.setDisable(true);
			} else {
				this.button_Next.setDisable(false);
			}
			if (newVal.intValue() > stages.size() - 1) {
				this.currentStage.set(0);
			}

			if (newVal.intValue() >= 0) {
				if (oldVal.intValue() < this.stageBtns.size()) {
					this.stageBtns.get(oldVal.intValue()).getStyleClass().remove("active");
					this.stageBtns.get(oldVal.intValue()).setFocusTraversable(false);
					this.stageBtns.get(this.currentStage.get()).getStyleClass().add("active");
					this.stageBtns.get(this.currentStage.get()).requestFocus();
				}

				this.borderPane_WizardContent.setCenter(this.stages.get(this.currentStage.get()));
//                this.borderPane_WizardContent.getCenter().requestFocus();
			}
		});

		// increment the currentStage counter. Will trigger its changeListener
		this.button_Next.setOnAction(event -> {
			this.currentStage.set(this.currentStage.get() + 1);

			if (currentStage.get() + 1 == stages.size()) {
				button_Create.requestFocus();
			}
		});

		// decrement the currentStage counter. Will trigger its changeListener
		this.button_Back.setOnAction(event -> this.currentStage.set(this.currentStage.get() - 1));

		// closes the wizard
		this.button_Cancel.setOnAction(event -> {
			GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
			GuiSvgPlott.getInstance().closeWizard(false);
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
			boolean doCreate = true;
			if (guiSvgOptions.getCsvPath() != null && guiSvgOptions.getCsvPath().isEmpty()) {
				Alert a = 	dialogUtil.alert(AlertType.CONFIRMATION, "empty_diagram_title", "empty_diagram_header", "empty_diagram_content");
				a.showAndWait();
				doCreate = a.getResult().getButtonData().isDefaultButton();
			}
			if (doCreate) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(userDir);
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
						"Scalable Vector Graphics (SVG)", "*.svg");
				fileChooser.getExtensionFilters().add(extFilter);
				String title = this.guiSvgOptions.getTitle().isEmpty() ? "untitled" : this.guiSvgOptions.getTitle();
				fileChooser.setInitialFileName(title.toLowerCase() + ".svg");
				File file = fileChooser.showSaveDialog(GuiSvgPlott.getInstance().getPrimaryStage());
				if (file != null) {
					try {
						File tempCSV = new File(this.guiSvgOptions.getCsvPath());
						File newCsv = new File(file.getAbsolutePath().replaceAll(".svg", ".csv"));
						if (tempCSV.exists())
							Files.copy(tempCSV.toPath(), newCsv.toPath(), StandardCopyOption.REPLACE_EXISTING);

						this.guiSvgOptions.setOutput(file.getAbsolutePath());
						this.svgOptionsService.buildSVG(guiSvgOptions.getOptions());
						this.popOver_infos.hide();
						this.popOver_warnings.hide();
						GuiSvgPlott.getInstance().closeWizard(true);
					} catch (ValidationException | IOException e) {
						logger.error(this.bundle.getString("svg_creation_validation_error"));
					}
				}
			}
		});

		// rerender preview
		this.button_rerenderPreview.graphicProperty().setValue(new Glyph("FontAwesome", '\uf021'));
		this.button_rerenderPreview
				.setOnAction(event -> this.svgOptionsService.buildPreviewSVG(this.guiSvgOptions, this.webView_svg));

		this.button_Load.graphicProperty().setValue(new Glyph("FontAwesome", FontAwesome.Glyph.FOLDER_OPEN));

	}

	protected void initiateAllStages() {

	}

	/**
	 * initiates the preset list and adds an event listener to the load button
	 */
	protected void initloadPreset() {
		button_Load.setOnAction(event -> {
			List<String> choices = new ArrayList<>();
			presets.sort((o1, o2) -> (o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase())));
			for (Preset p : presets) {
				choices.add(p.getName());
			}
			ChoiceDialog<String> dialog = new ChoiceDialog<>(bundle.getString("combo_preset_prompt"), choices);
			dialog.setTitle(bundle.getString("prompt_load_title"));
			dialog.setHeaderText(bundle.getString("prompt_load_header"));
			dialog.setContentText(bundle.getString("prompt_load_content"));
			dialogUtil.styleDialog(dialog);

			GuiSvgPlott.getInstance().getRootFrameController().loading.set(true);
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				for (Preset preset : presets) {
					if (preset.getName().equals(result.get())) {
						this.guiSvgOptions.update(preset.getOptions());
						svgOptionsService.buildPreviewSVG(guiSvgOptions, webView_svg);
					}

				}
			}
			GuiSvgPlott.getInstance().getRootFrameController().loading.set(false);
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
	 * @param visible whether field {@link Node} and {@link Label} should be
	 *                visible.
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
	 * Sets the value and visibility custom size {@link TextField}s and
	 * corresponding {@link Label}s.
	 *
	 * @param show if field and label should be visible.
	 */
	protected void toggleCustomSize(final boolean show) {
		this.toggleVisibility(show, this.label_customSizeWidth, this.textField_customSizeWidth);
		this.toggleVisibility(show, this.label_customSizeHeight, this.textField_customSizeHeight);

		if (show) {
			this.textField_customSizeWidth.setText(this.size.get().x());
			this.textField_customSizeWidth.requestFocus();
			this.textField_customSizeHeight.setText(this.size.get().y());
		}
	}

	/**
	 * Sets the value and visibility of x- and y-{@link Range} and corresponding
	 * {@link Node}s.
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
	protected void initOptionListeners() {
		this.guiSvgOptions.outputDeviceProperty().addListener((observable, oldValue, newValue) -> {
			this.choiceBox_outputDevice.getSelectionModel().select(newValue);
		});
		this.guiSvgOptions.gridStyleProperty().addListener((observable, oldValue, newValue) -> {
			this.choicebox_gridStyle.getSelectionModel().select(newValue);
		});
		this.guiSvgOptions.csvTypeProperty().addListener((observable, oldValue, newValue) -> {
			this.choiceBox_csvType.getSelectionModel().select(newValue);
		});
		this.guiSvgOptions.sizeProperty().addListener((observable, oldValue, newValue) -> {
			PageSize pageSize = PageSize.getByPoint(newValue);
			PageSize.PageOrientation pageOrientation = PageSize.PageOrientation.getByPoint(newValue);
			this.choiceBox_size.getSelectionModel().select(pageSize);
			if (pageSize.equals(PageSize.CUSTOM)) {
				if (pageOrientation.equals(PageSize.PageOrientation.PORTRAIT)) {
					textField_customSizeWidth.setText("" + newValue.getX());
					textField_customSizeHeight.setText("" + newValue.getY());
				} else {
					textField_customSizeWidth.setText("" + newValue.getY());
					textField_customSizeHeight.setText("" + newValue.getX());
				}
			}
			this.pageOrientation.set(pageOrientation);
		});
		this.guiSvgOptions.xRangeProperty().addListener((observable, oldValue, newValue) -> {
			this.xRange.set(newValue);
		});
		this.guiSvgOptions.yRangeProperty().addListener((observable, oldValue, newValue) -> {
			this.yRange.set(newValue);
		});
		this.guiSvgOptions.cssProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null && !newValue.isEmpty()) {
				if (newValue.contains("{")) {
					this.choiceBox_cssType.getSelectionModel().select(CssType.CUSTOM);
					this.textArea_cssCustom.setText(newValue);
				} else
					choiceBox_cssType.getSelectionModel().select(CssType.FILE);
			} else {
				choiceBox_cssType.getSelectionModel().select(CssType.NONE);
			}
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

		tempFileProp.set(tempFile);

		GuiSvgPlott.possibleTempFiles.add(tempFile);

		System.out.println(tempFile);

		return (tempFile != null) ? tempFile.toString() : "";
	}

	private void mergeDataSet(String originFile, Path resultFile) throws IOException {

		File f = resultFile.toFile();

		Files.write(resultFile, "".getBytes());

		Files.readAllLines(Paths.get(originFile)).forEach(line -> {

			try {
				Files.write(resultFile, (line + System.lineSeparator()).getBytes(Charset.defaultCharset()),
						StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		if (currentDataSet != null && currentDataSet.get() != null)
			Files.readAllLines(Paths.get(currentDataSet.get().getAbsolutePath())).forEach(line -> {

				try {
					Files.write(resultFile, (line + System.lineSeparator()).getBytes(Charset.defaultCharset()),
							StandardOpenOption.APPEND);
				} catch (IOException e) {
					e.printStackTrace();
				}

			});
		GuiSvgPlott.possibleTempFiles.add(resultFile);

		f.deleteOnExit();

	}

	protected ObjectProperty<Path> getResultFileProp() {
		return tempFileProp;
	}

	protected void initFieldListenersForPreview() {
		this.choiceBoxUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions,
				this.choiceBox_outputDevice, this.choiceBox_size, this.choicebox_gridStyle, this.choiceBox_cssType);
		this.textFieldUtil.addReloadPreviewOnChangeListener(this.webView_svg, this.guiSvgOptions, this.textField_title,
				this.textField_customSizeWidth, this.textField_customSizeHeight, this.textField_xfrom,
				this.textField_xto, this.textField_yfrom, this.textField_yto, this.textField_xlines,
				this.textField_ylines, this.textField_customSizeWidth, this.textField_customSizeHeight);
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

	public void incrementCurrentStage() {
		this.currentStage.set(this.currentStage.get() + 1);
	}

	public void decrementCurrentStage() {
		if (this.currentStage.get() >= 0) {
			this.currentStage.set(this.currentStage.get() - 1);
		}
	}

}
