package com.jbs.StockGame.controller;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jbs.StockGame.config.TaskHackGroup;
import com.jbs.StockGame.entity.Account;
import com.jbs.StockGame.entity.Group;
import com.jbs.StockGame.entity.HackAction;
import com.jbs.StockGame.entity.Message;
import com.jbs.StockGame.entity.StockListing;
import com.jbs.StockGame.entity.UnitQueue;
import com.jbs.StockGame.service.AccountService;
import com.jbs.StockGame.service.GameDataService;
import com.jbs.StockGame.service.GroupService;
import com.jbs.StockGame.service.MessageService;
import com.jbs.StockGame.service.ScraperService;
import com.jbs.StockGame.service.StockListingService;

import lombok.AllArgsConstructor;

/* To-Do List:
 * 1 - Create Data Persistence Layer
*/

@Controller
@AllArgsConstructor
public class StockGameController {
    private final AccountService accountService;
    private final StockListingService stockListingService;
    private final GroupService groupService;
    private final ScraperService scraperService;
    private final GameDataService gameDataService;
    private final MessageService messageService;
    private final TaskScheduler taskScheduler;

    @GetMapping("/index")
    public String loginSuccess(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        account.updateQueues();

        HackAction groupHackTarget = null;
        for(Group group : groupService.findAll()) {
            if(group.getFounder().equals(userDetails.getUsername())) {
                if(group.getHackTarget() != null) {
                    groupHackTarget = group.getHackTarget();
                    break;
                }
            } else if(group.getMemberList().contains(userDetails.getUsername())) {
                if(group.getHackTarget() != null) {
                    groupHackTarget = group.getHackTarget();
                    break;
                }
            }
        }
        
        model.addAttribute("accountService", accountService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCredits", account.getCredits());
        model.addAttribute("userCreditsString", account.getCreditsString());
        model.addAttribute("hackTarget", accountService.getHackTarget(userDetails.getUsername()));
        model.addAttribute("groupHackTarget", groupHackTarget);
        model.addAttribute("stockListings", stockListingService.findAll());
        model.addAttribute("ownedStockString", account.getOwnedStock().toString());
        model.addAttribute("availableInfluencerCount", accountService.getAvailableInfluencerCount(userDetails.getUsername()));
        
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
                if(account.getOwnedStock().get(stockListing.getSymbol()) == 0) {
                    account.getOwnedStock().remove(stockListing.getSymbol());
                }

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

    @GetMapping("/influenceStock/{stock_name}")
    public String influenceStock(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("stock_name") String stockName, @RequestParam(value="amountString", required=false) String amountString, @RequestParam(value="influenceDirection", required=false) String influenceDirection) {
        int availableCount = accountService.getAvailableInfluencerCount(userDetails.getUsername());
        int influencerCount = Integer.valueOf(amountString);
        if(influencerCount > availableCount) {
            influencerCount = availableCount;
        }

        if(influencerCount > 0) {
            stockListingService.addInfluencers(userDetails.getUsername(), stockName, influencerCount, influenceDirection);
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
        HackAction groupHackTarget = null;
        List<String> memberList = new ArrayList<>();
        ArrayList<String> requestedJoinGroupList = new ArrayList<>();
        List<String> requestedUserList = new ArrayList<>();
        for(Group group : groupService.findAll()) {
            if(group.getFounder().equals(userDetails.getUsername())) {
                groupName = group.getName();
                groupSymbol = group.getSymbol();
                groupStatus = "Founder";
                memberList.add(userDetails.getUsername());
                memberList.addAll(group.getMemberList());
                requestedUserList = group.getRequestList();
                if(group.getHackTarget() != null) {
                    groupHackTarget = group.getHackTarget();
                }
            } else if(group.getMemberList().contains(userDetails.getUsername())) {
                groupStatus = "Member";
                if(group.getHackTarget() != null) {
                    groupHackTarget = group.getHackTarget();
                }
            } else if(group.getRequestList().contains(userDetails.getUsername())) {
                requestedJoinGroupList.add(group.getSymbol());
            }
        }

        model.addAttribute("accountService", accountService);
        model.addAttribute("groupService", groupService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCreditsString", account.getCreditsString());
        model.addAttribute("hackTarget", accountService.getHackTarget(userDetails.getUsername()));
        model.addAttribute("groups", groupService.findAll());
        model.addAttribute("groupName", groupName);
        model.addAttribute("groupSymbol", groupSymbol);
        model.addAttribute("groupStatus", groupStatus);
        model.addAttribute("memberList", memberList);
        model.addAttribute("requestedJoinGroupList", requestedJoinGroupList.toString());
        model.addAttribute("requestedUserList", requestedUserList);
        model.addAttribute("hackTarget", accountService.findByUsername(userDetails.getUsername()).getHackTarget());
        model.addAttribute("groupHackTarget", groupHackTarget);
        model.addAttribute("availableHackerCount", accountService.getAvailableHackerCount(userDetails.getUsername()));
        model.addAttribute("availableGroupHackerCount", groupService.getAvailableHackerCount(groupSymbol));

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
            account.setInGroup(groupService.findBySymbol(groupSymbol));

            // Remove Any Open Group Requests //
            for(Group group : groupService.findAll()) {
                if(group.getRequestList().contains(account.getUsername())) {
                    group.getRequestList().remove(account.getUsername());
                }
            }
        }
        
        return "redirect:/groups";
    }

    @GetMapping("/disbandGroup")
    public String disbandGroup(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="groupSymbol", required=false) String groupSymbol) {
        Group group = groupService.findBySymbol(groupSymbol);
        if(group != null && group.getFounder().equals(userDetails.getUsername())) {
            Account account = accountService.findByUsername(userDetails.getUsername());
            account.setInGroup(null);

            for(String memberUsername : group.getMemberList()) {
                Account memberAccount = accountService.findByUsername(memberUsername);
                memberAccount.setInGroup(null);
                memberAccount.setGroupHackers(0);
            }

            int targetGroupIndex = -1;
            List<Group> groups = groupService.findAll();
            for(int i = 0; i < groups.size(); i++) {
                if(groups.get(i).getSymbol().equals(groupSymbol)) {
                    targetGroupIndex = i;
                }

                if(groups.get(i).getHackTarget() != null && groups.get(i).getHackTarget().getTargetGroupSymbol().equals(groupSymbol)) {
                    groups.get(i).setHackTarget(null);
                    for(String groupMember : group.getMemberList()) {
                        accountService.findByUsername(groupMember).setGroupHackers(0);
                    }
                }
            }
            if(targetGroupIndex != -1) {
                groups.remove(targetGroupIndex);
            }

            for(Account otherAccount : accountService.findAll()) {
                if(otherAccount.getHackTarget() != null && otherAccount.getHackTarget().getTargetGroupSymbol().equals(groupSymbol)) {
                    otherAccount.setHackTarget(null);
                }
            }
        }

        return "redirect:/groups";
    }

    @GetMapping("/removeFromGroup")
    public @ResponseBody void removeFromGroup(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("groupSymbol") String groupSymbol, @RequestParam("targetUser") String targetUser) {
        Group group = groupService.findBySymbol(groupSymbol);
        if(group.getFounder().equals(userDetails.getUsername())
        && group.getMemberList().contains(targetUser)) {
            group.getMemberList().remove(targetUser);

            Account targetUserAccount = accountService.findByUsername(targetUser);
            targetUserAccount.setInGroup(null);
        }
    }

    @GetMapping("/acceptDenyFromGroup")
    public @ResponseBody void acceptDenyFromGroup(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("groupSymbol") String groupSymbol, @RequestParam("targetUser") String targetUser, @RequestParam("targetAction") String targetAction) {
        Group group = groupService.findBySymbol(groupSymbol);
        if(group.getFounder().equals(userDetails.getUsername())
        && group.getRequestList().contains(targetUser)) {
            if(targetAction.equals("Accept")) {
                group.getMemberList().add(targetUser);
                group.getRequestList().remove(targetUser);

                for(Group otherGroup : groupService.findAll()) {
                    if(otherGroup.getRequestList().contains(targetUser)) {
                        otherGroup.getRequestList().remove(targetUser);
                    }
                }

                Account joinedAccount = accountService.findByUsername(targetUser);
                if(joinedAccount.getHackTarget() != null) {
                    if(joinedAccount.getHackTarget().getTargetGroupSymbol().equals(groupSymbol)) {
                        joinedAccount.setHackTarget(null);
                    }
                    joinedAccount.setInGroup(group);
                }
            }
            else if(targetAction.equals("Deny")) {
                group.getRequestList().remove(targetUser);
            }
        }
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

    @SuppressWarnings("deprecation")
    @GetMapping("/startHackGroup/{group_symbol}")
    public String startHackGroup(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("group_symbol") String groupSymbol, @RequestParam(value="amountString", required=false) String amountString, @RequestParam(value="groupHack", required=false) String groupHackString) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        Group hackerGroup = groupService.findByMember(userDetails.getUsername());
        
        boolean groupHack = false;
        int hackerAmount = Integer.valueOf(amountString);
        int maxHackerAmount = accountService.getAvailableHackerCount(userDetails.getUsername());
        if(groupHackString != null && groupHackString.equals("on") && hackerGroup != null && hackerGroup.getFounder().equals(userDetails.getUsername())) {
            maxHackerAmount = groupService.getAvailableHackerCount(hackerGroup.getSymbol());
            groupHack = true;
        }

        if(hackerAmount > maxHackerAmount) {
            hackerAmount = maxHackerAmount;
        }

        Group targetGroup = groupService.findBySymbol(groupSymbol);
        if(!targetGroup.getFounder().equals(userDetails.getUsername()) && !targetGroup.getMemberList().contains(userDetails.getUsername()) && hackerAmount > 0
        && ((groupHack == false && account.getHackTarget() == null) || (groupHack == true && hackerGroup.getHackTarget() == null))) {
            
            // User Hack Group //
            if(groupHack == false) {
                HackAction hackAction = new HackAction(userDetails.getUsername(), "None", groupSymbol, hackerAmount, LocalDateTime.now());
                account.setHackTarget(hackAction);
                Date targetTime = new Date();
                targetTime.setSeconds(targetTime.getSeconds() + hackAction.getHackTimeLength());
                taskScheduler.schedule(new TaskHackGroup(accountService, groupService, stockListingService, hackAction), targetTime);
            }

            // Group Hack Group //
            else {
                HackAction groupHackAction = new HackAction("None", hackerGroup.getSymbol(), groupSymbol, hackerAmount, LocalDateTime.now());
                hackerGroup.setHackTarget(groupHackAction);
                Date targetTime = new Date();
                targetTime.setSeconds(targetTime.getSeconds() + groupHackAction.getHackTimeLength());
                taskScheduler.schedule(new TaskHackGroup(accountService, groupService, stockListingService, groupHackAction), targetTime);
                groupService.setGroupHackers(hackerGroup.getSymbol(), hackerAmount);
            }
        }

        return "redirect:/groups";
    }

    @GetMapping("/infrastructure")
    public String assets(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        account.updateQueues();

        HackAction groupHackTarget = null;
        for(Group group : groupService.findAll()) {
            if(group.getFounder().equals(userDetails.getUsername())) {
                if(group.getHackTarget() != null) {
                    groupHackTarget = group.getHackTarget();
                    break;
                }
            } else if(group.getMemberList().contains(userDetails.getUsername())) {
                if(group.getHackTarget() != null) {
                    groupHackTarget = group.getHackTarget();
                    break;
                }
            }
        }

        List<String> unitList = new ArrayList<>(Arrays.asList("Tipster", "Influencer", "Hacker", "Analyst"));
        List<String> infrastructureList = new ArrayList<>(Arrays.asList("Firewall"));

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
        model.addAttribute("hackTarget", accountService.getHackTarget(userDetails.getUsername()));
        model.addAttribute("groupHackTarget", groupHackTarget);

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

        model.addAttribute("infrastructureList", infrastructureList);
        model.addAttribute("infrastructurePrices", gameDataService.infrastructurePrices.toString());
        model.addAttribute("infrastructureLevels", account.getInfrastructureLevels().toString());
        model.addAttribute("infrastructureQueue", account.getInfrastructureQueue());
        
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

    @GetMapping("/upgradeInfrastructure")
    public String upgradeInfrastructure(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="infrastructureType", required=false) String infrastructureType) {
        Account account = accountService.findByUsername(userDetails.getUsername());

        if(account.getInfrastructureQueue() == null && account.getCredits() >= gameDataService.infrastructurePrices.get(infrastructureType)) {
            account.setInfrastructureQueue(new UnitQueue(infrastructureType, gameDataService.infrastructurePrices.get(infrastructureType), 1, gameDataService.infrastructureUpgradeLength.get(infrastructureType), LocalDateTime.now()));
            account.setCredits(account.getCredits() - gameDataService.infrastructurePrices.get(infrastructureType));
        }

        return "redirect:/infrastructure";
    }

    @GetMapping("/messages")
    public String messages(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());
        account.updateQueues();

        HackAction groupHackTarget = null;
        for(Group group : groupService.findAll()) {
            if(group.getFounder().equals(userDetails.getUsername())) {
                if(group.getHackTarget() != null) {
                    groupHackTarget = group.getHackTarget();
                    break;
                }
            } else if(group.getMemberList().contains(userDetails.getUsername())) {
                if(group.getHackTarget() != null) {
                    groupHackTarget = group.getHackTarget();
                    break;
                }
            }
        }

        List<Message> messages = new ArrayList<>(account.getMessages());
        Collections.reverse(messages);

        model.addAttribute("accountService", accountService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCreditsString", account.getCreditsString());
        model.addAttribute("hackTarget", accountService.getHackTarget(userDetails.getUsername()));
        model.addAttribute("groupHackTarget", groupHackTarget);
        model.addAttribute("messages", messages);
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
    public String deleteMessage(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="messageIndex", required=false) String messageIndexString) {
        Account account = accountService.findByUsername(userDetails.getUsername());

        if(messageIndexString.equals("All")) {
            account.getMessages().clear();
        } else {
            int messageIndex = account.getMessages().size() - Integer.valueOf(messageIndexString) - 1;
            for(int i = 0; i < account.getMessages().size(); i++) {
                if(messageIndex == i) {
                    account.getMessages().remove(i);
                    break;
                }
            }
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
