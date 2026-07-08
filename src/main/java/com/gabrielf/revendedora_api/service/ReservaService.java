package com.gabrielf.revendedora_api.service;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import com.gabrielf.revendedora_api.domain.entity.Produto;
import com.gabrielf.revendedora_api.domain.entity.Reserva;
import com.gabrielf.revendedora_api.domain.enums.StatusReserva;
import com.gabrielf.revendedora_api.dto.ReservaRequest;
import com.gabrielf.revendedora_api.dto.ReservaResponse;
import com.gabrielf.revendedora_api.exception.RecursoNaoEncontradoException;
import com.gabrielf.revendedora_api.exception.RegraDeNegocioException;
import com.gabrielf.revendedora_api.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public ReservaResponse criar(ReservaRequest request) {
        Cliente cliente = clienteService.buscarEntidadePorId(request.clienteId());
        Produto produto = produtoService.buscarEntidadePorId(request.produtoId());

        if (produto.getQuantidadeEstoque() > 0) {
            throw new RegraDeNegocioException(
                    "Produto '" + produto.getNome() + "' possui estoque disponível (" +
                            produto.getQuantidadeEstoque() + " unidade(s)). Realize a venda diretamente, " +
                            "reservas são permitidas apenas quando o produto está em falta.");
        }

        Reserva reserva = Reserva.builder()
                .cliente(cliente)
                .produto(produto)
                .quantidade(request.quantidade())
                .build();

        Reserva salva = reservaRepository.save(reserva);
        return ReservaResponse.fromEntity(salva);

    }

    @Transactional(readOnly = true)
    public ReservaResponse buscarPorId(UUID id) {
        Reserva reserva = buscarEntidadePorId(id);
        return ReservaResponse.fromEntity(reserva);

    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarTodos() {
        return reservaRepository.findAll().stream()
                .map(ReservaResponse::fromEntity)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorCliente(UUID clienteId) {
        return reservaRepository.findByClienteId(clienteId).stream()
                .map(ReservaResponse::fromEntity)
                .toList();

    }

    public ReservaResponse cancelar(UUID id) {
        Reserva reserva = buscarEntidadePorId(id);

        if (reserva.getStatus() == StatusReserva.ENTREGUE) {
            throw new RegraDeNegocioException("Não é possível cancelar uma reserva já entregue");
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        Reserva atualizada = reservaRepository.save(reserva);
        return ReservaResponse.fromEntity(atualizada);

    }

    /**
     * Chamado pelo Controller logo apos ProdutoService.reporEstoque().
     * Marca como DISPONIVEL todas as reservas AGUARDANDO desse produto,
     * respeitando a ordem de chegada (fila) e o limite do estoque reposto.
     */
    public List<ReservaResponse> liberarReservasPorEstoque(UUID produtoId, Integer quantidadeReposta) {
        List<Reserva> aguardando = reservaRepository.findAguardandoPorProduto(produtoId);

        int estoqueDisponivel = quantidadeReposta;
        List<Reserva> liberadas = new ArrayList<>();

        for (Reserva reserva : aguardando) {
            if (estoqueDisponivel <= 0) {
                break;
            }
            if (reserva.getQuantidade() <= estoqueDisponivel) {
                reserva.setStatus(StatusReserva.DISPONIVEL);
                estoqueDisponivel -= reserva.getQuantidade();
                liberadas.add(reserva);
            }
        }

        reservaRepository.saveAll(liberadas);
        return liberadas.stream()
                .map(ReservaResponse::fromEntity)
                .toList();
    }




    //dry
    @Transactional(readOnly = true)
    public Reserva buscarEntidadePorId(UUID id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Reserva não encontrada com id: " + id));
    }

}
