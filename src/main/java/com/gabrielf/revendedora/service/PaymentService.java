package com.gabrielf.revendedora.service;

import com.gabrielf.revendedora.dto.PaymentDto;
import com.gabrielf.revendedora.exception.ResourceNotFoundException;
import com.gabrielf.revendedora.model.Order;
import com.gabrielf.revendedora.model.OrderStatus;
import com.gabrielf.revendedora.model.Payment;
import com.gabrielf.revendedora.repositories.OrderRepository;
import com.gabrielf.revendedora.repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public List<PaymentDto> findAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PaymentDto findById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));
        return toDTO(payment);
    }

    @Transactional
    public PaymentDto save(PaymentDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(LocalDate.now());                                         //quando um pagamento é registrado, soma o valor ao amountPaid do pedido e recalcula o status automaticamente.
        payment.setPaymentMethod(dto.getMethod());

        Payment saved = paymentRepository.save(payment);

        BigDecimal newAmountPaid = order.getAmountPaid().add(dto.getAmount());
        order.setAmountPaid(newAmountPaid);

        if (newAmountPaid.compareTo(BigDecimal.ZERO) == 0) {
            order.setOrderStatus(OrderStatus.PENDING);
        } else if (newAmountPaid.compareTo(order.getTotalAmount()) >= 0) {
            order.setOrderStatus(OrderStatus.PAID);
        } else {
            order.setOrderStatus(OrderStatus.PARTIAL);
        }
        orderRepository.save(order);
        return toDTO(saved);
            
    }

    @Transactional
    public void delete(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));
        orderRepository.findById(payment.getOrder().getId()).ifPresent(order -> {
            BigDecimal newAmountPaid = order.getAmountPaid().subtract(payment.getAmount());
            order.setAmountPaid(newAmountPaid);

            if (newAmountPaid.compareTo(BigDecimal.ZERO) == 0) {                     //quando um pagamento é removido, subtraí o valor do amountPaid e recalcula o status — o pedido volta para o estado correto.
               order.setOrderStatus(OrderStatus.PENDING);
            } else if (newAmountPaid.compareTo(order.getTotalAmount()) >= 0) {
                order.setOrderStatus(OrderStatus.PAID);
            } else {
                order.setOrderStatus(OrderStatus.PARTIAL);
            }
            orderRepository.save(order);
        });

        paymentRepository.delete(payment);
    }
    private PaymentDto toDTO(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getOrder().getCustomer().getName(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentMethod()
        );
    }


}
