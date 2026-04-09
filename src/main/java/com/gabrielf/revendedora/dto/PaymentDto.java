package com.gabrielf.revendedora.dto;

import com.gabrielf.revendedora.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PaymentDto {

    private UUID id;
    private UUID orderId;
    private String customerName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod method;

    public PaymentDto() {
    }

    public PaymentDto(UUID id, UUID orderId, String customerName, BigDecimal amount, LocalDate paymentDate, PaymentMethod method) {
        this.id = id;
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }


}
