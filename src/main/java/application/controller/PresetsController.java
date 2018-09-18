package application.controller;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.model.Options.*;
import application.model.Preset;
import application.service.PresetService;
import application.util.SvgOptionsUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    public SvgOptionsUtil svgOptionsUtil = SvgOptionsUtil.getInstance();
    private Preset currentPreset;
    private GuiSvgOptions currentOptions = new GuiSvgOptions(new SvgPlotOptions());
    private final ToggleGroup scaleGroup = new ToggleGroup();
    @FXML
    private final ToggleGroup pageOrientationTG = new ToggleGroup();
    DiagramType.DiagramTypeConverter converter = new DiagramType.DiagramTypeConverter();
    private PresetService presetService = PresetService.getInstance();
    private ObservableList<Texture> textures;
    private ObservableList<PointSymbol> customPointSymbols_scatterPlott;

    @FXML
    public VBox vbox_Preset_DataTable;
    @FXML
    private HBox hbox_firstLineStyle;
    @FXML
    private HBox hbox_secondLineStyle;
    @FXML
    private HBox hbox_thirdLineStyle;
    @FXML
    private HBox hbox_firstTexture;
    @FXML
    private HBox hbox_secondTexture;
    @FXML
    private HBox hbox_thirdTexture;
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
    private ChoiceBox choicebox_sortOrder;
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
    private Button button_CsvPath;
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
    private ChoiceBox choiceBox_csvType;
    @FXML
    private ChoiceBox choiceBox_gridStyle;
    @FXML
    private ChoiceBox choiceBox_dblaxes;
    @FXML
    private ChoiceBox choiceBox_cssType;
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
    private TextField textField_Csvpath;
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
    private TextField textField_PresetName;
    @FXML
    private RadioButton radioBtn_Scale_to_Data;
    @FXML
    private RadioButton radioBtn_customScale;
    @FXML
    private RadioButton radioBtn_Portrait;
    @FXML
    private RadioButton radioBtn_Landscape;
    @FXML
    private BorderPane overViewBorderPane;
    @FXML
    private BorderPane editorBorderPane;
    @FXML
    private ChoiceBox choiceBox_thirdLineStyle;
    @FXML
    private ChoiceBox choiceBox_firstLineStyle;
    @FXML
    private ChoiceBox choiceBox_baraccumulation;



    public PresetsController() {
        presetsController = this;
    }

    public static PresetsController getInstance() {
        return presetsController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.svgOptionsUtil = SvgOptionsUtil.getInstance();
        this.svgOptionsUtil.setBundle(resources);
        chartTypeObservableList.add(resources.getString("combo_diagram"));
        chartTypeObservableList.add(resources.getString("combo_function"));
        overViewBorderPane.setVisible(true);

        this.presetService.setBundle(bundle);
        if (super.presets == null) {
            presets = FXCollections.observableArrayList(this.presetService.getAll());
            loadTable();
        }
        super.presets.addListener(new ListChangeListener<Preset>() {
            @Override
            public void onChanged(Change<? extends Preset> c) {
                loadTable();
            }
        });
    }




    private void initEditor(){
        //outputdevice
        choiceBox_outputDevice.setItems(FXCollections.observableArrayList(OutputDevice.values()));
        choiceBox_outputDevice.setConverter(svgOptionsUtil.getOutputDeviceStringConverter());
        //pagesize
        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        choiceBox_size.setItems(sortedPageSizes);
        choiceBox_size.setConverter(svgOptionsUtil.getPageSizeStringConverter());
        choiceBox_size.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.equals(0)){
                    label_customSizeHeight.setVisible(true);
                    label_customSizeWidth.setVisible(true);
                    textField_customSizeHeight.setVisible(true);
                    textField_customSizeWidth.setVisible(true);
                }else{
                    label_customSizeHeight.setVisible(false);
                    label_customSizeWidth.setVisible(false);
                    textField_customSizeHeight.setVisible(false);
                    textField_customSizeWidth.setVisible(false);
                }
            }
        });
        //CSV
        button_CsvPath.setDisable(false);
        button_CsvPath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                textField_Csvpath.setText(file.getAbsolutePath());
            }
        });
        choiceBox_csvType.setItems(FXCollections.observableArrayList(CsvType.values()));
        choiceBox_csvType.setConverter(svgOptionsUtil.getCsvTypeStringConverter());
        //trendline
//        choiceBox_trendline.setItems(FXCollections.observableArrayList(TrendlineAlgorithm.values()));
//        choiceBox_trendline.setConverter(svgOptionsUtil.getTrendlineAlgorithmStringConverter());
//        choiceBox_trendline.getSelectionModel().select(0);
        //gridstyle
        choiceBox_gridStyle.setItems(FXCollections.observableArrayList(GridStyle.values()));
        choiceBox_gridStyle.setConverter(svgOptionsUtil.getGridStyleStringConverter());
        //doubleaxis
        choiceBox_dblaxes.setItems(FXCollections.observableArrayList(GuiAxisStyle.values()));
        choiceBox_dblaxes.setConverter(svgOptionsUtil.getAxisStyleStringConverter());
        //css
        choiceBox_cssType.setItems(FXCollections.observableArrayList(CssType.values()));
        choiceBox_cssType.getSelectionModel().select(0);
        choiceBox_cssType.setConverter(svgOptionsUtil.getCssTypeStringConverter());
        choiceBox_cssType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                switch (newValue.toString()){
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
        radioBtn_Scale_to_Data.setToggleGroup(scaleGroup);
        radioBtn_Scale_to_Data.setSelected(true);
        radioBtn_customScale.setToggleGroup(scaleGroup);
        radioBtn_customScale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if(isNowSelected){
                    label_x_from.setVisible(true);
                    textField_x_from.setVisible(true);
                    label_x_to.setVisible(true);
                    textField_x_to.setVisible(true);
                    label_y_from.setVisible(true);
                    textField_y_from.setVisible(true);
                    label_y_to.setVisible(true);
                    textField_y_to.setVisible(true);
                }
            }
        });
        radioBtn_Scale_to_Data.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if(isNowSelected){
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

    private void cssCustomHider(){
        label_cssCustom.setVisible(false);
        textArea_cssCustom.setVisible(false);
        label_cssPath.setVisible(false);
        hBox_cssPath.setVisible(false);
    }

    private void initLineChart(){
        textField_PresetName.setText(currentPreset.getName());
        label_secondLineStyle.setVisible(true);
        hbox_secondLineStyle.setVisible(true);
        label_thirdLineStyle.setVisible(true);
        hbox_thirdLineStyle.setVisible(true);
        label_firstLineStyle.setVisible(true);
        hbox_firstLineStyle.setVisible(true);
        label_linepoints.setVisible(true);
        choiceBox_linepoints.setVisible(true);
        ObservableList<LineStyle> secondLineStyleObservableList = FXCollections.observableArrayList(LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_secondLineStyle.setItems(secondLineStyleObservableList);
        choiceBox_secondLineStyle.setConverter(this.svgOptionsUtil.getLineStyleStringConverter());
        ObservableList<LineStyle> thirdLineStyleObservableList = FXCollections.observableArrayList(LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_thirdLineStyle.setItems(thirdLineStyleObservableList);
        choiceBox_thirdLineStyle.setConverter(this.svgOptionsUtil.getLineStyleStringConverter());
        ObservableList<LineStyle> firstLineStyleObservableList = FXCollections.observableArrayList(LineStyle.getByOutputDeviceOrderedById(currentPreset.getOptions().getOutputDevice()));
        choiceBox_firstLineStyle.setItems(firstLineStyleObservableList);
        choiceBox_firstLineStyle.setConverter(this.svgOptionsUtil.getLineStyleStringConverter());
        ObservableList<LinePointsOption> linePointsOptionObservableList = FXCollections.observableArrayList(LinePointsOption.values());
        choiceBox_linepoints.setItems(linePointsOptionObservableList);
        choiceBox_linepoints.setConverter(svgOptionsUtil.getLinePointsOptionStringConverter());
        button_resetFirstLineStyle.setOnAction(event -> {
            choiceBox_firstLineStyle.getSelectionModel().select(null);
        });
        button_resetSecondLineStyle.setOnAction(event -> {
            choiceBox_secondLineStyle.getSelectionModel().select(null);
        });
        button_resetThirdLineStyle.setOnAction(event -> {
            choiceBox_thirdLineStyle.getSelectionModel().select(null);
        });


    }

    private void initScatterPlot(){
        textField_PresetName.setText(currentPreset.getName());

        //label_pointSymbols_scatterPlot.setVisible(true);
        label_trendline.setVisible(true);
        label_trendline.setVisible(true);
        choiceBox_trendline.setVisible(true);
        //TODO: needs visibility handling
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections.observableArrayList(TrendlineAlgorithm.values());
        choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        choiceBox_trendline.setConverter(svgOptionsUtil.getTrendlineAlgorithmStringConverter());
        choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
//
//        ObservableList<PointSymbol> pointSymbolObservableList = FXCollections.observableArrayList(PointSymbol.getOrdered());
//        //this.customPointSymbols_scatterPlott = guiSvgOptions.getPointSymbols();
//        choiceBox_pointSymbols_scatterPlot.setVisible(true);
//        choiceBox_pointSymbols_scatterPlot.getItems().addAll(pointSymbolObservableList);
//        choiceBox_pointSymbols_scatterPlot.setConverter(this.svgOptionsUtil.getPointSymbolStringConverter());
////        choiceBox_pointSymbols_scatterPlot.getCheckModel().getCheckedItems().addListener(new ListChangeListener<PointSymbol>() {
////            public void onChanged(ListChangeListener.Change<? extends PointSymbol> ps) {
////                changePointSymbols(customPointSymbols_scatterPlott, choiceBox_pointSymbols_scatterPlot, pointSymbolObservableList);
////            }
////        });



    }

    private void initBarChart(){
        textField_PresetName.setText(currentPreset.getName());
        label_baraccumulation.setVisible(true);
        label_firstTexture.setVisible(true);
        hbox_firstTexture.setVisible(true);
        label_secondTexture.setVisible(true);
        hbox_secondTexture.setVisible(true);
        label_thirdTexture.setVisible(true);
        hbox_thirdTexture.setVisible(true);
        choicebox_sortOrder.setVisible(true);
        label_sortOrder.setVisible(true);
        choiceBox_baraccumulation.setVisible(true);
        button_resetFirstTexture.setOnAction(event -> {
            choiceBox_firstTexture.getSelectionModel().select(null);
        });
        button_resetSecondTexture.setOnAction(event -> {
            choiceBox_secondTexture.getSelectionModel().select(null);
        });
        button_resetThirdTexture.setOnAction(event -> {
            choiceBox_thirdTexture.getSelectionModel().select(null);
        });
        ObservableList<BarAccumulationStyle> barAccumulationStyleObservableList = FXCollections.observableArrayList(BarAccumulationStyle.values());
        choiceBox_baraccumulation.setItems(barAccumulationStyleObservableList);
        choiceBox_baraccumulation.setConverter(svgOptionsUtil.getBarAccumulationStyleStringConverter());

        ObservableList<Texture> textureObservableListFirstTexture = FXCollections.observableArrayList(Texture.values());
        ObservableList<Texture> textureObservableListSecondTexture = FXCollections.observableArrayList(Texture.values());
        ObservableList<Texture> textureObservableListThirdTexture = FXCollections.observableArrayList(Texture.values());
        choiceBox_firstTexture.setItems(textureObservableListFirstTexture);
        choiceBox_secondTexture.setItems(textureObservableListSecondTexture);
        choiceBox_thirdTexture.setItems(textureObservableListThirdTexture);
        choiceBox_firstTexture.setConverter(svgOptionsUtil.getTextureStringConverter());
        choiceBox_secondTexture.setConverter(svgOptionsUtil.getTextureStringConverter());
        choiceBox_thirdTexture.setConverter(svgOptionsUtil.getTextureStringConverter());

        ObservableList<SortingType> sortingTypeObservableList = FXCollections.observableArrayList(SortingType.values());
        choiceBox_sorting.setItems(sortingTypeObservableList);
        choiceBox_sorting.setConverter(svgOptionsUtil.getSortingTypeStringConverter());
        choiceBox_sorting.getSelectionModel().select(SortingType.None);

        ObservableList<SortOrder> sortOrderObservableList = FXCollections.observableArrayList(SortOrder.values());
        choicebox_sortOrder.setItems(sortOrderObservableList);
        choicebox_sortOrder.setConverter(svgOptionsUtil.getSortOrderStringConverter());
        choicebox_sortOrder.getSelectionModel().select(SortOrder.ASC);
    }



    private void initFunction() {
        //TODO
        textField_PresetName.setText(currentPreset.getName());

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

    @FXML
    private void createNewPreset() {
        diagramTypePrompt();
    }

    private void diagramTypePrompt() {
        // arbitrary default value
        DiagramType dt;
        List<String> choices = new ArrayList<>();
        //TODO: needs i18n implementation that doesnt suck
        choices.add(DiagramType.FunctionPlot.toString());
        choices.add(DiagramType.ScatterPlot.toString());
        choices.add(DiagramType.LineChart.toString());
        choices.add(DiagramType.BarChart.toString());
        ChoiceDialog<String> dialogue = new ChoiceDialog<>(DiagramType.LineChart.toString(), choices);
        dialogue.setTitle(bundle.getString("prompt_diagramtype_title"));
        dialogue.setResizable(true);
        dialogue.setHeaderText(bundle.getString("prompt_diagramtype_header"));
        dialogue.setContentText(bundle.getString("prompt_diagramtype_content"));
        Optional<String> result = dialogue.showAndWait();
        if (result.isPresent()) {
            dt = converter.convert(result.get());
            presetNamePrompt(dt);
        }
    }

    private void presetNamePrompt(DiagramType dt) {
        TextInputDialog nameDialogue = new TextInputDialog();
        nameDialogue.setTitle(bundle.getString("prompt_preset_name_title"));
        nameDialogue.setHeaderText(bundle.getString("prompt_preset_name_header"));
        nameDialogue.setContentText(bundle.getString("prompt_preset_name_content"));
        Optional<String> result = nameDialogue.showAndWait();
        if (result.get().equals("")) {
            emptyNameAlert();
        } else if (result.isPresent() && !super.presets.stream().map(p -> p.getName()).anyMatch(n -> n.equals(result.get()))) {
            currentPreset = new Preset(currentOptions, result.get(), dt);
            presetService.create(currentPreset);
            super.presets.add(currentPreset);

        } else {
            duplicateAlert(result);
        }
    }

    private void loadTable() {
        vbox_Preset_DataTable.getChildren().clear();
        for (Preset preset : super.presets) {
            vbox_Preset_DataTable.getChildren().add(generateTableEntry(preset));
        }
    }

    private HBox generateTableEntry(final Preset preset) {
        HBox row = new HBox();
        row.setSpacing(5);
        row.getStyleClass().add("data-row");

        TextField nameField = new TextField(preset.getName());
        nameField.setFocusTraversable(true);
        nameField.getStyleClass().add("data-cell-x");
        nameField.setMouseTransparent(true);
        nameField.setEditable(false);

        TextField creationDateField = new TextField(preset.getFormattedCreationDate());
        creationDateField.setFocusTraversable(true);
        creationDateField.getStyleClass().add("data-cell-y");
        creationDateField.setText(bundle.getString("label_created_on") + preset.getFormattedCreationDate());
        creationDateField.setEditable(false);
        creationDateField.setMouseTransparent(true);

        TextField diagramTypeField = new TextField("");
        diagramTypeField.setFocusTraversable(true);
        diagramTypeField.getStyleClass().add("data-cell-z");
        diagramTypeField.setText(preset.getDiagramTypeString());
        diagramTypeField.setEditable(false);
        diagramTypeField.setMouseTransparent(true);

        Button editButton = new Button();
        Button copyButton = new Button();
        copyButton.setDisable(true);
        Button removeButton = new Button();
        Glyph editGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.EDIT);
        Glyph copyGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.COPY);
        Glyph removeGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.TRASH);
        editButton.setGraphic(editGlyph);
        copyButton.setGraphic(copyGlyph);
        removeButton.setGraphic(removeGlyph);
        editButton.setOnAction(event -> {
            currentPreset = preset;
            overViewHider();
            switch (currentPreset.getDiagramType()) {
                case FunctionPlot:
                    initEditor();
                    initFunction();
                    editorDisplayer();
                    flagSetter(DiagramType.FunctionPlot, currentPreset);
                    break;
                case LineChart:
                    initEditor();
                    initLineChart();
                    editorDisplayer();
                    flagSetter(DiagramType.LineChart, currentPreset);
                    break;
                case ScatterPlot:
                    initEditor();
                    initScatterPlot();
                    editorDisplayer();
                    flagSetter(DiagramType.ScatterPlot, currentPreset);
                    break;
                case BarChart:
                    initEditor();
                    initBarChart();
                    editorDisplayer();
                    flagSetter(DiagramType.BarChart, currentPreset);
                    break;
            }
        });

        copyButton.setOnAction(event -> {
            vbox_Preset_DataTable.getChildren().add(row);
            Preset copiedPreset = new Preset(preset);
            //TODO: fix double id entry exception
            copiedPreset.setName(copiedPreset.getName() + " Kopie");
            this.presetService.create(preset);
        });

        removeButton.setOnAction(event -> {
            deleteConfirmationAlert(preset);
        });

        row.getChildren().addAll(nameField, creationDateField, diagramTypeField, editButton, copyButton, removeButton);
        HBox.setHgrow(nameField, Priority.ALWAYS);
        HBox.setHgrow(diagramTypeField, Priority.ALWAYS);
        HBox.setHgrow(creationDateField, Priority.ALWAYS);
        HBox.setHgrow(editButton, Priority.NEVER);
        HBox.setHgrow(copyButton, Priority.NEVER);
        HBox.setHgrow(removeButton, Priority.NEVER);
        return row;
    }

    private void flagGetter() {
        //Stage 1: basics
        currentPreset.getOptions().setTitle(textField_Title.getText());
        currentPreset.getOptions().setOutputDevice(choiceBox_outputDevice.getSelectionModel().getSelectedItem());
        //TODO: page orientation
        //currentPreset.getOptions().set
        //TODO: need help converting :C
        //currentOptions.setSize(PageSize.A4.getPageSizeWithOrientation(PageSize.A4));

        //Stage 2: data
        currentPreset.getOptions().setCsvPath(textField_Csvpath.getText());
        currentPreset.getOptions().setCsvType((CsvType) choiceBox_csvType.getSelectionModel().getSelectedItem());

        //Stage 3: specific
        //TODO
        switch (currentPreset.getDiagramTypeString()){
            case "FunctionPlot":
                //TODO
                //currentPreset.getOptions().setIntegral();

                break;
            case "BarChart":
                //TODO
                currentPreset.getOptions().setBarAccumulationStyle((BarAccumulationStyle)choiceBox_baraccumulation.getSelectionModel().getSelectedItem());

                currentPreset.getOptions().setSortingType((SortingType) choiceBox_sorting.getSelectionModel().getSelectedItem());
                currentPreset.getOptions().setSortOrder((SortOrder) choicebox_sortOrder.getSelectionModel().getSelectedItem());
                break;
            case "LineChart":
                //TODO

                //currentPreset.getOptions().setLineStyles();
                //currentPreset.getOptions().setLinePointsOption();
                break;
            case "ScatterPlot":
                //TODO
                //currentPreset.getOptions().setTrendLine();
                break;

        }
//        currentOptions.setTrendLine(choiceBox_trendline.getSelectionModel().getSelectedItem());

        //Stage 4: axis
        currentPreset.getOptions().setxUnit(textField_xAxisTitle.getText());
        currentPreset.getOptions().setyUnit(textField_yAxisTitle.getText());
        if(radioBtn_Scale_to_Data.isSelected()){
            currentPreset.getOptions().setAutoScale(true);
        }else{
            currentPreset.getOptions().setAutoScale(false);
        }

        //Stage 5: special
        currentPreset.getOptions().setGridStyle((GridStyle) choiceBox_gridStyle.getSelectionModel().getSelectedItem());
        if(textField_helpLinesX != null && !textField_helpLinesX.getText().isEmpty()){
            currentPreset.getOptions().setxLines(textField_helpLinesX.getText());
        }
        if(textField_helpLinesY != null && !textField_helpLinesY.getText().isEmpty()){
            currentPreset.getOptions().setyLines(textField_helpLinesY.getText());
        }
        currentPreset.getOptions().setAxisStyle((GuiAxisStyle) choiceBox_dblaxes.getSelectionModel().getSelectedItem());

        //Stage 6: display
        if(choiceBox_cssType.getSelectionModel().getSelectedItem().equals("CUSTOM")){
            currentPreset.getOptions().setCss(textArea_cssCustom.getText());
        }else if (choiceBox_cssType.getSelectionModel().getSelectedItem().equals("FILE")){
            currentPreset.getOptions().setCss(textField_cssPath.getText());
        }
    }


    private void flagSetter(DiagramType dt, Preset p){
        GuiSvgOptions options = p.getOptions();
        options.setDiagramType(dt);

        //Stage 1: basics
        textField_Title.setText(options.getTitle());
        choiceBox_outputDevice.getSelectionModel().select(options.getOutputDevice());
        //TODO how to get page orientation from presets?
        //if(currentPreset.getOptions().)
        pageOrientationTG.selectToggle(radioBtn_Portrait);
        //TODO how to get size of diagram from presets/options?
        choiceBox_size.getSelectionModel().select(PageSize.A4);

        //Stage 2: data
        textField_Csvpath.setText(options.getCsvPath());
        choiceBox_csvType.getSelectionModel().select(options.getCsvType());

        //Stage 3: specific
        switch (currentPreset.getDiagramTypeString()){
            case "FunctionPlot":
                //TODO


                break;
            case "BarChart":
                //TODO
                choiceBox_baraccumulation.getSelectionModel().select(currentPreset.getOptions().getBarAccumulationStyle());
                //this is not how it supposed to work but in the current implementation it works like this
                choiceBox_firstTexture.getSelectionModel().select(currentPreset.getOptions().getTextures().get(0));
                choiceBox_secondTexture.getSelectionModel().select(currentPreset.getOptions().getTextures().get(1));
                choiceBox_thirdTexture.getSelectionModel().select(currentPreset.getOptions().getTextures().get(2));
                choiceBox_sorting.getSelectionModel().select(currentPreset.getOptions().getSortingType());
                choicebox_sortOrder.getSelectionModel().select(currentPreset.getOptions().getSortOrder());
                break;
            case "LineChart":
                //TODO
                //choiceBox_firstLineStyle.getSelectionModel().select(currentPreset.getOptions().getLineStyles());
//                choiceBox_secondLineStyle.getSelectionModel().select(currentPreset.getOptions().getLineStyles());
//                choiceBox_thirdLineStyle.getSelectionModel().select(currentPreset.getOptions().getLineStyles());
                break;
            case "ScatterPlot":
                //TODO

                break;

        }

        //Stage 4: axis
        textField_xAxisTitle.setText(options.getxUnit());
        textField_yAxisTitle.setText(options.getyUnit());
        if(options.isAutoScale()){
            scaleGroup.selectToggle(radioBtn_Scale_to_Data);
        }else{
            scaleGroup.selectToggle(radioBtn_customScale);
            if( options.getxRange() != null || options.getyRange() != null) {
                textField_x_from.setText(String.valueOf(options.getxRange().getFrom()));
                textField_x_to.setText(String.valueOf(options.getxRange().getTo()));
                textField_y_from.setText(String.valueOf(options.getyRange().getFrom()));
                textField_y_to.setText(String.valueOf(options.getyRange().getTo()));
            }
        }
        //Stage 5: special
        choiceBox_gridStyle.getSelectionModel().select(options.getGridStyle());
        textField_helpLinesX.setText(options.getxLines());
        textField_helpLinesY.setText(options.getyLines());
        choiceBox_dblaxes.getSelectionModel().select(options.getAxisStyle());
        //Stage 6: display
        //TODO: remove following line before release
        choiceBox_cssType.getSelectionModel().select(0);
        if(options.getCss() != null && !options.getCss().isEmpty()){
            textArea_cssCustom.setText(options.getCss());
            textField_cssPath.setText(options.getCss());
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
        editorBorderPane.setVisible(true);
    }

    private void editorHider() {
        editorBorderPane.setVisible(false);
    }

    private void lineChartEditorHider(){
        label_secondLineStyle.setVisible(false);
        hbox_secondLineStyle.setVisible(false);
        label_thirdLineStyle.setVisible(false);
        hbox_thirdLineStyle.setVisible(false);
        label_firstLineStyle.setVisible(false);
        hbox_firstLineStyle.setVisible(false);
        label_linepoints.setVisible(false);
        choiceBox_linepoints.setVisible(false);
    }



    private void scatterPlotEditorHider(){
        label_trendline.setVisible(false);
        label_trendline.setVisible(false);
        choiceBox_trendline.setVisible(false);
    }



    private void barChartEditorHider() {
        label_baraccumulation.setVisible(false);
        label_firstTexture.setVisible(false);
        hbox_firstTexture.setVisible(false);
        label_secondTexture.setVisible(false);
        hbox_secondTexture.setVisible(false);
        label_thirdTexture.setVisible(false);
        hbox_thirdTexture.setVisible(false);
        choicebox_sortOrder.setVisible(false);
        label_sortOrder.setVisible(false);
        choiceBox_baraccumulation.setVisible(false);
    }


    private void functionPlotEditorHider(){

    }

    @FXML
    private void deletePreset() {
        Preset tobedeletedPreset = currentPreset;
        deleteConfirmationAlert(tobedeletedPreset);
        hideAllEditors();
        overviewDisplayer();
    }

    private void hideAllEditors() {
        editorHider();
        lineChartEditorHider();
        scatterPlotEditorHider();
        functionPlotEditorHider();
        barChartEditorHider();
    }

    @FXML
    private void backToOverview(){
        hideAllEditors();
        overviewDisplayer();
    }

    @FXML
    private void savePreset(){
        if(!isPresetNameEmpty()){
            flagGetter();
            hideAllEditors();
            presetService.save(currentPreset);
            overviewDisplayer();
        }else{
            emptyNameAlert();
        }
    }

    public boolean isPresetNameEmpty(){
        if(textField_PresetName.getText().isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    public void deleteConfirmationAlert(Preset p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("alert_preset_delete_title"));
        alert.setHeaderText(bundle.getString("alert_preset_delete_header"));
        alert.setContentText(bundle.getString("alert_preset_delete_content1") + p.getName() + bundle.getString("alert_preset_delete_content2"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            this.presetService.delete(p);
            super.presets.remove(p);
        } else {
            // ... user chose CANCEL or closed the dialog, hence do nothing
        }
    }

    public void duplicateAlert(Optional o) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("alert_preset_duplicate_title"));
        alert.setHeaderText(bundle.getString("alert_preset_duplicate_header1") + o.get() + bundle.getString("alert_preset_duplicate_header2"));
        alert.setContentText(bundle.getString("alert_preset_duplicate_content"));
        alert.showAndWait();
    }

    public void emptyNameAlert() {
        Alert alarm = new Alert(Alert.AlertType.ERROR);
        alarm.setTitle(bundle.getString("alert_preset_empty_title"));
        alarm.setHeaderText(bundle.getString("alert_preset_empty_header"));
        alarm.setContentText(bundle.getString("alert_preset_empty_content"));
        alarm.showAndWait();
    }

    @FXML
    private void quitToMainMenu() {
        GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
        GuiSvgPlott.getInstance().getRootFrameController().menuItem_Preset_Editor.setDisable(false);
        GuiSvgPlott.getInstance().closeWizard();
    }


}
