// evita que um nome com <tag> quebre a tela (ou execute script)
export function esc(valor) {
  return String(valor ?? '').replace(/[&<>"']/g, (c) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;',
  }[c]));
}

export const moeda = (valor) =>
  Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

export function mostrar(elemento, texto) {
  elemento.textContent = texto;
  elemento.classList.remove('hidden');
}

export function esconder(elemento) {
  elemento.classList.add('hidden');
}

// aceita data com hora (venda/reserva) e data pura (vencimento de parcela)
export function dataBR(valor) {
  if (!valor) return '';
  if (valor.length === 10) {
    const [ano, mes, dia] = valor.split('-');
    return `${dia}/${mes}/${ano}`;
  }
  return new Date(valor).toLocaleDateString('pt-BR');
}

// 21987654321 -> (21) 98765-4321
export function telefoneBR(valor) {
  const d = String(valor ?? '').replace(/\D/g, '');
  if (d.length === 11) return `(${d.slice(0, 2)}) ${d.slice(2, 7)}-${d.slice(7)}`;
  if (d.length === 10) return `(${d.slice(0, 2)}) ${d.slice(2, 6)}-${d.slice(6)}`;
  return d;
}

// data de hoje no fuso local, no formato 2026-11-09 (mesmo formato que a API devolve)
export const hojeISO = () => new Date().toLocaleDateString('sv-SE');

// "José" e "jose" passam a ser iguais na busca
export const semAcento = (texto) =>
  String(texto ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();