package com.brokerage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brokerage.entity.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByCustomerId(String customerId);
    //Asset findByCustomerIdAndAssetName(String customerId, String assetName);
    Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName);
}