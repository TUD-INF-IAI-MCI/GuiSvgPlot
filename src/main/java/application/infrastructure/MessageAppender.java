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
        patternLayoutWithStartingLinebreak.setPattern("%n %-5level: %msg");
        patternLayoutWithStartingLinebreak.start();

        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        String formattedMsg = patternLayout.doLayout(event);
        VBox vBox_messages = rootFrameController.vBox_messages;
//        if (vBox_messages.getChildren().size() > 0) {
//            formattedMsg = patternLayoutWithStartingLinebreak.doLayout(event);
//        }
        formattedMsg = formattedMsg.replace(event.getLevel().levelStr, this.bundle.getString(event.getLevel().levelStr.toLowerCase()));

        Label label = new Label(formattedMsg);
        label.setAccessibleText(formattedMsg);
        label.getStyleClass().add("label_message");
        label.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
        label.setAlignment(Pos.CENTER);
        label.setPrefWidth(rootFrameController.scrollPane_message.getWidth());


        vBox_messages.getChildren().add(label);

        ScrollPane scrollPane = rootFrameController.scrollPane_message;
        scrollPane.setVisible(true);
    }
}