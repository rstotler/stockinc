package com.jbs.StockGame.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    //@Scheduled(cron = "*/5 * * * * *")
    public void scrapeData() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        String stringTime = currentTime.format(timeFormat);
        System.out.println("Scheduler Time: " + stringTime);
    }
}
