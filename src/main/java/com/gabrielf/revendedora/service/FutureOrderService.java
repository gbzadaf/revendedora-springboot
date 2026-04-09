package com.gabrielf.revendedora.service;

import com.gabrielf.revendedora.dto.FutureOrderDto;
import com.gabrielf.revendedora.model.Customer;
import com.gabrielf.revendedora.model.FutureOrder;
import com.gabrielf.revendedora.model.Product;
import com.gabrielf.revendedora.repositories.CustomerRepository;
import com.gabrielf.revendedora.repositories.FutureOrderRepository;
import com.gabrielf.revendedora.repositories.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FutureOrderService {

    private final FutureOrderRepository futureOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public FutureOrderService(FutureOrderRepository futureOrderRepository, CustomerRepository customerRepository,
                              ProductRepository productRepository) {
        this.futureOrderRepository = futureOrderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<FutureOrderDto> findAll() {
        return futureOrderRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FutureOrderDto findById(UUID id) {
        FutureOrder futureOrder = futureOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido futuro não encontrado"));
        return toDTO(futureOrder);
    }

    public FutureOrderDto save(FutureOrderDto dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        FutureOrder futureOrder = new FutureOrder();
        futureOrder.setCustomer(customer);
        futureOrder.setProduct(product);
        futureOrder.setQuantity(dto.getQuantity());
        futureOrder.setNotes(dto.getNotes());

        FutureOrder saved = futureOrderRepository.save(futureOrder);
        return toDTO(saved);
    }

    public FutureOrderDto update(UUID id, FutureOrderDto dto) {
        FutureOrder futureOrder = futureOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido futuro não encontrado"));

        futureOrder.setQuantity(dto.getQuantity());
        futureOrder.setNotes(dto.getNotes());

        FutureOrder updated = futureOrderRepository.save(futureOrder);
        return toDTO(updated);
    }

    public void delete(UUID id) {
        FutureOrder futureOrder = futureOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido futuro não encontrado"));
        futureOrderRepository.delete(futureOrder);

    }

    private FutureOrderDto toDTO(FutureOrder futureOrder) {
        return new FutureOrderDto(
                futureOrder.getId(),
                futureOrder.getCustomer().getId(),
                futureOrder.getCustomer().getName(),
                futureOrder.getProduct().getId(),
                futureOrder.getProduct().getName(),
                futureOrder.getProduct().getBrand().getName(),
                futureOrder.getQuantity(),
                futureOrder.getNotes()
        );
    }

}
