package application.controller.wizard.chart;

import application.Wizard.StageController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for third chart stage.
 * Options: autoscale, xRange, yRange
 * TODO: validate text input
 */
public class Stage4Controller implements StageController {

    @FXML
    public CheckBox checkbox_Autoscale;
    @FXML
    public TextField textField_xfrom;
    @FXML
    public TextField textField_xTo;
    @FXML
    public TextField textField_yfrom;
    @FXML
    public TextField textField_yto;

    private Range xRange;
    private Range yRange;

    private SvgPlotOptions svgPlotOptions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public SvgPlotOptions getSvgPlotOptions() {
        return svgPlotOptions;
    }

    @Override
    public void setSvgPlotOptions(SvgPlotOptions svgPlotOptions) {
        this.svgPlotOptions = svgPlotOptions;
        this.initFields();
        this.initListeners();
    }

    private void initFields() {
        this.xRange = this.svgPlotOptions.getxRange();
        this.yRange = this.svgPlotOptions.getyRange();

        if (this.xRange != null) {
            this.textField_xfrom.setText("" + this.xRange.getFrom());
            this.textField_xTo.setText("" + this.xRange.getTo());
        }else{
            this.xRange = new Range(-8, 8);
        }

        if (this.yRange != null) {
            this.textField_yfrom.setText("" + this.yRange.getFrom());
            this.textField_yto.setText("" + this.yRange.getTo());
        }else{
            this.yRange = new Range(-8, 8);
        }

        this.checkbox_Autoscale.setSelected(this.svgPlotOptions.hasAutoScale());
    }

    /**
     * Initializes {@link ChangeListener} on fields.
     */
    private void initListeners() {
        this.checkbox_Autoscale.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setAutoScale(newValue);
            }
        });

        this.textField_xfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            xRange.setFrom(Double.parseDouble(newValue));
            svgPlotOptions.setxRange(xRange);
        });
        this.textField_xTo.textProperty().addListener((observable, oldValue, newValue) -> {
            xRange.setTo(Double.parseDouble(newValue));
            svgPlotOptions.setxRange(xRange);
        });

        this.textField_yfrom.textProperty().addListener((observable, oldValue, newValue) -> {
            yRange.setFrom(Double.parseDouble(newValue));
            svgPlotOptions.setxRange(yRange);
        });
        this.textField_yto.textProperty().addListener((observable, oldValue, newValue) -> {
            yRange.setTo(Double.parseDouble(newValue));
            svgPlotOptions.setxRange(yRange);
        });

    }


}
