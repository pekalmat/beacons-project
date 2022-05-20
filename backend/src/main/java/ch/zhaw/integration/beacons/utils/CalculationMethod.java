package ch.zhaw.integration.beacons.utils;

public enum CalculationMethod {

    // Basic - No Filtering/Smoothing
    TRILATERATION_NO_SMOOTHING,
    // Sliding-Window Signal Filtering/Smoothing
    TRILATERATION_SLIDING_WINDOW,
    // Kalmann-Filter Signal Filtering/Smoothing
    TRILATERATION_KALMANN_FILTER,
    // Combination Sliding-Window & Kalmann-Filter
    TRILATERATION_SLDING_WINDOW_AND_KALMANN_FILTER;
}
