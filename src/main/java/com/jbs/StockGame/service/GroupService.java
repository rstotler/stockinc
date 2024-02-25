package com.jbs.StockGame.service;

import java.text.DecimalFormat;
import java.util.*;

import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.Account;
import com.jbs.StockGame.entity.Group;
import com.jbs.StockGame.repository.GroupRepository;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GroupService {
    private AccountService accountService;
    private GroupRepository groupRepository;

    @PostConstruct
    public void postConstruct() {
        // Group noKidHero = new Group("No Kid Hero", "NKH", accountService.findAll().get(1).getUsername());
        // noKidHero.getMemberList().add(accountService.findAll().get(2).getUsername());
        // noKidHero.getMemberList().add(accountService.findAll().get(3).getUsername());
        // noKidHero.getMemberList().add(accountService.findAll().get(8).getUsername());
        // noKidHero.getMemberList().add(accountService.findAll().get(9).getUsername());
        // for(String username : noKidHero.getMemberList()) {
        //     accountService.findByUsername(username).setInGroup(noKidHero);
        // }
        // groups.add(noKidHero);

        // Group theGirls = new Group("Girl Power", "GIRL", accountService.findAll().get(4).getUsername());
        // theGirls.getMemberList().add(accountService.findAll().get(5).getUsername());
        // theGirls.getMemberList().add(accountService.findAll().get(6).getUsername());
        // theGirls.getMemberList().add(accountService.findAll().get(7).getUsername());
        // theGirls.getMemberList().add(accountService.findAll().get(10).getUsername());
        // for(String username : theGirls.getMemberList()) {
        //     accountService.findByUsername(username).setInGroup(theGirls);
        // }
        // groups.add(theGirls);
    }

    public Group create(String groupName, String groupSymbol, String founder) {
        Group group = new Group(groupName, groupSymbol, founder);
        groupRepository.save(group);
        
        return group;
    }

    public Group findBySymbol(String symbol) {
        Optional<Group> group = groupRepository.findById(symbol);
        if(group.isPresent()) {
            return group.get();
        }

        return null;
    }

    public Group findByMember(String username) {
        Group group = null;
        for(Group listGroup : findAll()) {
            if(listGroup.getFounder().equals(username) || listGroup.getMemberList().contains(username)) {
                group = listGroup;
                break;
            }
        }

        return group;
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public List<String> getTotalMemberList(String symbol) {
        Group group = findBySymbol(symbol);

        List<String> memberList = new ArrayList<>();
        memberList.add(group.getFounder());
        memberList.addAll(group.getMemberList());

        return memberList;
    }

    public float getTotalValue(String symbol) {
        Group targetGroup = null;
        for(Group group : findAll()) {
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

    public void setGroupHackers(String symbol, int totalAmount) {
        Group group = findBySymbol(symbol);

        // Founder //
        int amount = accountService.getAvailableHackerCount(group.getFounder());
        if(amount > totalAmount) {
            amount = totalAmount;
        }
        Account account = accountService.findByUsername(group.getFounder());
        account.setGroupHackers(amount);
        totalAmount -= amount;

        // Group Members //
        for(String username : group.getMemberList()) {
            amount = accountService.getAvailableHackerCount(username);
            if(amount > totalAmount) {
                amount = totalAmount;
            }
            account = accountService.findByUsername(username);
            account.setGroupHackers(amount);
            totalAmount -= amount;

            if(totalAmount == 0) {
                break;
            }
        }
    }

    public int getAvailableHackerCount(String symbol) {
        Group group = findBySymbol(symbol);

        int availableCount = 0;
        if(group != null) {
            availableCount += accountService.getAvailableHackerCount(group.getFounder());
            for(String username : group.getMemberList()) {
                availableCount += accountService.getAvailableHackerCount(username);
            }
        }

        return availableCount;
    }

    public int getAvailableAnalystCount(String symbol) {
        Group group = findBySymbol(symbol);

        int availableCount = accountService.getAvailableAnalystCount(group.getFounder());
        for(String username : group.getMemberList()) {
            availableCount += accountService.getAvailableAnalystCount(username);
        }

        return availableCount;
    }
}