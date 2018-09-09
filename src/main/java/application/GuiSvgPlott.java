package application;

import application.controller.RootFrameController;
import application.infrastructure.UTF8Control;
import com.google.gson.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class GuiSvgPlott extends Application {
//    private static final Logger logger = LoggerFactory.getLogger(GuiSvgPlott.class);

    private static GuiSvgPlott instance;

    private RootFrameController rootFrameController;
    private Stage primaryStage;
    private JsonObject settings;
    private ResourceBundle bundle;


    ///// PATHS ////

    public static URL RootFrame = GuiSvgPlott.class.getResource("/fxml/RootFrame.fxml");
    public static URL WizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/Wizard.fxml");
    public static URL PresetEditorFrame = GuiSvgPlott.class.getResource("/fxml/wizard/PresetEditorFrame.fxml");
    public static URL PresetOverviewFrame = GuiSvgPlott.class.getResource("/fxml/wizard/PresetOverviewFrame.fxml");
    public static URL IntroFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/Intro.fxml");
    public static URL CsvEditorFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/CsvEditor.fxml");
    public static URL FunctionWizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/functions/FunctionWizardFrame.fxml");
    public static URL ChartWizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/chart/ChartWizardFrame.fxml");


    ////////////////


    public GuiSvgPlott() {
        // favicon for macos
        try {
            // only works in macos
            //com.apple.eawt.Application.getApplication().setDockIconImage(new ImageIcon(getClass().getResource("/images/barchart_circle.png")).getImage());
        }catch(Exception e){}
        instance = this;
    }


    public static synchronized GuiSvgPlott getInstance() {
        if (instance == null)
            return new GuiSvgPlott();
        return instance;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.bundle = ResourceBundle.getBundle("langBundle", new UTF8Control());
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
            if (event.getCode() == KeyCode.F5) {
                rootFrameController.svgWizardController.button_rerenderPreview.fire();
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
