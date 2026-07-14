package com.gabrielf.revendedora_api.service;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import com.gabrielf.revendedora_api.domain.entity.Produto;
import com.gabrielf.revendedora_api.domain.entity.Reserva;
import com.gabrielf.revendedora_api.domain.enums.Marca;
import com.gabrielf.revendedora_api.domain.enums.StatusReserva;
import com.gabrielf.revendedora_api.dto.ReservaRequest;
import com.gabrielf.revendedora_api.dto.ReservaResponse;
import com.gabrielf.revendedora_api.exception.RecursoNaoEncontradoException;
import com.gabrielf.revendedora_api.exception.RegraDeNegocioException;
import com.gabrielf.revendedora_api.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ReservaService reservaService;

    private Cliente cliente;
    private Produto produtoSemEstoque;
    private Produto produtoComEstoque;
    private UUID clienteId;
    private UUID produtoSemEstoqueId;
    private UUID produtoComEstoqueId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        produtoSemEstoqueId = UUID.randomUUID();
        produtoComEstoqueId = UUID.randomUUID();

        cliente = Cliente.builder()
                .id(clienteId)
                .nome("Maria Silva")
                .telefone("11987654321")
                .build();

        produtoSemEstoque = Produto.builder()
                .id(produtoSemEstoqueId)
                .nome("Perfume Essencial")
                .marca(Marca.NATURA)
                .preco(new BigDecimal("89.90"))
                .quantidadeEstoque(0)
                .build();

        produtoComEstoque = Produto.builder()
                .id(produtoComEstoqueId)
                .nome("Batom Vermelho")
                .marca(Marca.AVON)
                .preco(new BigDecimal("25.90"))
                .quantidadeEstoque(5)
                .build();
    }

    @Test
    void devePermitirReservaQuandoProdutoSemEstoque() {
        ReservaRequest request = new ReservaRequest(clienteId, produtoSemEstoqueId, 1);

        when(clienteService.buscarEntidadePorId(clienteId)).thenReturn(cliente);
        when(produtoService.buscarEntidadePorId(produtoSemEstoqueId)).thenReturn(produtoSemEstoque);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservaResponse response = reservaService.criar(request);

        assertThat(response.status()).isEqualTo(StatusReserva.AGUARDANDO);
        assertThat(response.produtoId()).isEqualTo(produtoSemEstoqueId);
    }

    @Test
    void deveBloquearReservaQuandoProdutoTemEstoqueDisponivel() {
        ReservaRequest request = new ReservaRequest(clienteId, produtoComEstoqueId, 1);

        when(clienteService.buscarEntidadePorId(clienteId)).thenReturn(cliente);
        when(produtoService.buscarEntidadePorId(produtoComEstoqueId)).thenReturn(produtoComEstoque);

        assertThatThrownBy(() -> reservaService.criar(request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("possui estoque disponível");
    }

    @Test
    void deveCancelarReservaAguardando() {
        Reserva reserva = Reserva.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .produto(produtoSemEstoque)
                .quantidade(1)
                .status(StatusReserva.AGUARDANDO)
                .build();

        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        ReservaResponse response = reservaService.cancelar(reserva.getId());

        assertThat(response.status()).isEqualTo(StatusReserva.CANCELADA);
    }

    @Test
    void deveLancarExcecaoAoCancelarReservaJaEntregue() {
        Reserva reserva = Reserva.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .produto(produtoSemEstoque)
                .quantidade(1)
                .status(StatusReserva.ENTREGUE)
                .build();

        when(reservaRepository.findById(reserva.getId())).thenReturn(Optional.of(reserva));

        assertThatThrownBy(() -> reservaService.cancelar(reserva.getId()))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já entregue");
    }

    @Test
    void deveLancarExcecaoAoBuscarReservaInexistente() {
        UUID idInexistente = UUID.randomUUID();
        when(reservaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.buscarPorId(idInexistente))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveLiberarApenasReservasQueCabemNoEstoqueRepostoRespeitandoOrdemDeChegada() {
        // primeira a chegar, pede 3 - deve ser liberada
        Reserva reservaMaisAntiga = Reserva.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .produto(produtoSemEstoque)
                .quantidade(3)
                .status(StatusReserva.AGUARDANDO)
                .build();

        // segunda a chegar, pede 5 - nao cabe no restante (so sobra 2), deve continuar AGUARDANDO
        Reserva reservaMaisRecente = Reserva.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .produto(produtoSemEstoque)
                .quantidade(5)
                .status(StatusReserva.AGUARDANDO)
                .build();

        // repository ja devolve na ordem de chegada (ORDER BY dataReserva ASC, como definido na query real)
        when(reservaRepository.findAguardandoPorProduto(produtoSemEstoqueId))
                .thenReturn(List.of(reservaMaisAntiga, reservaMaisRecente));
        when(reservaRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        List<ReservaResponse> liberadas = reservaService.liberarReservasPorEstoque(produtoSemEstoqueId, 5);

        assertThat(liberadas).hasSize(1);
        assertThat(liberadas.get(0).quantidade()).isEqualTo(3);
        assertThat(reservaMaisAntiga.getStatus()).isEqualTo(StatusReserva.DISPONIVEL);
        assertThat(reservaMaisRecente.getStatus()).isEqualTo(StatusReserva.AGUARDANDO);
    }

    @Test
    void deveLiberarTodasReservasQuandoEstoqueRepostoForSuficiente() {
        Reserva reserva1 = Reserva.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .produto(produtoSemEstoque)
                .quantidade(2)
                .status(StatusReserva.AGUARDANDO)
                .build();

        Reserva reserva2 = Reserva.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .produto(produtoSemEstoque)
                .quantidade(3)
                .status(StatusReserva.AGUARDANDO)
                .build();

        when(reservaRepository.findAguardandoPorProduto(produtoSemEstoqueId))
                .thenReturn(List.of(reserva1, reserva2));
        when(reservaRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        List<ReservaResponse> liberadas = reservaService.liberarReservasPorEstoque(produtoSemEstoqueId, 10);

        assertThat(liberadas).hasSize(2);
        assertThat(reserva1.getStatus()).isEqualTo(StatusReserva.DISPONIVEL);
        assertThat(reserva2.getStatus()).isEqualTo(StatusReserva.DISPONIVEL);
    }

    @Test
    void naoDeveLiberarNenhumaReservaQuandoEstoqueRepostoForInsuficiente() {
        Reserva reserva = Reserva.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .produto(produtoSemEstoque)
                .quantidade(10)
                .status(StatusReserva.AGUARDANDO)
                .build();

        when(reservaRepository.findAguardandoPorProduto(produtoSemEstoqueId))
                .thenReturn(List.of(reserva));
        when(reservaRepository.saveAll(anyList()))
                .thenAnswer(inv -> inv.getArgument(0));

        List<ReservaResponse> liberadas = reservaService.liberarReservasPorEstoque(produtoSemEstoqueId, 2);

        assertThat(liberadas).isEmpty();
        assertThat(reserva.getStatus()).isEqualTo(StatusReserva.AGUARDANDO);
    }
}