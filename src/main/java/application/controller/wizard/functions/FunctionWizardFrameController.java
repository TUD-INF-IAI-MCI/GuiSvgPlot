package application.controller.wizard.functions;

import application.GuiSvgPlott;
import application.Wizard.SVGWizardController;
import application.model.GuiSvgOptions;
import application.model.PageSize;
import application.model.TrendlineAlgorithm;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.Color;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionWizardFrameController extends SVGWizardController {


    public BorderPane borderPane_Wizard;
    public Label label_Headline;


    /* stage 1 */
    @FXML
    private GridPane stage1;
    @FXML
    public ChoiceBox<DiagramType> choiceBox_FunctionType;
    @FXML
    public TextField textField_Title;
    @FXML
    public ChoiceBox<OutputDevice> choiceBox_outputDevice;
    @FXML
    private ChoiceBox<PageSize> choiceBox_size;

    /* stage 2*/
    @FXML
    private GridPane stage2;
    @FXML
    public TextField textField_CsvPath;
    @FXML
    private Button button_CsvPath;
    @FXML
    public ChoiceBox<CsvOrientation> choiceBox_CsvOrientation;
    @FXML
    public ChoiceBox<CsvType> choiceBox_CsvType;
    @FXML
    public ChoiceBox<SortingType> choiceBox_Sorting;
    @FXML
    public CheckBox checkbox_SortDesc;

    /* stage 3 */
    @FXML
    private GridPane stage3;
    @FXML
    public Label label_baraccumulation;
    @FXML
    public ChoiceBox<BarAccumulationStyle> choiceBox_baraccumulation;
    @FXML
    public Label label_linepoints;
    @FXML
    public CheckBox checkbox_linepoints;
    @FXML
    private Label label_trendline;
    @FXML
    private ChoiceBox<TrendlineAlgorithm> choiceBox_trendline;
    @FXML
    public Label label_trendline_alpha;
    @FXML
    public TextField textField_trendline_alpha;
    @FXML
    public Label label_trendline_forecast;
    @FXML
    public TextField textField_trendline_forecast;
    @FXML
    public Label label_trendline_n;
    @FXML
    public TextField textField_trendline_n;
    @FXML
    public Label label_hideOriginalPoints;
    @FXML
    public CheckBox checkbox_hideOriginalPoints;

    /* stage 4 */
    @FXML
    private GridPane stage4;
    @FXML
    public TextField textField_xunit;
    @FXML
    public TextField textField_yunit;
    @FXML
    public CheckBox checkbox_Autoscale;
    @FXML
    public TextField textField_xfrom;
    @FXML
    public Label label_xfrom;
    @FXML
    public TextField textField_xto;
    @FXML
    public Label label_xto;
    @FXML
    public TextField textField_yfrom;
    @FXML
    public Label label_yfrom;
    @FXML
    public TextField textField_yto;
    @FXML
    public Label label_yto;

    /* stage 5 */
    @FXML
    private GridPane stage5;
    @FXML
    public CheckBox checkbox_hgrid;
    @FXML
    public CheckBox checkbox_vgrid;
    @FXML
    public TextField textField_xlines;
    @FXML
    public TextField textField_ylines;
    @FXML
    public CheckBox checkbox_dblaxes;
    @FXML
    public CheckBox checkbox_pointsborderless;

    /* stage 6 */
    @FXML
    private GridPane stage6;
    @FXML
    public TextField textField_cssFile;
    @FXML
    public ChoiceBox<Color> choiceBox_color1;
    @FXML
    public ChoiceBox<Color> choiceBox_color2;

    //    // ############# Options / Fields / Helper ########################
//
//    private File userDir;
//    private IntegerProperty currentStage;
//
    private ArrayList<AnchorPane> stages;
//    private BooleanProperty isExtended;

    private GuiSvgOptions svgOptions;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userDir = new File(System.getProperty("user.home"));


        this.svgOptionsService.setBundle(resources);
        this.svgOptions = new GuiSvgOptions(new SvgPlotOptions());
        this.svgPlotOptions = new SvgPlotOptions();

        this.currentStage = new SimpleIntegerProperty();
        this.isExtended = new SimpleBooleanProperty(false);

        initListener();

        preProcessContent();

        initiateAllStages();

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
     * sets the {@code isExtended} value for the wizard
     *
     * @param isExtended {@link Boolean}-value for the isExtendedProperty
     */
    public void setExtended(boolean isExtended) {
        this.isExtended.set(isExtended);

    }

    /**
     * content-preprocessing. Will "hide" the content-tabPane and shows the first stage
     */
    protected void preProcessContent() {
        stages = new ArrayList<>();
        tabPane_ContentHolder.getTabs().forEach(tab -> stages.add((AnchorPane) tab.getContent()));
        currentStage.set(0);
        borderPane_Wizard.setCenter(stages.get(0));
        button_Back.setDisable(true);
    }


    /**
     * Will initiate the first stage. Depending on {@code extended}, some parts will dis- or enabled
     */
    private void initStage1() {


        if (!choiceBox_FunctionType.getItems().contains(DiagramType.FunctionPlot))
            choiceBox_FunctionType.getItems().add(DiagramType.FunctionPlot);
        if (!choiceBox_FunctionType.getItems().contains(DiagramType.ScatterPlot))
            choiceBox_FunctionType.getItems().add(DiagramType.ScatterPlot);

        choiceBox_outputDevice.getItems().addAll(OutputDevice.values());

        ObservableList<PageSize> pageSizeObservableList = FXCollections.observableArrayList(PageSize.values());
        ObservableList<PageSize> sortedPageSizes = pageSizeObservableList.sorted(Comparator.comparing(PageSize::getName));
        this.choiceBox_size.setItems(sortedPageSizes);
        this.choiceBox_size.setConverter(svgOptionsService.getPageSizeConverter());
        this.choiceBox_size.getSelectionModel().select(PageSize.A4);

        this.choiceBox_size.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Point size = new Point(newValue.getWidth(), newValue.getHeight());
            this.svgPlotOptions.setSize(size);
        });


    }

    /**
     * Will initiate the second stage. Depending on {@code isExtended}, some parts will dis- or enabled
     */
    private void initStage2() {

        textField_CsvPath.onDragDroppedProperty().addListener(event -> {
            System.out.println(event);

        });


    }

    /**
     * Will initiate the third stage. Depending on {@code isExtended}, some parts will dis- or enabled
     */
    private void initStage3() {
    }

    /**
     * Will initiate the fourth stage. Depending on {@code isExtended}, some parts will dis- or enabled
     */
    private void initStage4() {
    }

    private void initStage5() {
    }

    private void initStage6() {
    }


    /**
     * initiates all listeners for properties and elements
     */
    @Override
    protected void initListener() {


        // indicator for current stage. changes will automatically render the chosen stage
        currentStage.addListener((args, oldVal, newVal) -> {

            if (newVal.intValue() < 1) button_Back.setDisable(true);
            else button_Back.setDisable(false);

            if (newVal.intValue() >= stages.size() - 1) {
                currentStage.set(oldVal.intValue());
            }

            borderPane_Wizard.setCenter(stages.get(newVal.intValue()));
        });


        // listener for extended wizard mode
        isExtended.addListener((args, oldVal, newVal) -> {
            initiateAllStages();
        });

        // increment the currentStage counter. Will trigger its changeListener
        button_Next.setOnAction(event -> currentStage.set(currentStage.get() + 1));

        // decrement the currentStage counter. Will trigger its changeListener
        button_Back.setOnAction(event -> currentStage.set(currentStage.get() - 1));

        // closes the wizard
        button_Cancel.setOnAction(event -> {
            GuiSvgPlott.getInstance().closeWizard();
        });

        button_CsvPath.setOnAction(event -> {

        });


    }


}
