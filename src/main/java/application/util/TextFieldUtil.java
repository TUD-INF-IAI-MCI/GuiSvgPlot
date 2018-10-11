package application.util;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import application.service.SvgOptionsService;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.Label;

import java.awt.*;
import java.util.ResourceBundle;

/**
 * @author Emma Müller
 */
public class TextFieldUtil {
    private static final Logger logger = LoggerFactory.getLogger(TextFieldUtil.class);
    private static final TextFieldUtil INSTANCE = new TextFieldUtil();
    private SvgOptionsService svgOptionsService = SvgOptionsService.getInstance();
    private ResourceBundle bundle;

    private TextFieldUtil() {
        this.bundle = GuiSvgPlott.getInstance().getBundle();
    }

    public static TextFieldUtil getInstance() {
        return INSTANCE;
    }

    /**
     * Logs an error and adds the style class "invalid" to given {@link javafx.scene.control.Label} and {@link TextField} if the value of of the given {@link TextField}s are equal.
     *
     * @param textField1 the first {@link TextField}
     * @param textField2 the second {@link TextField}
     */
    public void addNotEqualValidation(final TextField textField1, final Label label1, final TextField textField2, final Label label2) {
        textField1.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTextFields(textField1, label1, textField2, label2);
        });
        textField2.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTextFields(textField1, label1, textField2, label2);
        });
        textField1.focusedProperty().addListener((observable, oldValue, newValue) -> {
            validateTextFields(textField1, label1, textField2, label2);
        });
        textField2.focusedProperty().addListener((observable, oldValue, newValue) -> {
            validateTextFields(textField1, label1, textField2, label2);
        });
    }

    private void validateTextFields(final TextField textField1, final Label label1, final TextField textField2, final Label label2) {

        String errorMessageString = this.bundle.getString("error_fieldsMustNotBeEqual").replace("{0}", label1.getText()).replace("{1}", label2.getText());

        if (textField1.getText().equals(textField2.getText())) {
            setTextFieldInvalid(textField1, label1, errorMessageString);
            setTextFieldInvalid(textField2, label2, errorMessageString);
        } else {
            setTextFieldValid(textField1, label1, errorMessageString);
            setTextFieldValid(textField2, label2, errorMessageString);
        }
    }

    private void validateGTH(final TextField textField1, final Label label1, final TextField textField2, final Label label2, final String message_key) {
        Double val1 = null;
        Double val2 = null;
        String message = message_key;

        try {
            val1 = Double.parseDouble(textField1.getText());
            val2 = Double.parseDouble(textField2.getText());
            message = "error_fieldsMustNotBeEmpty";
        } catch (NumberFormatException e) {

        }
        String errorMessageString = bundle.getString(message).replace("{0}", label1.getText()).replace("{1}", label2.getText());

        if (val1 == null || val2 == null) {
            setTextFieldInvalid(textField1, label1, errorMessageString);
            setTextFieldInvalid(textField2, label2, errorMessageString);

        } else if (val1 > val2) {
            setTextFieldInvalid(textField1, label1, errorMessageString);
            setTextFieldInvalid(textField2, label2, errorMessageString);
        } else {
            setTextFieldValid(textField1, label1, errorMessageString);
            setTextFieldValid(textField2, label2, errorMessageString);
        }
    }

    private void setTextFieldInvalid(final TextField textField, final Label label, final String errorMsgString) {
        label.getStyleClass().add("invalid");
        textField.getStyleClass().add("invalid");
        String textField1AccessibleHelp = textField.getAccessibleHelp() == null ? "" : textField.getAccessibleHelp();
        if (!textField1AccessibleHelp.contains(errorMsgString)) {
            textField.setAccessibleHelp(textField1AccessibleHelp + " " + errorMsgString);
        }
    }

    private void setTextFieldValid(final TextField textField, final Label label, final String errorMsgString) {
        label.getStyleClass().remove("invalid");
        textField.getStyleClass().remove("invalid");
        String textField1AccessibleHelp = textField.getAccessibleHelp() == null ? "" : textField.getAccessibleHelp();
        textField.setAccessibleHelp(textField1AccessibleHelp.split(errorMsgString)[0]);
    }

    /**
     * Logs an error and adds the style class "invalid" to given {@link javafx.scene.control.Label} and {@link TextField} if the value of textField is less then the given minimum.
     *
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param label     the {@link javafx.scene.control.Label}
     * @param minimum   the minimum value.
     */
    public void addMinimumIntegerValidation(final TextField textField, final Label label, final int minimum) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            if (!newValue) {
                String errorMessageString = bundle.getString("error_minimal_number_exception").replace("{0}", "" + minimum);
                String accessibleHelp = textField.getAccessibleHelp() == null ? "" : textField.getAccessibleHelp();
                if (Integer.parseInt(textField.getText()) < minimum) {
                    label.getStyleClass().add("invalid");
                    textField.getStyleClass().add("invalid");
                    if (!accessibleHelp.contains(errorMessageString)) {
                        textField.setAccessibleHelp(accessibleHelp + errorMessageString);
                    }
                } else {
                    textField.setAccessibleHelp(accessibleHelp.split(errorMessageString)[0]);
                    textField.getStyleClass().remove("invalid");
                    label.getStyleClass().remove("invalid");
                }
            }
        });
    }

    /**
     * Checks if the value of the {@link TextField} is an integer greater then given minimum.
     *
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param minimum   the minimum value.
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
     *
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
     *
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param label     the label
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
     * Checks if the value of the {@link TextField} is an double greater or equal then given minimum and less or equal the given maximum.
     *
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param label     the label
     * @param minimum   the minimum value.
     * @param maximum   the maximum value.
     */
    public void addDoubleValidationWithMinimumAndMaximum(final TextField textField, final Label label, final int minimum, final int maximum) {
        this.addNotEmptyValidationListener(textField, minimum);
        this.addDoubleValidation(textField, label);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double number = Double.parseDouble(newValue);
                if (number < minimum || number > maximum) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                textField.setText(oldValue);
            }

        });
    }

    /**
     * Checks if the value of the {@link TextField} is an double greater or equal then given minimum.
     *
     * @param textField the {@link TextField},whose entry is to be validated.
     * @param label     the label
     * @param minimum   the minimum value.
     */

    public void addDoubleValidationWithMinimum(final TextField textField, final Label label, final int minimum) {
        this.addNotEmptyValidationListener(textField, minimum);
        this.addDoubleValidation(textField, label);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double number = Double.parseDouble(newValue);
                if (number < minimum) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                textField.setText(oldValue);
            }

        });
    }

    /**
     * If {@link TextField} is empty, it sets the given defaultValue to the TextField.
     *
     * @param textField  the {@link TextField},whose entry is to be validated.
     * @param defaultVal the defaultVal
     */
    public void addNotEmptyValidationListener(final TextField textField, final Object defaultVal) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                textField.setText(defaultVal.toString());
            }
        });
    }

    public void addFirstNotGreaterThanSecondValidationListener(final TextField textField1, final Label label1, final TextField textField2, final Label label2) {

        textField1.focusedProperty().addListener((observable, oldValue, newValue) -> {
            this.validateGTH(textField1, label1, textField2, label2, "error_field1_must_be_less_than_field2");
        });
        textField2.focusedProperty().addListener((observable, oldValue, newValue) -> {
            this.validateGTH(textField1, label1, textField2, label2, "error_field1_must_be_less_than_field2");
        });
    }


    /**
     * Adds a change listener to each given {@link TextField}'s focusedProperty that updates the preview when user left the field.
     *
     * @param webView_svg   the {@link WebView} for the preview
     * @param guiSvgOptions the {@link GuiSvgOptions}, needed for the preview rendering
     * @param textFields    all {@link TextField}s
     */
    public void addReloadPreviewOnChangeListener(final WebView webView_svg, final GuiSvgOptions guiSvgOptions, final TextField... textFields) {
        for (TextField textField : textFields) {
            textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    this.svgOptionsService.buildPreviewSVG(guiSvgOptions, webView_svg);
                }
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
