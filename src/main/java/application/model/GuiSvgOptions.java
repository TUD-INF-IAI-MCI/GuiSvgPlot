package application.model;

import application.GuiSvgPlott;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.Gnuplot;
import tud.tangram.svgplot.styles.BarAccumulationStyle;

import java.util.ArrayList;

public class GuiSvgOptions {


    private SvgPlotOptions options;


    private ObjectProperty<CsvOrientation> csvOrientation;
    private ObjectProperty<OutputDevice> outputDevice;
    private ObjectProperty<Point> size;
    private ObjectProperty<BarAccumulationStyle> barAccumulationStyle;
    private ObjectProperty<SortingType> sortingType;
    private ObjectProperty<CsvType> csvType;

    private BooleanProperty pi;
    private BooleanProperty pointsBorderless;
    private BooleanProperty hideOriginalPoints;
    private BooleanProperty sortDescending;

    private ObservableList<Function> functions;
    private ObservableList<String> colors;
    private ObservableList<String> trendLinie;

    private final StringProperty xLines;
    private final StringProperty yLines;
    private final StringProperty title;
    private final StringProperty gnuplot;
    private final StringProperty css;
    private final StringProperty output;
    private final StringProperty csvPath;


    public GuiSvgOptions(SvgPlotOptions options) {

        this.options = options;

        this.outputDevice = new SimpleObjectProperty<>(options.getOutputDevice());
        this.csvOrientation = new SimpleObjectProperty<>(options.getCsvOrientation());
        this.barAccumulationStyle = new SimpleObjectProperty<>(options.getBarAccumulationStyle());
        this.size = new SimpleObjectProperty<>(options.getSize());
        this.csvType = new SimpleObjectProperty<>(options.getCsvType());
        this.sortingType = new SimpleObjectProperty<>(options.getSortingType());


        this.xLines = new SimpleStringProperty(options.getxLines());
        this.yLines = new SimpleStringProperty(options.getyLines());
        this.title = new SimpleStringProperty(options.getTitle());
        this.gnuplot = new SimpleStringProperty(GuiSvgPlott.getInstance().getGnuPlot());
        this.css = new SimpleStringProperty(options.getCss());
        this.output = new SimpleStringProperty(options.getOutput() != null ? options.getOutput().getAbsolutePath() : "");
        this.csvPath = new SimpleStringProperty(options.getCsvPath());


        this.pointsBorderless = new SimpleBooleanProperty(options.isPointsBorderless());
        this.pi = new SimpleBooleanProperty(options.isPi());
        this.hideOriginalPoints = new SimpleBooleanProperty(options.isHideOriginalPoints());
        this.sortDescending = new SimpleBooleanProperty(options.isSortDescending());


        this.functions = FXCollections.observableArrayList();
        this.colors = FXCollections.observableArrayList();
        this.trendLinie = FXCollections.observableArrayList();

    }


    public SvgPlotOptions getOptions() {
        return options;
    }

    public void setOptions(SvgPlotOptions options) {
        this.options = options;
    }

    public CsvOrientation getCsvOrientation() {
        return csvOrientation.get();
    }

    public ObjectProperty<CsvOrientation> csvOrientationProperty() {
        return csvOrientation;
    }

    public void setCsvOrientation(CsvOrientation csvOrientation) {
        this.csvOrientation.set(csvOrientation);
    }

    public OutputDevice getOutputDevice() {
        return outputDevice.get();
    }

    public ObjectProperty<OutputDevice> outputDeviceProperty() {
        return outputDevice;
    }

    public void setOutputDevice(OutputDevice outputDevice) {
        this.outputDevice.set(outputDevice);
    }

    public Point getSize() {
        return size.get();
    }

    public ObjectProperty<Point> sizeProperty() {
        return size;
    }

    public void setSize(Point size) {
        this.size.set(size);
    }

    public BarAccumulationStyle getBarAccumulationStyle() {
        return barAccumulationStyle.get();
    }

    public ObjectProperty<BarAccumulationStyle> barAccumulationStyleProperty() {
        return barAccumulationStyle;
    }

    public void setBarAccumulationStyle(BarAccumulationStyle barAccumulationStyle) {
        this.barAccumulationStyle.set(barAccumulationStyle);
    }

    public SortingType getSortingType() {
        return sortingType.get();
    }

    public ObjectProperty<SortingType> sortingTypeProperty() {
        return sortingType;
    }

    public void setSortingType(SortingType sortingType) {
        this.sortingType.set(sortingType);
    }

    public CsvType getCsvType() {
        return csvType.get();
    }

    public ObjectProperty<CsvType> csvTypeProperty() {
        return csvType;
    }

    public void setCsvType(CsvType csvType) {
        this.csvType.set(csvType);
    }

    public boolean isPi() {
        return pi.get();
    }

    public BooleanProperty piProperty() {
        return pi;
    }

    public void setPi(boolean pi) {
        this.pi.set(pi);
    }

    public boolean isPointsBorderless() {
        return pointsBorderless.get();
    }

    public BooleanProperty pointsBorderlessProperty() {
        return pointsBorderless;
    }

    public void setPointsBorderless(boolean pointsBorderless) {
        this.pointsBorderless.set(pointsBorderless);
    }

    public boolean isHideOriginalPoints() {
        return hideOriginalPoints.get();
    }

    public BooleanProperty hideOriginalPointsProperty() {
        return hideOriginalPoints;
    }

    public void setHideOriginalPoints(boolean hideOriginalPoints) {
        this.hideOriginalPoints.set(hideOriginalPoints);
    }

    public boolean isSortDescending() {
        return sortDescending.get();
    }

    public BooleanProperty sortDescendingProperty() {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending) {
        this.sortDescending.set(sortDescending);
    }

    public ObservableList<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(ObservableList<Function> functions) {
        this.functions = functions;
    }

    public ObservableList<String> getColors() {
        return colors;
    }

    public void setColors(ObservableList<String> colors) {
        this.colors = colors;
    }

    public ObservableList<String> getTrendLinie() {
        return trendLinie;
    }

    public void setTrendLinie(ObservableList<String> trendLinie) {
        this.trendLinie = trendLinie;
    }

    public String getxLines() {
        return xLines.get();
    }

    public StringProperty xLinesProperty() {
        return xLines;
    }

    public void setxLines(String xLines) {
        this.xLines.set(xLines);
    }

    public String getyLines() {
        return yLines.get();
    }

    public StringProperty yLinesProperty() {
        return yLines;
    }

    public void setyLines(String yLines) {
        this.yLines.set(yLines);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getGnuplot() {
        return gnuplot.get();
    }

    public StringProperty gnuplotProperty() {
        return gnuplot;
    }

    public void setGnuplot(String gnuplot) {
        this.gnuplot.set(gnuplot);
    }

    public String getCss() {
        return css.get();
    }

    public StringProperty cssProperty() {
        return css;
    }

    public void setCss(String css) {
        this.css.set(css);
    }

    public String getOutput() {
        return output.get();
    }

    public StringProperty outputProperty() {
        return output;
    }

    public void setOutput(String output) {
        this.output.set(output);
    }

    public String getCsvPath() {
        return csvPath.get();
    }

    public StringProperty csvPathProperty() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath.set(csvPath);
    }
}
