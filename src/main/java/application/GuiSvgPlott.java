package application;

import application.controller.SettingsDialogController;
import application.controller.RootFrameController;
import application.infrastructure.UTF8Control;
import application.model.Settings;
import com.google.gson.*;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Robert Schlegel
 */
public class GuiSvgPlott extends Application {


    private static GuiSvgPlott instance;

    private RootFrameController rootFrameController;
    private Stage primaryStage;
    private ResourceBundle bundle;
    private SimpleObjectProperty<Locale> locale;
    private Settings settings = Settings.getInstance();

    ///// PATHS ////

    public static URL RootFrame = GuiSvgPlott.class.getResource("/fxml/RootFrame.fxml");
    public static URL WizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/Wizard.fxml");
    public static URL PresetOverviewFrame = GuiSvgPlott.class.getResource("/fxml/wizard/PresetOverviewFrame.fxml");
    public static URL IntroFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/Intro.fxml");
    public static URL CsvEditorFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/CsvEditor.fxml");
    public static URL FunctionWizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/functions/FunctionWizardFrame.fxml");
    public static URL ChartWizardFrame = GuiSvgPlott.class.getResource("/fxml/wizard/content/chart/ChartWizardFrame.fxml");
    public static URL LanguageDialog = GuiSvgPlott.class.getResource("/fxml/wizard/SettingsDialog.fxml");
    public static URL CsvFormatHelper = GuiSvgPlott.class.getResource("/fxml/wizard/content/CSVFormatHelp.fxml");

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
            stage.setTitle(bundle.getString("application_settings"));
            stage.setScene(scene);

            SettingsDialogController controller = loader.getController();
            controller.init(stage);

            Locale lang = locale.get();
            stage.showAndWait();

            if (!locale.get().equals(lang))
                start(primaryStage);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setLanguage(Locale locale) {
        this.locale.set(locale);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        if (locale == null) {
            locale = new SimpleObjectProperty<>();
            locale.bindBidirectional(settings.currentLocaleProperty());
        }
        setSettings();

        this.bundle = ResourceBundle.getBundle("langBundle", locale.get(), new UTF8Control());

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
                    VBox messages = rootFrameController.vBox_messages;
                    ScrollPane messages_container = rootFrameController.scrollPane_message;
                    if (messages.getChildren().size() > 0) {
                        messages.getChildren().get(0).requestFocus();
                    } else {
                        messages_container.setFocusTraversable(true);
                        messages_container.requestFocus();
                    }
                    break;
                }
                case I: {
                    if (event.isControlDown()) {
                        rootFrameController.svgWizardController.button_Infos.fire();
                        break;
                    }
                }
                case W: {
                    if (event.isControlDown()) {
                        rootFrameController.svgWizardController.button_Warnings.fire();
                        break;
                    }
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
                case TAB: {
                    if (event.isControlDown()) {
                        if (!event.isShiftDown()) {
                            rootFrameController.svgWizardController.incrementCurrentStage();
                        } else {
                            rootFrameController.svgWizardController.decrementCurrentStage();
                        }
                    }
                }
            }
        });

    }

    private void setSettings() throws IOException {
        settings.loadSettings(primaryStage);
        locale.set(settings.getCurrentLocale());
//        Settings.getInstance().setGnuPlotPath(settingsJSON.get("gnu-path").getAsString());
    }


    public void closeWizard() {
        if (rootFrameController != null)
            rootFrameController.closeWizard();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }


    public RootFrameController getRootFrameController() {
        return rootFrameController;
    }

}
