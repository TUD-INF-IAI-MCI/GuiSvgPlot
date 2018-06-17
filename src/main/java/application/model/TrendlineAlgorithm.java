package application.model;

public enum TrendlineAlgorithm {
    MovingAverage,
    ExponentialSmoothing,
    BrownLES,
    LinearRegression,
    None;


    private TrendlineAlgorithm(){}



}
