package com.gabrielf.revendedora_api.repository;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import com.gabrielf.revendedora_api.domain.entity.Venda;
import com.gabrielf.revendedora_api.domain.enums.StatusVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VendaRepository extends JpaRepository<Venda, UUID> {

    List<Venda> findByClienteId(UUID clienteId);

    List<Venda> findByStatus(StatusVenda status);

    @Query("""
            SELECT DISTINCT v FROM Venda v
            JOIN FETCH v.cliente
            LEFT JOIN FETCH v.itens i
            LEFT JOIN FETCH i.produto
            WHERE v.id = :id
            """)
    Optional<Venda> findByIdComItensEProduto(@Param("id") UUID id);

    @Query("""
            SELECT DISTINCT v.cliente FROM Venda v
            JOIN v.parcelas p
            WHERE p.status IN ('PENDENTE', 'ATRASADA')
            """)
    List<Cliente> findClientesComParcelasPendentes();
}
