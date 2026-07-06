package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.Reserva;
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

    public static ReservaResponse fromEntity(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getCliente().getId(),
                reserva.getCliente().getNome(),
                reserva.getProduto().getId(),
                reserva.getProduto().getNome(),
                reserva.getQuantidade(),
                reserva.getDataReserva(),
                reserva.getStatus()
        );
    }
}
