package com.turleylabs.algo.trader.kata;

import com.turleylabs.algo.trader.kata.framework.Bar;
import com.turleylabs.algo.trader.kata.framework.CBOE;
import com.turleylabs.algo.trader.kata.framework.Slice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.function.BiConsumer;

class FakeSlice extends Slice {

    static HashMap<LocalDate, CBOE> cboeData = new HashMap<>();
    static HashMap<LocalDate, Bar> tqqqData = new HashMap<>();

    static {
        setupTQQQ();
        setupVIX();
    }

    private static void setupVIX() {
        BiConsumer<LocalDate, Double> localDateDoubleBiConsumer = (localDate1, close1) -> cboeData.put(localDate1, new CBOE(close1));
        processPriceData(localDateDoubleBiConsumer, "VIX.csv");
    }

    private static void setupTQQQ() {
        BiConsumer<LocalDate, Double> localDateDoubleBiConsumer = (localDate1, close1) -> tqqqData.put(localDate1, new Bar(close1));
        processPriceData(localDateDoubleBiConsumer, "TQQQ.csv");
    }

    private static void processPriceData(BiConsumer<LocalDate, Double> localDateDoubleBiConsumer, String name) {
        ClassLoader classLoader = FakeSlice.class.getClassLoader();
        try (BufferedReader br = new BufferedReader(new FileReader(classLoader.getResource(name).getFile()))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                LocalDate localDate = LocalDate.parse(values[0]);
                double close = Double.parseDouble(values[5]);
                localDateDoubleBiConsumer.accept(localDate, close);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FakeSlice(LocalDate tradeDate) {
        super(tradeDate);
    }

    @Override
    public boolean ContainsKey(String symbol) {
        return true;
    }

    @Override
    public CBOE getCBOE(String symbol) {
        return cboeData.get(this.getDate());
    }

    @Override
    public Bar get(String symbol) {
        return tqqqData.get(this.getDate());
    }
}
