package com.gabrielf.revendedora_api.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes
) {

    public ErroResponse(int status, String erro, String mensagem, String caminho) {
        this(LocalDateTime.now(), status, erro, mensagem, caminho, null);
    }

    public ErroResponse(int status, String erro, String mensagem, String caminho, List<String> detalhes) {
        this(LocalDateTime.now(), status, erro, mensagem, caminho, detalhes);
    }
}
