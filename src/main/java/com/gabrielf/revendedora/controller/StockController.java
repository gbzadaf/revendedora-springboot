package com.gabrielf.revendedora.controller;

import com.gabrielf.revendedora.dto.StockDto;
import com.gabrielf.revendedora.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<StockDto>> findAll() {
        return ResponseEntity.ok(stockService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(stockService.findById(id));
    }

    @PostMapping
    public ResponseEntity<StockDto> save(@RequestBody StockDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockDto> update(@PathVariable UUID id, @RequestBody StockDto dto) {
        return ResponseEntity.ok(stockService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        stockService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
