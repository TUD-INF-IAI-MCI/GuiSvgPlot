package application.Wizard;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import application.service.SvgOptionsService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SVGWizardController implements Initializable {
    // start logger
    private static final Logger logger = LoggerFactory.getLogger(SVGWizardController.class);

    @FXML
    protected Button button_Back;
    @FXML
    public BorderPane borderPane_WizardContent;
    @FXML
    protected HBox hBox_pagination;
    @FXML
    protected Button button_Next;
    @FXML
    protected Button button_Cancel;
    @FXML
    protected Button button_Create;
    //    @FXML
//    protected Label label_Headline;
    @FXML
    protected TabPane tabPane_ContentHolder;
    @FXML
    protected WebView webView_svg;

    @FXML
    public Button button_Warnings;
    @FXML
    public Button button_Infos;

    public VBox vBox_warnings;
    public PopOver popOver_warnings;
    public VBox vBox_infos;
    public PopOver popOver_infos;

    protected BooleanProperty isExtended;
    protected List<Button> stageBtns;
    protected List<Button> messageBtns;
    protected ResourceBundle bundle;
    protected IntegerProperty currentStage;
    protected File userDir;
    protected ArrayList<GridPane> stages;
    protected GuiSvgOptions svgOptions;
    protected SvgPlotOptions svgPlotOptions;
    protected SvgOptionsService svgOptionsService = SvgOptionsService.getInstance();

    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.svgOptionsService.setBundle(resources);
        this.currentStage = new SimpleIntegerProperty();
        this.isExtended = new SimpleBooleanProperty(false);
        this.bundle = resources;

        this.userDir = new File(System.getProperty("user.home"));
        initListener();

        preProcessContent();

        this.svgPlotOptions = new SvgPlotOptions();
        this.svgOptions = new GuiSvgOptions(svgPlotOptions);
        this.webView_svg.setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        this.webView_svg.setAccessibleHelp(this.bundle.getString("preview"));
    }

    public void setExtended(boolean isExtended) {
        this.isExtended.set(isExtended);
    }

    protected void initiatePagination(final HBox hBox_pagination, final int AMOUNTOFSTAGES) {
        this.stageBtns = new ArrayList<>();
        for (int stage = 0; stage < AMOUNTOFSTAGES; stage++) {
            Button stageBtn = new Button(bundle.getString("chart_stage" + stage));
            stageBtn.getStyleClass().add("stageBtn");
            if (this.currentStage.get() == stage) {
                stageBtn.getStyleClass().add("active");
            }

            final int stageNumber = stage;
            stageBtn.setOnAction(event -> {
                currentStage.set(stageNumber);
            });
            hBox_pagination.getChildren().add(stageBtn);
            this.stageBtns.add(stageBtn);
        }

        this.messageBtns = new ArrayList<>();
        hBox_pagination.getChildren().remove(this.button_Warnings);
        this.button_Warnings = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.WARNING));
        this.button_Warnings.getStyleClass().add("messageBtn");
        this.button_Warnings.setId("btn_warnings");
        this.button_Warnings.setDisable(true);
        hBox_pagination.getChildren().add(this.button_Warnings);

        hBox_pagination.getChildren().remove(this.button_Infos);
        this.button_Infos = new Button("", new Glyph("FontAwesome", FontAwesome.Glyph.INFO));
        this.button_Infos.getStyleClass().add("messageBtn");
        this.button_Infos.setId("btn_infos");
        this.button_Infos.setDisable(true);
        hBox_pagination.getChildren().add(this.button_Infos);
        this.messageBtns.add(this.button_Infos);

        popOver_warnings = new PopOver();
        popOver_infos = new PopOver();

        vBox_infos = new VBox();
        vBox_infos.getStyleClass().add("info");
        vBox_warnings = new VBox();
        vBox_warnings.getStyleClass().add("warn");

        button_Infos.setOnAction(event -> {
            popOver_infos.setTitle("Informationen");
            popOver_infos.setContentNode(vBox_infos);
            popOver_infos.show(button_Infos);
        });
        button_Warnings.setOnAction(event -> {
            popOver_warnings.setTitle("Warnungen");
            popOver_warnings.setContentNode(vBox_warnings);
            popOver_warnings.show(button_Infos);
        });

    }

    /**
     * content-preprocessing. Will "hide" the content-tabPane and shows the first stage
     */
    protected void preProcessContent() {
        stages = new ArrayList<>();
        tabPane_ContentHolder.getTabs().forEach(tab -> stages.add((GridPane) tab.getContent()));
        currentStage.set(0);
        borderPane_WizardContent.setCenter(stages.get(0));
        button_Back.setDisable(true);
    }

    /**
     * initiates all listeners for properties and elements
     */
    protected void initListener() {
        // indicator for current stage. changes will automatically render the chosen stage
        currentStage.addListener((args, oldVal, newVal) -> {

            if (newVal.intValue() < 1) button_Back.setDisable(true);
            else button_Back.setDisable(false);

            if (newVal.intValue() == stages.size() - 1) {
                button_Next.setDisable(true);
            } else {
                button_Next.setDisable(false);
            }
            if (newVal.intValue() > stages.size() - 1) {
                currentStage.set(oldVal.intValue());
            }

            if (oldVal.intValue() < this.stageBtns.size()) {
                this.stageBtns.get(oldVal.intValue()).getStyleClass().remove("active");
                this.stageBtns.get(currentStage.get()).getStyleClass().add("active");
            }

            borderPane_WizardContent.setCenter(stages.get(currentStage.get()));
            this.svgOptionsService.buildPreviewSVG(this.svgPlotOptions, this.webView_svg);
        });

        // increment the currentStage counter. Will trigger its changeListener
        button_Next.setOnAction(event -> {
            currentStage.set(currentStage.get() + 1);
        });

        // decrement the currentStage counter. Will trigger its changeListener
        button_Back.setOnAction(event -> currentStage.set(currentStage.get() - 1));

        // closes the wizard
        button_Cancel.setOnAction(event -> {
            GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
            GuiSvgPlott.getInstance().closeWizard();
        });

        // create chart
        button_Create.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(userDir);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Scalable Vector Graphics (SVG)", "*.svg");
            fileChooser.getExtensionFilters().add(extFilter);
            String title = this.svgPlotOptions.getTitle().isEmpty() ? "untitled" : this.svgPlotOptions.getTitle();
            fileChooser.setInitialFileName(title.toLowerCase() + ".svg");
            File file = fileChooser.showSaveDialog(GuiSvgPlott.getInstance().getPrimaryStage());
            if (file != null) {
                this.svgPlotOptions.setOutput(file);
                this.svgOptionsService.buildSVG(svgPlotOptions);
                GuiSvgPlott.getInstance().closeWizard();
            }
        });

    }

}
