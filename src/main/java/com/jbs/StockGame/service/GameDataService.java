package com.jbs.StockGame.service;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class GameDataService {
    public Map<String, Float> servicePrices = new HashMap<>();
    {
        servicePrices.put("Tipster", 125.0f);
    }

    public Map<String, Integer> serviceResetLength = new HashMap<>();
    {
        serviceResetLength.put("Tipster", 3);
    }
    
    public Map<String, Float> unitPrices = new HashMap<>();
    {
        unitPrices.put("Influencer", 100.0f);
        unitPrices.put("Hacker", 100.0f);
        unitPrices.put("Analyst", 100.0f);
    }

    public Map<String, Integer> createUnitLength = new HashMap<>();
    {
        createUnitLength.put("Influencer", 3);
        createUnitLength.put("Hacker", 3);
        createUnitLength.put("Analyst", 3);
    }

    public Map<String, Float> infrastructurePrices = new HashMap<>();
    {
        infrastructurePrices.put("Firewall", 150.0f);
    }

    public Map<String, Integer> infrastructureUpgradeLength = new HashMap<>();
    {
        infrastructureUpgradeLength.put("Firewall", 3);
    }
}
