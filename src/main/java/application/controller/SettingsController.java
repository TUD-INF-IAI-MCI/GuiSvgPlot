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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController extends SVGWizardController implements Initializable {

    private ResourceBundle bundle;
    private ObservableList<Preset> presets;
    private Preset currentPreset;
    private GuiSvgOptions currentOptions = new GuiSvgOptions(new SvgPlotOptions());
    private final ToggleGroup sortGroup = new ToggleGroup();
    private final ToggleGroup scaleGroup = new ToggleGroup();
    private ArrayList<String> savedPresetNames;
    private JsonObject settingsJSON = new JsonObject();
    private ArrayList flags = new ArrayList();
    private int numberOfEdgeCasesFound = 0;

    @FXML
    private ListView<Preset> presetNames = new ListView<>();
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
    /*@FXML
    private RadioButton radioBtn_Ascending;
    @FXML
    private RadioButton radioBtn_Descending;*/
    //TODO: make settingsgridpane dynamically filled with help of colleagues
    @FXML
    private GridPane settingsDiagramGridPane = new GridPane();
    @FXML
    private GridPane settingsFunctionGridPane = new GridPane();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        // can potentially cause problems upon i18n implementations, see this.line+7 and this.line+13
        chartTypeObservableList.add(resources.getString("combo_diagram"));
        chartTypeObservableList.add(resources.getString("combo_function"));
        combo_Type.setItems(chartTypeObservableList);
        settingsDiagramGridPane.setVisible(false);
        //System.out.println(chartTypeObservableList);
        combo_Type.setOnAction(event -> {
            if (combo_Type.getSelectionModel().getSelectedItem().equals("Diagramm")){
                if(!settingsDiagramGridPane.isVisible()){
                    settingsDiagramGridPane.setVisible(true);
                    settingsFunctionGridPane.setVisible(false);
                    initDiagram();
                }
            }else if(combo_Type.getSelectionModel().getSelectedItem().equals("Funktion")){
                if(!settingsFunctionGridPane.isVisible()){
                    settingsFunctionGridPane.setVisible(true);
                    settingsDiagramGridPane.setVisible(false);
                    initFunction();
                }

            }
        });

        presets = FXCollections.observableArrayList();
        savedPresetNames = new ArrayList<>();

        presetNames.setCellFactory(new Callback<ListView<Preset>, ListCell<Preset>>() {
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
        presetNames.setItems(presets);
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

    @FXML
    private void createNewPreset(){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Voreinstellungsname");
            dialog.setResizable(true);
            dialog.setHeaderText("Hier bitte den Namen ihrer Voreinstellung eingeben");
            dialog.setContentText("Name ihrer Voreinstellung:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !savedPresetNames.contains(result.get())){
                flagGetter();
                currentPreset = new Preset(currentOptions, result.get());
                presets.add(currentPreset);
                savedPresetNames.add(currentPreset.getPresetName());
                presetNames.setItems(presets);
                MenuItem newEntry = new MenuItem(currentPreset.getPresetName());
                GuiSvgPlott.getInstance().getRootFrameController().getMenu_Presets().getItems().add(3, newEntry);
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Voreinstellungsduplikat entdeckt");
                alert.setHeaderText("Eine Voreinstellung mit dem Namen " + result.get() + " existiert bereits.");
                alert.setContentText("Bitte wählen sie einen anderen Namen aus.");
                alert.showAndWait();
            }
    }

    private void flagGetter(){
        settingsJSON.addProperty("diagram_type", choiceBox_diagramType.getSelectionModel().getSelectedItem().toString());
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

    @FXML
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
    }

    @FXML
    private void quitToMainMenu(){
        GuiSvgPlott.getInstance().closeWizard();
    }
    @FXML
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
    }
    private void jsonLoader(){

    }

    private void listViewController(){
        //TODO: implement functionality upon clicketyclick
    }
    private void gridPaneController(){
        //TODO: implement functionality on filling the gridpane
    }



}
