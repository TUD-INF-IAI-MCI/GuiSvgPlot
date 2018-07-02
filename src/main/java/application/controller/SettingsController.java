package application.controller;

import application.service.ButtonService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import tud.tangram.svgplot.options.DiagramType;


import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private ResourceBundle bundle;

    @FXML
    public GridPane settingsGridPane = new GridPane();

    private Text text1 = new Text("Lorem Ipsum");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        ObservableList<DiagramType> diagramTypeObservableList = FXCollections.observableArrayList(DiagramType.values());
        for (DiagramType diagram : diagramTypeObservableList) {
            System.out.println(diagram);
        }


        //settingsGridPane.add(text1,0,0);
    }

    public void testFunction(){
        settingsGridPane.add(text1,0,0);
    }

}
