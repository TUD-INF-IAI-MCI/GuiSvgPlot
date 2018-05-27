package application;

import application.controller.RootFrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiSvgPlott extends Application {


    private static GuiSvgPlott instance;

    private RootFrameController rootFrameController;

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

        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/fxml/RootFrame.fxml"));

        AnchorPane anchorPane = loader.load();

        rootFrameController = loader.getController();

        rootFrameController.init();


        Scene scene = new Scene(anchorPane);

        primaryStage.setResizable(true);
        primaryStage.setTitle("FXML Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public void closeWizard() {

        if (rootFrameController != null)
            rootFrameController.closeWizard();

    }

}
