package com.jbs.StockGame.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.StockListing;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ScraperService {
    private StockListingService stockListingService;

    public void generatePrice(String stockName) {
        System.out.println("Generating Price For: " + stockName);

        ArrayList<String> titleList = new ArrayList<>();
        try{
            Document doc = Jsoup.connect("https://brutalist.report/").get();
            Elements elementsContainer = doc.getElementsByClass("brutal-grid");
            Elements elementsItems = elementsContainer.first().getElementsByTag("li");
            for(Element element : elementsItems) {
               titleList.add(" " + element.text().toLowerCase());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        int count = 0;
        List<String> keyList = stockListingService.findByName(stockName).getKeyList();
        for(String stockKey : keyList) {
            for(String title : titleList) {
                if(title.contains(" " + stockKey.toLowerCase() + " ")) {
                    count += 1;
                }
            }
        }

        System.out.println("Mentions Found: " + count);
        stockListingService.findByName(stockName).setPrice(count);
    }

    public void simulateUpdateOld() {
        System.out.println("Simulating Price Update");

        ArrayList<String> titleList = new ArrayList<>();
        try{
            Document doc = Jsoup.connect("https://brutalist.report/").get();
            Elements elementsContainer = doc.getElementsByClass("brutal-grid");
            Elements elementsItems = elementsContainer.first().getElementsByTag("li");
            for(Element element : elementsItems) {
               titleList.add(" " + element.text().toLowerCase());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        for(StockListing stockListing : stockListingService.findAll()) {
            int count = 0;
            List<String> keyList = stockListing.getKeyList();
            for(String stockKey : keyList) {
                for(String title : titleList) {
                    if(title.contains(" " + stockKey.toLowerCase() + " ")) {
                        count += 1;
                    }
                }
            }

            // stockListing.setPriceChange(count);
            // stockListing.setPrice(stockListing.getPrice() + count);
        }
    }

    public void simulateUpdate() {

        // Set Price Change //
        for(StockListing stockListing : stockListingService.findAll()) {
            if(stockListing.getAverageDayCount() > 0) {
                float newStockPrice = stockListing.getNextPriceChange();
                for(Float price : stockListing.getPriceList()) {
                    newStockPrice += price;
                }
                if(stockListing.getAverageDayCount() > 0) {
                    newStockPrice /= stockListing.getAverageDayCount();
                }
                if(newStockPrice < 0.0) {
                    newStockPrice = 0.0f;
                }
                stockListing.getPriceList().add(newStockPrice);

                if(stockListing.getAverageDayCount() == 30) {
                    stockListing.getPriceList().remove(0);
                    stockListing.setAverageDayCount(29);
                }
            }

            stockListing.setAverageDayCount(stockListing.getAverageDayCount() + 1);

            if(stockListing.getPriceList().size() > 1) {
                float diff = stockListing.getPriceList().get(stockListing.getPriceList().size() - 1) - stockListing.getPriceList().get(stockListing.getPriceList().size() - 2);
                float priceChange1Day = (diff / stockListing.getPriceList().get(stockListing.getPriceList().size() - 2)) * 100;
                stockListing.setPriceChange1Day(priceChange1Day);
            } else if(stockListing.getPriceList().size() == 1) {
                stockListing.setPriceChange1Day(stockListing.getPriceList().get(0));
            }
        }

        // Get Next Day's Price Change //
        for(StockListing stockListing : stockListingService.findAll()) {
            float nextPrice = 0.0f;
            nextPrice = new Random().nextFloat() * 2500.0f;
            stockListing.setNextPriceChange(nextPrice);
        }
    }
}
