package com.jbs.StockGame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jbs.StockGame.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    
}
