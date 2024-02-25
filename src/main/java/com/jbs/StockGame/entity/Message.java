package com.jbs.StockGame.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long messageId;
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
