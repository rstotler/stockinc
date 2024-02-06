package com.jbs.StockGame.entity;

import java.util.List;

import lombok.Data;

@Data
public class Group {
    private String name;
    private String symbol;
    private String founder;
    private List<String> memberList;

    public Group(String name, String symbol, String founder) {
        this.name = name;
        this.symbol = symbol;
        this.founder = founder;
    }
}
