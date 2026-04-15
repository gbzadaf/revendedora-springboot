package com.gabrielf.revendedora.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class CustomerDto {

    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    private String name;
    private String phone;

    private CustomerDto() {
    }

    public CustomerDto(UUID id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
