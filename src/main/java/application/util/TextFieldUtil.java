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

    /**
     *  Logs an error and adds the style class "invalid" to given {@link javafx.scene.control.Label} and {@link TextField} if the value of of the given {@link TextField}s are equal.
     * @param textField1 the first {@link TextField}
     * @param textField2 the second {@link TextField}
     */
    public void addNotEqualValidation(final TextField textField1, final Label label1, final TextField textField2, final Label label2){
        textField1.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTextFields(textField1, label1, textField2, label2);
        });
        textField2.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTextFields(textField1, label1, textField2, label2);
        });
    }
    private void validateTextFields(final TextField textField1, final Label label1, final TextField textField2, final Label label2){
        String errorMessageString = this.bundle.getString("error_fieldsMustNotBeEqual").replace("{0}", label1.getText()).replace("{1}", label2.getText());
        if (textField1.getText().equals(textField2.getText())){
            label1.getStyleClass().add("invalid");
            label2.getStyleClass().add("invalid");
            textField1.getStyleClass().add("invalid");
            textField2.getStyleClass().add("invalid");
            logger.error(errorMessageString);
        }else{
            VBox messages = GuiSvgPlott.getInstance().getRootFrameController().vBox_messages;
            Label errorMessageLabel = new Label();
            for (Node child : messages.getChildren()) {
                Label errorMessage = (Label) child;
                if (errorMessage.getText().contains(errorMessageString)) {
                    errorMessageLabel = errorMessage;
                }
            }
            messages.getChildren().remove(errorMessageLabel);
            label1.getStyleClass().remove("invalid");
            label2.getStyleClass().remove("invalid");
            textField1.getStyleClass().remove("invalid");
            textField2.getStyleClass().remove("invalid");
        }
    }
    /**
     * Logs an error and adds the style class "invalid" to given {@link javafx.scene.control.Label} and {@link TextField} if the value of textField is less then the given minimum.
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param label the {@link javafx.scene.control.Label}
     * @param minimum the minimum value.
     */
    public void addMinimumIntegerValidation(final TextField textField, final Label label, final int minimum) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue) {
                String errorMessageString = label.getText() + ": " + bundle.getString("error_minimal_number_exception").replace("{0}", "" + minimum);
                if (Integer.parseInt(textField.getText()) < minimum) {
                    label.getStyleClass().add("invalid");
                    textField.getStyleClass().add("invalid");
                    logger.error(errorMessageString);
                } else {
                    VBox messages = GuiSvgPlott.getInstance().getRootFrameController().vBox_messages;
                    Label errorMessageLabel = new Label();
                    for (Node child : messages.getChildren()) {
                        Label errorMessage = (Label) child;
                        if (errorMessage.getText().contains(errorMessageString)) {
                            errorMessageLabel = errorMessage;
                        }
                    }
                    messages.getChildren().remove(errorMessageLabel);
                    textField.getStyleClass().remove("invalid");
                    label.getStyleClass().remove("invalid");
                }
            }
        });
    }

    /**
     * Checks if the value of the {@link TextField} is an integer greater then given minimum.
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param minimum the minimum value.
     */
    public void addIntegerValidationWithMinimum(final TextField textField, final int minimum) {
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

    /**
     * Checks if the value of the {@link TextField} is an integer.
     * @param textField the {@link TextField},whose entry is to be validated.
     */
    public void addIntegerValidation(final TextField textField) {
        this.addNotEmptyValidationListener(textField, 0);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Checks if the value of the {@link TextField} is a double.
     * If it is no double, it logs an error and adds the style class "invalid" to given {@link javafx.scene.control.Label} and {@link TextField}.
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param label the label
     */
    public void addDoubleValidation(final TextField textField, final Label label) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.equals("-")) {
                    Double.parseDouble(newValue);
                }
                if (oldValue.equals("-")) {
                    VBox messages = GuiSvgPlott.getInstance().getRootFrameController().vBox_messages;
                    Label errorMessageLabel = new Label();
                    for (Node child : messages.getChildren()) {
                        Label errorMessage = (Label) child;
                        if (errorMessage.getText().contains(bundle.getString("error_numberFormatException"))) {
                            errorMessageLabel = errorMessage;
                        }
                    }
                    if (!errorMessageLabel.getText().isEmpty() || textField.getStyleClass().contains("invalid")) {
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
            if (!newValue && textField.getText().equals("-")) {
                label.getStyleClass().add("invalid");
                textField.getStyleClass().add("invalid");
                logger.error(label.getText() + ": " + bundle.getString("error_numberFormatException"));
            }
        });
    }

    /**
     * If {@link TextField} is empty, it sets the given defaultValue to the TextField.
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param defaultVal the defaultVal
     */
    public void addNotEmptyValidationListener(final TextField textField, final Object defaultVal) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
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
