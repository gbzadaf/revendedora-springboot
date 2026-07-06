package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import com.gabrielf.revendedora_api.domain.entity.ItemVenda;
import com.gabrielf.revendedora_api.domain.entity.Parcela;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record VendaRequest(

        @NotNull(message = "clienteId é obrigatório")
        UUID clienteId,

        @NotEmpty(message = "a venda deve ter pelo menos um item")
        @Valid
        List<ItemVendaRequest> itens,

        @NotNull(message = "número de parcelas é obrigatório")
        @Positive(message = "número de parcelas deve ser maior que zero")
        Integer numeroParcelas


) {
}
