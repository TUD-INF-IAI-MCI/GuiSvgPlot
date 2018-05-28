package application.Wizard;

import javafx.fxml.Initializable;
import tud.tangram.svgplot.options.SvgPlotOptions;

public interface SVGWizardController extends Initializable {

    int getCurrentStage();
    void setCurrentStage(int currentStage);

    String getFXMLLocation();
    String getTitle();
}
