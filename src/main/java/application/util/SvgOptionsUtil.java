package application.util;

import application.model.LinePointsOption;
import application.model.PageSize;
import application.model.TrendlineAlgorithm;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.styles.AxisStyle;
import tud.tangram.svgplot.styles.GridStyle;

import java.util.ResourceBundle;

public class SvgOptionsUtil {
    private static final SvgOptionsUtil INSTANCE = new SvgOptionsUtil();
    private ResourceBundle bundle;

    private SvgOptionsUtil() {
    }

    public static SvgOptionsUtil getInstance() {
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
                return bundle.getString(diagramType.toString());
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
                return pageSize.getName() + " " + bundle.getString(pageSize.getPageOrientationName().toLowerCase());
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

    public StringConverter<AxisStyle> getAxisStyleStringConverter() {
        return new StringConverter<AxisStyle>() {
            @Override
            public String toString(AxisStyle axisStyle) {
                return bundle.getString("axisstyle_" + axisStyle.toString().toLowerCase());
            }

            @Override
            public AxisStyle fromString(String string) {
                AxisStyle axisStyle = AxisStyle.GRAPH;
                for (AxisStyle as : FXCollections.observableArrayList(AxisStyle.values())) {
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

    /**
     * Sets the {@link ResourceBundle}
     *
     * @param bundle the {@link ResourceBundle}
     */
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
