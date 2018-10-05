package application.model.Options;

import tud.tangram.svgplot.coordinatesystem.Range;

/**
 * @author Emma Müller
 */
public enum IntegralOption {
    NONE(new Range(0.00001, 0.00001), false),
    XAXIS(new Range(-10,10), true),
    FUNCTION(new Range(-10,10), true);

    private Range defaultXRange;
    private boolean showIntegral;

    IntegralOption(final Range defaultXRange, final boolean showIntegral){
        this.defaultXRange = defaultXRange;
        this.showIntegral = showIntegral;
    }

    public Range getDefaultXRange() {
        return defaultXRange;
    }

    public boolean isShowIntegral() {
        return showIntegral;
    }
}
