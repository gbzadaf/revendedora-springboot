package com.gabrielf.revendedora_api.controller;

import com.gabrielf.revendedora_api.dto.ClienteRequest;
import com.gabrielf.revendedora_api.dto.ClienteResponse;
import com.gabrielf.revendedora_api.service.ClienteService;
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
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Cadastro e consulta de clientes")
public class ClienteController {

    private final ClienteService clienteService;


    @Operation(summary = "Cadastrar novo cliente")
    @PostMapping
    public ResponseEntity<ClienteResponse> criar (@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(
            summary = "Listar clientes",
            description = "Lista todos os clientes, ou filtra por nome (busca parcial, " +
                    "sem diferenciar maiúsculas/minúsculas) " + "usando o parâmetro opcional 'nome'."
    )
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarTodos (@RequestParam(required = false) String nome) {

        if (nome != null || !nome.isBlank()) {
            return ResponseEntity.ok(clienteService.buscarPorNome(nome));
        }

        return ResponseEntity.ok(clienteService.listarTodos());

    }

    @Operation(summary = "Buscar cliente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));

    }

    @Operation(summary = "Atualizar dados de um cliente existente")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar (@PathVariable UUID id,
                                                      @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));

    }

    @Operation(summary = "Remover um cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
