package com.gabrielf.revendedora_api.controller;

import com.gabrielf.revendedora_api.dto.ProdutoRequest;
import com.gabrielf.revendedora_api.dto.ProdutoResponse;
import com.gabrielf.revendedora_api.dto.ReporEstoqueResponse;
import com.gabrielf.revendedora_api.dto.ReservaResponse;
import com.gabrielf.revendedora_api.service.ProdutoService;
import com.gabrielf.revendedora_api.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Produtos", description = "Gestão de catálogo e estoque de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ReservaService reservaService;


    @Operation(summary = "Cadastrar novo produto")
    @PostMapping
    public ResponseEntity<ProdutoResponse> criar (@Valid @RequestBody ProdutoRequest request){
        ProdutoResponse response = produtoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(summary = "Listar todos os produtos cadastrados")
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());

    }

    @Operation(summary = "Listar produtos com estoque igual ou abaixo do mínimo definido")
    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<ProdutoResponse>> listarComEstoqueBaixo() {
        return ResponseEntity.ok(produtoService.listarComEstoqueBaixo());

    }

    @Operation(summary = "Buscar produto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId (@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));

    }

    @Operation(summary = "Atualizar dados de um produto existente")
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar (@PathVariable UUID id,
                                                      @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(id,request));

    }

    @Operation(summary = "Remover um produto do catálogo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar (@PathVariable UUID id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();

    }

    @Operation(
            summary = "Repor estoque de um produto",
            description = "Adiciona a quantidade informada ao estoque atual e libera automaticamente " +
                    "as reservas AGUARDANDO desse produto, respeitando a ordem de chegada e " +
                    "o limite de estoque reposto."
    )
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
