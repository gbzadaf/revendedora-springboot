package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.enums.StatusReserva;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaResponse(

        UUID id,
        UUID clienteId,
        String clienteNome,
        UUID produtoId,
        String produtoNome,
        Integer quantidade,
        LocalDateTime dataReserva,
        StatusReserva status
) {
}
