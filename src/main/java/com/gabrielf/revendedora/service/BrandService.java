package com.gabrielf.revendedora.service;

import com.gabrielf.revendedora.dto.BrandDto;
import com.gabrielf.revendedora.exception.ResourceNotFoundException;
import com.gabrielf.revendedora.model.Brand;
import com.gabrielf.revendedora.repositories.BrandRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }


    public List<BrandDto> findAll() {
        return brandRepository.findAll().stream()
                .map(brand -> new BrandDto(brand.getId(), brand.getName()))
                .collect(Collectors.toList());

    }

    public BrandDto findById(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada"));
        return new BrandDto(brand.getId(), brand.getName());

    }

    @Transactional
    public BrandDto save(BrandDto dto) {
        Brand brand = new Brand();
        brand.setName(dto.getName());
        Brand saved = brandRepository.save(brand);
        return new BrandDto(saved.getId(), saved.getName());

    }

    @Transactional
    public BrandDto update(UUID id, BrandDto dto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada"));
        brand.setName(dto.getName());
        Brand updated = brandRepository.save(brand);
        return new BrandDto(updated.getId(), updated.getName());


    }

    public void delete(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca não encontrada"));
        brandRepository.delete(brand);

    }


}