import { getToken } from './api.js';
import { fazerLogin, sair } from './auth.js';
import { carregarProdutos } from './produtos.js';
import { carregarReservas } from './reservas.js';
import { carregarClientes } from './clientes.js';

const carregadores = {
  produtos: carregarProdutos,
  reservas: carregarReservas,
  clientes: carregarClientes,
};

const telaLogin = document.getElementById('tela-login');
const telaApp = document.getElementById('tela-app');
const loginErro = document.getElementById('login-erro');

function mostrarTela() {
  const logado = !!getToken();
  telaLogin.classList.toggle('hidden', logado);
  telaApp.classList.toggle('hidden', !logado);
  if (logado) carregarProdutos();
}

document.getElementById('form-login').addEventListener('submit', async (e) => {
  e.preventDefault();
  loginErro.classList.add('hidden');
  try {
    await fazerLogin(
      document.getElementById('login').value,
      document.getElementById('senha').value
    );
    mostrarTela();
  } catch (erro) {
    loginErro.textContent = erro.message;
    loginErro.classList.remove('hidden');
  }
});

document.getElementById('btn-sair').addEventListener('click', () => {
  sair();
  mostrarTela();
});

window.addEventListener('sessao-expirada', mostrarTela);

// abas
document.querySelectorAll('nav button').forEach((botao) => {
  botao.addEventListener('click', () => {
    document.querySelectorAll('nav button').forEach((b) => b.classList.remove('ativa'));
    botao.classList.add('ativa');
    ['produtos', 'reservas', 'clientes'].forEach((aba) => {
      document.getElementById(`aba-${aba}`)
        .classList.toggle('hidden', aba !== botao.dataset.aba);
    });
    carregadores[botao.dataset.aba]?.();
  });
});

mostrarTela();