package application.model;

import application.model.Options.GuiAxisStyle;
import application.model.Options.LinePointsOption;
import application.model.Options.SortOrder;
import application.model.Options.VisibilityOfDataPoints;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.data.PointListList;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;
import tud.tangram.svgplot.plotting.line.LineStyle;
import tud.tangram.svgplot.plotting.point.PointSymbol;
import tud.tangram.svgplot.plotting.texture.Texture;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.Color;
import tud.tangram.svgplot.styles.GridStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Robert Schlegel, Emma Müller
 */
public class GuiSvgOptions {

    // These values were determined by trial and error, if the values are lower, the
    // application crashes!
    public static int MINIMUM_PAGE_WIDTH = 90;
    public static int MINIMUM_PAGE_HEIGHT = 100; // don't crash on >71 but when <100, svg-plott does strange things

    @JsonIgnore
    private SvgPlotOptions options;

    private ObjectProperty<DiagramType> diagramType;
    private ObjectProperty<CsvOrientation> csvOrientation;
    private ObjectProperty<OutputDevice> outputDevice;
    private ObjectProperty<Point> size;
    private ObjectProperty<BarAccumulationStyle> barAccumulationStyle;
    private ObjectProperty<SortingType> sortingType;
    private ObjectProperty<CsvType> csvType;
    private ObjectProperty<Range> xRange;
    private ObjectProperty<Range> yRange;
    private ObjectProperty<SortOrder> sortOrder;
    private ObjectProperty<VisibilityOfDataPoints> hideOriginalPoints;
    private ObjectProperty<LinePointsOption> linePointsOption;
    private ObjectProperty<GridStyle> gridStyle;
    private ObjectProperty<GuiAxisStyle> axisStyle;
    private ObjectProperty<IntegralPlotSettings> integral;

    // TODO: Document this extension
    private ObjectProperty<OutputGenerator> outputGenerator;
    private ObjectProperty<File> brlPlotConfig;
    private ObjectProperty<Boolean> brlPlotPrint;
    private ObjectProperty<Boolean> brlPlotTextDump;
    private ObjectProperty<Boolean> brlPlotSvgExport;
    private ObjectProperty<BrlPlotMode> brlPlotMode;

    @JsonIgnore
    private ObjectProperty<PointListList> points;

    private BooleanProperty pi;
    private BooleanProperty autoScale;

    private ObservableList<Function> functions;
    @JsonIgnore
    private ObservableList<Color> colors;
    @JsonIgnore
    private ObservableList<Texture> textures;
    @JsonIgnore
    private ObservableList<PointSymbol> pointSymbols;
    @JsonIgnore
    private ObservableList<LineStyle> lineStyles;
    private ObservableList<String> trendLine;

    @JsonDeserialize(using = StringDeserializer.class)
    @JsonSerialize(using = StringSerializer.class)
    private final StringProperty xLines;
    @JsonDeserialize(using = StringDeserializer.class)
    @JsonSerialize(using = StringSerializer.class)
    private final StringProperty yLines;
    @JsonIgnore
    private final StringProperty title;
    @JsonIgnore
    private final StringProperty gnuplot;
    @JsonDeserialize(using = StringDeserializer.class)
    @JsonSerialize(using = StringSerializer.class)
    private final StringProperty css;
    @JsonDeserialize(using = StringDeserializer.class)
    @JsonSerialize(using = StringSerializer.class)
    private final StringProperty output;
    @JsonIgnore
    private final StringProperty csvPath;
    @JsonDeserialize(using = StringDeserializer.class)
    @JsonSerialize(using = StringSerializer.class)
    private final StringProperty xUnit;
    @JsonDeserialize(using = StringDeserializer.class)
    @JsonSerialize(using = StringSerializer.class)
    private final StringProperty yUnit;

    // this necessary for jackson
    public GuiSvgOptions() {
        this(new SvgPlotOptions());
    }

    public GuiSvgOptions(SvgPlotOptions options) {
        this.options = options;
        this.diagramType = new SimpleObjectProperty<>(this.options.getDiagramType());

        // TODO: Document this extension
        outputGenerator = new SimpleObjectProperty<>(OutputGenerator.SvgPlot);
        brlPlotConfig = new SimpleObjectProperty<>(new File(""));
        brlPlotPrint = new SimpleObjectProperty<>(true);
        brlPlotTextDump = new SimpleObjectProperty<>(false);
        brlPlotSvgExport = new SimpleObjectProperty<>(false);
        //brlPlotMode = new SimpleObjectProperty<>(BrlPlotMode.DefaultPrinter);

        this.outputDevice = new SimpleObjectProperty<>(this.options.getOutputDevice());
        this.csvType = new SimpleObjectProperty<>(this.options.getCsvType());
        this.csvOrientation = new SimpleObjectProperty<>(this.options.getCsvOrientation());
        this.size = new SimpleObjectProperty<>(new Point(this.options.getSize()));
        this.xRange = new SimpleObjectProperty<>(this.options.getxRange());
        this.yRange = new SimpleObjectProperty<>(this.options.getyRange());
        this.barAccumulationStyle = new SimpleObjectProperty<>(this.options.getBarAccumulationStyle());
        this.sortingType = new SimpleObjectProperty<>(this.options.getSortingType());
        this.sortOrder = new SimpleObjectProperty<>(this.options.isSortDescending() ? SortOrder.DESC : SortOrder.ASC);
        this.hideOriginalPoints = new SimpleObjectProperty<>(
                this.options.isHideOriginalPoints() ? VisibilityOfDataPoints.HIDE : VisibilityOfDataPoints.SHOW);
        this.linePointsOption = new SimpleObjectProperty<>(
                LinePointsOption.getLinePointsOption(this.options.getShowLinePoints(), options.isPointsBorderless()));
        this.gridStyle = new SimpleObjectProperty<>(GridStyle.NONE);
        this.axisStyle = new SimpleObjectProperty<>(GuiAxisStyle.Barchart);
        this.integral = new SimpleObjectProperty<>(this.options.getIntegral());
        this.points = new SimpleObjectProperty<>(this.options.getPoints());
        initSimpleObjectListeners();

        this.gnuplot = new SimpleStringProperty(Settings.getInstance().getGnuPlotPath());
        if (!this.gnuplot.get().isEmpty()) {
            this.options.setGnuplot(this.gnuplot.getValue());
        }
        this.title = new SimpleStringProperty(this.options.getTitle());
        this.output = new SimpleStringProperty(
                this.options.getOutput() != null ? options.getOutput().getAbsolutePath() : "");
        this.csvPath = new SimpleStringProperty(this.options.getCsvPath());
        this.xLines = new SimpleStringProperty(this.options.getxLines());
        this.yLines = new SimpleStringProperty(this.options.getyLines());
        this.xUnit = new SimpleStringProperty(this.options.getxUnit());
        this.yUnit = new SimpleStringProperty(this.options.getyUnit());
        this.css = new SimpleStringProperty(this.options.getCss());
        initSimpleStringListerners();

        this.pi = new SimpleBooleanProperty(this.options.isPi());
        this.autoScale = new SimpleBooleanProperty(this.options.hasAutoScale());
        initSimpleBooleanListeners();

        this.functions = FXCollections.observableArrayList();
        this.colors = FXCollections.observableArrayList();
        this.trendLine = FXCollections.observableArrayList();
        this.textures = FXCollections.observableArrayList();
        this.pointSymbols = FXCollections.observableArrayList();
        this.lineStyles = FXCollections.observableArrayList();
        initObservableListListeners();

    }

    private void initSimpleObjectListeners() {
        this.diagramType.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.options.setDiagramType(newValue);
                switch (newValue) {
                    case FunctionPlot:
                        this.setFunctionPlotDefaultOptions();
                        break;
                    case LineChart:
                        this.setLineChartDefaultOptions();
                        break;
                    case BarChart:
                        this.setBarChartDefaultOptions();
                        break;
                    case ScatterPlot:
                        this.setScatterPlotDefaultOptions();
                        break;
                }
            }
        });
        this.outputDevice.addListener((observable, oldValue, newValue) -> {
            this.options.setOutputDevice(newValue);
            this.lineStyles.setAll(LineStyle.getByOutputDeviceOrderedById(newValue));
            if (newValue == OutputDevice.ScreenColor) {
                this.colors.setAll(Color.values());
            }
        });
        this.csvType.addListener((observable, oldValue, newValue) -> {
            this.options.setCsvType(newValue);
        });
        this.csvOrientation.addListener((observable, oldValue, newValue) -> {
            this.options.setCsvOrientation(newValue);
        });
        this.size.addListener((observable, oldValue, newValue) -> {
            this.options.setSize(newValue);
        });
        this.xRange.addListener((observable, oldValue, newValue) -> {
            this.options.setxRange(newValue);
        });
        this.yRange.addListener((observable, oldValue, newValue) -> {
            this.options.setyRange(newValue);
        });
        this.barAccumulationStyle.addListener((observable, oldValue, newValue) -> {
            this.options.setBarAccumulationStyle(newValue);
        });
        this.sortingType.addListener((observable, oldValue, newValue) -> {
            this.options.setSortingType(newValue);
        });
        this.sortOrder.addListener((observable, oldValue, newValue) -> {
            this.options.setSortDescending(newValue != null && newValue.equals(SortOrder.DESC));
        });
        this.hideOriginalPoints.addListener((observable, oldValue, newValue) -> {
            this.options.setHideOriginalPoints(newValue != null && newValue.equals(VisibilityOfDataPoints.HIDE));
        });
        this.linePointsOption.addListener((observable, oldValue, newValue) -> {
            this.options.setPointsBorderless(newValue != null && newValue.isPointsborderless());
            this.options.setShowLinePoints(newValue == null ? null : newValue.getShowLinePoints());
        });
        this.gridStyle.addListener((observable, oldValue, newValue) -> {
            this.options.setShowHorizontalGrid(newValue != null && newValue.showHorizontal() ? "on" : "off");
            this.options.setShowVerticalGrid(newValue != null && newValue.showVertical() ? "on" : "off");
        });
        this.axisStyle.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getAxisStyle() != null) {
                this.options.setShowDoubleAxes(newValue.getAxisStyle().equals(AxisStyle.BOX) ? "on" : "off");
            } else {
                this.options.setShowDoubleAxes(null);
            }
        });
        this.points.addListener((observable, oldValue, newValue) -> {
            this.options.setPoints(newValue);
        });
        this.integral.addListener((args, oldVal, newVal) -> {
            options.setIntegral(newVal);
        });
    }

    private void initSimpleStringListerners() {
        this.title.addListener((observable, oldValue, newValue) -> {
            this.options.setTitle(newValue);
        });
        this.output.addListener((observable, oldValue, newValue) -> {
            this.options.setOutput(new File(newValue));
        });
        this.csvPath.addListener((observable, oldValue, newValue) -> {
            this.options.setCsvPath(newValue);
        });
        this.xLines.addListener((observable, oldValue, newValue) -> {
            this.options.setxLines(newValue);
        });
        this.yLines.addListener((observable, oldValue, newValue) -> {
            this.options.setyLines(newValue);
        });
        this.xUnit.addListener((observable, oldValue, newValue) -> {
            this.options.setxUnit(newValue.trim().isEmpty() ? null : newValue);
        });
        this.yUnit.addListener((observable, oldValue, newValue) -> {
            this.options.setyUnit(newValue.trim().isEmpty() ? null : newValue);
        });
        this.css.addListener((observable, oldValue, newValue) -> {
            this.options.setCss(newValue);
        });
    }

    private void initSimpleBooleanListeners() {
        this.pi.addListener((observable, oldValue, newValue) -> {
            this.options.setPi(newValue);
        });
        this.autoScale.addListener((observable, oldValue, newValue) -> {
            this.options.setAutoScale(newValue);
        });
    }

    private void initObservableListListeners() {
        this.functions.addListener(new ListChangeListener<Function>() {
            @Override
            public void onChanged(Change<? extends Function> c) {
                options.setFunctions(functions);
            }
        });
        this.colors.addListener(new ListChangeListener<Color>() {
            @Override
            public void onChanged(Change<? extends Color> c) {
                List<String> colorStringList = new ArrayList<>();
                for (Color color : colors) {
                    String colorString = null;
                    if (color != null) {
                        colorString = color.getName();
                    }
                    colorStringList.add(colorString);
                }
                options.setCustomColors(colorStringList);
            }
        });
        this.trendLine.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                options.setTrendLine(trendLine);
            }
        });
        this.textures.addListener(new ListChangeListener<Texture>() {
            @Override
            public void onChanged(final Change<? extends Texture> c) {
                options.setTextures(textures);
            }
        });
        this.pointSymbols.addListener(new ListChangeListener<PointSymbol>() {
            @Override
            public void onChanged(final Change<? extends PointSymbol> c) {
                options.setPointSymbols(pointSymbols);
            }
        });
        this.lineStyles.addListener(new ListChangeListener<LineStyle>() {
            @Override
            public void onChanged(final Change<? extends LineStyle> c) {
                options.setLineStyles(lineStyles);
            }
        });

    }

    private void setFunctionPlotDefaultOptions() {
        if (this.options.getShowHorizontalGrid() == null && this.options.getShowVerticalGrid() == null) {
            this.gridStyle.set(GridStyle.FULL);
        }
    }

    private void setLineChartDefaultOptions() {
        if (this.options.getShowHorizontalGrid() == null && this.options.getShowVerticalGrid() == null) {
            this.gridStyle.set(GridStyle.FULL);
        }
        this.pointSymbols.add(PointSymbol.dot);
        if (this.lineStyles.size() == 0) {
            this.lineStyles.addAll(LineStyle.getByOutputDeviceOrderedById(this.outputDevice.get()));
        }
        this.axisStyle.set(GuiAxisStyle.Edge);
    }

    private void setScatterPlotDefaultOptions() {
        if (this.pointSymbols.size() == 0) {
            this.pointSymbols.addAll(PointSymbol.getOrdered());
        }
        this.axisStyle.set(GuiAxisStyle.Box);
    }

    private void setBarChartDefaultOptions() {
        if (this.textures.size() == 0) {
            this.textures.addAll(Texture.black, Texture.dotted_pattern, Texture.diagonal_line1_sp_t);
        }
        this.csvType.set(CsvType.X_ALIGNED_CATEGORIES);
        if (this.options.getShowHorizontalGrid() == null) {
            this.gridStyle.set(GridStyle.HORIZONTAL);
        }
        this.axisStyle.set(GuiAxisStyle.Barchart);
    }

    public SvgPlotOptions getOptions() {

        return options;
    }

    public void setOptions(SvgPlotOptions options) {
        this.options = options;
    }

    public DiagramType getDiagramType() {
        return diagramType.get();
    }

    public ObjectProperty<DiagramType> diagramTypeProperty() {
        return diagramType;
    }

    public void setDiagramType(final DiagramType diagramType) {
        this.diagramType.set(diagramType);
    }

    public CsvOrientation getCsvOrientation() {
        return csvOrientation.get();
    }

    public ObjectProperty<CsvOrientation> csvOrientationProperty() {
        return csvOrientation;
    }

    public void setCsvOrientation(final CsvOrientation csvOrientation) {
        this.csvOrientation.set(csvOrientation);
    }

    public OutputDevice getOutputDevice() {
        return outputDevice.get();
    }

    public ObjectProperty<OutputDevice> outputDeviceProperty() {
        return outputDevice;
    }

    public void setOutputDevice(final OutputDevice outputDevice) {
        this.outputDevice.set(outputDevice);
    }

    /*
    TODO: Document extension to GuiSvgOption container
    - added member, getter & setter for output generator property
     */
    public void setOutputGenerator(final OutputGenerator newGenerator) {
        outputGenerator.set(newGenerator);
    }
    public OutputGenerator getOutputGenerator() {
        return outputGenerator.get();
    }
    public void setBrlPlotConfig(File brlPlotConfig) {
        this.brlPlotConfig.set(brlPlotConfig);
    }
    public File getBrlPlotConfig() {
        return brlPlotConfig.get();
    }
    public Boolean getBrlPlotPrint() {
        return brlPlotPrint.get();
    }
    public void setBrlPlotPrint(Boolean brlPlotPrint) {
        this.brlPlotPrint.set(brlPlotPrint);
    }
    public Boolean getBrlPlotTextDump() {
        return brlPlotTextDump.get();
    }
    public void setBrlPlotTextDump(Boolean brlPlotTextDump) {
        this.brlPlotTextDump.set(brlPlotTextDump);
    }
    public Boolean getBrlPlotSvgExport() {
        return brlPlotSvgExport.get();
    }
    public void setBrlPlotSvgExport(Boolean brlPlotSvgExport) {
        this.brlPlotSvgExport.set(brlPlotSvgExport);
    }
    /*
    public void setBrlPlotMode(BrlPlotMode brlPlotMode) {
        this.brlPlotMode.set(brlPlotMode);
    }
    public BrlPlotMode getBrlPlotMode() {
        return brlPlotMode.get();
    }
     */

    public Point getSize() {
        return size.get();
    }

    public ObjectProperty<Point> sizeProperty() {
        return size;
    }

    public void setSize(final Point size) {
        this.size.set(size);
    }

    public BarAccumulationStyle getBarAccumulationStyle() {
        return barAccumulationStyle.get();
    }

    public ObjectProperty<BarAccumulationStyle> barAccumulationStyleProperty() {
        return barAccumulationStyle;
    }

    public void setBarAccumulationStyle(final BarAccumulationStyle barAccumulationStyle) {
        this.barAccumulationStyle.set(barAccumulationStyle);
    }

    public ObservableList<Texture> getTextures() {
        return textures;
    }

    @JsonGetter("textures")
    public List<Texture> getTexturesAsList() {
        return textures;
    }

    public void setTextures(final ObservableList<Texture> textures) {
        this.textures = textures;
    }

    @JsonSetter("textures")
    public void setTexturesList(final List<Texture> textures) {
        this.textures.setAll(textures);
    }

    public ObservableList<Texture> textureProperty() {
        return textures;
    }

    public ObservableList<PointSymbol> getPointSymbols() {
        return pointSymbols;
    }

    @JsonGetter("pointSymbols")
    public List<PointSymbol> getPointSymbolsAsList() {
        return pointSymbols;
    }

    public void setPointSymbols(final ObservableList<PointSymbol> pointSymbols) {
        this.pointSymbols = pointSymbols;
    }

    @JsonSetter("pointSymbols")
    public void setPointSymbolsList(final List<PointSymbol> pointSymbols) {
        this.pointSymbols.setAll(pointSymbols);
    }

    public SortingType getSortingType() {
        return sortingType.get();
    }

    public ObjectProperty<SortingType> sortingTypeProperty() {
        return sortingType;
    }

    public void setSortingType(final SortingType sortingType) {
        this.sortingType.set(sortingType);
    }

    public CsvType getCsvType() {
        return csvType.get();
    }

    public ObjectProperty<CsvType> csvTypeProperty() {
        return csvType;
    }

    public void setCsvType(final CsvType csvType) {
        this.csvType.set(csvType);
    }

    public Range getxRange() {
        return xRange.get();
    }

    public ObjectProperty<Range> xRangeProperty() {
        return xRange;
    }

    public void setxRange(final Range xRange) {
        this.xRange.set(xRange);
    }

    public Range getyRange() {
        return yRange.get();
    }

    public ObjectProperty<Range> yRangeProperty() {
        return yRange;
    }

    public void setyRange(final Range yRange) {
        this.yRange.set(yRange);
    }

    public SortOrder getSortOrder() {
        return sortOrder.get();
    }

    public ObjectProperty<SortOrder> sortOrderProperty() {
        return sortOrder;
    }

    public void setSortOrder(final SortOrder sortOrder) {
        this.sortOrder.set(sortOrder);
    }

    public VisibilityOfDataPoints getHideOriginalPoints() {
        return hideOriginalPoints.get();
    }

    public ObjectProperty<VisibilityOfDataPoints> hideOriginalPointsProperty() {
        return hideOriginalPoints;
    }

    public void setHideOriginalPoints(final VisibilityOfDataPoints hideOriginalPoints) {
        this.hideOriginalPoints.set(hideOriginalPoints);
    }

    public LinePointsOption getLinePointsOption() {
        return linePointsOption.get();
    }

    public ObjectProperty<LinePointsOption> linePointsOptionProperty() {
        return linePointsOption;
    }

    public void setLinePointsOption(final LinePointsOption linePointsOption) {
        this.linePointsOption.set(linePointsOption);
    }

    public GridStyle getGridStyle() {
        return gridStyle.get();
    }

    public ObjectProperty<GridStyle> gridStyleProperty() {
        return gridStyle;
    }

    public void setGridStyle(final GridStyle gridStyle) {
        this.gridStyle.set(gridStyle);
    }

    public GuiAxisStyle getAxisStyle() {
        return axisStyle.get();
    }

    public ObjectProperty<GuiAxisStyle> axisStyleProperty() {
        return axisStyle;
    }

    public void setAxisStyle(final GuiAxisStyle axisStyle) {
        this.axisStyle.set(axisStyle);
    }

    public boolean isPi() {
        return pi.get();
    }

    public BooleanProperty piProperty() {
        return pi;
    }

    public void setPi(final boolean pi) {
        this.pi.set(pi);
    }

    public boolean isAutoScale() {
        return autoScale.get();
    }

    public BooleanProperty autoScaleProperty() {
        return autoScale;
    }

    public void setAutoScale(final boolean autoScale) {
        this.autoScale.set(autoScale);
    }

    public ObservableList<Function> getFunctions() {
        return functions;
    }

    @JsonGetter("functions")
    public List<Function> getFunctionsAsList() {
        return functions;
    }

    public void setFunctions(final ObservableList<Function> functions) {
        this.functions = functions;
    }

    @JsonSetter("functions")
    public void setFunctionsList(final List<Function> functions) {
        this.functions.addAll(functions);
    }

    public ObservableList<Color> getColors() {
        return colors;
    }

    @JsonGetter("colors")
    public List<Color> getColorAsList() {
        return colors;
    }

    public void setColors(final ObservableList<Color> colors) {
        this.colors = colors;
    }

    @JsonSetter("colors")
    public void setColorsList(final List<Color> colors) {
        this.colors.setAll(colors);
    }

    public ObservableList<String> getTrendLine() {
        return trendLine;
    }

    @JsonGetter("trendLine")
    public List<String> getTrendlineAsList() {
        return trendLine;
    }

    public void setTrendLine(final ObservableList<String> trendLine) {
        this.trendLine.setAll(trendLine);
    }

    @JsonSetter("trendLine")
    public void setTrendlineList(final List<String> trendline) {
        this.trendLine.setAll(trendline);
    }

    public String getxLines() {
        return xLines.get();
    }

    public StringProperty xLinesProperty() {
        return xLines;
    }

    public void setxLines(final String xLines) {
        this.xLines.set(xLines);
    }

    public String getyLines() {
        return yLines.get();
    }

    public StringProperty yLinesProperty() {
        return yLines;
    }

    public void setyLines(final String yLines) {
        this.yLines.set(yLines);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(final String title) {
        this.title.set(title);
    }

    public String getGnuplot() {
        return gnuplot.get();
    }

    public StringProperty gnuplotProperty() {
        return gnuplot;
    }

    public void setGnuplot(final String gnuplot) {
        this.gnuplot.set(gnuplot);
    }

    public String getCss() {
        return css.get();
    }

    public StringProperty cssProperty() {
        return css;
    }

    public void setCss(final String css) {
        this.css.set(css);
    }

    public String getOutput() {
        return output.get();
    }

    public StringProperty outputProperty() {
        return output;
    }

    public void setOutput(final String output) {
        this.output.set(output);
    }

    public String getCsvPath() {
        return csvPath.get();
    }

    public StringProperty csvPathProperty() {
        return csvPath;
    }

    public void setCsvPath(final String csvPath) {
        this.csvPath.set(csvPath);
    }

    public String getxUnit() {
        return xUnit.get();
    }

    public StringProperty xUnitProperty() {
        return xUnit;
    }

    public void setxUnit(final String xUnit) {
        this.xUnit.set(xUnit);
    }

    public String getyUnit() {
        return yUnit.get();
    }

    public StringProperty yUnitProperty() {
        return yUnit;
    }

    public void setyUnit(final String yUnit) {
        this.yUnit.set(yUnit);
    }

    public PointListList getPoints() {
        return this.options.getPoints();
    }

    public ObservableList<LineStyle> getLineStyles() {
        return lineStyles;
    }

    @JsonGetter("lineStyles")
    public List<LineStyle> getLineStylesAsList() {
        return lineStyles;
    }

    public void setLineStyles(final ObservableList<LineStyle> lineStyles) {
        this.lineStyles.setAll(lineStyles);
    }

    @JsonSetter("lineStyles")
    public void setLineStylesList(final List<LineStyle> lineStyles) {
        this.lineStyles.setAll(lineStyles);
    }

    public void setIntegral(IntegralPlotSettings value) {
        this.integral.set(value);
    }

    public IntegralPlotSettings getIntegral() {
        return this.integral.get();
    }

    public ObjectProperty<IntegralPlotSettings> integralProperty() {
        return integral;
    }

    public ObjectProperty<PointListList> pointsProperty() {
        return points;
    }

    public void setPoints(final PointListList points) {
        this.points.set(points);
    }

    public void updatePointSpecificOptions() {
        if (this.diagramType.get() == DiagramType.ScatterPlot) {
            if (this.options.getPoints() != null && this.options.getPoints().size() > 0) {
                if (this.pointSymbols.size() == 0 || this.pointSymbols.equals(PointSymbol.getOrdered())) {
                    this.pointSymbols.clear();
                    this.pointSymbols.setAll(PointSymbol.getOrdered().subList(0, this.options.getPoints().size()));
                } else if (this.pointSymbols.size() < this.options.getPoints().size()) {
                    List<PointSymbol> tempList = new ArrayList<>();
                    for (int i = this.pointSymbols.size(); i < this.options.getPoints().size(); i++) {
                        tempList.add(PointSymbol.getOrdered().get(i));
                    }
                    this.pointSymbols.addAll(tempList);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "GuiSvgOptions{" + "diagramType=" + diagramType + ", csvOrientation=" + csvOrientation
                + ", outputDevice=" + outputDevice + ", size=" + size + ", barAccumulationStyle=" + barAccumulationStyle
                + ", sortingType=" + sortingType + ", csvType=" + csvType + ", xRange=" + xRange + ", yRange=" + yRange
                + ", hideOriginalPoints=" + hideOriginalPoints + ", linePointsOption=" + linePointsOption
                + ", gridStyle=" + gridStyle + ", axisStyle=" + axisStyle + ", integral=" + integral + ", pi=" + pi
                + ", autoScale=" + autoScale + ", functions=" + functions + ", colors=" + colors + ", textures="
                + textures + ", pointSymbols=" + pointSymbols + ", lineStyles=" + lineStyles + ", trendLine="
                + trendLine + ", xLines=" + xLines + ", yLines=" + yLines + ", title=" + title + ", gnuplot=" + gnuplot
                + ", css=" + css + ", output=" + output + ", csvPath=" + csvPath + ", xUnit=" + xUnit + ", yUnit="
                + yUnit + '}';
    }

    public void update(GuiSvgOptions guiSvgOptions) {
        // TODO: refactoring
        this.diagramType.set(guiSvgOptions.diagramType.get());

        this.autoScale.set(guiSvgOptions.autoScale.get());
        this.axisStyle.set(guiSvgOptions.axisStyle.get());
        this.barAccumulationStyle.set(guiSvgOptions.barAccumulationStyle.get());

        this.css.set(guiSvgOptions.css.get());
        this.csvOrientation.set(guiSvgOptions.getCsvOrientation());
        this.csvPath.set(guiSvgOptions.csvPath.get());
        this.csvType.set(guiSvgOptions.csvType.get());
        this.functions.setAll(guiSvgOptions.functions);
        this.gridStyle.set(guiSvgOptions.gridStyle.get());
        this.integral.set(guiSvgOptions.integral.get());
        this.linePointsOption.set(guiSvgOptions.getLinePointsOption());
        this.outputDevice.set(guiSvgOptions.getOutputDevice());
        this.pi.set(guiSvgOptions.pi.get());
        this.points.set(guiSvgOptions.getPoints());
        this.size.set(guiSvgOptions.getSize());
        this.sortingType.set(guiSvgOptions.getSortingType());
        this.sortOrder.set(guiSvgOptions.getSortOrder());
        this.textures.setAll(guiSvgOptions.getTextures());
        this.title.set(guiSvgOptions.getTitle());
        this.trendLine.setAll(guiSvgOptions.getTrendLine());
        this.xLines.set(guiSvgOptions.getxLines());
        this.xRange.set(guiSvgOptions.getxRange());
        this.xUnit.set(guiSvgOptions.getxUnit());
        this.yLines.set(guiSvgOptions.getyLines());
        this.yRange.set(guiSvgOptions.getyRange());
        this.yUnit.set(guiSvgOptions.getyUnit());

        this.lineStyles.setAll(guiSvgOptions.getLineStyles());
        this.pointSymbols.setAll(guiSvgOptions.getPointSymbols());
        if (guiSvgOptions.getColors() != null) {
            this.colors.setAll(guiSvgOptions.colors);
        }
        this.hideOriginalPoints.set(guiSvgOptions.hideOriginalPoints.get());

        // onUpdate Callback
        if(Objects.nonNull(onUpdateCallback)) {
            onUpdateCallback.accept(guiSvgOptions);
        }
    }

    /*
    TODO: Document extension: added callback function to react to updates
     */
    private Consumer<GuiSvgOptions> onUpdateCallback;
    public void onUpdate(Consumer<GuiSvgOptions> callback) {
        onUpdateCallback = callback;
    }

}
