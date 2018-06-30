package application.infrastructure;

import application.GuiSvgPlott;
import application.controller.RootFrameController;
import application.service.UTF8Control;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ResourceBundle;

/**
 * LogBack-Appender for slf4j logs. This renders log messages into the label_message of {@link RootFrameController}
 */
public class MessageAppender extends AppenderBase<ILoggingEvent> {

    private RootFrameController rootFrameController = GuiSvgPlott.getInstance().getRootFrameController();

    private PatternLayout patternLayout;
    private PatternLayout patternLayoutWithStartingLinebreak;
    private ResourceBundle bundle = ResourceBundle.getBundle("langBundle", new UTF8Control());

    @Override
    public void start() {
        patternLayout = new PatternLayout();
        patternLayout.setContext(getContext());
        patternLayout.setPattern("%-5level: %msg");
        patternLayout.start();

        patternLayoutWithStartingLinebreak = new PatternLayout();
        patternLayoutWithStartingLinebreak.setContext(getContext());
        patternLayoutWithStartingLinebreak.setPattern("%msg");
        patternLayoutWithStartingLinebreak.start();

        super.start();
    }

    @Override
    protected void append(final ILoggingEvent event) {

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
                renderInfoMessage(event);
                break;
            default:
//                notifications.showError();
                renderErrorMessage(event);
                break;
        }

    }

    private void renderErrorMessage(final ILoggingEvent event) {
        String formattedMsg = patternLayout.doLayout(event);
        formattedMsg = formattedMsg.replace(event.getLevel().levelStr, this.bundle.getString(event.getLevel().levelStr.toLowerCase()));
        VBox vBox_messages = rootFrameController.vBox_messages;
//        if (vBox_messages.getChildren().size() > 0) {
//            formattedMsg = patternLayoutWithStartingLinebreak.doLayout(event);
//        }


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

    private void renderInfoMessage(final ILoggingEvent event){
        String formattedMsg = patternLayoutWithStartingLinebreak.doLayout(event);
        Label label = new Label(formattedMsg);
        label.getStyleClass().add("label_message");
        label.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
        VBox infos = rootFrameController.svgWizardController.vBox_infos;
        infos.getChildren().add(label);
        rootFrameController.svgWizardController.button_Infos.setDisable(false);
    }

    private void renderWarnMessage(final ILoggingEvent event){
        String formattedMsg = patternLayoutWithStartingLinebreak.doLayout(event);
        Label label = new Label(formattedMsg);
        label.getStyleClass().add("label_message");
        label.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
        VBox warnings = rootFrameController.svgWizardController.vBox_warnings;
        warnings.getChildren().add(label);
        rootFrameController.svgWizardController.button_Warnings.setDisable(false);
    }
}