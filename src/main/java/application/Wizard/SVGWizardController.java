package application.Wizard;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import application.service.SvgOptionsService;
import application.util.SvgOptionsUtil;
import com.sun.javafx.scene.control.skin.ScrollPaneSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.File;
import java.lang.reflect.Field;
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
    @FXML
    public Button button_rerenderPreview;
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
    private PopOver popOver_warnings;
    public VBox vBox_infos;
    private PopOver popOver_infos;
    private Glyph warnIcon;
    private Glyph infoIcon;

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
    protected SvgOptionsUtil svgOptionsUtil = SvgOptionsUtil.getInstance();

    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        this.svgOptionsService.setBundle(resources);
        this.svgOptionsUtil.setBundle(resources);
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
            hBox_pagination.setAccessibleRole(AccessibleRole.MENU);
            stageBtn.accessibleRoleProperty().set(AccessibleRole.MENU_ITEM);
            this.stageBtns.add(stageBtn);
        }

        this.messageBtns = new ArrayList<>();
        hBox_pagination.getChildren().remove(this.button_Warnings);
        this.warnIcon = new Glyph("FontAwesome", FontAwesome.Glyph.WARNING);
        this.warnIcon.setColor(Color.valueOf("#f0ad4e"));
        this.button_Warnings = new Button("", warnIcon);
        this.button_Warnings.getStyleClass().add("messageBtn");
        this.button_Warnings.setId("btn_warnings");
        this.button_Warnings.setDisable(true);
        hBox_pagination.getChildren().add(this.button_Warnings);

        hBox_pagination.getChildren().remove(this.button_Infos);
        this.infoIcon = new Glyph("FontAwesome", FontAwesome.Glyph.INFO);
        this.infoIcon.setColor(Color.valueOf("#002557"));
        this.button_Infos = new Button("", infoIcon);
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
            ScrollPane infoScrollPane = new ScrollPane(vBox_infos);
            infoScrollPane.getStyleClass().add("scrollPane-message");
            infoScrollPane.getStyleClass().add("info");
            infoScrollPane.setMaxSize(340, 500);
            infoScrollPane.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
            infoScrollPane.setPadding(new Insets(0, 10, 0, 0));

            popOver_infos.setTitle("Informationen");
            popOver_warnings.getStyleClass().add("info");
            popOver_infos.setHeaderAlwaysVisible(true);
            popOver_infos.setContentNode(infoScrollPane);
            popOver_infos.show(button_Infos);
            fixBlurryText(infoScrollPane);
        });
        button_Warnings.setOnAction(event -> {
            ScrollPane warningScrollPane = new ScrollPane(vBox_warnings);
            warningScrollPane.getStyleClass().add("scrollPane-message");
            warningScrollPane.getStyleClass().add("warn");
            warningScrollPane.setMaxSize(340, 500);
            warningScrollPane.hbarPolicyProperty().set(ScrollPane.ScrollBarPolicy.NEVER);
            warningScrollPane.setPadding(new Insets(0, 10, 0, 0));

            popOver_warnings.setTitle("Warnungen");
            popOver_warnings.getStyleClass().add("warn");
            popOver_warnings.setHeaderAlwaysVisible(true);
            popOver_warnings.setContentNode(warningScrollPane);
            popOver_warnings.show(button_Warnings);
            fixBlurryText(warningScrollPane);
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
            popOver_warnings.hide();
            popOver_infos.hide();
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
                popOver_warnings.hide();
                popOver_infos.hide();
                GuiSvgPlott.getInstance().closeWizard();
            }
        });

        // rerender preview
        button_rerenderPreview.setOnAction(event ->
                this.svgOptionsService.buildPreviewSVG(this.svgPlotOptions, this.webView_svg)
        );

    }

    public Glyph getWarnIcon() {
        return warnIcon;
    }

    public Glyph getInfoIcon() {
        return infoIcon;
    }

    /**
     * Fixes blurry text issue of {@link ScrollPane} inside a {@link PopOver}.
     *
     * @param node the node
     */
    private static void fixBlurryText(Node node) {
        try {
            Field field = ScrollPaneSkin.class.getDeclaredField("viewRect");
            field.setAccessible(true);

            ScrollPane scrollPane = (ScrollPane) node.lookup(".scroll-pane");

            StackPane stackPane = (StackPane) field.get(scrollPane.getSkin());
            stackPane.setCache(false);

        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
