package com.gabrielf.revendedora_api.controller;

import com.gabrielf.revendedora_api.dto.VendaRequest;
import com.gabrielf.revendedora_api.dto.VendaResponse;
import com.gabrielf.revendedora_api.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponse> criar (@Valid @RequestBody VendaRequest request) {
        VendaResponse response = vendaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<VendaResponse>> listarTodos() {
        return ResponseEntity.ok(vendaService.listarTodos());

    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponse> buscarPorId (@PathVariable UUID id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));

    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VendaResponse>> listarPorCliente (@PathVariable UUID clienteId) {
        return ResponseEntity.ok(vendaService.listarPorCliente(clienteId));

    }

    @PatchMapping("/{id}/parcelas/{numeroParcela}/pagar")
    public ResponseEntity<VendaResponse> pagarParcela(
            @PathVariable UUID id, @PathVariable Integer numeroParcela) {
        return ResponseEntity.ok(vendaService.pagarParcela(id, numeroParcela));
    }

}
