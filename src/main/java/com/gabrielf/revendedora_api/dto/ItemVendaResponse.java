package com.gabrielf.revendedora_api.dto;

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
}
