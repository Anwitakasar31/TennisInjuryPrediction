package org.tensorflow.lite.examples.TennisInjuryPredictor.Algorithms;

import java.util.concurrent.TimeUnit;

/*
        * An exponentially weighted moving average implementation that decays based on the elapsed time since the last update,
        * approximating a time windowed moving average.
        */
public class ExponentiallyWeightedMovingAverage {
    private final long windowNanos;

    // Mutable state
    private volatile long lastNanos;
    private volatile double average;

    /**
     * Creates a moving average of samples over a {@code window} for the {@code timeUnit}.
     */
    public ExponentiallyWeightedMovingAverage(long window, TimeUnit timeUnit) {
        this.windowNanos = timeUnit.toNanos(window);
    }

    /**
     * Updates the average with the {@code sample}.
     */
    public synchronized void update(double sample) {
        long now = System.nanoTime();

        if (lastNanos == 0) {
            average = sample;
            lastNanos = now;
            return;
        }

        long elapsedNanos = now - lastNanos;
        double coeff = Math.exp(-1.0 * ((double) elapsedNanos / windowNanos));
        average = (1.0 - coeff) * sample + coeff * average;
        lastNanos = now;
    }

    /**
     * Returns the moving average.
     */
    public double get() {
        return average;
    }

    /**
     * Tick the internal moving average clock back by the {@code duration}. Useful for testing.
     */
    void tick(long duration, TimeUnit timeUnit) {
        if (lastNanos != 0)
            lastNanos -= timeUnit.toNanos(duration);
    }
}
