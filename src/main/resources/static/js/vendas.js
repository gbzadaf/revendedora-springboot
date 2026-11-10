import { api } from './api.js';
import { esc, moeda, mostrar, esconder, dataBR, hojeISO } from './ui.js';

const painel = document.getElementById('cliente-detalhe');
const painelLista = document.getElementById('clientes-painel-lista');
const elNome = document.getElementById('detalhe-nome');
const elResumo = document.getElementById('detalhe-resumo');
const erro = document.getElementById('detalhe-erro');
const historico = document.getElementById('detalhe-vendas');

const form = document.getElementById('form-venda');
const selProduto = document.getElementById('venda-produto');
const inputQtd = document.getElementById('venda-quantidade');
const btnAdd = document.getElementById('venda-add-item');
const tbodyItens = document.getElementById('venda-itens');
const inputParcelas = document.getElementById('venda-parcelas');
const elTotal = document.getElementById('venda-total');

const STATUS_VENDA = {
  PENDENTE: 'Pendente',
  PARCIALMENTE_PAGA: 'Parcialmente paga',
  QUITADA: 'Quitada',
  CANCELADA: 'Cancelada',
};

let cliente = null;
let aoMudar = () => {};
let produtos = [];
let vendas = [];
let carrinho = [];

export async function abrirDetalhe(c, callback) {
  cliente = c;
  aoMudar = callback;
  carrinho = [];
  form.reset();
  elNome.textContent = c.nome;
  esconder(erro);
  painelLista.classList.add('hidden');
  painel.classList.remove('hidden');
  renderCarrinho();
  await recarregar();
}

async function recarregar() {
  try {
    const [listaVendas, listaProdutos] = await Promise.all([
      api(`/vendas/cliente/${cliente.id}`),
      api('/produtos'),
    ]);
    vendas = listaVendas;
    produtos = listaProdutos;
    renderProdutos();
    renderResumo();
    renderHistorico();
  } catch (e) {
    mostrar(erro, e.message);
  }
}

function renderProdutos() {
  const disponiveis = produtos.filter((p) => p.quantidadeEstoque > 0);
  selProduto.innerHTML = disponiveis.length
    ? disponiveis.map((p) =>
        `<option value="${p.id}">${esc(p.nome)} (${moeda(p.preco)}, ${p.quantidadeEstoque} em estoque)</option>`
      ).join('')
    : '<option value="">Nenhum produto em estoque</option>';
}

function renderResumo() {
  const hoje = hojeISO();
  let centavos = 0;
  let abertas = 0;
  let atrasadas = 0;

  for (const venda of vendas) {
    for (const p of venda.parcelas) {
      if (p.status === 'PAGA') continue;
      centavos += Math.round(p.valor * 100);
      abertas++;
      if (p.dataVencimento < hoje) atrasadas++;
    }
  }

  elResumo.textContent = abertas === 0
    ? 'Nenhuma parcela em aberto.'
    : `Deve ${moeda(centavos / 100)} em ${abertas} parcela(s)` +
      (atrasadas > 0 ? `, ${atrasadas} atrasada(s).` : '.');
}

function renderHistorico() {
  if (vendas.length === 0) {
    historico.innerHTML = '<p>Nenhuma venda registrada para esse cliente.</p>';
    return;
  }

  const hoje = hojeISO();

  historico.innerHTML = [...vendas]
    .sort((a, b) => b.dataVenda.localeCompare(a.dataVenda))
    .map((v) => {
      const itens = v.itens.map((i) => `${i.quantidade}x ${esc(i.produtoNome)}`).join(', ');

      const parcelas = [...v.parcelas]
        .sort((a, b) => a.numeroParcela - b.numeroParcela)
        .map((p) => {
          const paga = p.status === 'PAGA';
          const atrasada = !paga && p.dataVencimento < hoje;
          const situacao = paga
            ? `Paga em ${dataBR(p.dataPagamento)}`
            : atrasada
              ? '<span class="selo selo-vermelho">atrasada</span>'
              : 'A vencer';

          return `
            <tr class="${atrasada ? 'cliente-atrasado' : ''}">
              <td>${p.numeroParcela}/${v.parcelas.length}</td>
              <td>${dataBR(p.dataVencimento)}</td>
              <td>${moeda(p.valor)}</td>
              <td>${situacao}</td>
              <td class="acoes">
                ${paga ? '' : `<button data-venda="${v.id}" data-pagar="${p.numeroParcela}">Marcar como paga</button>`}
              </td>
            </tr>`;
        }).join('');

      return `
        <article class="venda-card">
          <div class="venda-topo">
            <strong>${dataBR(v.dataVenda)}</strong>
            <span class="selo selo-cinza">${STATUS_VENDA[v.status] ?? esc(v.status)}</span>
            <strong class="total">${moeda(v.valorTotal)}</strong>
          </div>
          <p class="obs">${itens}</p>
          <table>
            <thead>
              <tr><th>Parcela</th><th>Vencimento</th><th>Valor</th><th>Situação</th><th></th></tr>
            </thead>
            <tbody>${parcelas}</tbody>
          </table>
        </article>`;
    }).join('');
}

function renderCarrinho() {
  tbodyItens.innerHTML = carrinho.length === 0
    ? '<tr><td colspan="5">Nenhum item adicionado.</td></tr>'
    : carrinho.map((i, idx) => `
        <tr>
          <td>${esc(i.nome)}</td>
          <td>${i.quantidade}</td>
          <td>${moeda(i.preco)}</td>
          <td>${moeda((Math.round(i.preco * 100) * i.quantidade) / 100)}</td>
          <td class="acoes">
            <button type="button" data-remover="${idx}" class="perigo">Remover</button>
          </td>
        </tr>`).join('');

  const centavos = carrinho.reduce(
    (soma, i) => soma + Math.round(i.preco * 100) * i.quantidade, 0);
  elTotal.textContent = `Total: ${moeda(centavos / 100)}`;
}

btnAdd.addEventListener('click', () => {
  esconder(erro);

  const produto = produtos.find((p) => p.id === selProduto.value);
  const quantidade = parseInt(inputQtd.value, 10);

  if (!produto) {
    mostrar(erro, 'Selecione um produto.');
    return;
  }
  if (!Number.isInteger(quantidade) || quantidade <= 0) {
    mostrar(erro, 'Informe uma quantidade maior que zero.');
    return;
  }

  const existente = carrinho.find((i) => i.produtoId === produto.id);
  const total = (existente?.quantidade ?? 0) + quantidade;

  if (total > produto.quantidadeEstoque) {
    mostrar(erro, `Estoque insuficiente de "${produto.nome}": só há ${produto.quantidadeEstoque} unidade(s).`);
    return;
  }

  if (existente) {
    existente.quantidade = total;
  } else {
    carrinho.push({
      produtoId: produto.id,
      nome: produto.nome,
      preco: produto.preco,
      quantidade,
    });
  }

  inputQtd.value = 1;
  renderCarrinho();
});

tbodyItens.addEventListener('click', (e) => {
  const botao = e.target.closest('button[data-remover]');
  if (!botao) return;
  carrinho.splice(Number(botao.dataset.remover), 1);
  renderCarrinho();
});

form.addEventListener('submit', async (e) => {
  e.preventDefault();
  esconder(erro);

  if (carrinho.length === 0) {
    mostrar(erro, 'Adicione pelo menos um item à venda.');
    return;
  }

  try {
    await api('/vendas', {
      method: 'POST',
      body: JSON.stringify({
        clienteId: cliente.id,
        itens: carrinho.map((i) => ({ produtoId: i.produtoId, quantidade: i.quantidade })),
        numeroParcelas: parseInt(inputParcelas.value, 10),
      }),
    });

    carrinho = [];
    form.reset();
    renderCarrinho();
    await recarregar();
    aoMudar();
  } catch (err) {
    mostrar(erro, err.message);
  }
});

historico.addEventListener('click', async (e) => {
  const botao = e.target.closest('button[data-pagar]');
  if (!botao) return;

  const venda = vendas.find((v) => v.id === botao.dataset.venda);
  const numero = Number(botao.dataset.pagar);
  const parcela = venda.parcelas.find((p) => p.numeroParcela === numero);

  if (!confirm(`Marcar a parcela ${numero} (${moeda(parcela.valor)}) como paga?`)) return;

  esconder(erro);
  try {
    await api(`/vendas/${venda.id}/parcelas/${numero}/pagar`, { method: 'PATCH' });
    await recarregar();
    aoMudar();
  } catch (err) {
    mostrar(erro, err.message);
  }
});

document.getElementById('detalhe-voltar').addEventListener('click', () => {
  painel.classList.add('hidden');
  painelLista.classList.remove('hidden');
  cliente = null;
});