package application.model;

import application.infrastructure.UTF8Control;
import com.google.gson.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Object for program settings like language.
 *
 * @author Emma Müller, Robert Schlegel
 */
public class Settings {

    private static final Settings INSTANCE = new Settings();

    public final List<Locale> supportedLocals = Arrays.asList(Locale.ENGLISH, Locale.GERMAN);
    private String gnuPlotPath;
    private SimpleObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>();
    private ResourceBundle bundle;
    private Path path;
    private JsonObject settingsJSON;

    /**
     * Instantiates a new Settings-Object. This is private because this is an Singleton.
     *
     * @author Emma Müller.
     */
    private Settings() {
        settingsJSON = new JsonObject();
        path = Paths.get(System.getProperty("user.home") + "/svgPlot/settings.json");
        currentLocale.set(Locale.getDefault());
        this.bundle = ResourceBundle.getBundle("langBundle", currentLocale.get(), new UTF8Control());
        this.currentLocale.addListener((observable, oldValue, newValue) -> {
            this.bundle = ResourceBundle.getBundle("langBundle", currentLocale.get(), new UTF8Control());
            try {
                storeLocale();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static Settings getInstance() {
        return INSTANCE;
    }

    public String getGnuPlotPath() {
        return gnuPlotPath;
    }

    public void setGnuPlotPath(final String gnuPlotPath) {
        this.gnuPlotPath = gnuPlotPath;
        try {
            this.storeGnuPlot();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Locale getCurrentLocale() {
        return currentLocale.get();
    }

    public SimpleObjectProperty<Locale> currentLocaleProperty() {
        return currentLocale;
    }

    public void setCurrentLocale(Locale locale) {
        this.currentLocale.set(locale);
    }

    public void loadSettings(Stage stage) throws IOException {
        /*@author Robert Schlegel */
        Files.createDirectories(path.getParent());
        if (!Files.exists(path))
            Files.createFile(path);

        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(path.toAbsolutePath().toString()));
            settingsJSON = jsonElement.getAsJsonObject();
        } catch (Exception e) {
            settingsJSON = new JsonObject();
        }
        if (!settingsJSON.has("gnu-path") || settingsJSON.get("gnu-path") == null || settingsJSON.get("gnu-path").getAsString().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle(bundle.getString("application_missing_gnuplot_path_title"));
            a.setHeaderText(bundle.getString("application_missing_gnuplot_path_message"));
            a.setContentText("");

            a.showAndWait();

            FileChooser fc = new FileChooser();
            File gnuFile = fc.showOpenDialog(stage);
            this.gnuPlotPath = gnuFile != null ? gnuFile.getAbsolutePath() : "";
            this.storeGnuPlot();
        } else {
            this.gnuPlotPath = settingsJSON.get("gnu-path").getAsString();
        }

        /*@author Emma Müller */
        if (settingsJSON.has("lang") && settingsJSON.get("lang") != null && !settingsJSON.get("lang").getAsString().isEmpty()) {
            Locale locale = Locale.forLanguageTag(this.settingsJSON.get("lang").getAsString());
            if (locale != null && !locale.getLanguage().isEmpty()) {
                this.currentLocale.set(locale);
            }
        } else if (this.currentLocale.get() != null && !this.currentLocale.get().toString().isEmpty()) {
            this.storeLocale();
        }


    }

    /**
     * Saves the gnuplot path into the json file.
     * @throws IOException
     * @author Emma Müller, Robert Schlegel
     */
    private void storeGnuPlot() throws IOException {
        settingsJSON.addProperty("gnu-path", this.gnuPlotPath);
        try (Writer writer = new FileWriter(path.toString())) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(settingsJSON, writer);
        }
    }

    /**
     * Saves the languagr into the json file.
     * @throws IOException
     * @author Emma Müller, Robert Schlegel
     */
    private void storeLocale() throws IOException {
        settingsJSON.addProperty("lang", currentLocale.get().toString());
        try (Writer writer = new FileWriter(path.toString())) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(settingsJSON, writer);
        }
    }
}