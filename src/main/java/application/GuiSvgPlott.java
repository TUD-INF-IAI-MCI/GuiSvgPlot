package application;

import application.controller.RootFrameController;
import application.service.UTF8Control;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class GuiSvgPlott extends Application {


    private static GuiSvgPlott instance;

    private RootFrameController rootFrameController;
    private Stage primaryStage;
    private JsonObject settings;


    public GuiSvgPlott() {
        instance = this;
    }


    public static synchronized GuiSvgPlott getInstance() {
        if (instance == null)
            return new GuiSvgPlott();
        return instance;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        setSettings();

        ResourceBundle bundle = ResourceBundle.getBundle("langBundle", new UTF8Control());
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(getClass().getResource("/fxml/RootFrame.fxml"));

        AnchorPane anchorPane = loader.load();

        rootFrameController = loader.getController();

        rootFrameController.init();


        Scene scene = new Scene(anchorPane);
        this.primaryStage = primaryStage;
        primaryStage.setResizable(true);
        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();


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
            System.out.println("empty settings File");
        }

        if (!settings.has("gnu-path")) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("missing gnu path");
            a.setHeaderText("GNU-Plot Pfad muss zuerst ausgewählt werden");
            a.setContentText("");

            a.showAndWait();

            FileChooser fc = new FileChooser();
            File gnuFile = fc.showOpenDialog(primaryStage);
            settings.addProperty("gnu-path", gnuFile != null ? gnuFile.getAbsolutePath() : "");
        }
    }


    public void closeWizard() {

        if (rootFrameController != null)
            rootFrameController.closeWizard();

    }

    public Window getPrimaryStage() {
        return primaryStage;
    }

    public String getGnuPlot() {
        return settings.has("gnu-path") ? settings.get("gnu-path").getAsString() : "";
    }

    public RootFrameController getRootFrameController() {
        return rootFrameController;
    }
}
