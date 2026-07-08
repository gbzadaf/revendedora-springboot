package com.gabrielf.revendedora_api.service;

import com.gabrielf.revendedora_api.domain.entity.Produto;
import com.gabrielf.revendedora_api.dto.ProdutoRequest;
import com.gabrielf.revendedora_api.dto.ProdutoResponse;
import com.gabrielf.revendedora_api.exception.RecursoNaoEncontradoException;
import com.gabrielf.revendedora_api.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProdutoService {

    private final ProdutoRepository  produtoRepository;


    public ProdutoResponse criar(ProdutoRequest request){
        Produto produto = Produto.builder()
                .nome(request.nome())
                .marca(request.marca())
                .preco(request.preco())
                .quantidadeEstoque(request.quantidadeEstoque())
                .quantidadeMinima(request.quantidadeMinima() != null ? request.quantidadeMinima() : 0)
                .build();

        Produto salvo = produtoRepository.save(produto);
        return ProdutoResponse.fromEntity(salvo);

    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId (UUID id) {
        Produto produto = buscarEntidadePorId(id);
        return ProdutoResponse.fromEntity(produto);

    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(ProdutoResponse::fromEntity)
                .toList();

    }

    public ProdutoResponse atualizar(UUID id, ProdutoRequest request) {
        Produto produto = buscarEntidadePorId(id);

        produto.setNome(request.nome());
        produto.setMarca(request.marca());
        produto.setPreco(request.preco());
        produto.setQuantidadeEstoque(request.quantidadeEstoque());
        produto.setQuantidadeMinima(request.quantidadeMinima());

        Produto atualizado = produtoRepository.save(produto);
        return ProdutoResponse.fromEntity(atualizado);

    }

    public void deletar(UUID id) {
        Produto produto = buscarEntidadePorId(id);
        produtoRepository.delete(produto);

    }

    public ProdutoResponse reporEstoque(UUID id, Integer quantidadeAdicionada) {
        Produto produto = buscarEntidadePorId(id);
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidadeAdicionada);
        Produto atualizado = produtoRepository.save(produto);
        return ProdutoResponse.fromEntity(atualizado);

    }



    //dry
    @Transactional(readOnly = true)
    public Produto buscarEntidadePorId(UUID id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado com id: " + id));
    }


}
