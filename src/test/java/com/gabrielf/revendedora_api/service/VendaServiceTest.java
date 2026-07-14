package com.gabrielf.revendedora_api.service;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import com.gabrielf.revendedora_api.domain.entity.Parcela;
import com.gabrielf.revendedora_api.domain.entity.Produto;
import com.gabrielf.revendedora_api.domain.entity.Venda;
import com.gabrielf.revendedora_api.domain.enums.Marca;
import com.gabrielf.revendedora_api.domain.enums.StatusParcela;
import com.gabrielf.revendedora_api.domain.enums.StatusVenda;
import com.gabrielf.revendedora_api.dto.ItemVendaRequest;
import com.gabrielf.revendedora_api.dto.VendaRequest;
import com.gabrielf.revendedora_api.dto.VendaResponse;
import com.gabrielf.revendedora_api.exception.EstoqueInsuficienteException;
import com.gabrielf.revendedora_api.exception.RecursoNaoEncontradoException;
import com.gabrielf.revendedora_api.exception.RegraDeNegocioException;
import com.gabrielf.revendedora_api.repository.VendaRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private VendaService vendaService;

    private Cliente cliente;
    private Produto produto;
    private UUID clienteId;
    private UUID produtoId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        produtoId = UUID.randomUUID();

        cliente = Cliente.builder()
                .id(clienteId)
                .nome("Maria Silva")
                .telefone("11987654321")
                .build();

        produto = Produto.builder()
                .id(produtoId)
                .nome("Batom Vermelho")
                .marca(Marca.AVON)
                .preco(new BigDecimal("25.90"))
                .quantidadeEstoque(10)
                .build();
    }

    @Test
    void deveCriarVendaComSucessoEDarBaixaNoEstoque() {
        VendaRequest request = new VendaRequest(
                clienteId,
                List.of(new ItemVendaRequest(produtoId, 2)),
                3
        );

        when(clienteService.buscarEntidadePorId(clienteId)).thenReturn(cliente);
        when(produtoService.buscarEntidadePorId(produtoId)).thenReturn(produto);
        when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

        VendaResponse response = vendaService.criar(request);

        assertThat(response.valorTotal()).isEqualByComparingTo("51.80");
        assertThat(response.itens()).hasSize(1);
        assertThat(response.itens().get(0).precoUnitario()).isEqualByComparingTo("25.90");
        assertThat(response.status()).isEqualTo(StatusVenda.PENDENTE);

        // confirma que o estoque foi debitado na entidade Produto (efeito colateral esperado)
        assertThat(produto.getQuantidadeEstoque()).isEqualTo(8);
    }

    @Test
    void deveDividirParcelasCorretamenteAjustandoArredondamentoNaUltima() {
        VendaRequest request = new VendaRequest(
                clienteId,
                List.of(new ItemVendaRequest(produtoId, 1)), // 1 unidade x 25.90 = 25.90
                3
        );

        when(clienteService.buscarEntidadePorId(clienteId)).thenReturn(cliente);
        when(produtoService.buscarEntidadePorId(produtoId)).thenReturn(produto);
        when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

        VendaResponse response = vendaService.criar(request);

        assertThat(response.parcelas()).hasSize(3);

        BigDecimal somaParcelas = response.parcelas().stream()
                .map(p -> p.valor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // a soma das parcelas deve bater exatamente com o total, sem perder centavo
        assertThat(somaParcelas).isEqualByComparingTo(response.valorTotal());
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        VendaRequest request = new VendaRequest(
                clienteId,
                List.of(new ItemVendaRequest(produtoId, 999)), // mais que o estoque disponivel (10)
                1
        );

        when(clienteService.buscarEntidadePorId(clienteId)).thenReturn(cliente);
        when(produtoService.buscarEntidadePorId(produtoId)).thenReturn(produto);

        assertThatThrownBy(() -> vendaService.criar(request))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessageContaining("Estoque insuficiente");

        // garante que o repository NUNCA foi chamado - a venda nao pode ser salva parcialmente
        verify(vendaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoBuscarVendaInexistente() {
        UUID idInexistente = UUID.randomUUID();
        when(vendaRepository.findByIdComItensEProduto(idInexistente))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.buscarPorId(idInexistente))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveLancarExcecaoAoTentarPagarParcelaJaPaga() {
        Venda venda = Venda.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .valorTotal(new BigDecimal("100.00"))
                .status(StatusVenda.PARCIALMENTE_PAGA)
                .build();

        Parcela parcelaPaga = Parcela.builder()
                .numeroParcela(1)
                .valor(new BigDecimal("100.00"))
                .status(StatusParcela.PAGA)
                .build();

        venda.adicionarParcela(parcelaPaga);

        when(vendaRepository.findByIdComItensEProduto(venda.getId()))
                .thenReturn(Optional.of(venda));

        assertThatThrownBy(() -> vendaService.pagarParcela(venda.getId(), 1))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("já está paga");
    }

    @Test
    void deveAtualizarStatusParaQuitadaQuandoTodasParcelasForemPagas() {
        Venda venda = Venda.builder()
                .id(UUID.randomUUID())
                .cliente(cliente)
                .valorTotal(new BigDecimal("50.00"))
                .status(StatusVenda.PARCIALMENTE_PAGA)
                .build();

        Parcela parcela1 = Parcela.builder()
                .numeroParcela(1)
                .valor(new BigDecimal("25.00"))
                .status(StatusParcela.PAGA)
                .build();

        Parcela parcela2 = Parcela.builder()
                .numeroParcela(2)
                .valor(new BigDecimal("25.00"))
                .status(StatusParcela.PENDENTE)
                .build();

        venda.adicionarParcela(parcela1);
        venda.adicionarParcela(parcela2);

        when(vendaRepository.findByIdComItensEProduto(venda.getId()))
                .thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(inv -> inv.getArgument(0));

        VendaResponse response = vendaService.pagarParcela(venda.getId(), 2);

        assertThat(response.status()).isEqualTo(StatusVenda.QUITADA);
    }
}