package application.controller.wizard.chart;

import application.Wizard.StageController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.styles.Color;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for third chart stage.
 * Options: size, css, colors
 * TODO: direct css command, more colors, validate input size inputs
 */
public class Stage6Controller implements StageController {

    @FXML
    public TextField textField_cssFile;
    @FXML
    public TextField textField_sizeWidth;
    @FXML
    public TextField textField_sizeHeight;
    @FXML
    public ChoiceBox<Color> choiceBox_color1;
    @FXML
    public ChoiceBox<Color> choiceBox_color2;

    private List<String> colors;
    private Point size;
    private SvgPlotOptions svgPlotOptions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initColorItems();
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
        this.colors = new ArrayList<>();
        this.size = this.svgPlotOptions.getSize();

        this.textField_sizeWidth.setText(this.svgPlotOptions.getSize().x());
        this.textField_sizeHeight.setText(this.svgPlotOptions.getSize().y());

        this.textField_cssFile.setText(this.svgPlotOptions.getCss());

        if (this.svgPlotOptions.getCustomColors().size() == 2) {
            Color color1 = Color.fromString(this.svgPlotOptions.getCustomColors().get(0));
            this.colors.add(color1.getName());
            this.choiceBox_color1.setValue(color1);

            Color color2 = Color.fromString(this.svgPlotOptions.getCustomColors().get(1));
            this.colors.add(color2.getName());
            this.choiceBox_color2.setValue(color2);
        }
    }

    /**
     * Initializes {@link ChangeListener} on fields.
     */
    private void initListeners() {

        this.textField_cssFile.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setCss(newValue);
        });

        this.textField_sizeWidth.textProperty().addListener((observable, oldValue, newValue) -> {
            size.setX(Double.parseDouble(newValue));
            svgPlotOptions.setSize(size);
        });

        this.textField_sizeHeight.textProperty().addListener((observable, oldValue, newValue) -> {
            size.setY(Double.parseDouble(newValue));
            svgPlotOptions.setSize(size);
        });

        this.choiceBox_color1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                colors.add(newValue.getName());
                svgPlotOptions.setCustomColors(colors);
            }
        });

        this.choiceBox_color2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                colors.add(newValue.getName());
                svgPlotOptions.setCustomColors(colors);
            }
        });
    }

    /**
     * Initializes the options of the {@link tud.tangram.svgplot.styles.Color} choice box .
     */
    private void initColorItems() {
        ObservableList<Color> sortingTypeObservableList = FXCollections.observableArrayList(Color.values());
        this.choiceBox_color1.setItems(sortingTypeObservableList);
        this.choiceBox_color2.setItems(sortingTypeObservableList);
    }
    /**
     * Initializes the text field as file input.
     */
    private void initCssPathField() {
        this.textField_cssFile.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            this.textField_cssFile.setText("");
            if (ev.getCode() != KeyCode.TAB) {
                openFileChooser();
            }
        });
        this.textField_cssFile.setOnMouseClicked(event -> openFileChooser());
    }

    /**
     * Opens a {@link FileChooser} and saves the path of the selected File in {@link SvgPlotOptions}.
     */
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSS files (*.css)", "*.css");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Resource File");

        File file = fileChooser.showOpenDialog(textField_cssFile.getScene().getWindow());
        if (file != null) {
            this.textField_cssFile.setText(file.getName());
            //TODO: css auslesen und speichern this.svgPlotOptions.setCss(file.)
        }
    }

}
