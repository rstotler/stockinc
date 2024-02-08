package com.jbs.StockGame.service;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class GameDataService {
    public Map<String, Float> unitPrices = new HashMap<>();
    {
        unitPrices.put("Hacker", 100.0f);
    }

    public Map<String, Integer> createUnitLength = new HashMap<>();
    {
        createUnitLength.put("Hacker", 5);
    }
}
