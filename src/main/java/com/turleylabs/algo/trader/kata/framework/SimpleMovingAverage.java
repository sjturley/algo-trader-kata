package com.turleylabs.algo.trader.kata.framework;

import java.util.LinkedList;

public class SimpleMovingAverage {
    LinkedList<Double> buffer = new LinkedList<>();
    private String symbol;
    private int length;

    public SimpleMovingAverage(String symbol, int length) {
        this.symbol = symbol;
        this.length = length;
    }

    public double getValue() {
        return this.buffer.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

    public boolean isReady() {
        return buffer.size() == length;
    }

    public void addData(double data) {
        this.buffer.add(data);

        if (this.buffer.size() > length)
        {
            this.buffer.removeFirst();
        }
    }

    public String getSymbol() {
        return symbol;
    }
}
