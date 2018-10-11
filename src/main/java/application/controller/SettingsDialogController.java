package application.controller;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import application.model.Settings;
import application.util.Converter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Robert Schlegel
 */
public class SettingsDialogController implements Initializable {

    private ResourceBundle bundle;

    @FXML
    private Button button_Save;
    @FXML
    private Button button_Cancel;
    @FXML
    private ChoiceBox<Locale> choiceBox_languages;
    @FXML
    private TextField textField_gnuplotPath;
    @FXML
    private Button button_gnuplotPath;

    private Locale chosenLocale;
    private String gnuplotPath;
    private ObservableList<Locale> availableLanguages;
    private Settings settings = Settings.getInstance();
    private Converter converter = Converter.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.chosenLocale = settings.getCurrentLocale();
        this.gnuplotPath = settings.getGnuPlotPath();
        converter.setBundle(bundle);
        availableLanguages = FXCollections.observableArrayList();
        availableLanguages.addAll(Settings.getInstance().supportedLocals);
    }

    public void init(Stage stage) {
        button_Save.setOnAction(event -> {
            if (chosenLocale != null)
                settings.setCurrentLocale(chosenLocale);
            if (gnuplotPath != null && !gnuplotPath.isEmpty())
                settings.setGnuPlotPath(gnuplotPath);
            stage.close();
        });
        button_Cancel.setOnAction(event -> stage.close());

        this.initLanguageSettings();
        this.initGnuplotSettings(stage);
    }

    private void initLanguageSettings() {
        choiceBox_languages.setItems(availableLanguages);
        choiceBox_languages.setConverter(Converter.getInstance().getLocaleStringConverter());
        choiceBox_languages.getSelectionModel().select(chosenLocale);
        choiceBox_languages.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                chosenLocale = newValue);
    }

    private void initGnuplotSettings(Stage stage) {
        this.textField_gnuplotPath.setText(settings.getGnuPlotPath());
        this.textField_gnuplotPath.textProperty().addListener((observable, oldValue, newValue) -> this.gnuplotPath = newValue);
        this.button_gnuplotPath.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                this.textField_gnuplotPath.setText(file.getAbsolutePath());
            }
        });

    }
}
