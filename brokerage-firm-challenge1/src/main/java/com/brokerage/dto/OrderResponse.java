package com.brokerage.dto;


import com.brokerage.entity.OrderSide;
import com.brokerage.entity.OrderStatus;
import java.time.OffsetDateTime;

public record OrderResponse(
        Long id,
        String customerId,
        String assetName,
        OrderSide side,
        double size,
        double price,
        OrderStatus status,
        OffsetDateTime createDate
) {
	
}

