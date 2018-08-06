package application.util;

import application.GuiSvgPlott;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.Label;

import java.util.ResourceBundle;

public class TextFieldUtil {
    private static final Logger logger = LoggerFactory.getLogger(TextFieldUtil.class);
    private static final TextFieldUtil INSTANCE = new TextFieldUtil();
    private ResourceBundle bundle;

    private TextFieldUtil() {
    }

    public static TextFieldUtil getInstance() {
        return INSTANCE;
    }


    public void addIntegerValidationWithMinimum(final TextField textField, final int minimum){
        this.addNotEmptyValidationListener(textField, minimum);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int integer = Integer.parseInt(newValue);
                if (integer < minimum) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                textField.setText(oldValue);
            }
        });
    }

    public void addIntegerValidation(final TextField textField){
        this.addNotEmptyValidationListener(textField, 0);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                textField.setText(oldValue);
            }
        });
    }

    public void addDoubleValidation(final TextField textField, final Label label){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if(!newValue.equals("-")){
                    Double.parseDouble(newValue);
                }
                if(oldValue.equals("-")){
                    VBox messages = GuiSvgPlott.getInstance().getRootFrameController().vBox_messages;
                    Label errorMessageLabel = new Label();
                    for (Node child : messages.getChildren()) {
                        Label errorMessage = (Label) child;
                        if (errorMessage.getText().contains( bundle.getString("error_numberFormatException"))){
                            errorMessageLabel = errorMessage;
                        }
                    }
                    if (!errorMessageLabel.getText().isEmpty() || textField.getStyleClass().contains("invalid")){
                        messages.getChildren().remove(errorMessageLabel);
                        textField.getStyleClass().remove("invalid");
                        label.getStyleClass().remove("invalid");
                    }
                }
            } catch (NumberFormatException e) {
                textField.setText(oldValue);
            }
        });
        textField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if(!newValue && textField.getText().equals("-"))
            {
                label.getStyleClass().add("invalid");
                textField.getStyleClass().add("invalid");
                logger.error(label.getText() + ": " + bundle.getString("error_numberFormatException"));
            }
        });
    }

    public void addNotEmptyValidationListener(final TextField textField, final Object defaultVal){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()){
                textField.setText(defaultVal.toString());
            }
        });
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
