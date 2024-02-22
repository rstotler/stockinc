package com.jbs.StockGame.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class HackAction {
    private String hackerUsername;
    private String hackerGroupSymbol;
    private String targetGroupSymbol;
    private int hackerCount;
    private LocalDateTime startTime;
    private int hackTimeLength;

    public HackAction(String hackerUsername, String hackerGroupSymbol, String targetGroupSymbol, int hackerCount, LocalDateTime startTime) {
        this.hackerUsername = hackerUsername;
        this.hackerGroupSymbol = hackerGroupSymbol;
        this.targetGroupSymbol = targetGroupSymbol;
        this.hackerCount = hackerCount;
        this.startTime = startTime;

        hackTimeLength = 60;
    }
}
