import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { api, ApiError } from './api';
import { auth } from './auth';

function jsonResponse(status: number, body: unknown): Response {
  return new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } });
}

describe('api', () => {
  const fetchMock = vi.fn();

  beforeEach(() => {
    vi.stubGlobal('fetch', fetchMock);
    auth.save({
      accessToken: 'meu-token',
      tokenType: 'Bearer',
      expiresAt: new Date(Date.now() + 60_000).toISOString(),
      user: { id: 1, username: 'admin', fullName: 'Admin', email: 'a@x', admin: true },
    });
  });

  afterEach(() => {
    fetchMock.mockReset();
    vi.unstubAllGlobals();
  });

  it('envia o token Bearer nas requisições autenticadas', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, []));

    await api.getTickets({ status: 'Aberto', priority: 'ALL' });

    const [url, init] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/tickets?status=Aberto');
    expect(new Headers(init.headers).get('Authorization')).toBe('Bearer meu-token');
  });

  it('não envia token no login', async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, { accessToken: 'x' }));

    await api.login('admin', 'senha');

    const [, init] = fetchMock.mock.calls[0];
    expect(new Headers(init.headers).get('Authorization')).toBeNull();
    expect(JSON.parse(init.body)).toEqual({ username: 'admin', password: 'senha' });
  });

  it('em 401 encerra a sessão e lança ApiError', async () => {
    const listener = vi.fn();
    const unsubscribe = auth.onUnauthorized(listener);
    fetchMock.mockResolvedValue(jsonResponse(401, { message: 'Token expirado' }));

    await expect(api.getMetrics()).rejects.toMatchObject({ status: 401, message: 'Token expirado' });
    expect(listener).toHaveBeenCalledOnce();
    expect(auth.token()).toBeNull();
    unsubscribe();
  });

  it('usa a mensagem e os erros de campo retornados pela API', async () => {
    fetchMock.mockResolvedValue(
      jsonResponse(400, { message: 'A senha deve ter entre 8 e 72 caracteres.', errors: { password: 'curta' } }),
    );

    const error = await api.createUser({ username: 'x', password: '1', admin: false }).catch((e) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect(error.message).toBe('A senha deve ter entre 8 e 72 caracteres.');
    expect(error.fieldErrors).toEqual({ password: 'curta' });
  });

  it('usa mensagem padrão quando a resposta não tem corpo', async () => {
    fetchMock.mockResolvedValue(new Response('', { status: 500 }));
    await expect(api.getUsers()).rejects.toThrow('Falha ao listar usuários');
  });

  it('retorna undefined em 204', async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }));
    await expect(api.deleteTicket(1)).resolves.toBeUndefined();
  });
});
