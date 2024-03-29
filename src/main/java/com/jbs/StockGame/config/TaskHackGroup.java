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
import com.jbs.StockGame.repository.AccountRepository;
import com.jbs.StockGame.repository.GroupRepository;
import com.jbs.StockGame.repository.MessageRepository;
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
    private AccountRepository accountRepository;
    private GroupRepository groupRepository;
    private MessageRepository messageRepository;
    private HackAction hackAction;

    public TaskHackGroup(AccountService accountService, GroupService groupService, StockListingService stockListingService, AccountRepository accountRepository, GroupRepository groupRepository, MessageRepository messageRepository, HackAction hackAction) {
        this.accountService = accountService;
        this.groupService = groupService;
        this.stockListingService = stockListingService;
        this.accountRepository = accountRepository;
        this.groupRepository = groupRepository;
        this.messageRepository = messageRepository;
        this.hackAction = hackAction;
    }

    @Override
    public void run() {
        Group targetGroup = groupService.findBySymbol(hackAction.getTargetGroupSymbol());
        if(targetGroup != null) {
            
            // Calculate Hack Chance //
            int defenderAnalystCount = groupService.getAvailableAnalystCount(hackAction.getTargetGroupSymbol());
            float hackChance = 25 * (hackAction.getHackerCount() / (defenderAnalystCount + 0.0f));
            if(hackChance > 95.0) {
                hackChance = 95.0f;
            }

            String[] accountList;
            int accountIndex = 0;
            Map<String, Map<String, Integer>> hackedStocks = new HashMap<>();
            Map<String, Float> hackedStockTotalValue = new HashMap<>();
            if(!hackAction.getHackerUsername().equals("None")) {
                accountList = new String[] {hackAction.getHackerUsername()};
                hackedStocks.put(hackAction.getHackerUsername(), new HashMap<>());
                hackedStockTotalValue.put(hackAction.getHackerUsername(), 0.0f);
            } else {
                Group hackingGroup = groupService.findBySymbol(hackAction.getHackerGroupSymbol());
                accountList = new String[1 + hackingGroup.getMemberList().size()];
                accountList[0] = hackingGroup.getFounder();
                hackedStocks.put(accountList[0], new HashMap<>());
                hackedStockTotalValue.put(accountList[0], 0.0f);
                for(int i = 1; i < hackingGroup.getMemberList().size() + 1; i++) {
                    accountList[i] = hackingGroup.getMemberList().get(i - 1);
                    hackedStocks.put(accountList[i], new HashMap<>());
                    hackedStockTotalValue.put(accountList[i], 0.0f);
                }
            }

            Account account = null;
            String hackString = "Hack";
            if(hackAction.getHackerUsername().equals("None")) {
                hackString = "Group hack";
            }

            // Hack Success //
            if(new Random().nextInt(101) <= hackChance) {
                int stealQuantity = 1 + (hackAction.getHackerCount() / 2);
                for(int i = 0; i < stealQuantity; i++) {
                    int randomMemberIndex = new Random().nextInt(targetGroup.getMemberList().size() + 1);
                    Account randomMemberAccount;
                    if(randomMemberIndex == 0) {
                        randomMemberAccount = accountService.findByUsername(targetGroup.getFounder());
                    } else {
                        randomMemberAccount = accountService.findByUsername(targetGroup.getMemberList().get(randomMemberIndex - 1));
                    }

                    account = accountService.findByUsername(accountList[accountIndex++]);
                    if(accountIndex == accountList.length) {
                        accountIndex = 0;
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

                        if(hackedStocks.get(account.getUsername()).containsKey(randomStockSymbol)) {
                            hackedStocks.get(account.getUsername()).put(randomStockSymbol, hackedStocks.get(account.getUsername()).get(randomStockSymbol) + 1);
                        } else {
                            hackedStocks.get(account.getUsername()).put(randomStockSymbol, 1);
                        }
                        StockListing hackedStock = stockListingService.findBySymbol(randomStockSymbol);
                        hackedStockTotalValue.put(account.getUsername(), hackedStockTotalValue.get(account.getUsername()) + hackedStock.getPrice());

                        if(randomMemberAccount.getOwnedStock().get(randomStockSymbol) == 1) {
                            randomMemberAccount.getOwnedStock().remove(randomStockSymbol);
                        } else {
                            randomMemberAccount.getOwnedStock().put(randomStockSymbol, randomMemberAccount.getOwnedStock().get(randomStockSymbol) - 1);
                        }

                        randomMemberAccount.setLastInvestmentAmount(randomMemberAccount.getLastInvestmentAmount() - hackedStock.getPrice());
                        if(randomMemberAccount.getLastInvestmentAmount() <= 0.01) {
                            randomMemberAccount.setLastInvestmentAmount(0.0f);
                        }
                    }

                    accountRepository.save(account);
                    accountRepository.save(randomMemberAccount);
                }

                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                
                for(int hackerIndex = 0; hackerIndex < accountList.length; hackerIndex++) {
                    String messageTitle = hackString + " against target " + hackAction.getTargetGroupSymbol() + " succeeded.";
                    String messageContent = "";

                    if(hackedStocks.size() > 0) {
                        messageContent = "Your " + hackString.toLowerCase() + " against " + hackAction.getTargetGroupSymbol() + " succeeded. You stole";
                        int i = 0;
                        for(String hackedStockSymbol : hackedStocks.get(accountList[hackerIndex]).keySet()) {
                            messageContent += " " + hackedStocks.get(accountList[hackerIndex]).get(hackedStockSymbol) + " " + hackedStockSymbol;
                            if(i < hackedStocks.get(accountList[hackerIndex]).size() - 1 && hackedStocks.get(accountList[hackerIndex]).size() != 2) {
                                messageContent += ",";
                            }
                            if(i == hackedStocks.get(accountList[hackerIndex]).size() - 2) {
                                messageContent += " and";
                            }
                            i++;
                        }
                        String hackedStockTotalValueString = decimalFormat.format(hackedStockTotalValue.get(accountList[hackerIndex]));
                        messageContent += " for a total value of $" + hackedStockTotalValueString + ".";
                    } else {
                        messageContent = "Your " + hackString.toLowerCase() + " against " + hackAction.getTargetGroupSymbol() + " succeeded, but you didn't receive any stocks.";
                    }
                    
                    account = accountService.findByUsername(accountList[hackerIndex]);
                    Message message = new Message(messageTitle, messageContent, LocalDateTime.now());
                    account.getMessages().add(message);
                    messageRepository.save(message);
                    accountRepository.save(account);
                }
            }

            // Hack Failed //
            else {
                String messageTitle = hackString + " against target " + hackAction.getTargetGroupSymbol() + " failed.";
                String messageContent = "Your " + hackString.toLowerCase() + " against " + hackAction.getTargetGroupSymbol() + " failed.";
                for(int hackerIndex = 0; hackerIndex < accountList.length; hackerIndex++) {
                    account = accountService.findByUsername(accountList[hackerIndex]);
                    Message message = new Message(messageTitle, messageContent, LocalDateTime.now());
                    account.getMessages().add(message);
                    messageRepository.save(message);
                    accountRepository.save(account);
                }
            }

            // Send Target Group Members Message //
            if(groupService.getAvailableAnalystCount(hackAction.getTargetGroupSymbol()) > 0) {
                String targetMessageTitle = "Unusual Server Activity Detected";
                String targetMessageContent = "Your analysts have noticed some suspicious activity on the company servers.";
                for(String targetGroupMemberString : groupService.getTotalMemberList(hackAction.getTargetGroupSymbol())) {
                    Account targetGroupMember = accountService.findByUsername(targetGroupMemberString);
                    Message message = new Message(targetMessageTitle, targetMessageContent, LocalDateTime.now());
                    targetGroupMember.getMessages().add(message);
                    messageRepository.save(message);
                    accountRepository.save(targetGroupMember);
                }
            }

            // Reset Hack Target & Group Hackers //
            if(!hackAction.getHackerUsername().equals("None")) {
                account = accountService.findByUsername(hackAction.getHackerUsername());
                account.setHackTarget(null);
                accountRepository.save(account);
            } else {
                Group group = groupService.findBySymbol(hackAction.getHackerGroupSymbol());
                group.setHackTarget(null);
                groupRepository.save(group);
                
                Account founderAccount = accountService.findByUsername(group.getFounder());
                founderAccount.setGroupHackers(0);
                accountRepository.save(founderAccount);

                for(String memberUsername : group.getMemberList()) {
                    Account memberAccount = accountService.findByUsername(memberUsername);
                    memberAccount.setGroupHackers(0);
                    accountRepository.save(memberAccount);
                }
            }
        }
    }
}
