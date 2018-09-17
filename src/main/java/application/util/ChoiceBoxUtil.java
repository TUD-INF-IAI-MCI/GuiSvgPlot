package application.util;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import application.service.SvgOptionsService;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * @author Emma Müller
 */
public class ChoiceBoxUtil {
    private static final Logger logger = LoggerFactory.getLogger(ChoiceBoxUtil.class);
    private static final ChoiceBoxUtil INSTANCE = new ChoiceBoxUtil();
    private SvgOptionsService svgOptionsService = SvgOptionsService.getInstance();
    private ResourceBundle bundle;

    private ChoiceBoxUtil() {
    }

    public static ChoiceBoxUtil getInstance() {
        return INSTANCE;
    }


    public void addNotEmptyValidationListener(final ChoiceBox choiceBox, final Label label){
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String errorMsg = label.getText() + ": " + bundle.getString("error_not_null");
            if (newValue == null) {
                choiceBox.getStyleClass().add("invalid");
                label.getStyleClass().add("invalid");
               // logger.error(errorMsg);
            } else {
                choiceBox.getStyleClass().remove("invalid");
                label.getStyleClass().remove("invalid");

                VBox messages = GuiSvgPlott.getInstance().getRootFrameController().vBox_messages;
                Label errorMessageLabel = new Label();
                for (Node child : messages.getChildren()) {
                    Label errorMessage = (Label) child;
                    if (errorMessage.getText().contains(errorMsg)) {
                        errorMessageLabel = errorMessage;
                    }
                }
                messages.getChildren().remove(errorMessageLabel);


            }
        });
    }

    /**
     * Adds a change listener to each given ChoiceBox that updates the preview.
     * @param webView_svg the {@link WebView} for the preview
     * @param guiSvgOptions the {@link GuiSvgOptions}, needed for the preview rendering
     * @param choiceBoxes all {@link ChoiceBox}es
     */
    public void addReloadPreviewOnChangeListener(final WebView webView_svg, final GuiSvgOptions guiSvgOptions, final ChoiceBox... choiceBoxes){
        for(ChoiceBox choiceBox : choiceBoxes) {
            choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                this.svgOptionsService.buildPreviewSVG(guiSvgOptions, webView_svg);
            });
        }
    }

    /**
     * Sets the {@link ResourceBundle}
     *
     * @param bundle the {@link ResourceBundle}
     */
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
