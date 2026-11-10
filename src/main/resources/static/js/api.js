const TOKEN_KEY = 'revendedora_token';
const BASE_URL = '/api/v1';

export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const setToken = (token) => localStorage.setItem(TOKEN_KEY, token);
export const clearToken = () => localStorage.removeItem(TOKEN_KEY);

export async function api(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  const resposta = await fetch(`${BASE_URL}${path}`, { ...options, headers });

  // token expirado/invalido: volta pro login
  if (token && (resposta.status === 401 || resposta.status === 403)) {
    clearToken();
    window.dispatchEvent(new Event('sessao-expirada'));
    throw new Error('Sessão expirada. Faça login novamente.');
  }

  if (!resposta.ok) {
    const erro = await resposta.json().catch(() => null);
    const detalhes = erro?.detalhes?.length ? ` (${erro.detalhes.join('; ')})` : '';
    throw new Error((erro?.mensagem ?? `Erro inesperado (HTTP ${resposta.status})`) + detalhes);
  }

  return resposta.status === 204 ? null : resposta.json();
}