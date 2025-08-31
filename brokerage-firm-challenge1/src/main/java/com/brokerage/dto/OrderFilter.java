package com.brokerage.dto;


import java.time.OffsetDateTime;

public record OrderFilter(String customerId, OffsetDateTime from, OffsetDateTime to) {
	
}

