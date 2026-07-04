package com.gabrielf.revendedora_api.repository;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    Optional<Cliente> findByTelefone(String telefone);

}
