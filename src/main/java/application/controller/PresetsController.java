package application.controller;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.model.Options.CssType;
import application.model.Options.GuiAxisStyle;
import application.model.Options.PageSize;
import application.model.Preset;
import application.service.PresetService;
import application.util.SvgOptionsUtil;
import com.google.gson.JsonObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.GridStyle;

import java.io.File;
import java.net.URL;
import java.util.*;

public class PresetsController extends SVGWizardController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(PresetsController.class);
    private ResourceBundle bundle;

    private static PresetsController presetsController;


    public SvgOptionsUtil svgOptionsUtil = SvgOptionsUtil.getInstance();
    private Preset currentPreset;
    private GuiSvgOptions defaultOptions;
    private GuiSvgOptions currentOptions = new GuiSvgOptions(new SvgPlotOptions());
    private final ToggleGroup scaleGroup = new ToggleGroup();
    @FXML
    private final ToggleGroup pageOrientationTG = new ToggleGroup();
    private JsonObject settingsJSON = new JsonObject();
    private ArrayList flags = new ArrayList();
    DiagramType.DiagramTypeConverter converter = new DiagramType.DiagramTypeConverter();
    private PresetService presetService = PresetService.getInstance();

    @FXML
    public VBox vbox_Preset_DataTable;
    @FXML
    private TableColumn table_column_name;
    @FXML
    private TableColumn table_column_date;
    @FXML
    private TableColumn table_column_diagram_type;
    @FXML
    private ObservableList<String> chartTypeObservableList = FXCollections.observableArrayList();
    @FXML
    private ChoiceBox choiceBox_diagramType;
    @FXML
    private Button button_CsvPath;
    @FXML
    private ChoiceBox choiceBox_csvOrientation;
    @FXML
    private ChoiceBox choiceBox_csvType;
    @FXML
    private ChoiceBox choiceBox_trendline;
    @FXML
    private ChoiceBox choiceBox_gridStyle;
    @FXML
    private ChoiceBox choiceBox_dblaxes;
    @FXML
    private ChoiceBox choiceBox_cssType;
    @FXML
    private TextField textField_Title;
    @FXML
    private TextField textField_Csvpath;
    @FXML
    private TextField textField_xAxisTitle;
    @FXML
    private TextField textField_yAxisTitle;
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
    private HBox hBox_cssPath;
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
    private BorderPane lineChartEditorBorderPane;
    /*@FXML
    private BorderPane scatterPlotEditorBorderPane;
    @FXML
    private BorderPane functionPlotEditorBorderPane;
    @FXML
    private BorderPane barChartEditorBorderPane;*/


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
        //settingsDiagramGridPane.setVisible(false);
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


    private void initLineChart(){
        //outputdevice
        choiceBox_outputDevice.setItems(FXCollections.observableArrayList(OutputDevice.values()));
        choiceBox_outputDevice.setConverter(svgOptionsUtil.getOutputDeviceStringConverter());
        //pagesize
        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        choiceBox_size.setItems(sortedPageSizes);
        choiceBox_size.setConverter(svgOptionsUtil.getPageSizeStringConverter());
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
        choiceBox_csvOrientation.setItems(FXCollections.observableArrayList(CsvOrientation.values()));
        choiceBox_csvOrientation.setConverter(svgOptionsUtil.getCsvOrientationStringConverter());
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

    private void initScatterPlot(){

    }

    private void initBarChart(){

    }



    private void initFunction() {


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
        /*choices.add(bundle.getString("function_plot"));
        choices.add(bundle.getString("diagramType_scatterplot"));
        choices.add(bundle.getString("diagramType_linechart"));
        choices.add(bundle.getString("diagramType_barchart"));*/
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
        nameField.setEditable(false);
        nameField.getStyleClass().add("data-cell-x");
        TextField creationDateField = new TextField(preset.getFormattedCreationDate());
        creationDateField.setFocusTraversable(true);
        creationDateField.setEditable(false);
        creationDateField.getStyleClass().add("data-cell-y");
        creationDateField.setPrefWidth(300);
        creationDateField.setText(bundle.getString("label_created_on") + preset.getFormattedCreationDate());
        creationDateField.setDisable(true);
        TextField diagramTypeField = new TextField("");
        diagramTypeField.setFocusTraversable(true);
        diagramTypeField.setEditable(false);
        diagramTypeField.setDisable(true);
        diagramTypeField.getStyleClass().add("data-cell-z");
        diagramTypeField.setText(preset.getDiagramTypeString());

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
            switch (preset.getDiagramType()) {
                case FunctionPlot:
                    //functionPlotEditorDisplayer();
                    //flagSetter(DiagramType.FunctionPlot, currentPreset);
                    break;
                case LineChart:
                    initLineChart();
                    lineChartEditorDisplayer();
                    flagSetter(DiagramType.LineChart, preset);
                    break;
                case ScatterPlot:
                    //scatterPlotEditorDisplayer();
                    //flagSetter(DiagramType.ScatterPlot, currentPreset);
                    break;
                case BarChart:
                    //barChartEditorDisplayer();
                    //flagSetter(DiagramType.BarChart, currentPreset);
                    break;
            }
        });

        copyButton.setOnAction(event -> {
            vbox_Preset_DataTable.getChildren().add(row);
            Preset copiedPreset = new Preset(preset);
            this.presetService.create(preset);
        });

        removeButton.setOnAction(event -> {
            deleteConfirmationAlert(preset);
            //vbox_Preset_DataTable.getChildren().remove(row);
        });

        row.getChildren().addAll(nameField, creationDateField, diagramTypeField, editButton, copyButton, removeButton);
        return row;
    }


    private void flagGetter() {
        settingsJSON.addProperty("diagram_type", currentPreset.getDiagramTypeString());
        currentOptions.setDiagramType((DiagramType) choiceBox_diagramType.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("diagram_title", textField_Title.getText());
        currentOptions.setTitle(textField_Title.getText());
        settingsJSON.addProperty("output_device", choiceBox_outputDevice.getSelectionModel().getSelectedItem().toString());
        currentOptions.setOutputDevice(choiceBox_outputDevice.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("size_of_diagram", choiceBox_size.getSelectionModel().getSelectedItem().getName());
        //TODO: not yet implemented -> implement page sizes in GuiSVGOptions
        //currentOptions.setPageSize(choiceBox_size.getSelectionModel().getSelectedItem().getName());
        settingsJSON.addProperty("csv_file_path", textField_Csvpath.getText());
        currentOptions.setCsvPath(textField_Csvpath.getText());
        settingsJSON.addProperty("csv_orientation", choiceBox_csvOrientation.getSelectionModel().getSelectedItem().toString());
        // TODO: not sure if this works
        currentOptions.setCsvOrientation((CsvOrientation) choiceBox_csvOrientation.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("csv_type", choiceBox_csvType.getSelectionModel().getSelectedItem().toString());
        currentOptions.setCsvType((CsvType) choiceBox_csvType.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("trendline", choiceBox_trendline.getSelectionModel().getSelectedItem().toString());
        //wtf why u want list of trendlines
        //currentOptions.setTrendLine(choiceBox_trendline.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("x_axis_title", textField_xAxisTitle.getText());
        // not sure if implemented correctly
        currentOptions.setxUnit(textField_xAxisTitle.getText());
        settingsJSON.addProperty("y_axis_title", textField_yAxisTitle.getText());
        currentOptions.setyUnit(textField_yAxisTitle.getText());
        //TODO: scale data implementation in GuiSVGOptions


        settingsJSON.addProperty("gridstyle", choiceBox_gridStyle.getSelectionModel().getSelectedItem().toString());
        currentOptions.setGridStyle((GridStyle) choiceBox_gridStyle.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("help_lines_x", textField_helpLinesX.getText());
        currentOptions.setxLines(textField_helpLinesX.getText());
        settingsJSON.addProperty("help_lines_y", textField_helpLinesY.getText());
        currentOptions.setyLines(textField_helpLinesY.getText());
        settingsJSON.addProperty("axis_style", choiceBox_dblaxes.getSelectionModel().getSelectedItem().toString());
        currentOptions.setAxisStyle((GuiAxisStyle) choiceBox_dblaxes.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("css_type", choiceBox_cssType.getSelectionModel().getSelectedItem().toString());
        currentOptions.setCss(choiceBox_cssType.getSelectionModel().getSelectedItem().toString());
    }


    private void flagSetter(DiagramType dt, Preset p){
        switch(dt){
            case LineChart:
                //initLineChart();
                break;
            case ScatterPlot:
                initScatterPlot();
                break;
            case BarChart:
                initBarChart();
                break;
            case FunctionPlot:
                initFunction();
                break;
        }
        textField_PresetName.setText(p.getName());
        GuiSvgOptions options = p.getOptions();
        options.setDiagramType(dt);
        textField_Title.setText(options.getTitle());
        //outputdevice
        choiceBox_outputDevice.getSelectionModel().select(options.getOutputDevice());
        //choiceBox_size.getSelectionModel().select(options.getSize().);
        //TODO: need help with page size sorcery
        /*switch (super.pageOrientation){
               case PageSize.PageOrientation.LANDSCAPE:
           }*/
        choiceBox_size.setItems(FXCollections.observableArrayList(PageSize.values()));
        //TODO: choiceBox_size.getSelectionModel().select(options.getSize().)
        textField_Csvpath.setText(options.getCsvPath());
        choiceBox_csvOrientation.getSelectionModel().select(options.getCsvOrientation());
        choiceBox_csvType.getSelectionModel().select(options.getCsvType());
        if(options.getTrendLine().size()>0){
            choiceBox_trendline.getSelectionModel().select(options.getTrendLine());
            choiceBox_gridStyle.getSelectionModel().select(options.getGridStyle());
        }
        //textField_xAxisTitle.setText(options.getXAxisTitle());
        //textField_yAxistitle.setText(options.getYAxisTitle());
        if(options.isAutoScale()){
            scaleGroup.selectToggle(radioBtn_Scale_to_Data);
        }else{
            scaleGroup.selectToggle(radioBtn_customScale);
        }
        //textField_displayAreaXfrom.setText(options.getxRange());
        choiceBox_dblaxes.getSelectionModel().select(options.getAxisStyle());
        choiceBox_cssType.getSelectionModel().select(options.getCss());


    }


    private void loadPreset(String presetName) {
        Preset queriedPreset;
        for (Preset p : presets) {
            if (p.getName() == presetName) {
                // If shit goes down, look here for problems
                queriedPreset = p;
                currentPreset = queriedPreset;
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

    private void lineChartEditorDisplayer() {
        lineChartEditorBorderPane.setVisible(true);
    }

    private void lineChartEditorHider() {
        lineChartEditorBorderPane.setVisible(false);
    }
/*
    private void scatterPlotEditorDisplayer(){
        scatterPlotEditorBorderPane.setVisible(true);
    }

    private void scatterPlotEditorHider(){
        scatterPlotEditorBorderPane.setVisible(false);
    }

    private void functionPlotEditorDisplayer(){
        functionPlotEditorBorderPane.setVisible(true);

    }

    private void functionPlotEditorHider(){
        functionPlotEditorBorderPane.setVisible(false);
    }

    private void barChartEditorDisplayer() {
        barChartEditorBorderPane.setVisible(true);
    }

    private void barChartEditorHider() {
        barChartEditorBorderPane.setVisible(false);
    }
*/


    @FXML
    private void deletePreset() {
        Preset tobedeletedPreset = currentPreset;
        deleteConfirmationAlert(tobedeletedPreset);
        hideAllEditors();
        overviewDisplayer();
    }

    private void hideAllEditors() {
        lineChartEditorHider();
        /*scatterPlotEditorHider();
        functionPlotEditorHider();
        barChartEditorHider();*/
    }

    @FXML
    private void savePreset(){
        currentPreset.setName(textField_PresetName.getText());
        //workaround ]:->
        //presetTable.refresh();
        //TODO: gets all the information out of the form and sets the appropriate values in the options
        //flagGetter();
        hideAllEditors();

        //System.out.println(currentPreset.getPresetHbox().getChildren().get(0).);
        presetService.save(currentPreset);
        overviewDisplayer();
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
        GuiSvgPlott.getInstance().closeWizard();
        int amountOfMenuItems = GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().size();
        GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().get(amountOfMenuItems - 1).setDisable(false);
    }


}
