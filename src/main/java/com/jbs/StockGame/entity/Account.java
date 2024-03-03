package com.jbs.StockGame.entity;

import java.text.DecimalFormat;
import java.util.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @ElementCollection(fetch=FetchType.EAGER)
    private Map<String, Integer> ownedStock;

    @JdbcTypeCode(SqlTypes.JSON)
    private UnitQueue tipsterQueue;
    @ElementCollection(fetch=FetchType.EAGER)
    private Map<String, Integer> ownedUnits;
    @ElementCollection(fetch=FetchType.EAGER)
    private List<UnitQueue> unitQueue;
    @ElementCollection(fetch=FetchType.EAGER)
    private Map<String, Integer> infrastructureLevels;
    @JdbcTypeCode(SqlTypes.JSON)
    private UnitQueue infrastructureQueue;

    @JdbcTypeCode(SqlTypes.JSON)
    private Group inGroup;
    @JdbcTypeCode(SqlTypes.JSON)
    private HackAction hackTarget;
    private int groupHackers;

    @ElementCollection(fetch=FetchType.EAGER)
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
}
