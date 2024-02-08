package com.jbs.StockGame.service;

import java.util.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.Account;
import com.jbs.StockGame.entity.StockListing;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {
    private StockListingService stockListingService;
    private List<Account> accounts = new ArrayList<>();
    private PasswordEncoder passwordEncoder;
    
    public void register(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole("User");
        account.setCredits(1000.0f);
        account.setLastInvestmentAmount(0.0f);
        account.setOwnedStock(new HashMap<>());
        account.setOwnedUnits(new HashMap<>());
        account.setUnitQueue(new ArrayList<>());
        accounts.add(account);
    }

    public Account findByUsername(String username) {
        return accounts.stream().filter(account -> account.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }

    public float getTotalInvestment(String username) {
        float amount = 0.0f;
        Account account = findByUsername(username);
        for(String symbol : account.getOwnedStock().keySet()) {
            StockListing stockListing = stockListingService.findBySymbol(symbol);
            amount += (stockListing.getPrice() * account.getOwnedStock().get(symbol));
        }

        return amount;
    }

    public float getGainLoss(String username) {
        Account account = findByUsername(username);
        return getTotalInvestment(username) - account.getLastInvestmentAmount();
    }

    public float getTotalOwnedStockValue(String username) {
        Account account = findByUsername(username);
        float totalValue = 0.0f;
        if(account != null) {
            for(String symbol : account.getOwnedStock().keySet()) {
                StockListing stockListing = stockListingService.findBySymbol(symbol);
                totalValue += stockListing.getPrice() * account.getOwnedStockQuantity(symbol);
            }
        }
        
        return totalValue;
    }

    public int getUnitCount(String username, String unitType) {
        Account account = findByUsername(username);
        
        if(account.getOwnedUnits().containsKey(unitType)) {
            return account.getOwnedUnits().get(unitType);
        } else {
            return 0;
        }
    }
}
