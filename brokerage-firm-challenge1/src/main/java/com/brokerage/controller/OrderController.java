package com.brokerage.controller;

import com.brokerage.dto.OrderCreateRequest;
import com.brokerage.dto.OrderFilter;
import com.brokerage.dto.OrderResponse;
import com.brokerage.entity.Asset;
import com.brokerage.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse createOrder(@Valid @RequestBody OrderCreateRequest req) {
        return orderService.createOrder(req);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> listOrders(
            @RequestParam String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        return orderService.listOrders(new OrderFilter(customerId, from, to));
    }

    @DeleteMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @GetMapping("/assets")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Asset> listAssets(@RequestParam String customerId) {
        return orderService.listAssets(customerId);
    }

    @PostMapping("/assets")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Asset createAsset(@RequestBody Asset asset) {
        return orderService.createOrUpdateAsset(asset);
    }
    
    // Bonus 2: admin matches pending orders
    @PostMapping("/orders/{id}/match")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse match(@PathVariable Long id) {
        return orderService.matchOrder(id);
    }
}
