package com.jbs.StockGame.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbs.StockGame.entity.Group;

public interface GroupRepository extends JpaRepository<Group, String> {
    
}
