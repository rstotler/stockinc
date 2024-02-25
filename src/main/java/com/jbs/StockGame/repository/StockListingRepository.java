package com.jbs.StockGame.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbs.StockGame.entity.StockListing;

public interface StockListingRepository extends JpaRepository<StockListing, String> {
    public Optional<StockListing> findByName(String name);
}
