package application.controller.wizard.chart;

import application.Wizard.StageController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.styles.BarAccumulationStyle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for third chart stage.
 * Options: xunit, yunit, trendline/baraccumulation/linepoints
 */
public class Stage3Controller implements StageController {

    @FXML
    public TextField textField_xunit;
    @FXML
    public TextField textField_yunit;
    @FXML
    public Label label_baraccumulation;
    @FXML
    public ChoiceBox<BarAccumulationStyle> choiceBox_baraccumulation;
    @FXML
    public Label label_linepoints;
    @FXML
    public CheckBox checkbox_linepoints;


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
        this.initBaraccumulationItems();
        this.initLinePoints();
        this.initListeners();
    }

    private void initFields() {
        this.textField_xunit.setText(this.svgPlotOptions.getxUnit());
        this.textField_yunit.setText(this.svgPlotOptions.getyUnit());
        DiagramType diagramType = this.svgPlotOptions.getDiagramType();
        if (diagramType != null) {
            switch (diagramType) {
                case BarChart:
                    setVisible(this.label_baraccumulation, this.choiceBox_baraccumulation);
                    break;
                case LineChart:
                    setVisible(this.label_linepoints, this.checkbox_linepoints);
                    break;
                default:
                    break;
            }
        } else {
            setVisible(this.label_baraccumulation, this.choiceBox_baraccumulation);
            setVisible(this.label_linepoints, this.checkbox_linepoints);
        }
    }

    private void initBaraccumulationItems() {
        ObservableList<BarAccumulationStyle> csvOrientationObservableList = FXCollections.observableArrayList(BarAccumulationStyle.values());
        this.choiceBox_baraccumulation.setItems(csvOrientationObservableList);
        this.choiceBox_baraccumulation.setValue(this.svgPlotOptions.getBarAccumulationStyle());
    }

    private void initLinePoints() {
        System.out.println(this.svgPlotOptions.getShowLinePoints());
        String showLinePoints = this.svgPlotOptions.getShowLinePoints();
        this.checkbox_linepoints.setSelected(showLinePoints != null && showLinePoints.equals("on"));
    }

    private void initListeners() {
        this.textField_yunit.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setyUnit(newValue);
        });
        this.textField_xunit.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setxUnit(newValue);
        });

        this.choiceBox_baraccumulation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BarAccumulationStyle>() {
            @Override
            public void changed(ObservableValue<? extends BarAccumulationStyle> observable, BarAccumulationStyle oldValue, BarAccumulationStyle newValue) {
                svgPlotOptions.setBarAccumulationStyle(newValue);
            }
        });

        this.checkbox_linepoints.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    svgPlotOptions.setShowLinePoints("on");
                } else {
                    svgPlotOptions.setShowLinePoints("off");
                }
            }
        });

    }

    private void setVisible(Label label, Node field) {
        label.setVisible(true);
        field.setVisible(true);
    }

}
