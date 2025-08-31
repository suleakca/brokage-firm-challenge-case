package com.brokerage;


import com.brokerage.dto.OrderCreateRequest;
import com.brokerage.entity.Asset;
import com.brokerage.entity.OrderSide;
import com.brokerage.entity.OrderStatus;
import com.brokerage.repository.AssetRepository;
import com.brokerage.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class BrokerageApplicationTests {
	  @Autowired
	    private OrderService orderService;

	    @Autowired
	    private AssetRepository assetRepository;

	    @Test
	    void buyOrderReservesTryUsable() {
	        // Seed TRY
	    	Asset tryAsset = assetRepository.save(new Asset(null, "c1", "TRY", 1000, 1000));
	        var resp = orderService.createOrder(new OrderCreateRequest("c1", "AAPL", OrderSide.BUY, 10, 5));
	        Assertions.assertEquals(OrderStatus.PENDING, resp.status());
	        Asset updatedTry = assetRepository.findByCustomerIdAndAssetName("c1", "TRY").orElseThrow();
	        Assertions.assertEquals(950, updatedTry.getUsableSize(), 0.0001);
	    }

	    @Test
	    void sellOrderReservesAssetUsable() {
	        // Seed asset
	    	Asset asset = assetRepository.save(new Asset(null, "c1", "AAPL", 20, 20));
	        var resp = orderService.createOrder(new OrderCreateRequest("c1", "AAPL", OrderSide.SELL, 5, 10));
	        Assertions.assertEquals(OrderStatus.PENDING, resp.status());
	        Asset updatedAsset = assetRepository.findByCustomerIdAndAssetName("c1", "AAPL").orElseThrow();
	        Assertions.assertEquals(15, updatedAsset.getUsableSize(), 0.0001);
	    }
}
