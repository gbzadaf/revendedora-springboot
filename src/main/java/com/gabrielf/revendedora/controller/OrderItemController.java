package com.gabrielf.revendedora.controller;

import com.gabrielf.revendedora.dto.OrderItemDto;
import com.gabrielf.revendedora.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> findAll() {
        return ResponseEntity.ok(orderItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> save(@RequestBody OrderItemDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderItemService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
