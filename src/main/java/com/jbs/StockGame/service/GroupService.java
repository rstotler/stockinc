package com.jbs.StockGame.service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.Group;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GroupService {
    private AccountService accountService;
    private List<Group> groups = new ArrayList<>();

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
}
