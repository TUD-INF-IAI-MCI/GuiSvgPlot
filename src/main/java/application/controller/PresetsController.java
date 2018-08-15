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
import javafx.scene.layout.GridPane;
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
    //private static PresetsController instance;

    private Preset currentPreset;
    private GuiSvgOptions currentOptions = new GuiSvgOptions(new SvgPlotOptions());
    private final ToggleGroup scaleGroup = new ToggleGroup();
    private static ArrayList<String> savedPresetNames;
    private JsonObject settingsJSON = new JsonObject();
    private ArrayList flags = new ArrayList();
    private Preset defaultDiagram;
    private Preset defaultFunction;
    Image editIcon = new Image(getClass().getResource("/images/editSmall.png").toExternalForm());
    Image copyIcon = new Image(getClass().getResource("/images/copySmall.png").toExternalForm());
    Image deleteIcon = new Image(getClass().getResource("/images/deleteSmall.png").toExternalForm());



    @FXML
    private TableView<Preset> presetTable;
    @FXML
    private TableColumn table_column_name;
    @FXML
    private TableColumn table_column_date;
    @FXML
    private TableColumn table_column_diagram_type;
    @FXML
    private TableColumn table_column_edit;
    @FXML
    private TableColumn table_column_copy;
    @FXML
    private TableColumn table_column_delete;
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
    private RadioButton radioBtn_Scale_to_Data;

    @FXML
    private RadioButton radioBtn_customScale;
    //TODO: make settingsgridpane dynamically filled with help of colleagues
    @FXML
    private GridPane settingsDiagramGridPane = new GridPane();
    @FXML
    private GridPane settingsFunctionGridPane = new GridPane();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        chartTypeObservableList.add(resources.getString("combo_diagram"));
        chartTypeObservableList.add(resources.getString("combo_function"));
        combo_Type.setItems(chartTypeObservableList);
        settingsDiagramGridPane.setVisible(false);
        combo_Type.setOnAction(event -> {
            if (combo_Type.getSelectionModel().getSelectedItem().equals(bundle.getString("combo_diagram"))){
                if(!settingsDiagramGridPane.isVisible()){
                    settingsDiagramGridPane.setVisible(true);
                    settingsFunctionGridPane.setVisible(false);
                    initDiagram();
                }
            }else if(combo_Type.getSelectionModel().getSelectedItem().equals(bundle.getString("combo_function"))){
                if(!settingsFunctionGridPane.isVisible()){
                    settingsFunctionGridPane.setVisible(true);
                    settingsDiagramGridPane.setVisible(false);
                    initFunction();
                }

            }
        });
        if(super.presets == null){
            presets = FXCollections.observableArrayList();
        }
        initSavedPresetNames();
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
        defaultDiagram  = new Preset(new GuiSvgOptions(new SvgPlotOptions()), bundle.getString("default_diagram_preset_name"), DiagramType.LineChart );
        defaultFunction = new Preset(new GuiSvgOptions(new SvgPlotOptions()), bundle.getString("default_function_preset_name"), DiagramType.FunctionPlot);
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

    private void addEditButtonToTable() {
        TableColumn<Preset, Void> colBtn = new TableColumn();
        Callback<TableColumn<Preset, Void>, TableCell<Preset, Void>> cellFactory = new Callback<TableColumn<Preset, Void>, TableCell<Preset, Void>>() {
            @Override
            public TableCell<Preset, Void> call(final TableColumn<Preset, Void> param) {
                final TableCell<Preset, Void> cell = new TableCell<Preset, Void>() {

                    private final Button btn = new Button(bundle.getString("table_column_edit"));
                    {
                        //TODO: implement
                        btn.setOnAction((ActionEvent event) -> {
                            Object data = getTableView().getItems().get(getIndex());
                            System.out.println(((Preset) data).getPresetName());
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

                    private final Button btn = new Button(bundle.getString("table_column_copy"));
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Object data = getTableView().getItems().get(getIndex());
                            System.out.println(((Preset) data).getDiagramType());
                            System.out.println(((Preset) data).getOptions().getDiagramType());
                            //Preset tempPreset = new Preset(((Preset) data).getOptions(), ((Preset) data).getPresetName()+ " (Kopie)", ((Preset) data).getDiagramType());
                            //presets.add(tempPreset);
                            //savedPresetNames.add(tempPreset.getPresetName());
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
                    private final Button btn = new Button(bundle.getString("table_column_delete"));
                    {
                            btn.setOnAction((ActionEvent event) -> {
                                Object data = getTableView().getItems().get(getIndex());
                                if(((Preset) data).getPresetName().contains("standard") || ((Preset) data).getPresetName().contains("default")){
                                    Alert alarm = new Alert(Alert.AlertType.ERROR);
                                    alarm.setTitle("Fehler");
                                    alarm.setHeaderText("Eine Standardvoreinstellung darf nicht gelöscht werden.");
                                    alarm.setContentText("Bitte wählen sie eine andere Voreinstellung aus.");
                                    alarm.showAndWait();
                                }else{
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Bestätigung erforderlich");
                                    alert.setHeaderText("Sie löschen hiermit die gewählte Voreinstellung!");
                                    alert.setContentText("Sind Sie sicher dass Sie " + ((Preset) data).getPresetName() + " löschen wollen?");
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get() == ButtonType.OK) {
                                        System.out.println(savedPresetNames);
                                        savedPresetNames.remove(getTableView().getItems().get(getIndex()).getPresetName());
                                        System.out.println(savedPresetNames);
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
        choiceBox_diagramType.getSelectionModel().select(0);
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
        choiceBox_dblaxes.getSelectionModel().select(GuiAxisStyle.Default);
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
        //TODO upon function completeness
        // also: dont know how to put function gridpane alongside in FXML
    }
    public static void initSavedPresetNames() {
        if (savedPresetNames == null){
            savedPresetNames = new ArrayList<>();
        }
    }

    @FXML
    private void createNewPreset(){
            diagramTypePrompt();
    }

    private void diagramTypePrompt(){
        // arbitrary default value
        DiagramType dt;
        List<String> choices = new ArrayList<>();
        choices.add("FunctionPlot");
        choices.add("ScatterPlot");
        choices.add("LineChart");
        choices.add("BarChart");
        ChoiceDialog<String> dialogue = new ChoiceDialog<>("LineChart", choices);
        dialogue.setTitle("Diagrammtyp auswählen");
        dialogue.setResizable(true);
        dialogue.setHeaderText("Wählen Sie bitte den Diagrammtyp aus");
        dialogue.setContentText("Diagrammtyp:");
        Optional<String> result = dialogue.showAndWait();
        if(result.isPresent()){
            dt = DiagramType.fromString(result.get());
            presetNamePrompt(dt);
        }
    }

    private void presetNamePrompt(DiagramType dt) {
        TextInputDialog nameDialogue = new TextInputDialog();
        nameDialogue.setTitle("Name für Ihre Voreinstellung erforderlich");
        nameDialogue.setHeaderText("Bitte geben Sie einen Namen für ihre Voreinstellung ein");
        nameDialogue.setContentText("Name der Voreinstellung:");
        Optional<String> result = nameDialogue.showAndWait();
        // TODO: currently only one diagramtype can be saved under a certain name, should maybe be made possible to save two presets with the same name but different diagramtype
        if(result.get().equals("")){
            emptyNameAlert();
        }else if (result.isPresent() && !savedPresetNames.contains(result.get())){
            currentPreset = new Preset(currentOptions, result.get(), dt);
            super.presets.add(currentPreset);
            savedPresetNames.add(currentPreset.getPresetName());
            MenuItem newEntry = new MenuItem(currentPreset.getPresetName());
            // 5 most recent entries because it's not "scalable" ladida...
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

    /*@FXML
    private void loadPreset(){
        // there are no presets
        if(presets.size() == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE+100);
            numberOfEdgeCasesFound++;
            alert.setTitle("Fehler in der Matrix");
            alert.setHeaderText("Es gibt keine ladbaren Voreinstellungen!");
            alert.setContentText("Sie haben ihren " + numberOfEdgeCasesFound + ". Edgecase gefunden. Glückwunsch! Finden Sie auch die restlichen?");
            alert.showAndWait();
        }else if(presetNames.getSelectionModel().getSelectedItem() != null){
            System.out.println(presetNames.getSelectionModel().getSelectedItem().getPresetName());
            // is this the right preset?
            Preset loadedPreset = currentPreset;

            flagSetter(loadedPreset.getOptions().getDiagramType(),loadedPreset);
            // no preset has been chosen out of the list
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE+100);
            alert.setTitle("Unkritischer Fehler");
            alert.setHeaderText("Welche Voreinstellung möchten Sie denn laden?");
            alert.setContentText("Bitte wählen Sie eine Voreinstellung aus der Liste aus und bestätigen Sie mit dem Laden-Knopf.");
            alert.showAndWait();
        }
    }*/
    @FXML
    private void quitToMainMenu(){
        GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
        GuiSvgPlott.getInstance().closeWizard();
        int amountOfMenuItems = GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().size();
        GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().get(amountOfMenuItems-1).setDisable(false);
    }

    /*@FXML
    private void deletePreset(){
        if(presets.size() == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE+100);
            alert.setTitle("Fehler in der Matrix");
            alert.setHeaderText("Es gibt keine löschbaren Voreinstellungen!");
            numberOfEdgeCasesFound++;
            alert.setContentText("Aber schön, dass Sie mal getestet haben, ob wir diesen Fall abfangen! Tschüss, bis zum nächsten Edgecase! \nSie haben bisher " + numberOfEdgeCasesFound + " Edgecases gefunden.");
            alert.showAndWait();
        }else if(presetNames.getSelectionModel().getSelectedItem() != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Bestätigung erforderlich");
            alert.setHeaderText("Sie löschen hiermit die gewählte Voreinstellung!");
            alert.setContentText("Sind Sie sicher dass Sie " + presetNames.getSelectionModel().getSelectedItem().getPresetName() + " löschen wollen?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK){
                savedPresetNames.remove(presetNames.getSelectionModel().getSelectedItem().getPresetName());
                presets.remove(presetNames.getSelectionModel().getSelectedItem());
            } else {
            // ... user chose CANCEL or closed the dialog, hence do nothing
            }
        }
    }*/

    private void jsonLoader(){

    }
    private void listViewController(){
        //TODO: implement functionality upon clicketyclick
    }

    private void gridPaneController(){
        //TODO: implement functionality on filling the gridpane
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


    public static ArrayList<String> getSavedPresetNames() {
        return savedPresetNames;
    }

    public static void duplicateAlert(Optional o){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Voreinstellungsduplikat entdeckt");
        alert.setHeaderText("Eine Voreinstellung mit dem Namen " + o.get() + " existiert bereits.");
        alert.setContentText("Bitte wählen sie einen anderen Namen aus.");
        alert.showAndWait();
    }

    public static void emptyNameAlert(){
        Alert alarm = new Alert(Alert.AlertType.ERROR);
        alarm.setTitle("Leerer Voreinstellungsname entdeckt");
        alarm.setHeaderText("Der Name der Voreinstellung muss mindestens einen Buchstaben lang sein.");
        alarm.setContentText("Bitte wählen sie einen Namen aus.");
        alarm.showAndWait();
    }
}
