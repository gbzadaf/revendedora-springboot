package com.gabrielf.revendedora_api.domain.enums;

public enum StatusReserva {
    AGUARDANDO,   // esperando reposicao de estoque
    DISPONIVEL,   // chegou estoque, pronta pra entregar
    ENTREGUE,
    CANCELADA
}
