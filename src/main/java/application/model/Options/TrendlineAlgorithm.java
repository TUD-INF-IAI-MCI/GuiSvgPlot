package application.model.Options;

/**
 * @author Emma Müller
 */
public enum TrendlineAlgorithm {
    None,
    MovingAverage,
    ExponentialSmoothing,
    BrownLES,
    LinearRegression;


    private TrendlineAlgorithm(){}


    public static TrendlineAlgorithm fromString(final String name){
        TrendlineAlgorithm searchedTrendlineAlgorithmn = TrendlineAlgorithm.None;
        for (TrendlineAlgorithm trendlineAlgorithm: TrendlineAlgorithm.values()) {
            if (trendlineAlgorithm.name().equals(name)) searchedTrendlineAlgorithmn = trendlineAlgorithm;
        }
        return searchedTrendlineAlgorithmn;
    }

}
