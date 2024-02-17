package com.jbs.StockGame.entity;

import java.util.*;

import lombok.Data;

@Data
public class Group {
    private String name;
    private String symbol;
    private String founder;
    private List<String> memberList;
    private List<String> requestList;

    public Group(String name, String symbol, String founder) {
        this.name = name;
        this.symbol = symbol;
        this.founder = founder;
        memberList = new ArrayList<>();
        requestList = new ArrayList<>();
    }
}
