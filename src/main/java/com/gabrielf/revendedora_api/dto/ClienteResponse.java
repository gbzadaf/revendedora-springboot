package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.Cliente;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponse(

        UUID id,
        String nome,
        String telefone,
        String endereco,
        String observacoes,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm

) {

    public static ClienteResponse fromEntity(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEndereco(),
                cliente.getObservacoes(),
                cliente.getCriadoEm(),
                cliente.getAtualizadoEm()
        );
    }
}
