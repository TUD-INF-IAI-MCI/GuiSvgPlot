package application.controller.wizard.chart;

import application.GuiSvgPlott;
import application.Wizard.SVGWizardController;
import application.Wizard.StageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.svgcreator.SvgCreator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartController implements SVGWizardController {

    private int AMOUNTOFSTAGES = 7;

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
        if (this.currentStageController != null) {
            this.svgPlotOptions = currentStageController.getSvgPlotOptions();
        }
        if (this.currentStage <= this.AMOUNTOFSTAGES) {
            this.setStageView();
        } else {
            buildSVG();
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
            ResourceBundle bundle = ResourceBundle.getBundle("langBundle");
            loader.setResources(bundle);
            loader.setLocation(getClass().getResource("/fxml/wizard/content/chart/stage" + this.currentStage + ".fxml"));

            try {
                this.boderPane_stage.setCenter(loader.load());
                this.currentStageController = loader.getController();
                this.currentStageController.setSvgPlotOptions(this.svgPlotOptions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildSVG(){
        this.svgPlotOptions.finalizeOptions();

        SvgCreator creator = this.svgPlotOptions.getDiagramType().getInstance(this.svgPlotOptions);
        try {
            creator.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

       GuiSvgPlott.getInstance().closeWizard();
    }
}
