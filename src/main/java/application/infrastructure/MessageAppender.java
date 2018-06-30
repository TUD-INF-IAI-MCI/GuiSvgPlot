package application.infrastructure;

import application.GuiSvgPlott;
import application.controller.RootFrameController;
import application.service.UTF8Control;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.scene.control.Label;

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
        Label label_message = rootFrameController.label_message;
        if (!label_message.getText().equals("")){
            formattedMsg = patternLayoutWithStartingLinebreak.doLayout(event);
        }
        formattedMsg = formattedMsg.replace(event.getLevel().levelStr, this.bundle.getString(event.getLevel().levelStr.toLowerCase()));
//        System.out.println(formattedMsg.replace(":", "="));
        label_message.setText(label_message.getText() + formattedMsg);
        label_message.setAccessibleText(formattedMsg);
        label_message.setVisible(true);
        label_message.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
    }
}