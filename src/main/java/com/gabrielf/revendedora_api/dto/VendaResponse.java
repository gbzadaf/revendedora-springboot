package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.Venda;
import com.gabrielf.revendedora_api.domain.enums.StatusVenda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VendaResponse(

        UUID id,
        UUID clienteId,
        String clienteNome,
        LocalDateTime dataVenda,
        BigDecimal valorTotal,
        StatusVenda status,
        List<ItemVendaResponse> itens,
        List<ParcelaResponse> parcelas,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public static VendaResponse fromEntity(Venda venda) {
        return new VendaResponse(
                venda.getId(),
                venda.getCliente().getId(),
                venda.getCliente().getNome(),
                venda.getDataVenda(),
                venda.getValorTotal(),
                venda.getStatus(),
                venda.getItens().stream().map(ItemVendaResponse::fromEntity).toList(),
                venda.getParcelas().stream().map(ParcelaResponse::fromEntity).toList(),
                venda.getCriadoEm(),
                venda.getAtualizadoEm()
        );
    }
}
