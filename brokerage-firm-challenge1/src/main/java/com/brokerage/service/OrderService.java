package com.brokerage.service;

import com.brokerage.dto.OrderCreateRequest;
import com.brokerage.dto.OrderFilter;
import com.brokerage.dto.OrderResponse;
import com.brokerage.entity.*;
import com.brokerage.exception.BadRequestException;
import com.brokerage.exception.NotFoundException;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    
    private static final String TRY = "TRY";
    
    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
    }

    private Asset getOrCreateAsset(String customerId, String assetName) {
      /*  Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);

        if (asset != null) {
            return asset;
        } else {
            Asset newAsset = new Asset();
            newAsset.setCustomerId(customerId);
            newAsset.setAssetName(assetName);
            newAsset.setSize(0.0);
            newAsset.setUsableSize(0.0);
            return assetRepository.save(newAsset);
        }*/
    	return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> {
                    Asset newAsset = new Asset();
                    newAsset.setCustomerId(customerId);
                    newAsset.setAssetName(assetName);
                    newAsset.setSize(0.0);
                    newAsset.setUsableSize(0.0);
                    return assetRepository.save(newAsset);
                });
    }

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest req) {
        String customerId = req.customerId();
        String assetName = req.assetName();
        double size = req.size();
        double price = req.price();

        if (!TRY.equalsIgnoreCase(assetName) && price <= 0) {
            throw new BadRequestException("Price must be > 0 for non-TRY assets.");
        }

        
        if (req.side() == OrderSide.BUY) {
            Asset tryAsset = getOrCreateAsset(customerId, TRY);
            double cost = size * price;
            if (tryAsset.getUsableSize() < cost) {
                throw new BadRequestException("Insufficient TRY usableSize. Need " + cost + ", have " + tryAsset.getUsableSize());
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize() - cost);
            assetRepository.save(tryAsset);
        } else { // SELL
            Asset asset = getOrCreateAsset(customerId, assetName);
            if (asset.getUsableSize() < size) {
                throw new BadRequestException("Insufficient asset usableSize to SELL. Need " + size + ", have " + asset.getUsableSize());
            }
            asset.setUsableSize(asset.getUsableSize() - size);
            assetRepository.save(asset);
        }

        Order order = new Order(
                null,
                customerId,
                assetName,
                req.side(),
                size,
                price,
                OrderStatus.PENDING,
                OffsetDateTime.now()
        );
        order = orderRepository.save(order);

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(OrderFilter filter) {
        if (filter.from() != null && filter.to() != null) {
            return orderRepository.findByCustomerIdAndCreateDateBetween(filter.customerId(), filter.from(), filter.to())
                    .stream().map(this::toResponse).toList();
        }
        return orderRepository.findByCustomerId(filter.customerId()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only PENDING orders can be canceled.");
        }
        
        if (order.getOrderSide() == OrderSide.BUY) {
            Asset tryAsset = getOrCreateAsset(order.getCustomerId(), TRY);
            double cost = order.getSize() * order.getPrice();
            tryAsset.setUsableSize(tryAsset.getUsableSize() + cost);
            assetRepository.save(tryAsset);
        } else {
            Asset asset = getOrCreateAsset(order.getCustomerId(), order.getAssetName());
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
            assetRepository.save(asset);
        }
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Transactional
    public OrderResponse matchOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only PENDING orders can be matched.");
        }
        if (order.getOrderSide() == OrderSide.BUY) {
            Asset tryAsset = getOrCreateAsset(order.getCustomerId(), TRY);
            double cost = order.getSize() * order.getPrice();
            
            if (tryAsset.getSize() < cost) {
                
                throw new BadRequestException("Insufficient TRY size to settle buy. Need " + cost + ", have " + tryAsset.getSize());
            }
            tryAsset.setSize(tryAsset.getSize() - cost);
            assetRepository.save(tryAsset);

            Asset asset = getOrCreateAsset(order.getCustomerId(), order.getAssetName());
            asset.setSize(asset.getSize() + order.getSize());
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
            assetRepository.save(asset);
        } else {
            Asset asset = getOrCreateAsset(order.getCustomerId(), order.getAssetName());
            if (asset.getSize() < order.getSize()) {
                throw new BadRequestException("Insufficient asset size to settle sell.");
            }
            asset.setSize(asset.getSize() - order.getSize());
            assetRepository.save(asset);

            Asset tryAsset = getOrCreateAsset(order.getCustomerId(), TRY);
            double proceeds = order.getSize() * order.getPrice();
            tryAsset.setSize(tryAsset.getSize() + proceeds);
            tryAsset.setUsableSize(tryAsset.getUsableSize() + proceeds);
            assetRepository.save(tryAsset);
        }
        order.setStatus(OrderStatus.MATCHED);
        orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<Asset> listAssets(String customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    private OrderResponse toResponse(Order o) {
        return new OrderResponse(o.getId(), o.getCustomerId(), o.getAssetName(), o.getOrderSide(),
                o.getSize(), o.getPrice(), o.getStatus(), o.getCreateDate());
    }

	public Asset createOrUpdateAsset(Asset asset) {
		return assetRepository.save(asset);

	}
}
