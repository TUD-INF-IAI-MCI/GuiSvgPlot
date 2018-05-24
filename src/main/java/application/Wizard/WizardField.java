package application.Wizard;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

public class WizardField {

    private Initializable controller;

    private FXMLLoader fxmlLoader;

    private Object svgPlotOption;

    public WizardField(Initializable controller, FXMLLoader fxmlLoader, Object svgPlotOption) {
        this.controller = controller;
        this.fxmlLoader = fxmlLoader;
        this.svgPlotOption = svgPlotOption;

        this.fxmlLoader.setController(this.controller);
    }

    public Initializable getController() {
        return controller;
    }

    public void setController(Initializable controller) {
        this.controller = controller;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public void setFxmlLoader(FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    public Object getSvgPlotOption() {
        return svgPlotOption;
    }

    public void setSvgPlotOption(Object svgPlotOption) {
        this.svgPlotOption = svgPlotOption;
    }
}
