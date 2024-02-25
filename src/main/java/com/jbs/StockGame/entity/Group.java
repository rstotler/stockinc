package com.jbs.StockGame.entity;

import java.util.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Group {
    @Id
    private String symbol;
    private String name;
    private String founder;
    private List<String> memberList;
    private List<String> requestList;
    @Transient
    private HackAction hackTarget;

    public Group(String name, String symbol, String founder) {
        this.symbol = symbol;
        this.name = name;
        this.founder = founder;
        memberList = new ArrayList<>();
        requestList = new ArrayList<>();
        hackTarget = null;
    }
}
