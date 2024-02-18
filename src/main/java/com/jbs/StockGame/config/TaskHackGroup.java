package com.jbs.StockGame.config;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Component;

import com.jbs.StockGame.entity.Account;
import com.jbs.StockGame.entity.Group;
import com.jbs.StockGame.entity.HackAction;
import com.jbs.StockGame.entity.Message;
import com.jbs.StockGame.entity.StockListing;
import com.jbs.StockGame.service.AccountService;
import com.jbs.StockGame.service.GroupService;
import com.jbs.StockGame.service.StockListingService;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class TaskHackGroup implements Runnable {
    private AccountService accountService;
    private GroupService groupService;
    private StockListingService stockListingService;
    private HackAction hackAction;

    public TaskHackGroup(AccountService accountService, GroupService groupService, StockListingService stockListingService, HackAction hackAction) {
        this.accountService = accountService;
        this.groupService = groupService;
        this.stockListingService = stockListingService;
        this.hackAction = hackAction;
    }

    @Override
    public void run() {
        Account account = accountService.findByUsername(hackAction.getHackerUsername());
        Group targetGroup = groupService.findBySymbol(hackAction.getTargetGroupSymbol());

        // Calculate Hack Chance //
        int defenderAnalystCount = groupService.getAvailableAnalystCount(hackAction.getTargetGroupSymbol());
        float hackChance = 25 * (hackAction.getHackerCount() / (defenderAnalystCount + 0.0f));
        if(hackChance > 95.0) {
            hackChance = 95.0f;
        }

        // Hack Success //
        if(new Random().nextInt(101) <= hackChance) {
            Map<String, Integer> hackedStocks = new HashMap<>();
            float hackedStockTotalValue = 0.0f;

            int stealQuantity = 1 + (hackAction.getHackerCount() / 2);
            for(int i = 0; i < stealQuantity; i++) {
                int randomMemberIndex = new Random().nextInt(targetGroup.getMemberList().size() + 1);
                Account randomMemberAccount;
                if(randomMemberIndex == 0) {
                    randomMemberAccount = accountService.findByUsername(targetGroup.getFounder());
                } else {
                    randomMemberAccount = accountService.findByUsername(targetGroup.getMemberList().get(randomMemberIndex - 1));
                }

                String[] stockSymbols = (String[]) randomMemberAccount.getOwnedStock().keySet().toArray(new String[randomMemberAccount.getOwnedStock().size()]);
                if(randomMemberAccount.getOwnedStock().size() > 0) {
                    int randomStockIndex = new Random().nextInt(randomMemberAccount.getOwnedStock().size());
                    String randomStockSymbol = stockSymbols[randomStockIndex];
                    if(account.getOwnedStock().containsKey(randomStockSymbol)) {
                        account.getOwnedStock().put(randomStockSymbol, account.getOwnedStock().get(randomStockSymbol) + 1);
                    } else {
                        account.getOwnedStock().put(randomStockSymbol, 1);
                    }
                    
                    if(hackedStocks.containsKey(randomStockSymbol)) {
                        hackedStocks.put(randomStockSymbol, hackedStocks.get(randomStockSymbol) + 1);
                    } else {
                        hackedStocks.put(randomStockSymbol, 1);
                    }
                    StockListing hackedStock = stockListingService.findBySymbol(randomStockSymbol);
                    hackedStockTotalValue += hackedStock.getPrice();

                    if(randomMemberAccount.getOwnedStock().get(randomStockSymbol) == 1) {
                        randomMemberAccount.getOwnedStock().remove(randomStockSymbol);
                    } else {
                        randomMemberAccount.getOwnedStock().put(randomStockSymbol, randomMemberAccount.getOwnedStock().get(randomStockSymbol) - 1);
                    }
                }
            }

            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            String hackedStockTotalValueString = decimalFormat.format(hackedStockTotalValue);

            String messageTitle = "Hack against target " + hackAction.getTargetGroupSymbol() + " succeeded.";
            String messageContent = "Your hack against " + hackAction.getTargetGroupSymbol() + " succeeded. You stole";
            int i = 0;
            for(String hackedStockSymbol : hackedStocks.keySet()) {
                messageContent += " " + hackedStocks.get(hackedStockSymbol) + " " + hackedStockSymbol;
                if(i < hackedStocks.size() - 1 && hackedStocks.size() != 2) {
                    messageContent += ",";
                }
                if(i == hackedStocks.size() - 2) {
                    messageContent += " and";
                }
                i++;
            }
            messageContent += " for a total value of $" + hackedStockTotalValueString + ".";

            account.getMessages().add(new Message(messageTitle, messageContent, LocalDateTime.now()));
        }

        // Hack Failed //
        else {
            String messageTitle = "Hack against target " + hackAction.getTargetGroupSymbol() + " failed.";
            String messageContent = "Your hack against " + hackAction.getTargetGroupSymbol() + " failed.";
            account.getMessages().add(new Message(messageTitle, messageContent, LocalDateTime.now()));
        }

        // Send Target Group Founder Message //
        if(groupService.getAvailableAnalystCount(hackAction.getTargetGroupSymbol()) > 0) {
            Account targetGroupFounder = accountService.findByUsername(targetGroup.getFounder());
            String targetMessageTitle = "Unusual Server Activity Detected";
            String targetMessageContent = "Your analysts have noticed some suspicious activity on the company servers.";
            targetGroupFounder.getMessages().add(new Message(targetMessageTitle, targetMessageContent, LocalDateTime.now()));
        }

        account.setHackTarget(null);
    }
}
