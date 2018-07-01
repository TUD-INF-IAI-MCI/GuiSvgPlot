package application.infrastructure;

import application.GuiSvgPlott;
import application.controller.RootFrameController;
import application.service.SvgOptionsService;
import application.service.UTF8Control;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.controlsfx.glyphfont.Glyph;

import java.util.ResourceBundle;

/**
 * LogBack-Appender for slf4j logs. This renders log messages into the label_message of {@link RootFrameController}
 */
public class MessageAppender extends AppenderBase<ILoggingEvent> {

    private RootFrameController rootFrameController = GuiSvgPlott.getInstance().getRootFrameController();

    private PatternLayout patternLayoutWithLevel;
    private PatternLayout patternLayoutOnlyMsg;
    private ResourceBundle bundle = ResourceBundle.getBundle("langBundle", new UTF8Control());

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
        String formattedMsg = patternLayoutWithLevel.doLayout(event);
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
        label.getStyleClass().add("label_message");
        label.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
        label.setAlignment(Pos.CENTER);
        label.setPrefWidth(rootFrameController.scrollPane_message.getWidth());
        vBox_messages.getChildren().add(label);

        // TODO: set accassible help text
        ScrollPane scrollPane = rootFrameController.scrollPane_message;
        scrollPane.setVisible(true);
    }

    private void renderInfoMessage(final ILoggingEvent event) {
        Button button_infos =  rootFrameController.svgWizardController.button_Infos;
        String formattedMsg = patternLayoutOnlyMsg.doLayout(event);
        Label label = new Label(formattedMsg);
        label.getStyleClass().add("label_message");
        label.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
        VBox infos = rootFrameController.svgWizardController.vBox_infos;
        infos.getChildren().add(label);
        button_infos.setDisable(false);

        createNotification(button_infos, "" + infos.getChildren().size(), rootFrameController.svgWizardController.getInfoIcon());
    }

    private void renderWarnMessage(final ILoggingEvent event) {
        Button button_warnings = rootFrameController.svgWizardController.button_Warnings;
        VBox warnings = rootFrameController.svgWizardController.vBox_warnings;
        String formattedMsg = patternLayoutOnlyMsg.doLayout(event);
        int numberOfWarn = warnings.getChildren().size() + 1;
        Label label = new Label(numberOfWarn + ": " +formattedMsg);
        label.getStyleClass().add("label_message");
        label.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
        warnings.getChildren().add(label);
        button_warnings.setDisable(false);

        createNotification(button_warnings, "" + warnings.getChildren().size(), rootFrameController.svgWizardController.getWarnIcon());
    }

    private Node createNotification(Button button, String number, final Glyph icon) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("hBox-0");
        hBox.setSpacing(0);
        Glyph iconCopy = icon.duplicate();
        iconCopy.setStyle("-fx-padding: 12 0 0 10;");
        hBox.getChildren().add(iconCopy);

        StackPane stackPane = new StackPane();
        Label numberLabel = new Label(number);
        numberLabel.getStyleClass().add("notification");
        numberLabel.setStyle("-fx-text-fill:white");
        Circle circle = new Circle(8);//, Color.rgb(200, 0, 0, .9));
        circle.getStyleClass().add("notification-circle");
        circle.setSmooth(true);
        stackPane.getChildren().addAll(circle, numberLabel);
        stackPane.setStyle("-fx-padding: -15 -20 0 -20;");
        hBox.getChildren().add(stackPane);
        button.setGraphic(hBox);
        return hBox;
    }
}