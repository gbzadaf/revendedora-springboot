package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.enums.Marca;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProdutoResponse(

        UUID id,
        String nome,
        Marca marca,
        BigDecimal preco,
        Integer quantidadeEstoque,
        Integer quantidadeMinima,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
