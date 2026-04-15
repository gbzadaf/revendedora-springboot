package com.gabrielf.revendedora.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class BrandDto {

    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    public BrandDto() {
    }

    public BrandDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
