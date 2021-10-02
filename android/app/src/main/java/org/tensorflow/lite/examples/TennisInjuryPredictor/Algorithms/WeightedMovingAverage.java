package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Queue;

public class WeightedMovingAverage {

    private final Queue<BigDecimal> window = new LinkedList<BigDecimal>();
    private final int period;
    private BigDecimal sum = BigDecimal.ZERO;

    public WeightedMovingAverage(int period) {
        assert period > 0 : "Period must be a positive integer";
        this.period = period;
    }

    public void add(BigDecimal num) {
        sum = sum.add(num);
        window.add(num);
        if (window.size() > period) {
            sum = sum.subtract(window.remove());
        }
    }

    public BigDecimal getAverage() {
        if (window.isEmpty()) return BigDecimal.ZERO; // technically the average is undefined
        BigDecimal divisor = BigDecimal.valueOf(window.size());
        return sum.divide(divisor, 2, RoundingMode.HALF_UP);
    }
}