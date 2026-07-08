package com.gabrielf.revendedora_api.service;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import com.gabrielf.revendedora_api.dto.ClienteRequest;
import com.gabrielf.revendedora_api.dto.ClienteResponse;
import com.gabrielf.revendedora_api.exception.RecursoNaoEncontradoException;
import com.gabrielf.revendedora_api.exception.RegraDeNegocioException;
import com.gabrielf.revendedora_api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;


    public ClienteResponse criar(ClienteRequest request) {
        String telefoneNormalizado = request.telefoneNormalizado();

        clienteRepository.findByTelefone(telefoneNormalizado).ifPresent(c -> {
            throw new RegraDeNegocioException("Já existe um cliente cadastrado com o telefone: + telefoneNormalizado");
        });

        Cliente cliente = Cliente.builder()
                .nome(request.nome())
                .telefone(telefoneNormalizado)
                .endereco(request.endereco())
                .observacoes(request.observacoes())
                .build();

        Cliente salvo = clienteRepository.save(cliente);
        return ClienteResponse.fromEntity(salvo);

    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId (UUID id) {
        Cliente cliente = buscarEntidadePorId(id);
        return ClienteResponse.fromEntity(cliente);

    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponse::fromEntity)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> buscarPorNome (String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(ClienteResponse::fromEntity)
                .toList();

    }

    public ClienteResponse atualizar(UUID id, ClienteRequest request) {
        Cliente cliente = buscarEntidadePorId(id);
        String telefoneNormalizado = request.telefoneNormalizado();

        clienteRepository.findByTelefone(telefoneNormalizado).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new RegraDeNegocioException(
                        "Já existe outro cliente cadastrado com o telefone: " + telefoneNormalizado);
            }
        });

        cliente.setNome(request.nome());
        cliente.setTelefone(telefoneNormalizado);
        cliente.setEndereco(request.endereco());
        cliente.setObservacoes(request.observacoes());

        Cliente atualizado = clienteRepository.save(cliente);
        return ClienteResponse.fromEntity(atualizado);


    }

    public void deletar(UUID id) {
        Cliente cliente = buscarEntidadePorId(id);
        clienteRepository.delete(cliente);

    }






    //dry
    @Transactional(readOnly = true)
    public Cliente buscarEntidadePorId(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado com id: " + id));
    }
}
