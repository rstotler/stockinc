package com.jbs.StockGame.service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.Account;
import com.jbs.StockGame.entity.Message;
import com.jbs.StockGame.entity.StockListing;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {
    private final StockListingService stockListingService;
    private final ScraperService scraperService;
    private List<Account> accounts = new ArrayList<>();
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void postConstruct() {
        for(int i = 0; i < 3; i++) {
            scraperService.simulateUpdate();
        }

        ArrayList<String> nameList = new ArrayList<>(Arrays.asList("a", "adeaddecember", "shootloadshoot", "againwithlove", "charmofadeadpoet", "bleedmylove", "noisenkisses12", "leshshshock", "ourmidairlove", "rafiko", "thefallbyavalon"));
        for(int i = 0; i < 11; i++) {
            Account account = new Account();
            account.setUsername(nameList.get(i));
            account.setPassword(nameList.get(i));
            register(account);

            for(int ii = 0; ii < new Random().nextInt(4); ii++) {
                int sNum = new Random().nextInt(stockListingService.findAll().size());
                StockListing s = stockListingService.findAll().get(sNum);
                account.getOwnedStock().put(s.getSymbol(), new Random().nextInt(10) + 1);
            }

            for(int ii = 0 ; ii < 3; ii++) {
                account.getMessages().add(new Message(String.valueOf(ii), "Test " + ii, LocalDateTime.now()));
            }
        }
    }

    public void register(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole("User");

        account.setCredits(10000.0f);
        account.setLastInvestmentAmount(0.0f);
        account.setOwnedStock(new HashMap<>());

        account.setTipsterQueue(null);
        account.setOwnedUnits(new HashMap<>());
        account.setUnitQueue(new ArrayList<>());

        account.setMessages(new ArrayList<>());
        
        accounts.add(account);
    }

    public List<Account> findAll() {
        return accounts;
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

    public String getTotalInvestmentString(String username) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        float amount = getTotalInvestment(username);
        return decimalFormat.format(amount);
    }

    public float getGainLoss(String username) {
        Account account = findByUsername(username);
        return getTotalInvestment(username) - account.getLastInvestmentAmount();
    }

    public String getGainLossString(String username) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        float amount = getGainLoss(username);
        return decimalFormat.format(amount);
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

    public void generateTipsterReport(String username) {
        List<StockListing> listCopy = new ArrayList<>(stockListingService.findAll());
        Collections.shuffle(listCopy);
        StockListing targetStock = null;

        for(StockListing stockListing : listCopy) {
            if(stockListing.getPriceList().size() > 0 && stockListing.getNextPrice() > stockListing.getPriceList().get(stockListing.getPriceList().size() - 1)) {
                targetStock = stockListing;
                break;
            }
        }
        if(targetStock == null) {
            targetStock = listCopy.get(0);
        }

        int dayCount = 29;
        if(targetStock.getPriceList().size() < 29) {
            dayCount = targetStock.getPriceList().size();
        }
        float nextPrice = targetStock.getNextKeyCount();
        for(int i = 0; i < dayCount; i++) {
            nextPrice += targetStock.getKeyCountList().get(targetStock.getKeyCountList().size() - 1 - i);
        }
        nextPrice /= (dayCount + 1);

        float currentPrice = targetStock.getPrice();
        float changePercent = ((nextPrice - currentPrice) / currentPrice) * 100;
        
        String riseDropString = "";
        if(changePercent > 10.0) {
            riseDropString = "significant rise";
        } else if(changePercent < 10.0) {
            riseDropString = "significant drop";
        } else if(changePercent > 0) {
            riseDropString = "rise";
        } else if(changePercent < 0) {
            riseDropString = "drop";
        }

        String title = "Tipster Report";
        LocalDateTime date = LocalDateTime.now();
        String content = "I have it on good authority that " + targetStock.getName() + " (" + targetStock.getSymbol() + ") will see a " + riseDropString + " tomorrow.";
        
        Account account = findByUsername(username);
        account.getMessages().add(new Message(title, content, date));
    }

    public boolean unreadMessagesCheck(String username) {
        Account account = findByUsername(username);
        
        boolean unreadMessages = false;
        for(Message message : account.getMessages()) {
            if(!message.isRead()) {
                unreadMessages = true;
                break;
            }
        }

        return unreadMessages;
    }
}
