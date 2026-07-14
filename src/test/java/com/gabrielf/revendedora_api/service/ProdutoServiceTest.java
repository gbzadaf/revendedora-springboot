package com.gabrielf.revendedora_api.service;

import com.gabrielf.revendedora_api.domain.entity.Produto;
import com.gabrielf.revendedora_api.domain.enums.Marca;
import com.gabrielf.revendedora_api.dto.ProdutoRequest;
import com.gabrielf.revendedora_api.dto.ProdutoResponse;
import com.gabrielf.revendedora_api.exception.RecursoNaoEncontradoException;
import com.gabrielf.revendedora_api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private UUID produtoId;

    @BeforeEach
    void setUp() {
        produtoId = UUID.randomUUID();
        produto = Produto.builder()
                .id(produtoId)
                .nome("Batom Vermelho")
                .marca(Marca.AVON)
                .preco(new BigDecimal("25.90"))
                .quantidadeEstoque(10)
                .quantidadeMinima(3)
                .build();
    }

    @Test
    void deveCriarProdutoComSucesso() {
        ProdutoRequest request = new ProdutoRequest(
                "Batom Vermelho", Marca.AVON, new BigDecimal("25.90"), 10, 3);

        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoResponse response = produtoService.criar(request);

        assertThat(response.nome()).isEqualTo("Batom Vermelho");
        assertThat(response.quantidadeEstoque()).isEqualTo(10);
    }

    @Test
    void deveAplicarZeroComoPadraoQuandoQuantidadeMinimaNaoInformada() {
        ProdutoRequest request = new ProdutoRequest(
                "Batom Vermelho", Marca.AVON, new BigDecimal("25.90"), 10, null);

        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoResponse response = produtoService.criar(request);

        assertThat(response.quantidadeMinima()).isZero();
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));

        ProdutoResponse response = produtoService.buscarPorId(produtoId);

        assertThat(response.id()).isEqualTo(produtoId);
    }

    @Test
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        UUID idInexistente = UUID.randomUUID();
        when(produtoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.buscarPorId(idInexistente))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveReporEstoqueSomandoAQuantidadeAtual() {
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoResponse response = produtoService.reporEstoque(produtoId, 5);

        assertThat(response.quantidadeEstoque()).isEqualTo(15); // 10 + 5
    }
}