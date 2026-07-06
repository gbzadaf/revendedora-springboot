package com.gabrielf.revendedora_api.dto;

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
}
