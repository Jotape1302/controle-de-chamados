import { describe, expect, it, vi } from 'vitest';
import { auth } from './auth';
import { LoginResponse } from '../types';

const user = { id: 1, username: 'admin', fullName: 'Admin', email: 'a@x', admin: true };

function response(expiresInMs: number): LoginResponse {
  return {
    accessToken: 'token-abc',
    tokenType: 'Bearer',
    expiresAt: new Date(Date.now() + expiresInMs).toISOString(),
    user,
  };
}

describe('auth', () => {
  it('salva e recupera a sessão', () => {
    auth.save(response(60_000));
    expect(auth.token()).toBe('token-abc');
    expect(auth.load()?.user.username).toBe('admin');
  });

  it('descarta sessão expirada', () => {
    auth.save(response(-1_000));
    expect(auth.load()).toBeNull();
  });

  it('descarta sessão corrompida', () => {
    localStorage.setItem('homeney.session', '{não é json');
    expect(auth.load()).toBeNull();
  });

  it('notifyUnauthorized limpa a sessão e avisa os ouvintes', () => {
    auth.save(response(60_000));
    const listener = vi.fn();
    const unsubscribe = auth.onUnauthorized(listener);

    auth.notifyUnauthorized();

    expect(listener).toHaveBeenCalledOnce();
    expect(auth.token()).toBeNull();
    unsubscribe();
  });
});
