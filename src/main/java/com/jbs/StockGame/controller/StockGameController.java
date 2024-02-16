package com.jbs.StockGame.controller;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jbs.StockGame.entity.Account;
import com.jbs.StockGame.entity.Group;
import com.jbs.StockGame.entity.StockListing;
import com.jbs.StockGame.entity.UnitQueue;
import com.jbs.StockGame.service.AccountService;
import com.jbs.StockGame.service.GameDataService;
import com.jbs.StockGame.service.GroupService;
import com.jbs.StockGame.service.MessageService;
import com.jbs.StockGame.service.ScraperService;
import com.jbs.StockGame.service.StockListingService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class StockGameController {
    private final AccountService accountService;
    private final StockListingService stockListingService;
    private final GroupService groupService;
    private final ScraperService scraperService;
    private final GameDataService gameDataService;
    private final MessageService messageService;

    @GetMapping("/index")
    public String loginSuccess(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        account.updateQueues();

        model.addAttribute("accountService", accountService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCredits", account.getCredits());
        model.addAttribute("userCreditsString", account.getCreditsString());
        model.addAttribute("stockListings", stockListingService.findAll());
        model.addAttribute("ownedStockString", account.getOwnedStock().toString());
        
        return "game/index";
    }

    @GetMapping("/buyStock/{stock_name}")
    public String buyStock(@PathVariable("stock_name") String stockName, @RequestParam(value="amountString", required=false) String amountString, @AuthenticationPrincipal UserDetails userDetails) {
        if(!amountString.isEmpty() && isInteger(amountString)) {
            int amount = Integer.parseInt(amountString);
            StockListing stockListing = stockListingService.findByName(stockName);
            Account account = accountService.findByUsername(userDetails.getUsername());
            
            if(amount * stockListing.getPrice() > account.getCredits()) {
                amount = (int) (account.getCredits() / stockListing.getPrice());
            }

            int ownedAmount = 0;
            if(account.getOwnedStock().containsKey(stockListing.getSymbol())) {
                ownedAmount = account.getOwnedStock().get(stockListing.getSymbol());
            }

            account.getOwnedStock().put(stockListing.getSymbol(), ownedAmount + amount);
            account.setCredits(account.getCredits() - (amount * stockListing.getPrice()));

            account.setLastInvestmentAmount(account.getLastInvestmentAmount() + (amount * stockListing.getPrice()));
        }

        return "redirect:/index";
    }

    @GetMapping("/sellStock/{stock_name}")
    public String sellStock(@PathVariable("stock_name") String stockName, @RequestParam(value="amountString", required=false) String amountString, @AuthenticationPrincipal UserDetails userDetails) {
        if(!amountString.isEmpty() && isInteger(amountString)) {
            int amount = Integer.parseInt(amountString);
            StockListing stockListing = stockListingService.findByName(stockName);
            Account account = accountService.findByUsername(userDetails.getUsername());

            if(account.getOwnedStock().containsKey(stockListing.getSymbol())) {
                if(amount > account.getOwnedStockQuantity(stockListing.getSymbol())) {
                    amount = account.getOwnedStockQuantity(stockListing.getSymbol());
                }

                int newAmount = account.getOwnedStockQuantity(stockListing.getSymbol()) - amount;
                account.getOwnedStock().put(stockListing.getSymbol(), newAmount);
                account.setCredits(account.getCredits() + (stockListing.getPrice() * amount));

                if(account.getLastInvestmentAmount() > 0) {
                    account.setLastInvestmentAmount(account.getLastInvestmentAmount() - (stockListing.getPrice() * amount));
                    if(account.getLastInvestmentAmount() < 0) {
                        account.setLastInvestmentAmount(0.0f);
                    }
                }
            }
        }

        return "redirect:/index";
    }

    @GetMapping("/generatePrice/{stock_name}")
    public String generatePrice(@PathVariable("stock_name") String stockName) {
        scraperService.generatePrice(stockName);
        return "redirect:/index";
    }

    @GetMapping("/simulateUpdate")
    public String simulateUpdate() {
        scraperService.simulateUpdate();
        return "redirect:/index";
    }

    @GetMapping("/groups")
    public String groups(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        account.updateQueues();

        String groupName = "None";
        String groupSymbol = "None";
        String groupStatus = "None";
        ArrayList<String> requestedJoinGroupList = new ArrayList<>();
        List<String> requestedUserList = new ArrayList<>();
        for(Group group : groupService.findAll()) {
            if(group.getFounder().equals(userDetails.getUsername())) {
                groupName = group.getName();
                groupSymbol = group.getSymbol();
                groupStatus = "Founder";
                requestedUserList = group.getRequestList();
            } else if(group.getMemberList().contains(userDetails.getUsername())) {
                groupStatus = "Member";
            } else if(group.getRequestList().contains(userDetails.getUsername())) {
                requestedJoinGroupList.add(group.getSymbol());
            }
        }

        model.addAttribute("accountService", accountService);
        model.addAttribute("groupService", groupService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCreditsString", account.getCreditsString());
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("groupName", groupName);
        model.addAttribute("groupSymbol", groupSymbol);
        model.addAttribute("groupStatus", groupStatus);
        model.addAttribute("requestedJoinGroupList", requestedJoinGroupList.toString());
        model.addAttribute("requestedUserList", requestedUserList);

        return "game/groups";
    }

    @GetMapping("/createGroup")
    public String createGroup(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="groupName", required=false) String groupName, @RequestParam(value="groupSymbol", required=false) String groupSymbol) {
        groupSymbol = groupSymbol.toUpperCase();

        Account account = accountService.findByUsername(userDetails.getUsername());
        boolean createGroupCheck = account.getCredits() >= 100.0 && groupName.length() > 0 && groupSymbol.length() > 0;
        if(createGroupCheck) {
            for(Group group : groupService.findAll()) {
                if(userDetails.getUsername().equals(group.getFounder()) || group.getMemberList().contains(userDetails.getUsername())) {
                    createGroupCheck = false;
                } else if(groupName.toLowerCase().equals(group.getName().toLowerCase()) || groupSymbol.equals(group.getSymbol())) {
                    createGroupCheck = false;
                }
            }
        }
        
        if(createGroupCheck) {
            account.setCredits(account.getCredits() - 100.0f);
            groupService.create(groupName, groupSymbol, userDetails.getUsername());
        }
        
        return "redirect:/groups";
    }

    @GetMapping("/disbandGroup")
    public String disbandGroup(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="groupSymbol", required=false) String groupSymbol) {
        Group group = groupService.findBySymbol(groupSymbol);
        if(group != null && group.getFounder().equals(userDetails.getUsername()))  {
            List<Group> groups = groupService.findAll();
            for(int i = 0; i < groups.size(); i++) {
                if(groups.get(i).getSymbol().equals(groupSymbol)) {
                    groups.remove(i);
                    break;
                }
            }
        }

        return "redirect:/groups";
    }

    @GetMapping("/requestJoinGroup")
    public String requestJoinGroup(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="groupSymbol", required=false) String groupSymbol) {
        boolean inGroupCheck = false;
        for(Group group : groupService.findAll()) {
            if(group.getFounder().equals(userDetails.getUsername())
            || group.getMemberList().contains(userDetails.getUsername())
            || (group.getSymbol().equals(groupSymbol) && group.getRequestList().contains(userDetails.getUsername()))) {
                inGroupCheck = true;
                break;
            }
        }

        if(!inGroupCheck) {
            Group targetGroup = groupService.findBySymbol(groupSymbol);
            targetGroup.getRequestList().add(userDetails.getUsername());
        }

        return "redirect:/groups";
    }

    @GetMapping("/infrastructure")
    public String assets(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        account.updateQueues();

        List<String> unitList = new ArrayList<>(Arrays.asList("Tipster", "Hacker"));

        String tipsterCooldown = "None";
        LocalDateTime tipsterCooldownTime = null;
        int tipsterCooldownTimeLength = 0;
        if(account.getTipsterQueue() != null) {
            tipsterCooldown = "Tipster";
            tipsterCooldownTime = account.getTipsterQueue().getStartTime();
            tipsterCooldownTimeLength = account.getTipsterQueue().getCreateUnitLength();
        }

        String creatingUnitType = "None";
        LocalDateTime creatingUnitTime = null;
        int creatingUnitTimeLength = 0;
        if(account.getUnitQueue().size() > 0) {
            creatingUnitType = account.getUnitQueue().get(0).getUnitType();
            creatingUnitTime = account.getUnitQueue().get(0).getStartTime();
            creatingUnitTimeLength = account.getUnitQueue().get(0).getCreateUnitLength();
        }
        
        model.addAttribute("accountService", accountService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCreditsString", account.getCreditsString());

        model.addAttribute("servicePrices", gameDataService.servicePrices.toString());
        model.addAttribute("tipsterCooldown", tipsterCooldown);
        model.addAttribute("tipsterCooldownTime", tipsterCooldownTime);
        model.addAttribute("tipsterCooldownTimeLength", tipsterCooldownTimeLength);
        
        model.addAttribute("unitList", unitList);
        model.addAttribute("unitPrices", gameDataService.unitPrices.toString());
        model.addAttribute("unitCounts", account.getOwnedUnits().toString());
        model.addAttribute("creatingUnitType", creatingUnitType);
        model.addAttribute("creatingUnitTime", creatingUnitTime);
        model.addAttribute("creatingUnitTimeLength", creatingUnitTimeLength);
        model.addAttribute("unitQueue", account.getUnitQueue());
        
        return "game/infrastructure";
    }

    @GetMapping("/buyTipster")
    public String buyTipster(@AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());

        if(account.getTipsterQueue() == null && account.getCredits() >= gameDataService.servicePrices.get("Tipster")) {
            account.setTipsterQueue(new UnitQueue("Tipster", 0, 0, gameDataService.serviceResetLength.get("Tipster"), LocalDateTime.now()));
            account.setCredits(account.getCredits() - gameDataService.servicePrices.get("Tipster"));
            accountService.generateTipsterReport(userDetails.getUsername());
        }

        return "redirect:/infrastructure"; 
    }

    @GetMapping("/buyUnits")
    public String buyUnits(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="unitType", required=false) String unitType, @RequestParam(value="unitCount", required=false) int unitCount) {
        if(gameDataService.unitPrices.containsKey(unitType)) {
            Account account = accountService.findByUsername(userDetails.getUsername());
            int maxBuyAmount = (int) (account.getCredits() / gameDataService.unitPrices.get(unitType));
            if(unitCount > maxBuyAmount) {
                unitCount = maxBuyAmount;
            }

            if(unitCount > 0) {
                account.getUnitQueue().add(new UnitQueue(unitType, gameDataService.unitPrices.get(unitType), unitCount, gameDataService.createUnitLength.get(unitType), LocalDateTime.now()));
                float totalCost = gameDataService.unitPrices.get(unitType) * unitCount;
                account.setCredits(account.getCredits() - totalCost);
            }
        }

        return "redirect:/infrastructure";
    }

    @GetMapping("/messages")
    public String messages(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        account.updateQueues();

        model.addAttribute("accountService", accountService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCreditsString", account.getCreditsString());
        model.addAttribute("messages", account.getMessages());
        model.addAttribute("messageService", messageService);

        return "game/messages"; 
    }

    @GetMapping("/clickMessage")
    public @ResponseBody void clickMessage(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("messageIndex") String messageIndexString) {
        int messageIndex = Integer.valueOf(messageIndexString);
        Account account = accountService.findByUsername(userDetails.getUsername());
        if(messageIndex < account.getMessages().size() && !account.getMessages().get(messageIndex).isRead()) {
            account.getMessages().get(messageIndex).setRead(true);
        }
    }

    @GetMapping("/deleteMessage")
    public String deleteMessage(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="messageIndex", required=false) int messageIndex) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        if(messageIndex <= account.getMessages().size() - 1) {
            account.getMessages().remove(messageIndex);
        }

        return "redirect:/messages";
    }

    public boolean isInteger(String targetString) {
        try {
            Integer.parseInt(targetString);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
