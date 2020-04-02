package com.turleylabs.algo.trader.kata.framework;

import com.turleylabs.algo.trader.kata.framework.Bar;
import com.turleylabs.algo.trader.kata.framework.CBOE;

import java.io.*;
import java.time.LocalDate;

public class Slice {
    private final LocalDate date;

    public Slice(LocalDate tradeDate) {
        this.date = tradeDate;
    }

    private Bar findBar() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (BufferedReader br = new BufferedReader(new FileReader(classLoader.getResource("TQQQ.csv").getFile()))) {
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

    public boolean ContainsKey(String symbol) {
        return findBar() == null;
    }

    public CBOE getCBOE(String symbol) {
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
        return findBar();
    }

    public LocalDate getDate() {
        return date;
    }
}
