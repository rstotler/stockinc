package com.jbs.StockGame.entity;

import java.text.DecimalFormat;
import java.util.*;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class StockListing {
    @Id
    private String symbol;
    private String name;
    @Column(length = 1000)
    private List<String> keyList;
    
    private List<Float> priceList;
    private List<Integer> keyCountList;
    private int nextKeyCount;

    @ElementCollection
    private Map<String, Integer> influencerUpCount;
    @ElementCollection
    private Map<String, Integer> influencerDownCount;

    public StockListing(String name, String symbol, List<String> keyList) {
        this.symbol = symbol;
        this.name = name;
        this.keyList = keyList;

        priceList = new ArrayList<>();
        keyCountList = new ArrayList<>();
        nextKeyCount = -9999;

        influencerUpCount = new HashMap<>();
        influencerDownCount = new HashMap<>();
    }

    public void addNewPrice() {
        int dayCount = 30;
        if(keyCountList.size() < 30) {
            dayCount = keyCountList.size();
        }

        float newPrice = 0.0f;
        for(int i = 0; i < dayCount; i++) {
            newPrice += keyCountList.get(keyCountList.size() - 1 - i);
        }
        newPrice /= dayCount;
        priceList.add(newPrice);
    }

    public float getPrice() {
        float price = 0.0f;
        if(priceList.size() > 0) {
            price = priceList.get(priceList.size() - 1);
        }

        return price;
    }

    public String getPriceString() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(getPrice());
    }

    public float getNextPrice() {
        int dayCount = 29;
        if(priceList.size() < 29) {
            dayCount = priceList.size();
        }

        float nextPrice = nextKeyCount;
        for(int i = 0; i < dayCount; i++) {
            nextPrice += keyCountList.get(keyCountList.size() - 1 - i);
        }
        nextPrice += getTotalInfluence();
        nextPrice /= (dayCount + 1);

        return nextPrice;
    }

    public float getPriceChangePercent() {
        float priceChangePercent = 0.0f;

        if(priceList.size() > 0) {
            float currentPrice = getPrice();
            float lastPrice = 0.0f;
            if(priceList.size() > 1) {
                lastPrice = priceList.get(priceList.size() - 2);
            }
            
            float priceDifference = currentPrice - lastPrice;

            if(lastPrice != 0.0) {
                priceChangePercent = (priceDifference / lastPrice) * 100;
            } else {
                priceChangePercent = priceDifference;
            }
        }

        return Math.round(priceChangePercent * 100.0) / 100.0f;
    }

    public String getPriceChangePercentString() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(getPriceChangePercent());
    }

    public int getTotalInfluence() {
        int totalInfluence = 0;
        for(String username : influencerUpCount.keySet()) {
            totalInfluence += (influencerUpCount.get(username) * 10);
        }
        for(String username : influencerDownCount.keySet()) {
            totalInfluence -= (influencerDownCount.get(username) * 10);
        }

        return totalInfluence;
    }
}
