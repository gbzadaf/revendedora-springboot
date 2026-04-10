package com.gabrielf.revendedora.controller;

import com.gabrielf.revendedora.dto.BrandDto;
import com.gabrielf.revendedora.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandDto>> findAll() {

        return ResponseEntity.ok(brandService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> findById(@PathVariable UUID id) {

        return ResponseEntity.ok(brandService.findById(id));

    }

    @PostMapping
    public ResponseEntity<BrandDto> save(@RequestBody BrandDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.save(dto));

    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> update(@PathVariable UUID id, @RequestBody BrandDto dto) {

        return ResponseEntity.ok(brandService.update(id, dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
