package com.brokerage.dto;

import java.time.OffsetDateTime;

public record ApiError(String message, OffsetDateTime timestamp) {
	
}


