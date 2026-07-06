package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.Produto;
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

    public static ProdutoResponse fromEntity(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getMarca(),
                produto.getPreco(),
                produto.getQuantidadeEstoque(),
                produto.getQuantidadeMinima(),
                produto.getCriadoEm(),
                produto.getAtualizadoEm()
        );
    }
}
