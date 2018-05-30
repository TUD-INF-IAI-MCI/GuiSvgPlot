package application.controller.wizard.functions;

import application.GuiSvgPlott;
import application.Wizard.SVGWizardController;
import application.Wizard.StageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FunctionFrameController implements SVGWizardController {


    private int currentStageLevel;

    @FXML
    private BorderPane borderPane_stage;


    private StageController currentStageController;
    private SvgPlotOptions svgPlotOptions;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }


    @Override
    public int getCurrentStage() {
        return currentStageLevel;
    }

    @Override
    public void setCurrentStage(int currentStage) {
        this.currentStageLevel = currentStage;
        loadStage();

    }

    @Override
    public String getFXMLLocation() {
        return "/fxml/wizard/content/functions/FunctionFrame.fxml";
    }

    @Override
    public String getTitle() {
        return "Functions";
    }


    private void loadStage() {

        if (this.currentStageLevel > 0) {
            FXMLLoader loader = new FXMLLoader();
            ResourceBundle bundle = ResourceBundle.getBundle("langBundle");
            loader.setResources(bundle);

            URL path = GuiSvgPlott.getInstance().getClass().getResource("/fxml/wizard/content/functions/FunctionStage" + this.currentStageLevel + ".fxml");
            File f = new File(path.toString());

            System.out.println(f);
            loader.setLocation(path);

            try {
                AnchorPane pane = loader.load();

                this.borderPane_stage.setCenter(pane.getChildren().get(0));
                this.currentStageController = loader.getController();
                this.currentStageController.setSvgPlotOptions(this.svgPlotOptions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
