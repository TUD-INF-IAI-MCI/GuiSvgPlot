package application;

import application.controller.RootFrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class GuiSvgPlott extends Application {


    private static GuiSvgPlott instance;

    private RootFrameController rootFrameController;
    private Stage primaryStage;

    public GuiSvgPlott() {
        instance = this;
    }


    public static synchronized GuiSvgPlott getInstance() {
        if (instance == null)
            return new GuiSvgPlott();
        return instance;
    }


    static final Logger log = LoggerFactory.getLogger(GuiSvgPlott.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("langBundle");
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

    public void closeWizard() {

        if (rootFrameController != null)
            rootFrameController.closeWizard();

    }

    public Window getPrimaryStage() {
        return primaryStage;
    }
}
