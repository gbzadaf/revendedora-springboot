package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.ItemVenda;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemVendaResponse(

        UUID id,
        UUID produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal


) {

    public static ItemVendaResponse fromEntity(ItemVenda itemVenda) {
        return new ItemVendaResponse(
                itemVenda.getId(),
                itemVenda.getProduto().getId(),
                itemVenda.getProduto().getNome(),
                itemVenda.getQuantidade(),
                itemVenda.getPrecoUnitario(),
                itemVenda.getSubtotal()
        );
    }
}
