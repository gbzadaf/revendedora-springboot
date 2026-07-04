package com.gabrielf.revendedora_api.repository;

import com.gabrielf.revendedora_api.domain.entity.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, UUID> {

    List<ItemVenda> findByVendaId(UUID vendaId);

    List<ItemVenda> findByProdutoId(UUID produtoId);

}
