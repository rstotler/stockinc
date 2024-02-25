package com.jbs.StockGame.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbs.StockGame.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
}
