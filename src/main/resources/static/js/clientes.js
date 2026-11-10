import { api } from './api.js';
import { esc, moeda, mostrar, esconder, telefoneBR, hojeISO, semAcento } from './ui.js';
import { abrirDetalhe } from './vendas.js';

const lista = document.getElementById('clientes-lista');
const form = document.getElementById('form-cliente');
const erro = document.getElementById('clientes-erro');
const busca = document.getElementById('clientes-busca');
const btnSalvar = document.getElementById('cliente-salvar');
const btnCancelar = document.getElementById('cliente-cancelar');

const campo = {
  id: document.getElementById('cliente-id'),
  nome: document.getElementById('cliente-nome'),
  telefone: document.getElementById('cliente-telefone'),
  endereco: document.getElementById('cliente-endereco'),
  observacoes: document.getElementById('cliente-observacoes'),
};

let clientes = [];
let devido = new Map(); // clienteId -> { centavos, atrasado }

export async function carregarClientes() {
  try {
    const [listaClientes, vendas] = await Promise.all([api('/clientes'), api('/vendas')]);
    clientes = listaClientes;
    calcularDevido(vendas);
    renderizar();
    esconder(erro);
  } catch (e) {
    mostrar(erro, e.message);
  }
}

// soma em centavos pra nao acumular erro de ponto flutuante
function calcularDevido(vendas) {
  const hoje = hojeISO();
  devido = new Map();
  for (const venda of vendas) {
    for (const p of venda.parcelas) {
      if (p.status === 'PAGA') continue;
      const atual = devido.get(venda.clienteId) ?? { centavos: 0, atrasado: false };
      atual.centavos += Math.round(p.valor * 100);
      if (p.dataVencimento < hoje) atual.atrasado = true;
      devido.set(venda.clienteId, atual);
    }
  }
}

function renderizar() {
  const termo = semAcento(busca.value.trim());
  const dados = (c) => devido.get(c.id) ?? { centavos: 0, atrasado: false };

  // atrasados primeiro, depois quem deve mais, depois ordem alfabetica
  const visiveis = clientes
    .filter((c) => semAcento(c.nome).includes(termo))
    .sort((a, b) => {
      const da = dados(a);
      const db = dados(b);
      if (da.atrasado !== db.atrasado) return da.atrasado ? -1 : 1;
      if (da.centavos !== db.centavos) return db.centavos - da.centavos;
      return a.nome.localeCompare(b.nome, 'pt-BR');
    });

  if (visiveis.length === 0) {
    const msg = clientes.length === 0 ? 'Nenhum cliente cadastrado.' : 'Nenhum cliente encontrado.';
    lista.innerHTML = `<tr><td colspan="4">${msg}</td></tr>`;
    return;
  }

  lista.innerHTML = visiveis.map((c) => {
    const d = dados(c);
    return `
      <tr class="${d.atrasado ? 'cliente-atrasado' : ''}">
        <td>
          ${esc(c.nome)}
          ${c.observacoes ? `<br><small class="obs">${esc(c.observacoes)}</small>` : ''}
        </td>
        <td>${telefoneBR(c.telefone)}</td>
        <td>
          ${d.centavos > 0 ? moeda(d.centavos / 100) : '—'}
          ${d.atrasado ? '<span class="selo selo-vermelho">atrasada</span>' : ''}
        </td>
        <td class="acoes">
          <button data-acao="vendas" data-id="${c.id}">Vendas</button>
          <button data-acao="editar" data-id="${c.id}" class="secundario">Editar</button>
          <button data-acao="excluir" data-id="${c.id}" class="perigo">Excluir</button>
        </td>
      </tr>`;
  }).join('');
}

function limparFormulario() {
  form.reset();
  campo.id.value = '';
  btnSalvar.textContent = 'Cadastrar';
  btnCancelar.classList.add('hidden');
}

function iniciarEdicao(cliente) {
  campo.id.value = cliente.id;
  campo.nome.value = cliente.nome;
  campo.telefone.value = telefoneBR(cliente.telefone);
  campo.endereco.value = cliente.endereco ?? '';
  campo.observacoes.value = cliente.observacoes ?? '';
  btnSalvar.textContent = 'Salvar alterações';
  btnCancelar.classList.remove('hidden');
  campo.nome.focus();
}

busca.addEventListener('input', renderizar);
btnCancelar.addEventListener('click', limparFormulario);

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  esconder(erro);

  const corpo = {
    nome: campo.nome.value.trim(),
    telefone: campo.telefone.value.trim(),
    endereco: campo.endereco.value.trim() || null,
    observacoes: campo.observacoes.value.trim() || null,
  };

  try {
    if (campo.id.value) {
      await api(`/clientes/${campo.id.value}`, { method: 'PUT', body: JSON.stringify(corpo) });
    } else {
      await api('/clientes', { method: 'POST', body: JSON.stringify(corpo) });
    }
    limparFormulario();
    await carregarClientes();
  } catch (err) {
    mostrar(erro, err.message);
  }
});

lista.addEventListener('click', async (e) => {
  const botao = e.target.closest('button[data-acao]');
  if (!botao) return;

  const cliente = clientes.find((c) => c.id === botao.dataset.id);
  esconder(erro);

  if (botao.dataset.acao === 'editar') {
    iniciarEdicao(cliente);
  }

  if (botao.dataset.acao === 'vendas') {
    abrirDetalhe(cliente, carregarClientes);
  }

  if (botao.dataset.acao === 'excluir') {
    if (!confirm(`Excluir "${cliente.nome}"?`)) return;
    try {
      await api(`/clientes/${cliente.id}`, { method: 'DELETE' });
      await carregarClientes();
    } catch (err) {
      mostrar(erro, err.message);
    }
  }
});