package com.jbs.StockGame.entity;

import java.text.DecimalFormat;
import java.util.*;

import lombok.Data;

@Data
public class StockListing {
    private String name;
    private String symbol;
    private List<String> keyList;
    
    private List<Float> priceList;
    private float priceChange1Day;

    private float nextPriceChange;
    private int averageDayCount;

    public StockListing(String name, String symbol, List<String> keyList) {
        this.name = name;
        this.symbol = symbol;
        this.keyList = keyList;

        priceList = new ArrayList<>();
        priceChange1Day = 0.0f;

        nextPriceChange = 0.0f;
        averageDayCount = 0;
    }

    public float getPrice() {
        if(priceList.size() > 0) {
            return priceList.get(priceList.size() - 1);
        }
        return 0.0f;
    }

    public String getPriceString() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(getPrice());
    }

    public float getPriceChange1Day() {
        return Math.round(priceChange1Day * 100.0) / 100.0f;
    }

    public String getPriceChange1DayString() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(getPriceChange1Day());
    }
}
