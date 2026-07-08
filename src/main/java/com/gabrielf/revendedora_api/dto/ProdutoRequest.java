package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.Produto;
import com.gabrielf.revendedora_api.domain.enums.Marca;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoRequest(

        @NotBlank(message = "nome é obrigatório")
        String nome,

        @NotNull(message = "marca é obrigatória")
        Marca marca,

        @NotNull(message = "preço é obrigatório")
        @DecimalMin(value = "0.01", message = "preço deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "quantidade em estoque é obrigatória")
        @PositiveOrZero(message = "quantidade em estoque não pode ser negativa")
        Integer quantidadeEstoque,

        @PositiveOrZero(message = "quantidade mínima não pode ser negativa")
        Integer quantidadeMinima

) {

}
