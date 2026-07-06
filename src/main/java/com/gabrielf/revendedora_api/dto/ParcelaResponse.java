package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.Parcela;
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

    public static ParcelaResponse fromEntity(Parcela parcela) {
        return new ParcelaResponse(
                parcela.getId(),
                parcela.getNumeroParcela(),
                parcela.getValor(),
                parcela.getDataVencimento(),
                parcela.getDataPagamento(),
                parcela.getStatus()
        );
    }
}
