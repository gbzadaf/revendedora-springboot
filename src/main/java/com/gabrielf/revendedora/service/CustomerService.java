package com.gabrielf.revendedora.service;

import com.gabrielf.revendedora.dto.CustomerDto;
import com.gabrielf.revendedora.exception.ResourceNotFoundException;
import com.gabrielf.revendedora.model.Customer;
import com.gabrielf.revendedora.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> findAll() {

        return customerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

    }

    public CustomerDto findById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return toDTO(customer);

    }

    @Transactional
    public CustomerDto save(CustomerDto dto) {

        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        Customer saved = customerRepository.save(customer);
        return toDTO(saved);

    }

    @Transactional
    public CustomerDto update(UUID id, CustomerDto dto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        Customer updated = customerRepository.save(customer);
        return toDTO(updated);
    }

    @Transactional
    public void delete(UUID id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        customerRepository.delete(customer);
    }

    private CustomerDto toDTO(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getPhone()
        );
    }

}
