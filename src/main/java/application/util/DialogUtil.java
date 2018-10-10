package application.util;

import application.model.Settings;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ResourceBundle;

/**
 * This class provides methods for creating several types of dialogs.
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

    public void styleDialog(final Dialog dialog){
        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(Settings.getInstance().favicon);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/stylesheet/guiSvgPlott.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog");
    }

    public Alert alert(final Alert.AlertType alertType, final String titleMsgStr, final String headerMsgStr, final String contextMsgStr){
        Alert alert = new Alert(alertType);
        alert.setTitle(bundle.getString(titleMsgStr));
        alert.setHeaderText(bundle.getString(headerMsgStr));
        alert.setContentText(bundle.getString(contextMsgStr));
        styleDialog(alert);
        return alert;
    }

    public Alert alertWithTexts(final Alert.AlertType alertType, final String title, final String header, final String context){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        styleDialog(alert);
        return alert;
    }


    public TextInputDialog textInputDialog(final String titleMsgStr, final String headerMsgStr, final String contextMsgStr){
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(bundle.getString(titleMsgStr));
        textInputDialog.setHeaderText(bundle.getString(headerMsgStr));
        textInputDialog.setContentText(bundle.getString(contextMsgStr));
        styleDialog(textInputDialog);
        return textInputDialog;
    }


    public void setBundle(final ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
