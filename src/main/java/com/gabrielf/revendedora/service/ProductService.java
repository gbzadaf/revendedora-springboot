package com.gabrielf.revendedora.service;

import com.gabrielf.revendedora.dto.ProductDto;
import com.gabrielf.revendedora.exception.ResourceNotFoundException;
import com.gabrielf.revendedora.model.Brand;
import com.gabrielf.revendedora.model.Product;
import com.gabrielf.revendedora.repositories.BrandRepository;
import com.gabrielf.revendedora.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
    }

    public List<ProductDto> findAll() {

        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

    }

    public ProductDto findById(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        return toDTO(product);

    }

    @Transactional
    public ProductDto save(ProductDto dto) {

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada"));
        Product product = new Product();
        product.setName(dto.getName());
        product.setCostPrice(dto.getCostPrice());
        product.setSalePrice(dto.getSalePrice());
        product.setBrand(brand);
        Product saved = productRepository.save(product);
        return toDTO(saved);

    }

    @Transactional
    public ProductDto update(UUID id, ProductDto dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada"));
        product.setName(dto.getName());
        product.setCostPrice(dto.getCostPrice());
        product.setSalePrice(dto.getSalePrice());
        product.setBrand(brand);
        Product updated = productRepository.save(product);
        return toDTO(updated);
    }

    @Transactional
    public void delete(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        productRepository.delete(product);
    }

    private ProductDto toDTO(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCostPrice(),
                product.getSalePrice(),
                product.getBrand().getId(),
                product.getBrand().getName()
        );
    }


}
