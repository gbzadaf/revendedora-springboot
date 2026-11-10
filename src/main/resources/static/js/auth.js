import { api, setToken, clearToken } from './api.js';

export async function fazerLogin(login, senha) {
  clearToken(); // garante que nao manda token velho no login
  const resposta = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ login, senha }),
  });
  setToken(resposta.token);
}

export function sair() {
  clearToken();
}