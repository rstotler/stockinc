package com.jbs.StockGame.service;

import java.text.DecimalFormat;
import java.util.*;

import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.Group;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GroupService {
    private AccountService accountService;
    private List<Group> groups = new ArrayList<>();

    @PostConstruct
    public void postConstruct() {
        Group noKidHero = new Group("No Kid Hero", "NKH", accountService.findAll().get(1).getUsername());
        noKidHero.getMemberList().add(accountService.findAll().get(2).getUsername());
        noKidHero.getMemberList().add(accountService.findAll().get(3).getUsername());
        noKidHero.getMemberList().add(accountService.findAll().get(8).getUsername());
        noKidHero.getMemberList().add(accountService.findAll().get(9).getUsername());
        groups.add(noKidHero);

        Group theGirls = new Group("The Girls", "GIRL", accountService.findAll().get(4).getUsername());
        theGirls.getMemberList().add(accountService.findAll().get(5).getUsername());
        theGirls.getMemberList().add(accountService.findAll().get(6).getUsername());
        theGirls.getMemberList().add(accountService.findAll().get(7).getUsername());
        theGirls.getMemberList().add(accountService.findAll().get(10).getUsername());
        groups.add(theGirls);
    }

    public void create(String groupName, String groupSymbol, String founder) {
        groups.add(new Group(groupName, groupSymbol, founder));
    }

    public Group findBySymbol(String symbol) {
        return groups.stream().filter(group -> group.getSymbol().equals(symbol))
            .findFirst()
            .orElse(null);
    }

    public List<Group> findAll() {
        return groups;
    }

    public float getTotalValue(String symbol) {
        Group targetGroup = null;
        for(Group group : groups) {
            if(group.getSymbol() == symbol) {
                targetGroup = group;
                break;
            }
        }
        
        float totalValue = 0.0f;
        if(targetGroup != null) {
            totalValue += accountService.getTotalOwnedStockValue(targetGroup.getFounder());
            for(String memberUsername : targetGroup.getMemberList()) {
                totalValue += accountService.getTotalOwnedStockValue(memberUsername);
            }
        }
        
        return totalValue;
    }

    public String getTotalValueString(String symbol) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        float amount = getTotalValue(symbol);
        return decimalFormat.format(amount);
    }
}
