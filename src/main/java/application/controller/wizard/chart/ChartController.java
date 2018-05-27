package application.controller.wizard.chart;

import application.Wizard.SVGWizardController;
import application.Wizard.StageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


// hideoriginalPoints (trendlinie)
// sorting, sortdescending
// autoscale
// size
// device
// output

public class ChartController implements SVGWizardController {

    private int currentStage;
    private String title;

    private StageController currentStageController;
    private SvgPlotOptions svgPlotOptions;

    @FXML
    public BorderPane boderPane_stage;

    public ChartController(){
        this.currentStage = 0;
        this.title="Diagramm";
        this.svgPlotOptions = new SvgPlotOptions();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            this.setStageView();
    }

    @Override
    public int getCurrentStage() {
        return this.currentStage;
    }

    @Override
    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
        this.setStageView();
        if (this.currentStageController != null){
            this.svgPlotOptions = currentStageController.getSvgPlotOptions();
        }
    }

    @Override
    public String getFXMLLocation() {
        return "/fxml/wizard/content/chart/chart.fxml";
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    private void setStageView(){
        if(this.currentStage > 0) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/wizard/content/chart/stage" + this.currentStage + ".fxml"));

            try {
                boderPane_stage.setCenter(loader.load());
                currentStageController = loader.getController();
                currentStageController.setSvgPlotOptions(this.svgPlotOptions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
