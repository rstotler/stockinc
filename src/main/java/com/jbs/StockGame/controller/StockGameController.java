package com.jbs.StockGame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.jbs.StockGame.entity.Account;
import com.jbs.StockGame.entity.Group;
import com.jbs.StockGame.entity.StockListing;
import com.jbs.StockGame.service.AccountService;
import com.jbs.StockGame.service.GroupService;
import com.jbs.StockGame.service.ScraperService;
import com.jbs.StockGame.service.StockListingService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class StockGameController {
    @Autowired
    private AccountService accountService;
    private StockListingService stockListingService;
    private GroupService groupService;
    private ScraperService scraperService;

    @GetMapping("/index")
    public String loginSuccess(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Account account = accountService.findByUsername(userDetails.getUsername());

        model.addAttribute("accountService", accountService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCredits", account.getCredits());
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

        model.addAttribute("accountService", accountService);
        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("userCredits", account.getCredits());
        model.addAttribute("groups", groupService.findAll());

        return "game/groups";
    }

    @GetMapping("/createGroup")
    public String createGroup(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value="groupName", required=false) String groupName, @RequestParam(value="groupSymbol", required=false) String groupSymbol) {
        groupSymbol = groupSymbol.toUpperCase();

        Account account = accountService.findByUsername(userDetails.getUsername());
        boolean createGroupCheck = account.getCredits() >= 100.0;
        if(createGroupCheck) {
            for(Group group : groupService.findAll()) {
                if(userDetails.getUsername() == group.getFounder() || group.getMemberList().contains(userDetails.getUsername())) {
                    createGroupCheck = false;
                } else if(groupName.toLowerCase() == group.getName().toLowerCase() || groupSymbol == group.getSymbol()) {
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

    public boolean isInteger(String targetString) {
        try {
            Integer.parseInt(targetString);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
