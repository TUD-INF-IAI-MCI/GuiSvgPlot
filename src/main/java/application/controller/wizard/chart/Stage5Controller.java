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
 * Options: hgrid, vgrid, xlines, ylines, doubleaxes, pointsborderless
 * TODO: validate text input
 */
public class Stage5Controller implements StageController {

    @FXML
    public CheckBox checkbox_hgrid;
    @FXML
    public CheckBox checkbox_vgrid;
    @FXML
    public TextField textField_xlines;
    @FXML
    public TextField textField_ylines;
    @FXML
    public CheckBox checkbox_dblaxes;
    @FXML
    public CheckBox checkbox_pointsborderless;


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
        String showHorizontalGrid = this.svgPlotOptions.getShowHorizontalGrid();
        this.checkbox_hgrid.setSelected(showHorizontalGrid != null && showHorizontalGrid.equals("on"));

        String showVerticalGrid = this.svgPlotOptions.getShowVerticalGrid();
        this.checkbox_vgrid.setSelected(showVerticalGrid != null && showVerticalGrid.equals("on"));

        this.textField_xlines.setText(this.svgPlotOptions.getxLines());
        this.textField_ylines.setText(this.svgPlotOptions.getyLines());

        String showDoubleAxes = this.svgPlotOptions.getShowDoubleAxes();
        this.checkbox_dblaxes.setSelected(showDoubleAxes != null && showDoubleAxes.equals("on"));

        this.checkbox_pointsborderless.setSelected(this.svgPlotOptions.isPointsBorderless());
    }

    /**
     * Initializes {@link ChangeListener} on fields.
     */
    private void initListeners() {
        this.checkbox_hgrid.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    svgPlotOptions.setShowHorizontalGrid("on");
                } else {
                    svgPlotOptions.setShowHorizontalGrid("off");
                }
            }
        });

        this.checkbox_vgrid.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    svgPlotOptions.setShowVerticalGrid("on");
                } else {
                    svgPlotOptions.setShowVerticalGrid("off");
                }
            }
        });

        this.textField_xlines.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setxLines(newValue);
        });
        this.textField_ylines.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setyLines(newValue);
        });

        this.checkbox_dblaxes.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    svgPlotOptions.setShowDoubleAxes("on");
                } else {
                    svgPlotOptions.setShowDoubleAxes("off");
                }
            }
        });

        this.checkbox_pointsborderless.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setPointsBorderless(newValue);
            }
        });

    }


}
