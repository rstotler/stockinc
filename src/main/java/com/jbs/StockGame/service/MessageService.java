package com.jbs.StockGame.service;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MessageService {
    public String getDateTimeString(String dateTime) {
        return dateTime;
    }
}
