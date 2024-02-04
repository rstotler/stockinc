package com.jbs.StockGame.entity;

import java.util.*;

import lombok.Data;

@Data
public class Account {
    private String username;
    private String password;
    private String role;

    private float credits;
    private float lastInvestmentAmount;
    private Map<String, Integer> ownedStock;

    public int getOwnedStockQuantity(String stockSymbol) {
        if(ownedStock.containsKey(stockSymbol)) {
            return ownedStock.get(stockSymbol);
        } else {
            return 0;
        }
    }
}
