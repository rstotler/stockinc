package com.jbs.StockGame.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    private Map<String, Integer> ownedUnits;
    private List<UnitQueue> unitQueue;

    public int getOwnedStockQuantity(String stockSymbol) {
        if(ownedStock.containsKey(stockSymbol)) {
            return ownedStock.get(stockSymbol);
        } else {
            return 0;
        }
    }

    public void updateUnitQueue() {
        if(unitQueue.size() > 0) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime creationStartTime = unitQueue.get(0).getStartTime();
            long secondsPassed = ChronoUnit.SECONDS.between(creationStartTime, now);
            updateUnitQueueUtility(secondsPassed);
        }
    }

    public void updateUnitQueueUtility(long secondsPassed) {
        int unitsToCreate = (int) (secondsPassed / unitQueue.get(0).getCreateUnitLength());
        if(unitsToCreate > 0) {
            if(unitsToCreate > unitQueue.get(0).getUnitCount()) {
                unitsToCreate = unitQueue.get(0).getUnitCount();
            }
            createUnits(unitQueue.get(0).getUnitType(), unitsToCreate);
            secondsPassed -= (unitQueue.get(0).getCreateUnitLength() * unitsToCreate);

            if(unitsToCreate == unitQueue.get(0).getUnitCount()) {
                unitQueue.remove(0);
            } else {
                int newCount = unitQueue.get(0).getUnitCount() - unitsToCreate;
                unitQueue.get(0).setUnitCount(newCount);
            }
            
            if(unitQueue.size() > 0) {
                updateUnitQueueStartTimes();
                updateUnitQueueUtility(secondsPassed);
            }
        }
    }

    public void updateUnitQueueStartTimes() {
        for(UnitQueue unitQueueItem : unitQueue) {
            unitQueueItem.setStartTime(LocalDateTime.now());
        }
    }

    public void createUnits(String unitType, int unitCount) {
        if(ownedUnits.containsKey(unitType)) {
            ownedUnits.put(unitType, ownedUnits.get(unitType) + unitCount);
        } else {
            ownedUnits.put(unitType, unitCount);
        }
    }
}
