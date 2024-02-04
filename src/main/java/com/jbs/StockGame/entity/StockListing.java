package com.jbs.StockGame.entity;

import java.util.List;

import lombok.Data;

@Data
public class StockListing {
    private String name;
    private String symbol;
    private List<String> keyList;
    private float price;
    private float priceChange;

    public StockListing(String name, String symbol, List<String> keyList) {
        this.name = name;
        this.symbol = symbol;
        this.keyList = keyList;

        price = 0.0f;
        priceChange = 0.0f;
    }
}
