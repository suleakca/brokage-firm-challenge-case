package com.brokerage.dto;

import com.brokerage.entity.OrderSide;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
        @NotBlank String customerId,
        @NotBlank String assetName,
        @NotNull OrderSide side,
        @Min(1) double size,
        @Min(0) double price
) {}
