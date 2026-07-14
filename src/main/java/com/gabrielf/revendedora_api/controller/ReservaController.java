package com.gabrielf.revendedora_api.controller;

import com.gabrielf.revendedora_api.dto.ReservaRequest;
import com.gabrielf.revendedora_api.dto.ReservaResponse;
import com.gabrielf.revendedora_api.service.ReservaService;
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
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Reserva de produtos em falta no estoque")
public class ReservaController {

    private final ReservaService reservaService;


    @Operation(
            summary = "Criar reserva de produto",
            description = "Só é permitido reservar produtos com estoque zerado. Se o produto tiver " +
                    "estoque disponível, a API retorna erro orientando a realizar a venda diretamente."
    )
    @PostMapping
    public ResponseEntity<ReservaResponse> criar(@Valid @RequestBody ReservaRequest request) {
        ReservaResponse response = reservaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todas as reservas")
    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listarTodos() {
        return ResponseEntity.ok(reservaService.listarTodos());
    }

    @Operation(summary = "Buscar reserva por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas as reservas de um cliente específico")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponse>> listarPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(reservaService.listarPorCliente(clienteId));
    }

    @Operation(
            summary = "Cancelar uma reserva",
            description = "Não é possível cancelar uma reserva que já foi marcada como ENTREGUE."
    )
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

}

/* liberarReservasPorEstoque() sem endpoint próprio de propósito, porque ele só faz sentido sendo chamado em conjunto
com reporEstoque(), é por isso que ele já está encadeado dentro do ProdutoController.reporEstoque(). Se eu expusesse
ele solto aqui, teria risco de alguém chamar fora de ordem (liberar reserva sem o estoque real ter sido atualizado),
gerando inconsistência.
 */
