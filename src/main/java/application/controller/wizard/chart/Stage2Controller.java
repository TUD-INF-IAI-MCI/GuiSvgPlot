package application.controller.wizard.chart;

import application.Wizard.StageController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for second chart stage: CSV.
 * Options: csvpath, csvorientation, csvtype
 */
public class Stage2Controller implements StageController {

    @FXML
    public TextField textField_CsvPath;

    @FXML
    public ChoiceBox<CsvOrientation> choiceBox_CsvOrientation;

    @FXML
    public ChoiceBox<CsvType> choiceBox_CsvType;

    @FXML
    public ChoiceBox<SortingType> choiceBox_Sorting;

    @FXML
    public CheckBox checkbox_SortDesc;


    private SvgPlotOptions svgPlotOptions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.initCsvOrientationItems();
        this.initCsvTypeItems();
        this.initCsvPathField();
        this.initSortingTypeItems();
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

    private void initFields(){
        // TODO: file Name
        this.textField_CsvPath.setText(this.svgPlotOptions.getCsvPath());

        this.choiceBox_CsvOrientation.setValue(this.svgPlotOptions.getCsvOrientation());
        this.choiceBox_CsvType.setValue(this.svgPlotOptions.getCsvType());

        this.choiceBox_Sorting.setValue(this.svgPlotOptions.getSortingType());
        this.checkbox_SortDesc.setSelected(this.svgPlotOptions.isSortDescending());
    }

    /**
     * Initializes the options of the {@link CsvOrientation} choice box .
     */
    private void initCsvOrientationItems() {
        ObservableList<CsvOrientation> csvOrientationObservableList = FXCollections.observableArrayList(CsvOrientation.values());
        this.choiceBox_CsvOrientation.setItems(csvOrientationObservableList);
    }

    /**
     * Initializes the options of the {@link CsvType} choice box .
     */
    private void initCsvTypeItems() {
        ObservableList<CsvType> csvTypeObservableList = FXCollections.observableArrayList(CsvType.values());
        this.choiceBox_CsvType.setItems(csvTypeObservableList);
    }


    /**
     * Initializes the options of the {@link SortingType} choice box .
     */
    private void initSortingTypeItems() {
        ObservableList<SortingType> sortingTypeObservableList = FXCollections.observableArrayList(SortingType.values());
        this.choiceBox_Sorting.setItems(sortingTypeObservableList);
    }

    /**
     * Initializes the text field as file input.
     */
    private void initCsvPathField() {
        this.textField_CsvPath.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            this.textField_CsvPath.setText("");
            if (ev.getCode() != KeyCode.TAB) {
                openFileChooser();
            }
        });
        this.textField_CsvPath.setOnMouseClicked(event -> openFileChooser());
    }

    /**
     * Opens a {@link FileChooser} and saves the path of the selected File in {@link SvgPlotOptions}.
     */
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Resource File");

        File file = fileChooser.showOpenDialog(textField_CsvPath.getScene().getWindow());
        if (file != null) {
            this.textField_CsvPath.setText(file.getName());
            this.svgPlotOptions.setCsvPath(file.getPath());
        }
    }

    /**
     * Initializes {@link ChangeListener} on fields.
     */
    private void initListeners() {
        this.textField_CsvPath.textProperty().addListener((observable, oldValue, newValue) -> {
            svgPlotOptions.setCsvPath(newValue);
        });

        this.choiceBox_CsvOrientation.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CsvOrientation>() {
            @Override
            public void changed(ObservableValue<? extends CsvOrientation> observable, CsvOrientation oldValue, CsvOrientation newValue) {
                svgPlotOptions.setCsvOrientation(newValue);
            }
        });
        this.choiceBox_CsvType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CsvType>() {
            @Override
            public void changed(ObservableValue<? extends CsvType> observable, CsvType oldValue, CsvType newValue) {
                svgPlotOptions.setCsvType(newValue);
            }
        });

        this.choiceBox_Sorting.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SortingType>() {
            @Override
            public void changed(ObservableValue<? extends SortingType> observable, SortingType oldValue, SortingType newValue) {
                svgPlotOptions.setSortingType(newValue);
            }
        });

        this.checkbox_SortDesc.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                svgPlotOptions.setSortDescending(newValue);
            }
        });
    }
}
