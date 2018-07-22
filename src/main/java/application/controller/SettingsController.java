package application.controller;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import application.model.Preset;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private ResourceBundle bundle;
    private ObservableList<Preset> presets;
    private Preset currentPreset;
    private GuiSvgOptions currentOptions;
    private final ToggleGroup group = new ToggleGroup();
    @FXML
    private ListView<Preset> presetNames = new ListView<>();





    @FXML
    private GridPane settingsGridPane = new GridPane();
    @FXML
    private RadioButton radio_Ascending;
    @FXML
    private RadioButton radio_Descending;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        /*ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        for (DiagramType diagram : diagramTypeObservableList) {
            System.out.println(diagram);
        }*/
        radio_Ascending.setToggleGroup(group);
        radio_Ascending.setSelected(true);
        radio_Descending.setToggleGroup(group);
        presets = FXCollections.observableArrayList();

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
            if (result.isPresent()){
                currentPreset = new Preset(currentOptions, result.get());
                presets.add(currentPreset);
                presetNames.setItems(presets);
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
            alert.setContentText("Allerdings schön dass Sie mal getestet haben, ob wir den Fehler abfangen! Tschüss, bis zum nächsten Edgecase!");
            alert.showAndWait();
        }else{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung erforderlich");
        alert.setHeaderText("Sie löschen hiermit die gewählte Voreinstellung!");
        alert.setContentText("Sind Sie sicher?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){
            presets.remove(presetNames.getSelectionModel().getSelectedItem());
        } else {
            // ... user chose CANCEL or closed the dialog, hence do nothing
        }}
    }



}
