package application.util;

import java.util.Optional;
import java.util.ResourceBundle;

import application.model.Settings;
import application.util.dialog.AccessibleTextInputDialog;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * This class provides methods for creating several types of dialogs.
 * 
 * @author Emma Müller
 */
public class DialogUtil {
	private static final DialogUtil INSTANCE = new DialogUtil();
	private ResourceBundle bundle;

	private DialogUtil() {
	}

	public static DialogUtil getInstance() {
		return INSTANCE;
	}

	public void styleDialog(final Dialog<?> dialog) {
		Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
		alertStage.getIcons().add(Settings.getInstance().favicon);
		dialog.getDialogPane().getStylesheets()
				.add(getClass().getResource("/stylesheet/guiSvgPlott.css").toExternalForm());
		dialog.getDialogPane().getStyleClass().add("dialog");
	}

	public Alert alert(final Alert.AlertType alertType, final String titleMsgStr, final String headerMsgStr,
			final String contextMsgStr) {
		Alert alert = new Alert(alertType);
		alert.setTitle(bundle.getString(titleMsgStr));
		alert.setHeaderText(bundle.getString(headerMsgStr));
		alert.setContentText(bundle.getString(contextMsgStr));
		styleDialog(alert);
		setAccessibility(alert);
		return alert;
	}

	public Alert alertWithTexts(final Alert.AlertType alertType, final String title, final String header,
			final String context) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.getDialogPane().setAccessibleHelp(context);
		styleDialog(alert);
		setAccessibility(alert);
		return alert;
	}

	public AccessibleTextInputDialog textInputDialog(final String titleMsgStr, final String headerMsgStr,
													 final String contextMsgStr) {
		AccessibleTextInputDialog textInputDialog = new AccessibleTextInputDialog();
		textInputDialog.setTitle(bundle.getString(titleMsgStr));
		textInputDialog.setHeaderText(bundle.getString(headerMsgStr));
		textInputDialog.setContentText(bundle.getString(contextMsgStr));
		textInputDialog.getTextField().setAccessibleHelp(bundle.getString(contextMsgStr));
		styleDialog(textInputDialog);
		setAccessibility(textInputDialog);
		return textInputDialog;
	}

	public void setBundle(final ResourceBundle bundle) {
		this.bundle = bundle;
	}

    /**
     * Sets AccessibleText of given {@link Dialog}'s content label and focuses it on showing.
     * @param dialog the {@link Dialog}
     */
	public void setAccessibility(final Dialog dialog){
		dialog.showingProperty().addListener((observable, oldValue, newValue) -> {
			dialog.getDialogPane().getChildren().forEach(node -> {
				if (node instanceof Label) {
					node.setFocusTraversable(true);
					node.setAccessibleText(dialog.getHeaderText() + " " + dialog.getContentText());
					node.requestFocus();
				}
			});
		});
	}
}
