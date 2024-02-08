package com.jbs.StockGame.entity;

import java.time.*;

import lombok.Data;

@Data
public class UnitQueue {
    private String unitType;
    private float unitPrice;
    private int unitCount;
    private int createUnitLength;
    private LocalDateTime startTime; 

    public UnitQueue(String unitType, float unitPrice, int unitCount, int createUnitLength, LocalDateTime startTime) {
        this.unitType = unitType;
        this.unitPrice = unitPrice;
        this.unitCount = unitCount;
        this.createUnitLength = createUnitLength;
        this.startTime = startTime;
    }
}
