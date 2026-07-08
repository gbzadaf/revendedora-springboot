package com.gabrielf.revendedora_api.controller;

import com.gabrielf.revendedora_api.dto.ProdutoRequest;
import com.gabrielf.revendedora_api.dto.ProdutoResponse;
import com.gabrielf.revendedora_api.dto.ReporEstoqueResponse;
import com.gabrielf.revendedora_api.dto.ReservaResponse;
import com.gabrielf.revendedora_api.service.ProdutoService;
import com.gabrielf.revendedora_api.service.ReservaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ReservaService reservaService;


    @PostMapping
    public ResponseEntity<ProdutoResponse> criar (@Valid @RequestBody ProdutoRequest request){
        ProdutoResponse response = produtoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());

    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<ProdutoResponse>> listarComEstoqueBaixo() {
        return ResponseEntity.ok(produtoService.listarComEstoqueBaixo());

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId (@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar (@PathVariable UUID id,
                                                      @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(id,request));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar (@PathVariable UUID id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<ReporEstoqueResponse> reporEstoque(
            @PathVariable UUID id, @Valid @RequestBody ReporEstoqueRequest request) {

        ProdutoResponse produtoAtualizado = produtoService.reporEstoque(id, request.quantidade());

        List<ReservaResponse> reservasLiberadas =
                reservaService.liberarReservasPorEstoque(id, request.quantidade());

        ReporEstoqueResponse resultado = new ReporEstoqueResponse(produtoAtualizado, reservasLiberadas);


        return ResponseEntity.ok(resultado);

    }



    // inner record, dentro do próprio Controller, como esse DTO é usado só nesse endpoint específico,
    // não faz sentido criar um arquivo separado em dto/
    public record ReporEstoqueRequest(
            @NotNull(message = "quantidade é obrigatória")
            @Positive(message = "quantidade deve ser maior que zero")
            Integer quantidade
    ) {
    }

}
