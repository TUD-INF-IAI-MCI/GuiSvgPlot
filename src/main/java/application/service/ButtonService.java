package application.service;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ButtonService {

    private static final ButtonService INSTANCE = new ButtonService();

    private ButtonService(){}

    public static ButtonService getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an EventHandler to the given button that fires the ActionEvent by clicking Enter.
     * @param button the Button
     */
    public void addEnterEventHandler(Button button){
        if(button != null) {
            button.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
                if (ev.getCode() == KeyCode.ENTER) {
                    button.fire();
                    ev.consume();
                }
            });
        }
    }
}
