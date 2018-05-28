package application.Wizard;

import javafx.fxml.Initializable;
import tud.tangram.svgplot.options.SvgPlotOptions;

public interface StageController extends Initializable {

    void setSvgPlotOptions(SvgPlotOptions svgPlotOptions);
    SvgPlotOptions getSvgPlotOptions();
}
