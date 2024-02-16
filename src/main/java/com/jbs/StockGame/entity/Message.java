package com.jbs.StockGame.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Message {
    private String title;
    private String content;
    private LocalDateTime date;
    private boolean isRead;

    public Message(String title, String content, LocalDateTime date) {
        this.title = title;
        this.content = content;
        this.date = date;
        isRead = false;
    }
}
