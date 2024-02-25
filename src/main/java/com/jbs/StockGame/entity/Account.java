package com.jbs.StockGame.entity;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Account {
    @Id
    private String username;
    private String password;
    private String role;

    private float credits;
    private float lastInvestmentAmount;
    @ElementCollection
    private Map<String, Integer> ownedStock;

    @JdbcTypeCode(SqlTypes.JSON)
    private UnitQueue tipsterQueue;
    @ElementCollection
    private Map<String, Integer> ownedUnits;
    @ElementCollection
    private List<UnitQueue> unitQueue;
    @ElementCollection
    private Map<String, Integer> infrastructureLevels;
    @JdbcTypeCode(SqlTypes.JSON)
    private UnitQueue infrastructureQueue;

    @JdbcTypeCode(SqlTypes.JSON)
    private Group inGroup;
    @Transient
    private HackAction hackTarget;
    @Transient
    private int groupHackers;

    @ElementCollection
    private List<Message> messages;

    public String getCreditsString() {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(credits);
    }

    public int getOwnedStockQuantity(String stockSymbol) {
        if(ownedStock.containsKey(stockSymbol)) {
            return ownedStock.get(stockSymbol);
        } else {
            return 0;
        }
    }

    public void updateQueues() {
        LocalDateTime now = LocalDateTime.now();

        if(tipsterQueue != null) {
            LocalDateTime serviceStartTime = tipsterQueue.getStartTime();
            long secondsPassed = ChronoUnit.SECONDS.between(serviceStartTime, now);
            if(secondsPassed >= tipsterQueue.getCreateUnitLength()) {
                tipsterQueue = null;
            }
        }

        if(unitQueue.size() > 0) {
            LocalDateTime creationStartTime = unitQueue.get(0).getStartTime();
            long secondsPassed = ChronoUnit.SECONDS.between(creationStartTime, now);
            updateUnitQueueUtility(secondsPassed);
        }

        if(infrastructureQueue != null) {
            LocalDateTime upgradeStartTime = infrastructureQueue.getStartTime();
            long secondsPassed = ChronoUnit.SECONDS.between(upgradeStartTime, now);
            if(secondsPassed >= infrastructureQueue.getCreateUnitLength()) {
                int currentLevel = infrastructureLevels.get(infrastructureQueue.getUnitType());
                infrastructureLevels.put(infrastructureQueue.getUnitType(), currentLevel + 1);
                infrastructureQueue = null;
            }
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
