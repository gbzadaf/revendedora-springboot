package com.gabrielf.revendedora.service;

import com.gabrielf.revendedora.dto.StockDto;
import com.gabrielf.revendedora.exception.ResourceNotFoundException;
import com.gabrielf.revendedora.model.Product;
import com.gabrielf.revendedora.model.Stock;
import com.gabrielf.revendedora.repositories.ProductRepository;
import com.gabrielf.revendedora.repositories.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    public StockService(StockRepository stockRepository, ProductRepository productRepository) {
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
    }

    public List<StockDto> findAll() {

        return stockRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

    }

    public StockDto findById(UUID id) {

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));
        return toDTO(stock);
    }

    @Transactional
    public StockDto save(StockDto dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(dto.getQuantity());
        stock.setUpdateAt(LocalDate.now());
        Stock saved = stockRepository.save(stock);
        return toDTO(saved);
    }

    @Transactional
    public StockDto update(UUID id, StockDto dto) {

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));
        stock.setQuantity(dto.getQuantity());
        stock.setUpdateAt(LocalDate.now());
        Stock updated = stockRepository.save(stock);
        return toDTO(updated);
    }

    @Transactional
    public void delete(UUID id) {

        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado"));
        stockRepository.delete(stock);
    }

    private StockDto toDTO(Stock stock) {
        return new StockDto(
                stock.getId(),
                stock.getProduct().getId(),
                stock.getProduct().getName(),
                stock.getQuantity(),
                stock.getUpdateAt()
        );
    }

}
