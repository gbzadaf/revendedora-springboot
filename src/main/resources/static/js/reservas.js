import { api } from './api.js';
import { esc, mostrar, esconder, dataBR } from './ui.js';

const lista = document.getElementById('reservas-lista');
const form = document.getElementById('form-reserva');
const erro = document.getElementById('reservas-erro');
const filtro = document.getElementById('reservas-filtro');
const selCliente = document.getElementById('reserva-cliente');
const selProduto = document.getElementById('reserva-produto');
const inputQtd = document.getElementById('reserva-quantidade');

const ROTULO = {
  AGUARDANDO: 'Aguardando',
  DISPONIVEL: 'Disponível: avisar a cliente',
  ENTREGUE: 'Entregue',
  CANCELADA: 'Cancelada',
};

let reservas = [];

export async function carregarReservas() {
  try {
    const [listaReservas, clientes, produtos] = await Promise.all([
      api('/reservas'),
      api('/clientes'),
      api('/produtos'),
    ]);
    reservas = listaReservas;
    preencherSelects(clientes, produtos);
    renderizar();
    esconder(erro);
  } catch (e) {
    mostrar(erro, e.message);
  }
}

function preencherSelects(clientes, produtos) {
  selCliente.innerHTML = '<option value="">Selecione...</option>' +
    clientes.map((c) => `<option value="${c.id}">${esc(c.nome)}</option>`).join('');

  // a API so aceita reserva de produto com estoque zerado
  const semEstoque = produtos.filter((p) => p.quantidadeEstoque === 0);
  selProduto.innerHTML = semEstoque.length
    ? '<option value="">Selecione...</option>' +
      semEstoque.map((p) => `<option value="${p.id}">${esc(p.nome)}</option>`).join('')
    : '<option value="">Nenhum produto sem estoque</option>';
}

function passaNoFiltro(r) {
  const f = filtro.value;
  if (f === 'todas') return true;
  if (f === 'abertas') return r.status === 'AGUARDANDO' || r.status === 'DISPONIVEL';
  return r.status === f;
}

function renderizar() {
  // disponiveis primeiro (pedem acao), depois por ordem de chegada
  const visiveis = reservas
    .filter(passaNoFiltro)
    .sort((a, b) => {
      if (a.status === 'DISPONIVEL' && b.status !== 'DISPONIVEL') return -1;
      if (b.status === 'DISPONIVEL' && a.status !== 'DISPONIVEL') return 1;
      return a.dataReserva.localeCompare(b.dataReserva);
    });

  if (visiveis.length === 0) {
    lista.innerHTML = '<tr><td colspan="6">Nenhuma reserva nesse filtro.</td></tr>';
    return;
  }

  lista.innerHTML = visiveis.map((r) => {
    const podeCancelar = r.status === 'AGUARDANDO' || r.status === 'DISPONIVEL';
    return `
      <tr class="${r.status === 'DISPONIVEL' ? 'reserva-disponivel' : ''}">
        <td>${esc(r.clienteNome)}</td>
        <td>${esc(r.produtoNome)}</td>
        <td>${r.quantidade}</td>
        <td>${dataBR(r.dataReserva)}</td>
        <td>${ROTULO[r.status] ?? esc(r.status)}</td>
        <td class="acoes">
          ${r.status === 'DISPONIVEL'
            ? `<button data-acao="entregar" data-id="${r.id}">Marcar como entregue</button>`
            : ''}
          ${podeCancelar
            ? `<button data-acao="cancelar" data-id="${r.id}" class="perigo">Cancelar</button>`
            : ''}
        </td>
      </tr>`;
  }).join('');
}

filtro.addEventListener('change', renderizar);

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  esconder(erro);
  try {
    await api('/reservas', {
      method: 'POST',
      body: JSON.stringify({
        clienteId: selCliente.value,
        produtoId: selProduto.value,
        quantidade: parseInt(inputQtd.value, 10),
      }),
    });
    form.reset();
    await carregarReservas();
  } catch (e) {
    mostrar(erro, e.message);
  }
});

lista.addEventListener('click', async (e) => {
  const botao = e.target.closest('button[data-acao]');
  if (!botao) return;

  const reserva = reservas.find((r) => r.id === botao.dataset.id);
  esconder(erro);

  try {
    if (botao.dataset.acao === 'entregar') {
      if (!confirm(`Confirmar a entrega de "${reserva.produtoNome}" para ${reserva.clienteNome}?`)) return;
      await api(`/reservas/${reserva.id}/entregar`, { method: 'PATCH' });
    }

    if (botao.dataset.acao === 'cancelar') {
      if (!confirm(`Cancelar a reserva de "${reserva.produtoNome}" para ${reserva.clienteNome}?`)) return;
      await api(`/reservas/${reserva.id}/cancelar`, { method: 'PATCH' });
    }

    await carregarReservas();
  } catch (e) {
    mostrar(erro, e.message);
  }
});