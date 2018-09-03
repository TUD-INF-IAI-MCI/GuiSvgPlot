package application.controller;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.model.Options.CssType;
import application.model.Options.GuiAxisStyle;
import application.model.Options.PageSize;
import application.model.Options.TrendlineAlgorithm;
import application.model.Preset;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;
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

    private ResourceBundle bundle;

    private Preset currentPreset;
    private GuiSvgOptions currentOptions = new GuiSvgOptions(new SvgPlotOptions());
    private final ToggleGroup scaleGroup = new ToggleGroup();
    private JsonObject settingsJSON = new JsonObject();
    private ArrayList flags = new ArrayList();
    private Preset defaultDiagram;
    private Preset defaultFunction;
    Image editIcon = new Image(getClass().getResource("/images/editSmall.png").toExternalForm());
    Image copyIcon = new Image(getClass().getResource("/images/copySmall.png").toExternalForm());
    Image deleteIcon = new Image(getClass().getResource("/images/deleteSmall.png").toExternalForm());
    DiagramType.DiagramTypeConverter converter = new DiagramType.DiagramTypeConverter();



    @FXML
    private TableView<Preset> presetTable;
    @FXML
    private TableColumn table_column_name;
    @FXML
    private TableColumn table_column_date;
    @FXML
    private TableColumn table_column_diagram_type;
    @FXML
    private ObservableList<String> chartTypeObservableList = FXCollections.observableArrayList();
    @FXML
    private ComboBox combo_Type = new ComboBox();
    @FXML
    private ChoiceBox choiceBox_diagramType = new ChoiceBox();
    @FXML
    private Button button_CsvPath = new Button();
    @FXML
    private ChoiceBox choiceBox_csvOrientation = new ChoiceBox();
    @FXML
    private ChoiceBox choiceBox_csvType = new ChoiceBox();
    @FXML
    private ChoiceBox choiceBox_trendline = new ChoiceBox();
    @FXML
    private ChoiceBox choiceBox_gridStyle = new ChoiceBox();
    @FXML
    private ChoiceBox choiceBox_dblaxes = new ChoiceBox();
    @FXML
    private ChoiceBox choiceBox_cssType = new ChoiceBox();
    @FXML
    private TextField textField_Title = new TextField();
    @FXML
    private TextField textField_Csvpath = new TextField();
    @FXML
    private TextField textField_xAxisTitle = new TextField();
    @FXML
    private TextField textField_yAxisTitle = new TextField();
    @FXML
    private TextField textField_displayAreaXfrom = new TextField();
    @FXML
    private TextField textField_displayAreaXto = new TextField();
    @FXML
    private TextField textField_displayAreaYfrom = new TextField();
    @FXML
    private TextField textField_displayAreaYto = new TextField();
    @FXML
    private TextField textField_helpLinesX = new TextField();
    @FXML
    private TextField textField_helpLinesY = new TextField();
    @FXML
    private TextField textFieldPresetName;
    @FXML
    private RadioButton radioBtn_Scale_to_Data;
    @FXML
    private RadioButton radioBtn_customScale;
    @FXML
    private GridPane settingsDiagramGridPane = new GridPane();
    @FXML
    private GridPane settingsFunctionGridPane = new GridPane();
    @FXML
    private BorderPane overViewBorderPane;
    @FXML
    private BorderPane editorDiagramBorderPane;
    @FXML
    private BorderPane editorFunctionBorderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        chartTypeObservableList.add(resources.getString("combo_diagram"));
        chartTypeObservableList.add(resources.getString("combo_function"));
        combo_Type.setItems(chartTypeObservableList);
        settingsDiagramGridPane.setVisible(false);
        editorFunctionBorderPane.setVisible(false);
        overViewBorderPane.setVisible(true);
        combo_Type.setOnAction(event -> {
            if (combo_Type.getSelectionModel().getSelectedItem().equals(bundle.getString("combo_diagram"))){
                if(!settingsDiagramGridPane.isVisible()){
                    settingsDiagramGridPane.setVisible(true);
                    settingsFunctionGridPane.setVisible(false);
                    initDiagram();
                }
            }else if(combo_Type.getSelectionModel().getSelectedItem().equals(bundle.getString("combo_function"))) {
                if (!settingsFunctionGridPane.isVisible()) {
                    settingsFunctionGridPane.setVisible(true);
                    settingsDiagramGridPane.setVisible(false);
                    initFunction();
                }
            }
        });
        if(super.presets == null){
            presets = FXCollections.observableArrayList();
        }
        initPresetTable();

        int amountOfMenuItems = GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().size();
        GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().get(amountOfMenuItems-1).setDisable(true);
    /*presetNames.setCellFactory(new Callback<ListView<Preset>, ListCell<Preset>>() {
            @Override
            public ListCell<Preset> call(ListView<Preset> param) {

                ListCell cell = new ListCell<Preset>(){

                    @Override
                    protected void updateItem(Preset item, boolean empty) {
                        super.updateItem(item, empty);


                        if(item == null || empty){
                            setText("");
                            return;
                        }

                        setText(item.getPresetName());
                    }
                };

                return cell;
            }
        });
        presetNames.setItems(presets);*/
        }



    private void initPresetTable() {
        initDefaultPresets();
        if(super.presets.size() < 2 ){
            super.presets.addAll(defaultDiagram, defaultFunction);
        }
        table_column_name.setCellValueFactory(new PropertyValueFactory<Preset, String>("presetName"));
        table_column_date.setCellValueFactory(new PropertyValueFactory<Preset, String>("creationDate"));
        table_column_diagram_type.setCellValueFactory(new PropertyValueFactory<Preset,String>("diagramType"));
        addEditButtonToTable();
        addCopyButtonToTable();
        addDeleteButtonToTable();
        presetTable.setItems(super.presets);
    }
    private void initDefaultPresets(){
        defaultDiagram = new Preset(new GuiSvgOptions(new SvgPlotOptions()), bundle.getString("default_diagram_preset_name"), DiagramType.LineChart );
        defaultFunction  = new Preset(new GuiSvgOptions(new SvgPlotOptions()), bundle.getString("default_function_preset_name"), DiagramType.FunctionPlot);
    }

    private void addEditButtonToTable() {
        TableColumn<Preset, Void> colBtn = new TableColumn();
        Callback<TableColumn<Preset, Void>, TableCell<Preset, Void>> cellFactory = new Callback<TableColumn<Preset, Void>, TableCell<Preset, Void>>() {
            @Override
            public TableCell<Preset, Void> call(final TableColumn<Preset, Void> param) {
                final TableCell<Preset, Void> cell = new TableCell<Preset, Void>() {

                    private final Button btn = new Button(bundle.getString("table_column_edit"));
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Object data = getTableView().getItems().get(getIndex());
                            loadPreset(((Preset) data).getPresetName());
                            //hide current stuff
                            overViewHider();
                            textFieldPresetName.setText(((Preset) data).getPresetName());
                            //show settings stuff
                            if(converter.convert(((Preset) data).getDiagramType()) == DiagramType.FunctionPlot){
                                combo_Type.setValue(bundle.getString("combo_function"));
                                //flagGetter();
                                functionEditorDisplayer();
                            }else{
                                combo_Type.setValue(bundle.getString("combo_diagram"));
                                //flagGetter();
                                diagramEditorDisplayer();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);
        presetTable.getColumns().add(colBtn);
    }

    private void addCopyButtonToTable() {
        TableColumn<Preset, Void> colBtn = new TableColumn();
        Callback<TableColumn<Preset, Void>, TableCell<Preset, Void>> cellFactory = new Callback<TableColumn<Preset, Void>, TableCell<Preset, Void>>() {
            @Override
            public TableCell<Preset, Void> call(final TableColumn<Preset, Void> param) {
                final TableCell<Preset, Void> cell = new TableCell<Preset, Void>() {

                    //private final Button btn = new Button("", new ImageView(copyIcon));
                    private final Button btn = new Button(bundle.getString("table_column_copy"));
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Object data = getTableView().getItems().get(getIndex());
                            Preset tempPreset = new Preset(((Preset) data).getOptions(), ((Preset) data).getPresetName()+ " (Kopie)", converter.convert(((Preset) data).getDiagramType()));
                            presets.add(tempPreset);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);
        presetTable.getColumns().add(colBtn);
    }

    private void addDeleteButtonToTable() {
        TableColumn<Preset, Void> colBtn = new TableColumn();
        Callback<TableColumn<Preset, Void>, TableCell<Preset, Void>> cellFactory = new Callback<TableColumn<Preset, Void>, TableCell<Preset, Void>>() {
            @Override
            public TableCell<Preset, Void> call(final TableColumn<Preset, Void> param) {
                final TableCell<Preset, Void> cell = new TableCell<Preset, Void>() {
                    //private final Button btn = new Button("", new ImageView(deleteIcon));
                    private final Button btn = new Button(bundle.getString("table_column_delete"));
                    {
                            btn.setOnAction((ActionEvent event) -> {
                                Object data = getTableView().getItems().get(getIndex());
                                if(((Preset) data).getPresetName().equalsIgnoreCase(bundle.getString("default_diagram_preset_name")) || ((Preset) data).getPresetName().equalsIgnoreCase(bundle.getString("default_function_preset_name"))){
                                    defaultDeleteAlert();
                                }else{
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle(bundle.getString("alert_preset_delete_title"));
                                    alert.setHeaderText(bundle.getString("alert_preset_delete_header"));
                                    alert.setContentText(bundle.getString("alert_preset_delete_content1") + ((Preset) data).getPresetName() + bundle.getString("alert_preset_delete_content2"));
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get() == ButtonType.OK) {
                                        presets.remove(getTableView().getItems().get(getIndex()).getPresetName());
                                        presets.remove(data);
                                    } else {
                                        // ... user chose CANCEL or closed the dialog, hence do nothing
                                    }
                                }
                            });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);
        presetTable.getColumns().add(colBtn);
    }


    private void initDiagram(){
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        choiceBox_diagramType.setItems(diagramTypeObservableList);
        DiagramType dt = converter.convert(currentPreset.getDiagramType());
        //System.out.println(diagramTypeObservableList);
        //System.out.println(dt);
        switch(dt){
            case ScatterPlot:
                choiceBox_diagramType.getSelectionModel().select(1);
            case LineChart:
                choiceBox_diagramType.getSelectionModel().select(3);
            case BarChart:
                choiceBox_diagramType.getSelectionModel().select(2);
        }
        flags.add(choiceBox_diagramType);
        ObservableList<OutputDevice> outputDevices = FXCollections.observableArrayList(OutputDevice.values());
        choiceBox_outputDevice.setItems(outputDevices);
        choiceBox_outputDevice.getSelectionModel().select(0);
        flags.add(choiceBox_outputDevice);
        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        choiceBox_size.setItems(sortedPageSizes);
        choiceBox_size.getSelectionModel().select(PageSize.A4);
        flags.add(choiceBox_size);
        button_CsvPath.setDisable(false);
        button_CsvPath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                textField_Csvpath.setText(file.getAbsolutePath());
            }});
        flags.add(button_CsvPath);
        ObservableList<CsvOrientation> csvOrientationObservableList = FXCollections.observableArrayList(CsvOrientation.values());
        choiceBox_csvOrientation.setItems(csvOrientationObservableList);
        choiceBox_csvOrientation.getSelectionModel().select(0);
        flags.add(choiceBox_csvOrientation);
        ObservableList<CsvType> csvTypeObservableList = FXCollections.observableArrayList(CsvType.values());
        choiceBox_csvType.setItems(csvTypeObservableList);
        choiceBox_csvType.getSelectionModel().select(0);
        flags.add(choiceBox_csvType);
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections.observableArrayList(TrendlineAlgorithm.values());
        choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
        flags.add(choiceBox_trendline);
        ObservableList<GridStyle> gridStyleObservableList = FXCollections.observableArrayList(GridStyle.values());
        choiceBox_gridStyle.setItems(gridStyleObservableList);
        choiceBox_gridStyle.getSelectionModel().select(GridStyle.NONE);
        flags.add(choiceBox_gridStyle);
        ObservableList<GuiAxisStyle> axisStyleObservableList = FXCollections.observableArrayList(GuiAxisStyle.values());
        choiceBox_dblaxes.setItems(axisStyleObservableList);
        choiceBox_dblaxes.getSelectionModel().select(GuiAxisStyle.Barchart);
        flags.add(choiceBox_dblaxes);
        ObservableList<CssType> cssTypeObservableList = FXCollections.observableArrayList(CssType.values());
        choiceBox_cssType.setItems(cssTypeObservableList);
        choiceBox_cssType.getSelectionModel().select(CssType.NONE);
        flags.add(choiceBox_cssType);


        // Radiobutton groupings
        /*radioBtn_Ascending.setToggleGroup(sortGroup);
        radioBtn_Ascending.setSelected(true);
        radioBtn_Descending.setToggleGroup(sortGroup);*/
        radioBtn_Scale_to_Data.setToggleGroup(scaleGroup);
        radioBtn_Scale_to_Data.setSelected(true);
        radioBtn_customScale.setToggleGroup(scaleGroup);

    }

    private void initFunction(){


    }


    @FXML
    private void createNewPreset(){
            diagramTypePrompt();
    }

    private void diagramTypePrompt(){
        // arbitrary default value
        DiagramType dt;
        List<String> choices = new ArrayList<>();
        //needs i18n implementation that doesnt suck
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
        if(result.isPresent()){
            dt = DiagramType.fromString(result.get());
            presetNamePrompt(dt);
        }
    }

    private void presetNamePrompt(DiagramType dt) {
        TextInputDialog nameDialogue = new TextInputDialog();
        nameDialogue.setTitle(bundle.getString("prompt_preset_name_title"));
        nameDialogue.setHeaderText(bundle.getString("prompt_preset_name_header"));
        nameDialogue.setContentText(bundle.getString("prompt_preset_name_content"));
        Optional<String> result = nameDialogue.showAndWait();
        // TODO: currently only one diagramtype can be saved under a certain name, should maybe be made possible to save two presets with the same name but different diagramtype
        if(result.get().equals("")){
            emptyNameAlert();
        }else if (result.isPresent() && !presets.contains(result.get())){
            currentPreset = new Preset(currentOptions, result.get(), dt);
            super.presets.add(currentPreset);
            MenuItem newEntry = new MenuItem(currentPreset.getPresetName());
            // 5 most recent entries
            if(GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().size() < 11){
                GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().add(3, newEntry);
            }
        }else{
            duplicateAlert(result);
        }
    }

    private void flagGetter(){
        settingsJSON.addProperty("diagram_type", currentPreset.getDiagramType());
        currentOptions.setDiagramType((DiagramType)choiceBox_diagramType.getSelectionModel().getSelectedItem());
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
        currentOptions.setCsvType((CsvType)choiceBox_csvType.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("trendline", choiceBox_trendline.getSelectionModel().getSelectedItem().toString());
        //wtf why u want list of trendlines
        //currentOptions.setTrendLine(choiceBox_trendline.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("x_axis_title", textField_xAxisTitle.getText());
        // not sure if implemented correctly
        currentOptions.setxUnit(textField_xAxisTitle.getText());
        settingsJSON.addProperty("y_axis_title", textField_yAxisTitle.getText());
        currentOptions.setyUnit(textField_yAxisTitle.getText());
        //TODO: i18n improvement goes here
        RadioButton selectedRadioButton = (RadioButton) scaleGroup.getSelectedToggle();
        //TODO: scale data implementation in GuiSVGOptions
        settingsJSON.addProperty("scale_data", selectedRadioButton.getText());
        if(selectedRadioButton.getId().equals("radioBtn_Scale_to_Data")){
            currentOptions.setAutoScale(true);
        }else{
            currentOptions.setAutoScale(false);
        }
        //not sure if necessary to implement
        settingsJSON.addProperty("display_area_x_from", textField_displayAreaXfrom.getText());
        settingsJSON.addProperty("display_area_x_to", textField_displayAreaXto.getText());
        settingsJSON.addProperty("display_area_y_from", textField_displayAreaYfrom.getText());
        settingsJSON.addProperty("display_area_y_to", textField_displayAreaYto.getText());

        settingsJSON.addProperty("gridstyle", choiceBox_gridStyle.getSelectionModel().getSelectedItem().toString());
        currentOptions.setGridStyle( (GridStyle) choiceBox_gridStyle.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("help_lines_x", textField_helpLinesX.getText());
        currentOptions.setxLines(textField_helpLinesX.getText());
        settingsJSON.addProperty("help_lines_y", textField_helpLinesY.getText());
        currentOptions.setyLines(textField_helpLinesY.getText());
        settingsJSON.addProperty("axis_style", choiceBox_dblaxes.getSelectionModel().getSelectedItem().toString());
        currentOptions.setAxisStyle( (GuiAxisStyle) choiceBox_dblaxes.getSelectionModel().getSelectedItem());
        settingsJSON.addProperty("css_type", choiceBox_cssType.getSelectionModel().getSelectedItem().toString());
        currentOptions.setCss(choiceBox_cssType.getSelectionModel().getSelectedItem().toString());
    }

    private void flagSetter(DiagramType dt, Preset p){
        // diagram
        // can be made dynamic but in the interest of time
        if(dt.toString() == "FunctionPlot" || dt.toString() == "ScatterPlot" || dt.toString() == "LineChart" || dt.toString() == "Barchart"){
            GuiSvgOptions options = p.getOptions();
            choiceBox_diagramType.getSelectionModel().select(options.getDiagramType());
            textField_Title.setText(options.getTitle());
            choiceBox_outputDevice.getSelectionModel().select(options.getOutputDevice());
            // why size in points?
            //choiceBox_size.getSelectionModel().select(options.getSize());
            textField_Csvpath.setText(options.getCsvPath());
            choiceBox_csvOrientation.getSelectionModel().select(options.getCsvOrientation());
            choiceBox_csvType.getSelectionModel().select(options.getCsvType());
            choiceBox_trendline.getSelectionModel().select(options.getTrendLine());
            choiceBox_gridStyle.getSelectionModel().select(options.getGridStyle());
            //textField_xAxisTitle.setText(options.getXAxisTitle());
            //textField_yAxistitle.setText(options.getYAxisTitle());
            if(options.isAutoScale()){
                scaleGroup.selectToggle(radioBtn_Scale_to_Data);
            }else{
                scaleGroup.selectToggle(radioBtn_customScale);
            }
            //textField_displayAreaXfrom.setText(options.getxRange());
            choiceBox_dblaxes.getSelectionModel().select(options.getAxisStyle()); //TODO: unsure if axis style == double axis etc

            choiceBox_cssType.getSelectionModel().select(options.getCss());

        // function
        }else{
            //TODO
        }
    }


    private void loadPreset(String presetName){
        Preset queriedPreset;
        for (Preset p : presets) {
            if (p.getPresetName() == presetName){
                // If shit goes down, look here for problems
                queriedPreset = p;
                currentPreset = queriedPreset;
            }
        }
    }



    @FXML
    private void quitToMainMenu(){
        GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
        GuiSvgPlott.getInstance().closeWizard();
        int amountOfMenuItems = GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().size();
        GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().get(amountOfMenuItems-1).setDisable(false);
    }


    private void jsonLoader(){

    }

    // TODO: needs some kind of error handling
    public Preset presetLoader(String presetName){
        Preset queriedPreset;
        for (Preset p : presets) {
            if (p.getPresetName() == presetName){
                queriedPreset = p;
                return queriedPreset;
            }
        }
        return null;
    }

    private void overViewHider(){
        overViewBorderPane.setVisible(false);
    }

    private void diagramEditorHider(){
        editorDiagramBorderPane.setVisible(false);

    }

    private void functionEditorHider(){
        editorFunctionBorderPane.setVisible(false);
    }

    private void overviewDisplayer(){
        overViewBorderPane.setVisible(true);
    }

    private void diagramEditorDisplayer(){
        editorDiagramBorderPane.setVisible(true);
    }

    private void functionEditorDisplayer(){
        editorFunctionBorderPane.setVisible(true);
    }


    @FXML
    private void deletePreset(){
        Preset tobedeletedPreset = currentPreset;
        if(!isDefault(tobedeletedPreset)){
            deleteConfirmationAlert(tobedeletedPreset);
            functionEditorHider();
            diagramEditorHider();
            overviewDisplayer();
        }else{
            defaultDeleteAlert();
        }

    }

    @FXML
    private void savePreset(){
        currentPreset.setPresetName(textFieldPresetName.getText());
        //workaround ]:->
        presetTable.refresh();
        flagGetter();
        functionEditorHider();
        diagramEditorHider();
        overviewDisplayer();
    }

    public void deleteConfirmationAlert(Preset p){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("alert_preset_delete_title"));

        alert.setHeaderText(bundle.getString("alert_preset_delete_header"));
        alert.setContentText(bundle.getString("alert_preset_delete_content1") + p.getPresetName() + bundle.getString("alert_preset_delete_content2"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            presets.remove(p);
        } else {
            // ... user chose CANCEL or closed the dialog, hence do nothing
        }
    }

    public void duplicateAlert(Optional o){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("alert_preset_duplicate_title"));
        alert.setHeaderText(bundle.getString("alert_preset_duplicate_header1") + o.get() + bundle.getString("alert_preset_duplicate_header2"));
        alert.setContentText(bundle.getString("alert_preset_duplicate_content"));
        alert.showAndWait();
    }

    public void emptyNameAlert(){
        Alert alarm = new Alert(Alert.AlertType.ERROR);
        alarm.setTitle(bundle.getString("alert_preset_empty_title"));
        alarm.setHeaderText(bundle.getString("alert_preset_empty_header"));
        alarm.setContentText(bundle.getString("alert_preset_empty_content"));
        alarm.showAndWait();
    }

    public void defaultDeleteAlert(){
        Alert alarm = new Alert(Alert.AlertType.ERROR);
        alarm.setTitle(bundle.getString("alert_preset_defaultdelete_title"));
        alarm.setHeaderText(bundle.getString("alert_preset_defaultdelete_header"));
        alarm.setContentText(bundle.getString("alert_preset_defaultdelete_content"));
        alarm.showAndWait();
    }
    public boolean isDefault(Preset p){
        if(p.getPresetName().contains(bundle.getString("default_diagram_preset_name")) || p.getPresetName().contains((bundle.getString("default_function_preset_name")))){
            return true;
        }
        return false;
    }

}
