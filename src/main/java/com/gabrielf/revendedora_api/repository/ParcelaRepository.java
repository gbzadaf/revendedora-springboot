package com.gabrielf.revendedora_api.repository;

import com.gabrielf.revendedora_api.domain.entity.Parcela;
import com.gabrielf.revendedora_api.domain.enums.StatusParcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ParcelaRepository extends JpaRepository<Parcela, UUID> {

    List<Parcela> findByVendaId(UUID vendaId);

    List<Parcela> findByStatus(StatusParcela status);

    List<Parcela> findByStatusAndDataVencimentoBefore(StatusParcela status, LocalDate data);

    @Query("""
            SELECT p FROM Parcela p
            JOIN FETCH p.venda v
            JOIN FETCH v.cliente
            WHERE v.cliente.id = :clienteId
            AND p.status IN ('PENDENTE', 'ATRASADA')
            ORDER BY p.dataVencimento ASC
            """)
    List<Parcela> findParcelasPendentesPorCliente(@Param("clienteId") UUID clienteId);

    @Query("""
            SELECT COALESCE(SUM(p.valor), 0) FROM Parcela p
            WHERE p.venda.cliente.id = :clienteId
            AND p.status IN ('PENDENTE', 'ATRASADA')
            """)
    BigDecimal calcularTotalDevidoPorCliente(@Param("clienteId") UUID clienteId);

}


/*COALESCE(SUM(p.valor), 0) = se o cliente não tiver nenhuma parcela pendente, SUM() sozinho retornaria NULL
(comportamento padrão do SQL quando não há linhas pra agregar), o que quebraria o código com NullPointerException
na hora de usar o BigDecimal.  O COALESCE troca esse NULL por 0, garantindo que o metodo sempre devolve um número usavel
 */
