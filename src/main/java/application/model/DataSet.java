package application.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tud.tangram.svgplot.options.DiagramType;

import java.util.ArrayList;

public class DataSet {

    private DiagramType type;

    private String name;

    private ObservableList<DataPoint> points;


    public DataSet(DiagramType diagramType, String name) {
        this.type = diagramType;
        this.name = name;
        this.points = FXCollections.observableArrayList();
    }


    public void addPoint(DataPoint point) {
        points.add(point);
    }

    public void removePoint(String key) {
        points.removeIf(item -> item.getKey().equals(key));
    }

    public ObservableList<DataPoint> getAllPoints() {
        return points;
    }


    public String getName() {
        return this.name;
    }
}
