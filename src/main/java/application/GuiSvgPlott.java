package application;

import application.controller.LanguageDialogController;
import application.controller.RootFrameController;
import com.google.gson.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

public class GuiSvgPlott extends Application {


    private static GuiSvgPlott instance;

    private RootFrameController rootFrameController;
    private Stage primaryStage;
    private JsonObject settings;
    private ResourceBundle bundle;
    private Locale locale;


    ///// PATHS ////

    public static URL RootFrame = GuiSvgPlott.class.getResource("/fxml/RootFrame.fxml");
    public static URL WizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/Wizard.fxml");
    public static URL PresetEditorFrame = GuiSvgPlott.class.getResource("/fxml/wizard/PresetEditorFrame.fxml");
    public static URL PresetOverviewFrame = GuiSvgPlott.class.getResource("/fxml/wizard/PresetOverviewFrame.fxml");
    public static URL IntroFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/Intro.fxml");
    public static URL CsvEditorFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/CsvEditor.fxml");
    public static URL FunctionWizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/functions/FunctionWizardFrame.fxml");
    public static URL ChartWizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/chart/ChartWizardFrame.fxml");
    public static URL LanguageDialog = GuiSvgPlott.class.getResource("/fxml/wizard/LanguageDialog.fxml");

    ////////////////


    public GuiSvgPlott() {
        // favicon for macos
        try {
            // only works in macos
            //com.apple.eawt.Application.getApplication().setDockIconImage(new ImageIcon(getClass().getResource("/images/barchart_circle.png")).getImage());
        } catch (Exception e) {
        }
        instance = this;
    }


    public static synchronized GuiSvgPlott getInstance() {
        if (instance == null)
            return new GuiSvgPlott();
        return instance;
    }

    public void setLanguageDialog() {

        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(GuiSvgPlott.LanguageDialog);

        try {
            AnchorPane anchorPane = loader.load();

            Scene scene = new Scene(anchorPane);
            Stage stage = new Stage();
            stage.setResizable(true);
            stage.setTitle(bundle.getString("application_language"));
            stage.setScene(scene);

            LanguageDialogController controller = loader.getController();
            controller.init(stage);

            String lang = locale.getCountry();
            stage.showAndWait();

            if (!locale.getCountry().equals(lang))
                start(primaryStage);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setLanguage(String lang) {
        for (Locale l : Locale.getAvailableLocales()) {
            if (l.getLanguage().equals(lang)) {
                locale = l;
            }
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        if (locale == null)
            locale = Locale.GERMAN;

        this.bundle = ResourceBundle.getBundle("langBundle", locale);

        setSettings();

        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(GuiSvgPlott.RootFrame);

        AnchorPane anchorPane = loader.load();


        rootFrameController = loader.getController();

        rootFrameController.init();


        Scene scene = new Scene(anchorPane);
        this.primaryStage = primaryStage;
        primaryStage.setResizable(true);
        primaryStage.setTitle(bundle.getString("application_title"));
        primaryStage.setScene(scene);
        Image favicon = new Image(getClass().getResource("/images/barchart_circle.png").toExternalForm());
        primaryStage.getIcons().add(favicon);


        primaryStage.show();

        primaryStage.getScene().setOnKeyPressed(event -> {

            switch (event.getCode()) {
                case F5: {
                    rootFrameController.svgWizardController.button_rerenderPreview.fire();
                    break;
                }
                case F1: {
                    rootFrameController.svgWizardController.button_Warnings.requestFocus();
                    break;
                }
                case Q: {
                    if (event.isControlDown()) {
                        primaryStage.close();
                        break;
                    }
                }
                case P: {
                    if (event.isControlDown()) {
                        rootFrameController.startPresetOverview();
                        break;
                    }
                }
                case S: {
                    if (event.isControlDown()) {
                        //TODO: update this upon completing the functionality
                        //rootFrameController.svgWizardController.initSaveAsPreset();
                        break;
                    }
                }
                case N: {
                    if (event.isControlDown()) {
                        try {
                            start(primaryStage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        });

    }

    private void setSettings() throws IOException {

        settings = new JsonObject();

        Path path = Paths.get(System.getProperty("user.home") + "/svgPlot/settings.json");
        Files.createDirectories(path.getParent());
        if (!Files.exists(path))
            Files.createFile(path);

        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(path.toAbsolutePath().toString()));
            settings = jsonElement.getAsJsonObject();
        } catch (Exception e) {
            settings = new JsonObject();
//            logger.debug("empty settings file");
        }

        if (!settings.has("gnu-path") || settings.get("gnu-path").getAsString().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle(bundle.getString("application_missing_gnuplot_path_title"));
            a.setHeaderText(bundle.getString("application_missing_gnuplot_path_message"));
            a.setContentText("");

            a.showAndWait();

            FileChooser fc = new FileChooser();
            File gnuFile = fc.showOpenDialog(primaryStage);
            settings.addProperty("gnu-path", gnuFile != null ? gnuFile.getAbsolutePath() : "");


            try (Writer writer = new FileWriter(path.toString())) {
                Gson gson = new GsonBuilder().create();
                gson.toJson(settings, writer);
            }


        }
    }


    public void closeWizard() {
        if (rootFrameController != null)
            rootFrameController.closeWizard();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public String getGnuPlot() {
        return settings.has("gnu-path") ? settings.get("gnu-path").getAsString() : "";
    }

    public RootFrameController getRootFrameController() {
        return rootFrameController;
    }
}
