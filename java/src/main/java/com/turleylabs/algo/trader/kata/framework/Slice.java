package com.turleylabs.algo.trader.kata.framework;

import java.io.*;
import java.time.LocalDate;

public class Slice {
    private final LocalDate date;
    private String symbol;

    public Slice(LocalDate tradeDate, String symbol) {
        this.date = tradeDate;
        this.symbol = symbol;
    }

    private Bar findBar(String symbol) {
        ClassLoader classLoader = getClass().getClassLoader();
        try (BufferedReader br = new BufferedReader(new FileReader(classLoader.getResource(symbol + ".csv").getFile()))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (LocalDate.parse(values[0]).equals(this.date)) {
                    return new Bar(Double.parseDouble(values[5]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean containsKey(String symbol) {
        return symbol.equals(this.symbol);
    }

    public CBOE getCBOE(String symbol) {
        if (!containsKey(symbol)) {
            return null;
        }
        return findCBOE();
    }

    private CBOE findCBOE() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (BufferedReader br = new BufferedReader(new FileReader(classLoader.getResource("VIX.csv").getFile()))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (LocalDate.parse(values[0]).equals(this.date)) {
                    return new CBOE(Double.parseDouble(values[5]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bar get(String symbol) {
        if (!containsKey(symbol)) {
            return null;
        }
        return findBar(symbol);
    }

    public LocalDate getDate() {
        return date;
    }
}
