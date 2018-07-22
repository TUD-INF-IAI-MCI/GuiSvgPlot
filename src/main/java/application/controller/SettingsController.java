package application.controller;

import application.GuiSvgPlott;
import application.model.Preset;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private ResourceBundle bundle;
    private ArrayList<Preset> presets;


    @FXML
    public Button button_newPreset;
    public Button button_Cancel;
    public Button button_deletePreset;
    public GridPane settingsGridPane = new GridPane();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        System.out.println("test");


        /*ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        for (DiagramType diagram : diagramTypeObservableList) {
            System.out.println(diagram);
        }*/
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    }


    @FXML
    private void createNewPreset(){
        button_newPreset.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog("Bitte hier den Namen ihrer Voreinstellung eingeben");
            dialog.setTitle("Text Input Dialog");
            dialog.setHeaderText("Look, a Text Input Dialog");
            dialog.setContentText("Please enter your name:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                System.out.println("Your name: " + result.get());
            }

// The Java 8 way to get the response value (with lambda expression).
            result.ifPresent(name -> System.out.println("Your name: " + name));
        });
    }
    @FXML
    private void quitToMainMenu(){
        button_Cancel.setOnAction(event -> {
            System.out.println("are you even working?!");
            GuiSvgPlott.getInstance().closeWizard();
        });
    }
    @FXML
    private void deletePreset(){
        System.out.println("why u no delete");
    }



}
