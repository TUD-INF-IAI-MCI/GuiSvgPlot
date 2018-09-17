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



}
