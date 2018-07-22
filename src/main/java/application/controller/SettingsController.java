package application.controller;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import application.model.Preset;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private ResourceBundle bundle;
    private ObservableList<Preset> presets;
    private Preset currentPreset;
    private GuiSvgOptions currentOptions;


    @FXML
    private Button button_newPreset;
    @FXML
    private Button button_Cancel;
    @FXML
    private Button button_deletePreset;
    @FXML
    private GridPane settingsGridPane = new GridPane();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        /*ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        for (DiagramType diagram : diagramTypeObservableList) {
            System.out.println(diagram);
        }*/

        presets = FXCollections.observableArrayList();
        presets.addListener(new ListChangeListener<Preset>(){
            @Override
            public void onChanged(Change<? extends Preset> c) {
                while(c.next()){

                }
            }
        });
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
            }

    }
    @FXML
    private void quitToMainMenu(){
        GuiSvgPlott.getInstance().closeWizard();
    }
    @FXML
    private void deletePreset(){

        
    }



}
