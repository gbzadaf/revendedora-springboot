package com.gabrielf.revendedora_api.dto;

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
}
