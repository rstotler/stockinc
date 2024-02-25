package com.jbs.StockGame.entity;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.jbs.StockGame.repository.AccountRepository;

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
}
