package com.gabrielf.revendedora_api.dto;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotBlank(message = "nome é obrigatório")
        @Size(max = 150, message = "nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "telefone é obrigatório")
        @Pattern(
                regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}\\-?\\d{4}$",
                message = "telefone deve estar no formato (XX) XXXXX-XXXX ou apenas números")
        String telefone,

        @Size(max = 255, message = "endereço deve ter no máximo 255 caracteres")
        String endereco,

        @Size(max = 500, message = "observações devem ter no máximo 500 caracteres")
        String observacoes

) {

        // funcao utilitaria de validacao/formatacao
        public String telefoneNormalizado() {
                return this.telefone().replaceAll("\\D", "");
        }
}
