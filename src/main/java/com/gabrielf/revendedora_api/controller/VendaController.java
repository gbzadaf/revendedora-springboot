package com.gabrielf.revendedora_api.controller;

import com.gabrielf.revendedora_api.dto.VendaRequest;
import com.gabrielf.revendedora_api.dto.VendaResponse;
import com.gabrielf.revendedora_api.service.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Vendas", description = "Criação de vendas parceladas e controle de pagamento")
public class VendaController {

    private final VendaService vendaService;

    @Operation(
            summary = "Registrar nova venda",
            description = "Cria a venda com seus itens, dá baixa automática no estoque de cada produto " +
                    "e gera as parcelas de acordo com o número informado. O preço de cada item é " +
                    "sempre o preço atual do produto no momento da venda, não é aceito valor vindo da requisição."
    )
    @PostMapping
    public ResponseEntity<VendaResponse> criar (@Valid @RequestBody VendaRequest request) {
        VendaResponse response = vendaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(summary = "Listar todas as vendas")
    @GetMapping
    public ResponseEntity<List<VendaResponse>> listarTodos() {
        return ResponseEntity.ok(vendaService.listarTodos());

    }

    @Operation(summary = "Buscar venda por ID")
    @GetMapping("/{id}")
    public ResponseEntity<VendaResponse> buscarPorId (@PathVariable UUID id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));

    }

    @Operation(summary = "Listar todas as vendas de um cliente específico")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VendaResponse>> listarPorCliente (@PathVariable UUID clienteId) {
        return ResponseEntity.ok(vendaService.listarPorCliente(clienteId));

    }

    @Operation(
            summary = "Marcar uma parcela como paga",
            description = "Atualiza o status da parcela para PAGA e recalcula automaticamente o status " +
                    "da venda (PENDENTE, PARCIALMENTE_PAGA ou QUITADA) com base nas parcelas restantes."
    )
    @PatchMapping("/{id}/parcelas/{numeroParcela}/pagar")
    public ResponseEntity<VendaResponse> pagarParcela(
            @PathVariable UUID id, @PathVariable Integer numeroParcela) {
        return ResponseEntity.ok(vendaService.pagarParcela(id, numeroParcela));
    }

}
