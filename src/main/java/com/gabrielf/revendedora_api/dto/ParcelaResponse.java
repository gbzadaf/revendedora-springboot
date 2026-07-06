package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.enums.StatusParcela;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ParcelaResponse(

        UUID id,
        Integer numeroParcela,
        BigDecimal valor,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        StatusParcela status
) {
}
