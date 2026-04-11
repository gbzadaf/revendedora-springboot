package com.gabrielf.revendedora.controller;

import com.gabrielf.revendedora.dto.FutureOrderDto;
import com.gabrielf.revendedora.service.FutureOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/future-orders")
public class FutureOrderController {

    private final FutureOrderService futureOrderService;

    public FutureOrderController(FutureOrderService futureOrderService) {
        this.futureOrderService = futureOrderService;
    }

    @GetMapping
    public ResponseEntity<List<FutureOrderDto>> findAll() {
        return ResponseEntity.ok(futureOrderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FutureOrderDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(futureOrderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FutureOrderDto> save(@RequestBody FutureOrderDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(futureOrderService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FutureOrderDto> update(@PathVariable UUID id, @RequestBody FutureOrderDto dto) {
        return ResponseEntity.ok(futureOrderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        futureOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
