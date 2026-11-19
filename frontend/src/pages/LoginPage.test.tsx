import { describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LoginPage } from './LoginPage';

describe('LoginPage', () => {
  it('envia usuário (sem espaços) e senha', async () => {
    const onLogin = vi.fn().mockResolvedValue(undefined);
    render(<LoginPage onLogin={onLogin} />);

    await userEvent.type(screen.getByLabelText('Usuário'), '  admin ');
    await userEvent.type(screen.getByLabelText('Senha'), 'segredo123');
    await userEvent.click(screen.getByRole('button', { name: /entrar/i }));

    expect(onLogin).toHaveBeenCalledWith('admin', 'segredo123');
  });

  it('valida campos vazios sem chamar a API', async () => {
    const onLogin = vi.fn();
    render(<LoginPage onLogin={onLogin} />);

    await userEvent.click(screen.getByRole('button', { name: /entrar/i }));

    expect(onLogin).not.toHaveBeenCalled();
    expect(screen.getByRole('alert')).toHaveTextContent('Informe usuário e senha.');
  });

  it('mostra o erro retornado pelo login', async () => {
    const onLogin = vi.fn().mockRejectedValue(new Error('Usuário ou senha inválidos.'));
    render(<LoginPage onLogin={onLogin} />);

    await userEvent.type(screen.getByLabelText('Usuário'), 'admin');
    await userEvent.type(screen.getByLabelText('Senha'), 'errada');
    await userEvent.click(screen.getByRole('button', { name: /entrar/i }));

    expect(await screen.findByRole('alert')).toHaveTextContent('Usuário ou senha inválidos.');
    expect(screen.getByRole('button', { name: /entrar/i })).toBeEnabled();
  });

  it('avisa quando a sessão expirou', () => {
    render(<LoginPage onLogin={vi.fn()} sessionExpired />);
    expect(screen.getByRole('status')).toHaveTextContent('Sua sessão expirou');
  });
});
