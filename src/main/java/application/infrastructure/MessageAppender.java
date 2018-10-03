package application.infrastructure;

import application.GuiSvgPlott;
import application.controller.RootFrameController;
import application.model.Settings;
import application.service.SvgOptionsService;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.controlsfx.glyphfont.Glyph;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Emma Müller
 * LogBack-Appender for slf4j logs. This renders log messages into the label_message of {@link RootFrameController}
 */
public class MessageAppender extends AppenderBase<ILoggingEvent> {

    private RootFrameController rootFrameController;

    private PatternLayout patternLayoutWithLevel;
    private PatternLayout patternLayoutOnlyMsg;
    private Locale locale = Settings.getInstance().getCurrentLocale() == null ? Locale.getDefault() : Settings.getInstance().getCurrentLocale();
    private ResourceBundle bundle = ResourceBundle.getBundle("langBundle", locale, new UTF8Control());

    @Override
    public void start() {
        patternLayoutWithLevel = new PatternLayout();
        patternLayoutWithLevel.setContext(getContext());
        patternLayoutWithLevel.setPattern("%-5level: %msg");
        patternLayoutWithLevel.start();

        patternLayoutOnlyMsg = new PatternLayout();
        patternLayoutOnlyMsg.setContext(getContext());
        patternLayoutOnlyMsg.setPattern("%msg");
        patternLayoutOnlyMsg.start();

        super.start();
    }

    @Override
    protected void append(final ILoggingEvent event) {
        rootFrameController = GuiSvgPlott.getInstance().getRootFrameController();
        // https://controlsfx.bitbucket.io/ --> NotificationPane/Notification
//        Notifications notifications = Notifications.create()
//                .title(this.bundle.getString(event.getLevel().levelStr.toLowerCase()))
//                .text(formattedMsg);
        switch (event.getLevel().levelStr) {
            case "WARN":
                renderWarnMessage(event);
//                notifications.showWarning();
                break;
            case "INFO":
//                notifications.showInformation();
                if (event.getLoggerName().equals(SvgOptionsService.getInstance().getLoggerName())) {
                    renderMessage(event);
                } else {
                    renderInfoMessage(event);
                }
                break;
            default:
//                notifications.showError();
                renderMessage(event);
                break;
        }

    }

    private void renderMessage(final ILoggingEvent event) {
        String formattedMsg = patternLayoutWithLevel.doLayout(event);
        formattedMsg = formattedMsg.replace(event.getLevel().levelStr, this.bundle.getString(event.getLevel().levelStr.toLowerCase()));

        VBox vBox_messages = rootFrameController.vBox_messages;
        Label label = new Label(formattedMsg);
        label.setAccessibleText(formattedMsg);
        label.getStyleClass().add("notification");
        label.getStyleClass().add("notification-label");
        label.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
        label.setAlignment(Pos.CENTER);
        label.setPrefWidth(rootFrameController.scrollPane_message.getWidth());
        vBox_messages.getChildren().add(0, label);

        // TODO: set accassible help text
        ScrollPane scrollPane = rootFrameController.scrollPane_message;
        scrollPane.setAccessibleText("");
        scrollPane.setVisible(true);
        label.setFocusTraversable(true);
//        label.requestFocus();
    }

    private void renderInfoMessage(final ILoggingEvent event) {
        Button button_infos = rootFrameController.svgWizardController.button_Infos;
        VBox infos = rootFrameController.svgWizardController.vBox_infos;

        Text formattedMsg = getTextOfMessage(event, infos, "info");
        infos.getChildren().add(formattedMsg);
        button_infos.setDisable(false);
        button_infos.setAccessibleHelp(this.bundle.getString("info_message") + " " + infos.getChildren().size());
        button_infos.setAccessibleRole(AccessibleRole.MENU_ITEM);

        createNotification(button_infos, "" + infos.getChildren().size(), rootFrameController.svgWizardController.getInfoIcon());
    }

    private void renderWarnMessage(final ILoggingEvent event) {
        Button button_warnings = rootFrameController.svgWizardController.button_Warnings;
        VBox warnings = rootFrameController.svgWizardController.vBox_warnings;

        Text formattedMsg = getTextOfMessage(event, warnings, "warn");
        warnings.getChildren().add(formattedMsg);
        button_warnings.setDisable(false);
        button_warnings.setAccessibleHelp(this.bundle.getString("warn_message") + ":" + warnings.getChildren().size());
        button_warnings.setAccessibleRole(AccessibleRole.MENU_ITEM);

        createNotification(button_warnings, "" + warnings.getChildren().size(), rootFrameController.svgWizardController.getWarnIcon());
    }

    private Text getTextOfMessage(final ILoggingEvent event, final VBox vBox, final String bundleKey) {
        int numberOfWarn = vBox.getChildren().size() + 1;
        String message = numberOfWarn + ". " + patternLayoutOnlyMsg.doLayout(event);
        if (numberOfWarn > 1) {
            message = "\n" + message;
        }
        Text formattedMsg = new Text(message);
        formattedMsg.setWrappingWidth(300);
        formattedMsg.getStyleClass().add("notification");
        formattedMsg.getStyleClass().add("notification-label");
        formattedMsg.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
        formattedMsg.textAlignmentProperty().set(TextAlignment.JUSTIFY);
        formattedMsg.setFocusTraversable(true);
        formattedMsg.setAccessibleText(numberOfWarn + ". " + bundle.getString(bundleKey) + ": " + patternLayoutOnlyMsg.doLayout(event));

        return formattedMsg;
    }

    private Node createNotification(Button button, String number, final Glyph icon) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("hBox-0");
        hBox.setSpacing(0);
        Glyph iconCopy = icon.duplicate();
        iconCopy.setStyle("-fx-padding: 12 0 0 15;");
        hBox.getChildren().add(iconCopy);

        StackPane stackPane = new StackPane();
        Label numberLabel = new Label(number);
        numberLabel.getStyleClass().add("notification-marker");
        numberLabel.setStyle("-fx-text-fill:white");
        Circle circle = new Circle(8);
        circle.getStyleClass().add("notification-marker-circle");
        circle.setSmooth(true);
        stackPane.getChildren().addAll(circle, numberLabel);
        stackPane.setStyle("-fx-padding: -15 -20 0 -20;");
        hBox.getChildren().add(stackPane);
        button.setGraphic(hBox);
        return hBox;
    }
}