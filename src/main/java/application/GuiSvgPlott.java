package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import application.controller.RootFrameController;
import application.controller.SettingsDialogController;
import application.infrastructure.UTF8Control;
import application.model.Settings;
import application.util.DialogUtil;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    public static URL PresetOverviewFrame = GuiSvgPlott.class.getResource("/fxml/wizard/PresetOverviewFrame.fxml");
    public static URL FunctionWizardFrame = GuiSvgPlott.class
            .getResource("/fxml/wizard/content/functions/FunctionWizardFrame.fxml");
    public static URL ChartWizardFrame = GuiSvgPlott.class
            .getResource("/fxml/wizard/content/chart/ChartWizardFrame.fxml");
    public static URL LanguageDialog = GuiSvgPlott.class.getResource("/fxml/wizard/SettingsDialog.fxml");
    public static URL CsvFormatHelper = GuiSvgPlott.class.getResource("/fxml/wizard/content/CSVFormatHelp.fxml");
    public static HashSet<Path> possibleTempFiles = new HashSet<>();

    ////////////////

    public GuiSvgPlott() {
        // favicon for macos

        instance = this;
    }

    public static synchronized GuiSvgPlott getInstance() {
        if (instance == null)
            return new GuiSvgPlott();
        return instance;
    }

    public void setSettingsDialog() {

        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(GuiSvgPlott.LanguageDialog);

        try {
            AnchorPane anchorPane = loader.load();

            Scene scene = new Scene(anchorPane);
            Stage stage = new Stage();
            stage.setResizable(true);
            stage.setTitle(bundle.getString("application_settings"));
            stage.getIcons().add(Settings.getInstance().favicon);
            stage.setScene(scene);

            stage.setMinHeight(200);
            stage.setMinWidth(350);

            SettingsDialogController controller = loader.getController();
            controller.init(stage);

            Locale lang = locale.get();
            stage.showAndWait();

            if (!locale.get().equals(lang))
                start(primaryStage);

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

        DialogUtil dialogUtil = DialogUtil.getInstance();
        dialogUtil.setBundle(this.bundle);

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
        primaryStage.getIcons().add(settings.favicon);

        primaryStage.show();

        setHotKeys(primaryStage);

        primaryStage.setOnCloseRequest(event ->
                possibleTempFiles.forEach(path -> {
                    if (path.toFile().exists())
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                })
        );

    }

    private void setHotKeys(Stage primaryStage) {
        primaryStage.getScene().setOnKeyPressed(event -> {

            switch (event.getCode()) {
                case F5: {
                    if (rootFrameController.svgWizardController != null) {
                        rootFrameController.svgWizardController.button_rerenderPreview.fire();
                    }
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
                    if (event.isControlDown() && rootFrameController.svgWizardController != null) {
                        rootFrameController.svgWizardController.button_Infos.fire();
                        break;
                    }
                }
                case W: {
                    if (event.isControlDown() && rootFrameController.svgWizardController != null) {
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
                        rootFrameController.saveAsPreset();
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
                    if (event.isControlDown() && rootFrameController.svgWizardController != null) {
                        if (!event.isShiftDown()) {
                            rootFrameController.svgWizardController.incrementCurrentStage();
                        } else {
                            rootFrameController.svgWizardController.decrementCurrentStage();
                        }
                    }
                }
                default:
                    break;
            }
        });

    }

    private void setSettings() throws IOException {
        settings.loadSettings(primaryStage);
    }

    public boolean closeWizard(boolean created) {
        DialogUtil dialogUtil = DialogUtil.getInstance();
        dialogUtil.setBundle(this.bundle);
        Alert a = dialogUtil.alert(AlertType.CONFIRMATION, "alert_stage_exit_title", "alert_stage_exit_header",
                "alert_stage_exit_content");

        Optional<ButtonType> result = Optional.empty();

        if (!created)
            result = a.showAndWait();


        if ((!result.isPresent() || result.get().equals(ButtonType.OK))) {
            if (rootFrameController != null) {
                rootFrameController.closeWizard();
                return true;
            }
        }
        return false;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public RootFrameController getRootFrameController() {
        return rootFrameController;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

}
