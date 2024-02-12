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

        // System.out.println("Mentions Found: " + count);
        // stockListingService.findByName(stockName).setPrice(count);
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

        // Set Next Price //
        for(StockListing stockListing : stockListingService.findAll()) {
            if(stockListing.getNextKeyCount() != -9999) {
                stockListing.getKeyCountList().add(stockListing.getNextKeyCount());
                stockListing.addNewPrice();
            }
        }

        // Get Next Day's Price Change //
        for(StockListing stockListing : stockListingService.findAll()) {
            int nextCount = new Random().nextInt(2500);
            stockListing.setNextKeyCount(nextCount);
        }
    }
}
