package com.gabrielf.revendedora_api.service;

import com.gabrielf.revendedora_api.domain.entity.Cliente;
import com.gabrielf.revendedora_api.dto.ClienteRequest;
import com.gabrielf.revendedora_api.dto.ClienteResponse;
import com.gabrielf.revendedora_api.exception.RecursoNaoEncontradoException;
import com.gabrielf.revendedora_api.exception.RegraDeNegocioException;
import com.gabrielf.revendedora_api.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private UUID clienteId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cliente = Cliente.builder()
                .id(clienteId)
                .nome("Maria Silva")
                .telefone("11987654321")
                .build();
    }

    @Test
    void deveCriarClienteNormalizandoTelefone() {
        ClienteRequest request = new ClienteRequest(
                "Maria Silva", "(11) 98765-4321", "Rua A, 123", null);

        when(clienteRepository.findByTelefone("11987654321")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteResponse response = clienteService.criar(request);

        assertThat(response.telefone()).isEqualTo("11987654321");
    }

    @Test
    void deveLancarExcecaoAoCriarClienteComTelefoneJaCadastrado() {
        ClienteRequest request = new ClienteRequest(
                "Outra Pessoa", "(11) 98765-4321", null, null);

        when(clienteRepository.findByTelefone("11987654321")).thenReturn(Optional.of(cliente));

        assertThatThrownBy(() -> clienteService.criar(request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Já existe um cliente");
    }

    @Test
    void devePermitirAtualizarClienteMantendoOProprioTelefone() {
        ClienteRequest request = new ClienteRequest(
                "Maria Silva Santos", "(11) 98765-4321", "Rua Nova, 456", null);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByTelefone("11987654321")).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteResponse response = clienteService.atualizar(clienteId, request);

        assertThat(response.nome()).isEqualTo("Maria Silva Santos");
    }

    @Test
    void deveLancarExcecaoAoAtualizarComTelefoneDeOutroCliente() {
        Cliente outroCliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nome("Outra Pessoa")
                .telefone("11999998888")
                .build();

        ClienteRequest request = new ClienteRequest(
                "Maria Silva", "(11) 99999-8888", null, null);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByTelefone("11999998888")).thenReturn(Optional.of(outroCliente));

        assertThatThrownBy(() -> clienteService.atualizar(clienteId, request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("outro cliente");
    }

    @Test
    void deveLancarExcecaoAoBuscarClienteInexistente() {
        UUID idInexistente = UUID.randomUUID();
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(idInexistente))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
