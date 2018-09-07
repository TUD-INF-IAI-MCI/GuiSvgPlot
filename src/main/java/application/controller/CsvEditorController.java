package application.controller;

import application.controller.wizard.SVGWizardController;
import application.controller.wizard.functions.FunctionWizardFrameController;
import application.model.GuiSvgOptions;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.plotting.Function;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class CsvEditorController implements Initializable {


    @FXML
    private TableView<Function> tableView_Functions;
    @FXML
    private TableColumn<Function, String> tableColumn_Function;
    @FXML
    private TableColumn<Function, Button> tableColumn_FunctionDelete;
    @FXML
    private TableColumn<Function, Button> tableColumn_FunctionVisible;

    @FXML
    private TableView<AbstractMap.SimpleEntry<Double, Double>> tableView_Coords;
    @FXML
    private TableColumn<AbstractMap.SimpleEntry<Double, Double>, Double> tableColumn_X;
    @FXML
    private TableColumn<AbstractMap.SimpleEntry<Double, Double>, Double> tableColumn_Y;
    @FXML
    private TableColumn<AbstractMap.SimpleEntry<Double, Double>, Button> tableColumn_Delte;
    @FXML
    private TableColumn<AbstractMap.SimpleEntry<Double, Double>, Button> tableColumn_Visible;

    @FXML
    private ListView<String> listView_Points;

    @FXML
    private AnchorPane ap_Container;

    @FXML
    public Button button_Cancel;
    @FXML
    private Button button_Accept;

    @FXML
    private Button button_add;

    // for functions
    private ObservableList<Function> functions;
    private HashMap<String, String> points;

    private SimpleStringProperty path;
    private GuiSvgOptions options;
    private File tempFile;
    private ResourceBundle bundle;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
    }

    public void init(HashMap<DiagramType, File> fileMap, String csvPath, GuiSvgOptions guiSvgOptions, SVGWizardController svgWizardController) throws IOException {


        if (guiSvgOptions.getDiagramType().equals(DiagramType.FunctionPlot))
            functions = ((FunctionWizardFrameController) svgWizardController).getFunctionList();


        this.path = new SimpleStringProperty(csvPath);
        this.options = guiSvgOptions;

        this.tempFile = fileMap.get(guiSvgOptions.getDiagramType());

        if (tempFile == null) {
            tempFile = File.createTempFile("temp_" + guiSvgOptions.getDiagramType(), ".csv");
            fileMap.put(guiSvgOptions.getDiagramType(), tempFile);
        }

        determineInterface();
        button_Accept.setOnAction(button_Cancel.getOnAction());

    }

    private void determineInterface() {

        switch (options.getDiagramType()) {
            case FunctionPlot: {
                initFunctions();
                break;
            }
            case LineChart: {
                initCharts();
                break;
            }
            case BarChart: {
                initCharts();
                break;
            }
            case ScatterPlot: {
                initCharts();
                break;
            }

        }


    }

    private void initCharts() {


        ap_Container.getChildren().remove(tableView_Functions);
        if (!ap_Container.getChildren().contains(tableView_Coords)) {
            ap_Container.getChildren().add(tableView_Coords);
        }

        tableColumn_X.setCellValueFactory(dbl -> new SimpleObjectProperty<>(dbl.getValue().getKey()));


    }


    private void initFunctions() {


        parseFunctions();

        ap_Container.getChildren().remove(tableView_Coords);
        if (!ap_Container.getChildren().contains(tableView_Functions)) {
            ap_Container.getChildren().add(tableView_Functions);
        }

        tableColumn_Function.setCellValueFactory(func -> new SimpleStringProperty(func.getValue().getFunction()));


        tableColumn_FunctionDelete.setCellValueFactory(btn -> {
            Button b = new Button();

            b.setOnAction(event ->
                    functions.remove(btn.getValue()));

            return new SimpleObjectProperty<>(b);
        });

        tableColumn_Function.setCellFactory(cell -> new TableCell<Function, String>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);

                    return;
                }
                setAlignment(Pos.CENTER_LEFT);
                setText(item);
            }
        });


        tableColumn_FunctionDelete.setCellFactory(cell -> new TableCell<Function, Button>() {

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);

                    return;
                }
                item.setText("-");
                setAlignment(Pos.CENTER);
                setGraphic(item);
            }
        });

        tableView_Functions.setItems(functions);


        button_add.setOnAction(event -> {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setContentText(bundle.getString("function_name") + ":");
            dialog.setHeaderText(bundle.getString("function_add"));
            dialog.showAndWait();
            if (dialog.getResult() == null || dialog.getResult().trim().isEmpty())
                return;
//            System.out.println(dialog.getResult());
            functions.add(new Function("", dialog.getResult()));

        });


    }

    private void parseFunctions() {

        if (path != null && path.get() != null)
            try {
                Files.readAllLines(Paths.get(path.get())).forEach(line -> {


                });
            } catch (IOException e) {
            }


    }

    private void initTable() {


    }


}
