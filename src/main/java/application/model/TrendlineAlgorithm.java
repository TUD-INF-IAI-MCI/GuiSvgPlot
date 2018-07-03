package application.model;

public enum TrendlineAlgorithm {
    None,
    MovingAverage,
    ExponentialSmoothing,
    BrownLES,
    LinearRegression;


    private TrendlineAlgorithm(){}



}
