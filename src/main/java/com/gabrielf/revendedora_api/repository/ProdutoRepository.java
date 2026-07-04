package com.gabrielf.revendedora_api.repository;

import com.gabrielf.revendedora_api.domain.entity.Produto;
import com.gabrielf.revendedora_api.domain.enums.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    List<Produto> findByMarca(Marca marca);

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByQuantidadeEstoqueLessThanEqual(Integer quantidade);


}
