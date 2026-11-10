import { api } from './api.js';
import { esc, moeda, mostrar, esconder } from './ui.js';

const lista = document.getElementById('produtos-lista');
const form = document.getElementById('form-produto');
const erro = document.getElementById('produtos-erro');
const aviso = document.getElementById('produtos-aviso');
const btnSalvar = document.getElementById('produto-salvar');
const btnCancelar = document.getElementById('produto-cancelar');

const campo = {
  id: document.getElementById('produto-id'),
  nome: document.getElementById('produto-nome'),
  marca: document.getElementById('produto-marca'),
  preco: document.getElementById('produto-preco'),
  estoque: document.getElementById('produto-estoque'),
  minimo: document.getElementById('produto-minimo'),
};

let produtos = [];

export async function carregarProdutos() {
  try {
    produtos = await api('/produtos');
    renderizar();
    esconder(erro);
  } catch (e) {
    mostrar(erro, e.message);
  }
}

function renderizar() {
  if (produtos.length === 0) {
    lista.innerHTML = '<tr><td colspan="6">Nenhum produto cadastrado.</td></tr>';
    return;
  }

  lista.innerHTML = produtos.map((p) => {
    const baixo = p.quantidadeEstoque <= p.quantidadeMinima;
    return `
      <tr class="${baixo ? 'estoque-baixo' : ''}">
        <td>${esc(p.nome)}</td>
        <td>${esc(p.marca)}</td>
        <td>${moeda(p.preco)}</td>
        <td>${p.quantidadeEstoque}${baixo ? ' <span class="selo">baixo</span>' : ''}</td>
        <td>${p.quantidadeMinima}</td>
        <td class="acoes">
          <button data-acao="repor" data-id="${p.id}">Repor estoque</button>
          <button data-acao="editar" data-id="${p.id}" class="secundario">Editar</button>
          <button data-acao="excluir" data-id="${p.id}" class="perigo">Excluir</button>
        </td>
      </tr>`;
  }).join('');
}

function limparFormulario() {
  form.reset();
  campo.id.value = '';
  campo.estoque.disabled = false;
  btnSalvar.textContent = 'Cadastrar';
  btnCancelar.classList.add('hidden');
}

function iniciarEdicao(produto) {
  campo.id.value = produto.id;
  campo.nome.value = produto.nome;
  campo.marca.value = produto.marca;
  campo.preco.value = produto.preco;
  campo.estoque.value = produto.quantidadeEstoque;
  campo.minimo.value = produto.quantidadeMinima;
  // estoque so muda pelo botao "Repor estoque" (ele libera as reservas)
  campo.estoque.disabled = true;
  btnSalvar.textContent = 'Salvar alterações';
  btnCancelar.classList.remove('hidden');
  campo.nome.focus();
}

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  esconder(erro);

  const corpo = {
    nome: campo.nome.value.trim(),
    marca: campo.marca.value,
    preco: Number(campo.preco.value),
    quantidadeEstoque: parseInt(campo.estoque.value, 10),
    quantidadeMinima: parseInt(campo.minimo.value, 10) || 0,
  };

  try {
    if (campo.id.value) {
      await api(`/produtos/${campo.id.value}`, { method: 'PUT', body: JSON.stringify(corpo) });
    } else {
      await api('/produtos', { method: 'POST', body: JSON.stringify(corpo) });
    }
    limparFormulario();
    await carregarProdutos();
  } catch (e) {
    mostrar(erro, e.message);
  }
});

btnCancelar.addEventListener('click', limparFormulario);

lista.addEventListener('click', async (e) => {
  const botao = e.target.closest('button[data-acao]');
  if (!botao) return;

  const produto = produtos.find((p) => p.id === botao.dataset.id);
  esconder(erro);
  esconder(aviso);

  try {
    if (botao.dataset.acao === 'editar') {
      iniciarEdicao(produto);
    }

    if (botao.dataset.acao === 'excluir') {
      if (!confirm(`Excluir "${produto.nome}"?`)) return;
      await api(`/produtos/${produto.id}`, { method: 'DELETE' });
      await carregarProdutos();
    }

    if (botao.dataset.acao === 'repor') {
      const texto = prompt(`Quantas unidades chegaram de "${produto.nome}"?`);
      if (texto === null) return;
      const quantidade = parseInt(texto, 10);
      if (!Number.isInteger(quantidade) || quantidade <= 0) {
        mostrar(erro, 'Informe um número inteiro maior que zero.');
        return;
      }

      const resultado = await api(`/produtos/${produto.id}/estoque`, {
        method: 'PATCH',
        body: JSON.stringify({ quantidade }),
      });

      const liberadas = resultado.reservasLiberadas;
      if (liberadas.length > 0) {
        const nomes = liberadas
          .map((r) => `${r.clienteNome} (${r.quantidade}x)`)
          .join(', ');
        mostrar(aviso, `${liberadas.length} reserva(s) liberada(s): avise ${nomes}.`);
      }
      await carregarProdutos();
    }
  } catch (e) {
    mostrar(erro, e.message);
  }
});