package application.model.Options;

import tud.tangram.svgplot.styles.AxisStyle;

public enum GuiAxisStyle {
    Barchart(null),
    Box(AxisStyle.BOX),
    Edge(AxisStyle.EDGE);

    private AxisStyle axisStyle;

    GuiAxisStyle(final AxisStyle axisStyle) {
        this.axisStyle = axisStyle;
    }

    public AxisStyle getAxisStyle() {
        return this.axisStyle;
    }

}
