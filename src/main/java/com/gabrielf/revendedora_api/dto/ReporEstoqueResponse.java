package com.gabrielf.revendedora_api.dto;

import java.util.List;

public record ReporEstoqueResponse(
        ProdutoResponse produto,
        List<ReservaResponse> reservasLiberadas
) {
}
