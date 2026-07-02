package com.gabrielf.revendedora_api.domain.enums;

public enum StatusVenda {
    PENDENTE,
    PARCIALMENTE_PAGA, // pelo menos uma parcela paga, mas nao todas
    QUITADA, // todas as parcelas pagas
    CANCELADA
}
