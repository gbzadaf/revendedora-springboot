package com.gabrielf.revendedora.controller;

import com.gabrielf.revendedora.dto.ProductDto;
import com.gabrielf.revendedora.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> findAll() {

        return ResponseEntity.ok(productService.findAll());

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable UUID id) {

        return ResponseEntity.ok(productService.findById(id));

    }

    @PostMapping
    public ResponseEntity<ProductDto> save(@Valid @RequestBody ProductDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(dto));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable UUID id, @Valid @RequestBody ProductDto dto) {

        return ResponseEntity.ok(productService.update(id, dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
