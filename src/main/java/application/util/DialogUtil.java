package application.util;

import java.util.ResourceBundle;

import application.model.Settings;
import application.util.dialog.AccessibleTextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
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
		// this is necessary for accessibility --> nvda only reads the title and OK-Button and ignores ohter elements
		alert.setTitle(bundle.getString(titleMsgStr) + " " + bundle.getString(contextMsgStr));
		alert.setHeaderText(bundle.getString(headerMsgStr));
		alert.setContentText(bundle.getString(contextMsgStr));
		alert.getDialogPane().setFocusTraversable(true);
		styleDialog(alert);
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
		return textInputDialog;
	}

	public void setBundle(final ResourceBundle bundle) {
		this.bundle = bundle;
	}
}
