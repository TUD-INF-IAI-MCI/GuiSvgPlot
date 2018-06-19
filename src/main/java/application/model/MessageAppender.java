package application.model;

import application.GuiSvgPlott;
import application.controller.RootFrameController;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.scene.control.Label;

/**
 * LogBack-Appender for slf4j logs. This renders log messages into the label_message of {@link RootFrameController}
 */
public class MessageAppender extends AppenderBase<ILoggingEvent> {

    private RootFrameController rootFrameController = GuiSvgPlott.getInstance().getRootFrameController();

    private PatternLayout patternLayout;


    @Override
    public void start() {
        patternLayout = new PatternLayout();
        patternLayout.setContext(getContext());
        patternLayout.setPattern("%msg");
        patternLayout.start();

        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        String formattedMsg = patternLayout.doLayout(event);

        Label label_message = rootFrameController.label_message;
        label_message.setText(formattedMsg);
        label_message.setAccessibleText(formattedMsg);
        label_message.setVisible(true);
        label_message.getStyleClass().add(event.getLevel().levelStr.toLowerCase());
    }
}