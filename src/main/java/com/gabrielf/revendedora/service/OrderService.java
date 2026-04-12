package com.gabrielf.revendedora.service;

import com.gabrielf.revendedora.dto.OrderDto;
import com.gabrielf.revendedora.exception.ResourceNotFoundException;
import com.gabrielf.revendedora.model.Customer;
import com.gabrielf.revendedora.model.Order;
import com.gabrielf.revendedora.model.OrderStatus;
import com.gabrielf.revendedora.repositories.CustomerRepository;
import com.gabrielf.revendedora.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public List<OrderDto> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDto findById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        return toDTO(order);
    }

    @Transactional
    public OrderDto save(OrderDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(dto.getTotalAmount());

        if (dto.getAmountPaid() != null) {

            order.setAmountPaid(dto.getAmountPaid());
        } else {
            order.setAmountPaid(BigDecimal.ZERO);
        }

        order.setOrderStatus(calculateStatus(order.getTotalAmount(), order.getAmountPaid()));
        Order saved = orderRepository.save(order);
        return toDTO(saved);


    }

    @Transactional
    public OrderDto update(UUID id, OrderDto dto) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        order.setTotalAmount(dto.getTotalAmount());
        order.setAmountPaid(dto.getAmountPaid());
        order.setOrderStatus(calculateStatus(order.getTotalAmount(), order.getAmountPaid()));

        Order updated = orderRepository.save(order);
        return toDTO(updated);

    }

    @Transactional
    public void delete(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        orderRepository.delete(order);
    }

    private OrderStatus calculateStatus(BigDecimal total, BigDecimal paid) {
        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            return OrderStatus.PENDING;
        } else if (paid.compareTo(total) >= 0) {
            return OrderStatus.PAID;
        } else {
            return OrderStatus.PARTIAL;
        }
    }

    private OrderDto toDTO(Order order) {
        BigDecimal remaining = order.getTotalAmount().subtract(order.getAmountPaid());
        return new OrderDto(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getAmountPaid(),
                remaining,
                order.getOrderStatus()
        );
    }


}


