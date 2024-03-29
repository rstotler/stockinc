package com.jbs.StockGame.service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.Account;
import com.jbs.StockGame.entity.HackAction;
import com.jbs.StockGame.entity.Message;
import com.jbs.StockGame.entity.StockListing;
import com.jbs.StockGame.entity.UnitQueue;
import com.jbs.StockGame.repository.AccountRepository;
import com.jbs.StockGame.repository.MessageRepository;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {
    private final StockListingService stockListingService;
    private final ScraperService scraperService;
    private PasswordEncoder passwordEncoder;
    private AccountRepository accountRepository;
    private MessageRepository messageRepository;

    @PostConstruct
    public void postConstruct() {
        // for(int i = 0; i < 3; i++) {
        //     scraperService.simulateUpdate();
        // }

        // ArrayList<String> nameList = new ArrayList<>(Arrays.asList("a", "adeaddecember", "shootloadshoot", "againwithlove", "charmofadeadpoet", "bleedmylove", "noisenkisses12", "leshshshock", "ourmidairlove", "rafiko", "thefallbyavalon"));
        // for(int i = 0; i < 11; i++) {
        //     Account account = new Account();
        //     account.setUsername(nameList.get(i));
        //     account.setPassword(nameList.get(i));
        //     register(account);

        //     for(int ii = 0; ii < new Random().nextInt(4); ii++) {
        //         int sNum = new Random().nextInt(stockListingService.findAll().size());
        //         StockListing s = stockListingService.findAll().get(sNum);
        //         account.getOwnedStock().put(s.getSymbol(), new Random().nextInt(10) + 1);
        //     }

        //     account.getOwnedUnits().put("Hacker", new Random().nextInt(5) + 1);
        // }
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

        account.setInfrastructureLevels(new HashMap<>());
        account.getInfrastructureLevels().put("Firewall", 1);

        account.setInfrastructureQueue(null);

        account.setInGroup(null);
        account.setHackTarget(null);
        account.setGroupHackers(0);

        account.setMessages(new ArrayList<>());
        
        accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findByUsername(String username) {
        Optional<Account> account = accountRepository.findById(username);
        if(account.isPresent()) {
            return account.get();
        }

        return null;
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

    public String getAbsoluteGainLossString(String username) {
        String amountString = getGainLossString(username);
        if(amountString.length() > 0 && amountString.charAt(0) == '-') {
            return amountString.substring(1);
        }
        return amountString;
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

        float currentPrice = targetStock.getPrice();
        float changePercent = ((targetStock.getNextPrice() - currentPrice) / currentPrice) * 100;
        
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
        Message message = new Message(title, content, date);

        Account account = findByUsername(username);
        account.getMessages().add(message);

        messageRepository.save(message);
    }

    public int getUnreadMessageCount(String username) {
        Account account = findByUsername(username);
        
        int unreadMessageCount = 0;
        for(Message message : account.getMessages()) {
            if(!message.isRead()) {
                unreadMessageCount++;
            }
        }

        return unreadMessageCount;
    }

    public int getAvailableInfluencerCount(String username) {
        Account account = findByUsername(username);

        int totalAvailableCount = 0;
        if(account.getOwnedUnits().containsKey("Influencer")) {
            totalAvailableCount = account.getOwnedUnits().get("Influencer");
        }
        
        for(StockListing stockListing : stockListingService.findAll()) {
            if(stockListing.getInfluencerUpCount().containsKey(username)) {
                totalAvailableCount -= stockListing.getInfluencerUpCount().get(username);
            }
            if(stockListing.getInfluencerDownCount().containsKey(username)) {
                totalAvailableCount -= stockListing.getInfluencerDownCount().get(username);
            }
        }

        return totalAvailableCount;
    }

    public int getAvailableHackerCount(String username) {
        Account account = findByUsername(username);

        int totalAvailableCount = 0;
        if(account.getOwnedUnits().containsKey("Hacker")) {
            totalAvailableCount = account.getOwnedUnits().get("Hacker");
        }

        if(account.getHackTarget() != null) {
            totalAvailableCount -= account.getHackTarget().getHackerCount();
        }
        
        if(account.getGroupHackers() > 0) {
            totalAvailableCount -= account.getGroupHackers();
        }

        return totalAvailableCount;
    }

    public int getAvailableAnalystCount(String username) {
        Account account = findByUsername(username);

        int availableCount = 0;
        if(account.getOwnedUnits().containsKey("Analyst")) {
            availableCount = account.getOwnedUnits().get("Analyst");
        }

        return availableCount;
    }

    public int getInfrastructureLevel(String username, String infrastructureType) {
        Account account = findByUsername(username);
        if(account.getInfrastructureLevels().containsKey(infrastructureType)) {
            return account.getInfrastructureLevels().get(infrastructureType);
        }

        return 0;
    }

    public HackAction getHackTarget(String username) {
        return findByUsername(username).getHackTarget();
    }

    public void updateQueues(String username) {
        Account account = findByUsername(username);
        LocalDateTime now = LocalDateTime.now();

        if(account.getTipsterQueue() != null) {
            LocalDateTime serviceStartTime = account.getTipsterQueue().getStartTime();
            long secondsPassed = ChronoUnit.SECONDS.between(serviceStartTime, now);
            if(secondsPassed >= account.getTipsterQueue().getCreateUnitLength()) {
                account.setTipsterQueue(null);
            }
        }

        if(account.getUnitQueue().size() > 0) {
            LocalDateTime creationStartTime = account.getUnitQueue().get(0).getStartTime();
            long secondsPassed = ChronoUnit.SECONDS.between(creationStartTime, now);
            updateUnitQueueUtility(account, secondsPassed);
        }

        if(account.getInfrastructureQueue() != null) {
            LocalDateTime upgradeStartTime = account.getInfrastructureQueue().getStartTime();
            long secondsPassed = ChronoUnit.SECONDS.between(upgradeStartTime, now);
            if(secondsPassed >= account.getInfrastructureQueue().getCreateUnitLength()) {
                int currentLevel = account.getInfrastructureLevels().get(account.getInfrastructureQueue().getUnitType());
                account.getInfrastructureLevels().put(account.getInfrastructureQueue().getUnitType(), currentLevel + 1);
                account.setInfrastructureQueue(null);
            }
        }

        accountRepository.save(account);
    }

    public void updateUnitQueueUtility(Account account, long secondsPassed) {
        int unitsToCreate = (int) (secondsPassed / account.getUnitQueue().get(0).getCreateUnitLength());
        if(unitsToCreate > 0) {
            if(unitsToCreate > account.getUnitQueue().get(0).getUnitCount()) {
                unitsToCreate = account.getUnitQueue().get(0).getUnitCount();
            }
            createUnits(account, unitsToCreate);
            secondsPassed -= (account.getUnitQueue().get(0).getCreateUnitLength() * unitsToCreate);

            if(unitsToCreate == account.getUnitQueue().get(0).getUnitCount()) {
                account.getUnitQueue().remove(0);
            } else {
                int newCount = account.getUnitQueue().get(0).getUnitCount() - unitsToCreate;
                account.getUnitQueue().get(0).setUnitCount(newCount);
            }
            
            if(account.getUnitQueue().size() > 0) {
                updateUnitQueueStartTimes(account);
                updateUnitQueueUtility(account, secondsPassed);
            }
        }
    }

    public void updateUnitQueueStartTimes(Account account) {
        for(UnitQueue unitQueueItem : account.getUnitQueue()) {
            unitQueueItem.setStartTime(LocalDateTime.now());
        }
    }

    public void createUnits(Account account, int unitCount) {
        String unitType = account.getUnitQueue().get(0).getUnitType();
        if(account.getOwnedUnits().containsKey(unitType)) {
            account.getOwnedUnits().put(unitType, account.getOwnedUnits().get(unitType) + unitCount);
        } else {
            account.getOwnedUnits().put(unitType, unitCount);
        }
    }
}
