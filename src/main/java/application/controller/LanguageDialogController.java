package application.controller;

import application.GuiSvgPlott;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageDialogController implements Initializable {
    private ResourceBundle bundle;

    @FXML
    private Button button_Save;
    @FXML
    private Button button_Cancel;
    @FXML
    private ListView<Locale> listView_languages;


    private String chosenLanguage;
    private ObservableList<Locale> availableLanguages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        availableLanguages = FXCollections.observableArrayList();
        availableLanguages.addAll(Locale.getAvailableLocales());

    }


    public void init(Stage stage) {

        button_Save.setOnAction(event -> {
            if (chosenLanguage != null)
                GuiSvgPlott.getInstance().setLanguage(chosenLanguage);
            stage.close();
        });

        button_Cancel.setOnAction(event -> stage.close());


        listView_languages.setCellFactory(cell -> new LanguageCell());
        listView_languages.setItems(availableLanguages);

    }

    private class LanguageCell extends ListCell<Locale> {

        @Override
        protected void updateItem(Locale item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setText("");
                return;
            }
            setText(item.getDisplayLanguage());

            setOnMouseClicked(event -> {
                chosenLanguage = item.getLanguage();
            });
        }
    }

}
