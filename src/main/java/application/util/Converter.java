package application.util;

import application.model.Options.*;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import tud.tangram.svgplot.data.parse.CsvOrientation;
import tud.tangram.svgplot.data.parse.CsvType;
import tud.tangram.svgplot.data.sorting.SortingType;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.OutputDevice;
import tud.tangram.svgplot.plotting.line.LineStyle;
import tud.tangram.svgplot.plotting.point.PointSymbol;
import tud.tangram.svgplot.plotting.texture.Texture;
import tud.tangram.svgplot.styles.BarAccumulationStyle;
import tud.tangram.svgplot.styles.GridStyle;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Emma Müller
 */
public class Converter {
    private static final Converter INSTANCE = new Converter();
    private ResourceBundle bundle;

    private Converter() {
    }

    public static Converter getInstance() {
        return INSTANCE;
    }

    /**
     * Builds a {@link StringConverter} which converts a {@link DiagramType} to a string from language bundle.
     *
     * @return the {@link StringConverter}
     */
    public StringConverter<DiagramType> getDiagramTypeStringConverter() {
        return new StringConverter<DiagramType>() {
            @Override
            public String toString(DiagramType diagramType) {
                return bundle.getString("diagramType_" + diagramType.toString().toLowerCase());
            }

            @Override
            public DiagramType fromString(String string) {
                DiagramType diagramType = DiagramType.FunctionPlot;
                for (DiagramType dt : FXCollections.observableArrayList(DiagramType.values())) {
                    if (this.toString(dt).equals(string)) {
                        diagramType = dt;
                    }
                }
                return diagramType;
            }
        };
    }

    /**
     * Builds a {@link StringConverter} which converts a {@link PageSize} to a string from language bundle.
     *
     * @return the {@link StringConverter}
     */
    public StringConverter<PageSize> getPageSizeStringConverter() {
        return new StringConverter<PageSize>() {
            @Override
            public String toString(PageSize pageSize) {
                if (pageSize == PageSize.CUSTOM) {
                    return bundle.getString("radio_custom_scale");
                }
                return pageSize.getName();
            }

            @Override
            public PageSize fromString(String string) {
                PageSize pageSize = PageSize.A4;
                for (PageSize ps : FXCollections.observableArrayList(PageSize.values())) {
                    if (this.toString(ps).equals(string)) {
                        pageSize = ps;
                    }
                }
                return pageSize;
            }
        };
    }

    public StringConverter<LinePointsOption> getLinePointsOptionStringConverter() {
        return new StringConverter<LinePointsOption>() {
            @Override
            public String toString(LinePointsOption linePointsOption) {
                return bundle.getString("linepointsoption_" + linePointsOption.toString());
            }

            @Override
            public LinePointsOption fromString(String string) {
                LinePointsOption linePointsOption = LinePointsOption.Hide;
                for (LinePointsOption lpo : FXCollections.observableArrayList(LinePointsOption.values())) {
                    if (this.toString(lpo).equals(string)) {
                        linePointsOption = lpo;
                    }
                }
                return linePointsOption;
            }
        };
    }

    public StringConverter<GuiAxisStyle> getAxisStyleStringConverter() {
        return new StringConverter<GuiAxisStyle>() {
            @Override
            public String toString(GuiAxisStyle axisStyle) {
                return bundle.getString("axisstyle_" + axisStyle.toString().toLowerCase());
            }

            @Override
            public GuiAxisStyle fromString(String string) {
                GuiAxisStyle axisStyle = GuiAxisStyle.Barchart;
                for (GuiAxisStyle as : FXCollections.observableArrayList(GuiAxisStyle.values())) {
                    if (this.toString(as).equals(string)) {
                        axisStyle = as;
                    }
                }
                return axisStyle;
            }
        };
    }

    public StringConverter<GridStyle> getGridStyleStringConverter() {
        return new StringConverter<GridStyle>() {
            @Override
            public String toString(GridStyle gridStyle) {
                return bundle.getString("gridstyle_" + gridStyle.toString().toLowerCase());
            }

            @Override
            public GridStyle fromString(String string) {
                GridStyle gridStyle = GridStyle.NONE;
                for (GridStyle gs : FXCollections.observableArrayList(GridStyle.values())) {
                    if (this.toString(gs).equals(string)) {
                        gridStyle = gs;
                    }
                }
                return gridStyle;
            }
        };
    }

    public StringConverter<TrendlineAlgorithm> getTrendlineAlgorithmStringConverter() {
        return new StringConverter<TrendlineAlgorithm>() {
            @Override
            public String toString(TrendlineAlgorithm trendlineAlgorithm) {
                return bundle.getString("trendlineAlgorithm_" + trendlineAlgorithm.toString().toLowerCase());
            }

            @Override
            public TrendlineAlgorithm fromString(String string) {
                TrendlineAlgorithm trendlineAlgorithm = TrendlineAlgorithm.None;
                for (TrendlineAlgorithm ta : FXCollections.observableArrayList(TrendlineAlgorithm.values())) {
                    if (this.toString(ta).equals(string)) {
                        trendlineAlgorithm = ta;
                    }
                }
                return trendlineAlgorithm;
            }
        };
    }

    public StringConverter<CsvOrientation> getCsvOrientationStringConverter() {
        return new StringConverter<CsvOrientation>() {
            @Override
            public String toString(CsvOrientation csvOrientation) {
                return bundle.getString("csvOrientation_" + csvOrientation.toString().toLowerCase());
            }

            @Override
            public CsvOrientation fromString(String string) {
                CsvOrientation csvOrientation = CsvOrientation.HORIZONTAL;
                for (CsvOrientation co : FXCollections.observableArrayList(CsvOrientation.values())) {
                    if (this.toString(co).equals(string)) {
                        csvOrientation = co;
                    }
                }
                return csvOrientation;
            }
        };
    }

    public StringConverter<CsvType> getCsvTypeStringConverter() {
        return new StringConverter<CsvType>() {
            @Override
            public String toString(CsvType csvType) {
                return bundle.getString("csvType_" + csvType.toString().toLowerCase());
            }

            @Override
            public CsvType fromString(String string) {
                CsvType csvType = CsvType.DOTS;
                for (CsvType ct : FXCollections.observableArrayList(CsvType.values())) {
                    if (this.toString(ct).equals(string)) {
                        csvType = ct;
                    }
                }
                return csvType;
            }
        };
    }

    public StringConverter<BarAccumulationStyle> getBarAccumulationStyleStringConverter() {
        return new StringConverter<BarAccumulationStyle>() {
            @Override
            public String toString(BarAccumulationStyle barAccumulationStyle) {
                return bundle.getString("barAccumulationStyle_" + barAccumulationStyle.toString().toLowerCase());
            }

            @Override
            public BarAccumulationStyle fromString(String string) {
                BarAccumulationStyle barAccumulationStyle = BarAccumulationStyle.GROUPED;
                for (BarAccumulationStyle bas : FXCollections.observableArrayList(BarAccumulationStyle.values())) {
                    if (this.toString(bas).equals(string)) {
                        barAccumulationStyle = bas;
                    }
                }
                return barAccumulationStyle;
            }
        };
    }

    public StringConverter<SortingType> getSortingTypeStringConverter() {
        return new StringConverter<SortingType>() {
            @Override
            public String toString(SortingType sortingType) {
                return bundle.getString("sortingType_" + sortingType.toString().toLowerCase());
            }

            @Override
            public SortingType fromString(String string) {
                SortingType sortingType = SortingType.None;
                for (SortingType st : FXCollections.observableArrayList(SortingType.values())) {
                    if (this.toString(st).equals(string)) {
                        sortingType = st;
                    }
                }
                return sortingType;
            }
        };
    }


    public StringConverter<OutputDevice> getOutputDeviceStringConverter() {
        return new StringConverter<OutputDevice>() {
            @Override
            public String toString(OutputDevice outputDevice) {
                return bundle.getString("outputDevice_" + outputDevice.toString().toLowerCase());
            }

            @Override
            public OutputDevice fromString(String string) {
                OutputDevice outputDevice = OutputDevice.Default;
                for (OutputDevice device : FXCollections.observableArrayList(OutputDevice.values())) {
                    if (this.toString(device).equals(string)) {
                        outputDevice = device;
                    }
                }
                return outputDevice;
            }
        };
    }

    public StringConverter<SortOrder> getSortOrderStringConverter() {
        return new StringConverter<SortOrder>() {
            @Override
            public String toString(SortOrder sortOrder) {
                return bundle.getString("sortOrder_" + sortOrder.toString().toLowerCase());
            }

            @Override
            public SortOrder fromString(String string) {
                SortOrder sortOrder = SortOrder.ASC;
                for (SortOrder so : FXCollections.observableArrayList(SortOrder.values())) {
                    if (this.toString(so).equals(string)) {
                        sortOrder = so;
                    }
                }
                return sortOrder;
            }
        };
    }

    public StringConverter<VisibilityOfDataPoints> getVisibilityOfDataPointsStringConverter() {
        return new StringConverter<VisibilityOfDataPoints>() {
            @Override
            public String toString(VisibilityOfDataPoints visibilityOfDataPoints) {
                return bundle.getString("visibilityOfDataPoints_" + visibilityOfDataPoints.toString().toLowerCase());
            }

            @Override
            public VisibilityOfDataPoints fromString(String string) {
                VisibilityOfDataPoints visibilityOfDataPoints = VisibilityOfDataPoints.SHOW;
                for (VisibilityOfDataPoints visibility : FXCollections.observableArrayList(VisibilityOfDataPoints.values())) {
                    if (this.toString(visibility).equals(string)) {
                        visibilityOfDataPoints = visibility;
                    }
                }
                return visibilityOfDataPoints;
            }
        };
    }

    public StringConverter<CssType> getCssTypeStringConverter() {
        return new StringConverter<CssType>() {
            @Override
            public String toString(CssType cssType) {
                return bundle.getString("cssType_" + cssType.toString().toLowerCase());
            }

            @Override
            public CssType fromString(String string) {
                CssType cssType = CssType.NONE;
                for (CssType type : FXCollections.observableArrayList(CssType.values())) {
                    if (this.toString(type).equals(string)) {
                        cssType = type;
                    }
                }
                return cssType;
            }
        };
    }

    public StringConverter<Texture> getTextureStringConverter() {
        return new StringConverter<Texture>() {
            @Override
            public String toString(Texture texture) {
                return bundle.getString("texture_" + texture.toString().toLowerCase());
            }

            @Override
            public Texture fromString(String string) {
                Texture texture = Texture.black;
                for (Texture t : FXCollections.observableArrayList(Texture.values())) {
                    if (this.toString(t).equals(string)) {
                        texture = t;
                    }
                }
                return texture;
            }
        };
    }

    /**
     * Sets the {@link ResourceBundle}
     *
     * @param bundle the {@link ResourceBundle}
     */
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public StringConverter<LineStyle> getLineStyleStringConverter() {
        return new StringConverter<LineStyle>() {
            @Override
            public String toString(LineStyle lineStyle) {
                if (lineStyle != null)
                    return bundle.getString("lineStyle_" + lineStyle.toString().toLowerCase());
                else return "";
            }

            @Override
            public LineStyle fromString(String string) {
                LineStyle lineStyle = LineStyle.Default_line;
                for (LineStyle ls : FXCollections.observableArrayList(LineStyle.values())) {
                    if (this.toString(ls).equals(string)) {
                        lineStyle = ls;
                    }
                }
                return lineStyle;
            }
        };
    }

    public StringConverter<PointSymbol> getPointSymbolStringConverter() {
        return new StringConverter<PointSymbol>() {
            @Override
            public String toString(PointSymbol pointSymbol) {
                return bundle.getString("pointSymbol_" + pointSymbol.toString().toLowerCase());
            }

            @Override
            public PointSymbol fromString(String string) {
                PointSymbol pointSymbol = PointSymbol.dot;
                for (PointSymbol ps : FXCollections.observableArrayList(PointSymbol.values())) {
                    if (this.toString(ps).equals(string)) {
                        pointSymbol = ps;
                    }
                }
                return pointSymbol;
            }
        };
    }

    /**
     * Builds a {@link StringConverter} which converts a {@link java.util.Locale} to a string from language bundle.
     *
     * @return the {@link StringConverter}
     */
    public StringConverter<Locale> getLocaleStringConverter() {
        return new StringConverter<Locale>() {
            @Override
            public String toString(Locale locale) {
                return bundle.getString("locale_" + locale.toString().toLowerCase());
            }

            @Override
            public Locale fromString(String string) {
                Locale locale = Locale.getDefault();
                for (Locale l : FXCollections.observableArrayList(Locale.getAvailableLocales())) {
                    if (this.toString(l).equals(string)) {
                        locale = l;
                    }
                }
                return locale;
            }
        };
    }

    public StringConverter<IntegralOption> getIntegralOptionStringConverter() {
        return new StringConverter<IntegralOption>() {
            @Override
            public String toString(IntegralOption integralOption) {
                return bundle.getString("integralOption_" + integralOption.toString().toLowerCase());
            }

            @Override
            public IntegralOption fromString(String string) {
                IntegralOption integralOption = IntegralOption.NONE;
                for (IntegralOption io : FXCollections.observableArrayList(IntegralOption.values())) {
                    if (this.toString(io).equals(string)) {
                        integralOption = io;
                    }
                }
                return integralOption;
            }
        };
    }

}
