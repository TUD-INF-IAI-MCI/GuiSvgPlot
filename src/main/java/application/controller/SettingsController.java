package application.controller;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.model.Options.CssType;
import application.model.Options.GuiAxisStyle;
import application.model.Options.PageSize;
import application.model.Options.TrendlineAlgorithm;
import application.model.Preset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.spreadsheet.Grid;
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
    private RadioButton radioBtn_Scale_to_Data;
    @FXML
    private RadioButton radioBtn_customScale;
    /*@FXML
    private RadioButton radioBtn_Ascending;
    @FXML
    private RadioButton radioBtn_Descending;*/
    //TODO: make settingsgridpane dynamically filled with help of colleagues
    @FXML
    private GridPane settingsGridPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        chartTypeObservableList.add(resources.getString("combo_diagram"));
        chartTypeObservableList.add(resources.getString("combo_function"));
        combo_Type.setItems(chartTypeObservableList);
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        choiceBox_diagramType.setItems(diagramTypeObservableList);
        choiceBox_diagramType.getSelectionModel().select(0);
        ObservableList<OutputDevice> outputDevices = FXCollections.observableArrayList(OutputDevice.values());
        choiceBox_outputDevice.setItems(outputDevices);
        choiceBox_outputDevice.getSelectionModel().select(0);
        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        choiceBox_size.setItems(sortedPageSizes);
        choiceBox_size.getSelectionModel().select(PageSize.A4);
        button_CsvPath.setDisable(false);
        button_CsvPath.setOnAction(event -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(this.userDir);
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
                    fileChooser.getExtensionFilters().add(extFilter);
                    File file = fileChooser.showOpenDialog(GuiSvgPlott.getInstance().getPrimaryStage());
                    if (file != null) {
                        this.textField_csvPath.setText(file.getAbsolutePath());
                    }});
        ObservableList<CsvOrientation> csvOrientationObservableList = FXCollections.observableArrayList(CsvOrientation.values());
        choiceBox_csvOrientation.setItems(csvOrientationObservableList);
        ObservableList<CsvType> csvTypeObservableList = FXCollections.observableArrayList(CsvType.values());
        choiceBox_csvType.setItems(csvTypeObservableList);
        ObservableList<TrendlineAlgorithm> trendlineAlgorithmObservableList = FXCollections.observableArrayList(TrendlineAlgorithm.values());
        choiceBox_trendline.setItems(trendlineAlgorithmObservableList);
        choiceBox_trendline.getSelectionModel().select(TrendlineAlgorithm.None);
        ObservableList<GridStyle> gridStyleObservableList = FXCollections.observableArrayList(GridStyle.values());
        choiceBox_gridStyle.setItems(gridStyleObservableList);
        choiceBox_gridStyle.getSelectionModel().select(GridStyle.NONE);
        ObservableList<GuiAxisStyle> axisStyleObservableList = FXCollections.observableArrayList(GuiAxisStyle.values());
        choiceBox_dblaxes.setItems(axisStyleObservableList);
        choiceBox_dblaxes.getSelectionModel().select(GuiAxisStyle.Default);
        ObservableList<CssType> cssTypeObservableList = FXCollections.observableArrayList(CssType.values());
        choiceBox_cssType.setItems(cssTypeObservableList);
        choiceBox_cssType.getSelectionModel().select(CssType.NONE);


        // Radiobutton groupings
        /*radioBtn_Ascending.setToggleGroup(sortGroup);
        radioBtn_Ascending.setSelected(true);
        radioBtn_Descending.setToggleGroup(sortGroup);*/
        radioBtn_Scale_to_Data.setToggleGroup(scaleGroup);
        radioBtn_Scale_to_Data.setSelected(true);
        radioBtn_customScale.setToggleGroup(scaleGroup);

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



    @FXML
    private void createNewPreset(){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Voreinstellungsname");
            dialog.setResizable(true);
            dialog.setHeaderText("Hier bitte den Namen ihrer Voreinstellung eingeben");
            dialog.setContentText("Name ihrer Voreinstellung:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !savedPresetNames.contains(result.get())){
                currentPreset = new Preset(currentOptions, result.get());
                presets.add(currentPreset);
                savedPresetNames.add(currentPreset.getPresetName());
                presetNames.setItems(presets);
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Voreinstellungsduplikat entdeckt");
                alert.setHeaderText("Eine Voreinstellung mit dem Namen " + result.get() + " existiert bereits.");
                alert.setContentText("Bitte wählen sie einen anderen Namen aus.");
                alert.showAndWait();
            }
    }

    private void flagChecker(){

    }

    private void listViewController(){
        //TODO: implement functionality upon clicketyclick
    }
    private void gridPaneController(){
        //TODO: implement functionality on filling the gridpane
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
            alert.setContentText("Aber schön, dass Sie mal getestet haben, ob wir den Fehler abfangen! Tschüss, bis zum nächsten Edgecase!");
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



}
