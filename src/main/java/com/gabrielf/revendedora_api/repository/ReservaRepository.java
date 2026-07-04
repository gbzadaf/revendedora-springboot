package com.gabrielf.revendedora_api.repository;

import com.gabrielf.revendedora_api.domain.entity.Reserva;
import com.gabrielf.revendedora_api.domain.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    List<Reserva> findByClienteId(UUID clienteId);

    List<Reserva> findByStatus(StatusReserva status);

    @Query("""
            SELECT r FROM Reserva r
            JOIN FETCH r.cliente
            JOIN FETCH r.produto
            WHERE r.produto.id = :produtoId
            AND r.status = 'AGUARDANDO'
            ORDER BY r.dataReserva ASC
            """)
    List<Reserva> findAguardandoPorProduto(@Param("produtoId") UUID produtoId);


}



/* findAguardandoPorProduto = clientes que reservam produtos que não tem, mas vai vir na próxima entrega e ordena por
   quem reservou primeiro.
*/
