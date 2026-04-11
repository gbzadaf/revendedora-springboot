package com.gabrielf.revendedora.service;


import com.gabrielf.revendedora.dto.OrderItemDto;
import com.gabrielf.revendedora.model.Order;
import com.gabrielf.revendedora.model.OrderItem;
import com.gabrielf.revendedora.model.Product;
import com.gabrielf.revendedora.repositories.OrderItemRepository;
import com.gabrielf.revendedora.repositories.OrderRepository;
import com.gabrielf.revendedora.repositories.ProductRepository;
import com.gabrielf.revendedora.repositories.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository,
                            ProductRepository productRepository, StockRepository stockRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;

    }

    public List<OrderItemDto> findAll() {

        return orderItemRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OrderItemDto findById(UUID id) {

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        return toDTO(orderItem);

    }

    @Transactional
    public OrderItemDto save(OrderItemDto dto) {

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        stockRepository.findByProduct(product).ifPresent(stock -> {
            if (stock.getQuantity() < dto.getQuantity()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + product.getName());
            }
            stock.setQuantity(stock.getQuantity() - dto.getQuantity());
            stock.setUpdateAt(LocalDate.now());
            stockRepository.save(stock);
        });

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setUnitPrice(product.getSalePrice());

        OrderItem saved = orderItemRepository.save(orderItem);
        return toDTO(saved);

    }

    @Transactional
    public void delete(UUID id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        orderItemRepository.delete(orderItem);
    }

    private OrderItemDto toDTO(OrderItem orderItem) {
        BigDecimal totalPrice = orderItem.getUnitPrice()
                .multiply(new BigDecimal(orderItem.getQuantity()));
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                totalPrice
        );
    }


}
