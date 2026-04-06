package com.gabrielf.revendedora.service;

import com.gabrielf.revendedora.model.Brand;
import com.gabrielf.revendedora.repositories.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> findAll() {
        return brandRepository.findAll();

    }

    public Brand findById(UUID id) {
        return brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Marca não encontrada"));

    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);

    }

    public Brand update(UUID id, Brand brand) {
        Brand existing = findById(id);
        existing.setName(brand.getName());
        return brandRepository.save(existing);

    }

    public void delete(UUID id) {
        brandRepository.delete(findById(id));

    }

    
}
