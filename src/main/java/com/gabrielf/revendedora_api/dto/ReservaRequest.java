package com.gabrielf.revendedora_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ReservaRequest(

        @NotNull(message = "clienteId é obrigatório")
        UUID clienteId,

        @NotNull(message = "produtoId é obrigatório")
        UUID produtoId,

        @NotNull(message = "quantidade é obrigatória")
        @Positive(message = "quantidade deve ser maior que zero")
        Integer quantidade
) {
}
