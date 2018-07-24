package application.controller.wizard.functions;

import application.controller.wizard.SVGWizardController;
import application.model.Options.LinePointsOption;
import application.model.Options.SortOrder;
import application.model.Options.TrendlineAlgorithm;
import application.model.Options.VisibilityOfDataPoints;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.GridStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class FunctionWizardFrameController extends SVGWizardController {

    private static final int AMOUNTOFSTAGES = 6;

    /*Begin: FXML Nodes*/

    /* stage 1 */
    @FXML
    private GridPane stage1;

    /* stage 2*/
    @FXML
    private GridPane stage2;

    /* stage 3 */
    @FXML
    private GridPane stage3;

    /* stage 4 */
    @FXML
    private GridPane stage4;


    /* stage 5 */
    @FXML
    private GridPane stage5;

    /* stage 6 */
    @FXML
    private GridPane stage6;

    /*End: FXML Nodes*/

    public FunctionWizardFrameController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.initiatePagination(this.hBox_pagination, AMOUNTOFSTAGES);
        this.initiateAllStages();

        this.guiSvgOptions.setDiagramType(DiagramType.FunctionPlot);

    }


    private void initiateAllStages() {
        initStage1();
        initStage2();
        initStage3();
        initStage4();
        initStage5();
        initStage6();
    }


    /**
     * Will initiate the first stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage1() {

        super.initGeneralFieldListeners();

    }


    /**
     * Will initiate the second stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage2() {
        super.initCsvFieldListeners();
    }

    /**
     * Will initiate the third stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage3() {

    }

    /**
     * Will initiate the forth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage4() {
        super.initAxisFieldListeners();
    }

    /**
     * Will initiate the fifth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage5() {
        super.initSpecialFieldListeners();
    }

    /**
     * Will initiate the sixth stage. Depending on {@code extended}, some parts will be dis- or enabled.
     */
    private void initStage6() {
        super.initStylingFieldListeners();
    }


}
