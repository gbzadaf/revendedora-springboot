package com.gabrielf.revendedora_api.service;

import com.gabrielf.revendedora_api.domain.entity.ItemVenda;
import com.gabrielf.revendedora_api.domain.entity.Parcela;
import com.gabrielf.revendedora_api.domain.entity.Produto;
import com.gabrielf.revendedora_api.domain.entity.Venda;
import com.gabrielf.revendedora_api.domain.enums.StatusParcela;
import com.gabrielf.revendedora_api.domain.enums.StatusVenda;
import com.gabrielf.revendedora_api.dto.ItemVendaRequest;
import com.gabrielf.revendedora_api.dto.VendaRequest;
import com.gabrielf.revendedora_api.dto.VendaResponse;
import com.gabrielf.revendedora_api.exception.EstoqueInsuficienteException;
import com.gabrielf.revendedora_api.exception.RecursoNaoEncontradoException;
import com.gabrielf.revendedora_api.exception.RegraDeNegocioException;
import com.gabrielf.revendedora_api.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VendaService {

    private static final int DIAS_ATE_PRIMEIRO_VENCIMENTO = 30;

    private final VendaRepository vendaRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public VendaResponse criar (VendaRequest request) {
        Venda venda = Venda.builder()
                .cliente(clienteService.buscarEntidadePorId(request.clienteId()))
                .build();

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemVendaRequest itemRequest : request.itens()) {
            Produto produto = produtoService.buscarEntidadePorId(itemRequest.produtoId());

            if (produto.getQuantidadeEstoque() < itemRequest.quantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o produto '" + produto.getNome() +
                                "'. Disponível: " + produto.getQuantidadeEstoque() +
                                ", solicitado: " + itemRequest.quantidade());

            }

            // baixa no estoque
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemRequest.quantidade());

            ItemVenda item = ItemVenda.builder()
                    .produto(produto)
                    .quantidade(itemRequest.quantidade())
                    .precoUnitario(produto.getPreco()) // snapshot do preco atual
                    .build();

            venda.adicionarItem(item);
            valorTotal = valorTotal.add(item.getSubtotal());

        }

        venda.setValorTotal(valorTotal);
        gerarParcelas(venda, request.numeroParcelas());

        Venda salva = vendaRepository.save(venda);
        return VendaResponse.fromEntity(salva);

    }

    private void gerarParcelas(Venda venda, int numeroParcelas) {
        BigDecimal valorTotal = venda.getValorTotal();
        BigDecimal valorParcela = valorTotal
                .divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.FLOOR);

        // ajusta a diferenca de arredondamento (centavos) na ultima parcela
        BigDecimal somaParcelas = valorParcela.multiply(BigDecimal.valueOf(numeroParcelas));
        BigDecimal diferenca = valorTotal.subtract(somaParcelas);

        for (int i = 1; i <= numeroParcelas; i++) {
            BigDecimal valorDestaParcela = (i == numeroParcelas)
                    ? valorParcela.add(diferenca)
                    : valorParcela;

            Parcela parcela = Parcela.builder()
                    .numeroParcela(i)
                    .valor(valorDestaParcela)
                    .dataVencimento(LocalDate.now().plusDays(DIAS_ATE_PRIMEIRO_VENCIMENTO).plusMonths(i - 1))
                    .status(StatusParcela.PENDENTE)
                    .build();

            venda.adicionarParcela(parcela);

        }

    }

    @Transactional(readOnly = true)
    public VendaResponse buscarPorId(UUID id) {
        Venda venda = vendaRepository.findByIdComItensEProduto(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Venda não encontrada com id: " + id));
        return VendaResponse.fromEntity(venda);

    }

    @Transactional(readOnly = true)
    public List<VendaResponse> listarTodos() {
        return vendaRepository.findAll().stream()
                .map(VendaResponse::fromEntity)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<VendaResponse> listarPorCliente(UUID clienteId) {
        return vendaRepository.findByClienteId(clienteId).stream()
                .map(VendaResponse::fromEntity)
                .toList();

    }

    public VendaResponse pagarParcela(UUID vendaId, Integer numeroParcela) {
        Venda venda = vendaRepository.findByIdComItensEProduto(vendaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Venda não encontrada com id: " + vendaId));

        Parcela parcela = venda.getParcelas().stream()
                .filter(p -> p.getNumeroParcela().equals(numeroParcela))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Parcela número " + numeroParcela + " não encontrada nessa venda"));

        if (parcela.getStatus() == StatusParcela.PAGA) {
            throw new RegraDeNegocioException("Essa parcela já está paga");
        }

        parcela.setStatus(StatusParcela.PAGA);
        parcela.setDataPagamento(LocalDate.now());

        atualizarStatusVenda(venda);

        Venda atualizada = vendaRepository.save(venda);
        return VendaResponse.fromEntity(atualizada);
    }

    private void atualizarStatusVenda(Venda venda) {
        boolean todasPagas = venda.getParcelas().stream()
                .allMatch(p -> p.getStatus() == StatusParcela.PAGA);

        boolean algumaPaga = venda.getParcelas().stream()
                .anyMatch(p -> p.getStatus() == StatusParcela.PAGA);

        if (todasPagas) {
            venda.setStatus(StatusVenda.QUITADA);
        } else if (algumaPaga) {
            venda.setStatus(StatusVenda.PARCIALMENTE_PAGA);
        }
    }



}
