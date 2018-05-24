package application.Wizard;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.util.List;
import java.util.Map;

public class WizardStage {

    private String title;

    private String description;

    private List<WizardField> inputFields;

    public WizardStage(String title, String description, List<WizardField> inputFields) {
        this.title = title;
        this.description = description;
        this.inputFields = inputFields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<WizardField> getInputFields() {
        return inputFields;
    }

    public void setInputFields(List<WizardField> inputFields) {
        this.inputFields = inputFields;
    }
}
