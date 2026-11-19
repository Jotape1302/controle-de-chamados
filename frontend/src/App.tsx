import React, { useCallback, useEffect, useState } from 'react';
import { Loader2 } from 'lucide-react';
import { Dashboard } from './pages/Dashboard';
import { LoginPage } from './pages/LoginPage';
import { api } from './services/api';
import { auth } from './services/auth';
import { User } from './types';

type AuthState =
  | { status: 'checking' }
  | { status: 'anonymous'; sessionExpired: boolean }
  | { status: 'authenticated'; user: User };

export const App: React.FC = () => {
  const [state, setState] = useState<AuthState>(() =>
    auth.load() ? { status: 'checking' } : { status: 'anonymous', sessionExpired: false },
  );

  // Valida a sessão salva no navegador contra a API ao abrir a aplicação.
  useEffect(() => {
    if (state.status !== 'checking') return;
    api
      .me()
      .then((user) => setState({ status: 'authenticated', user }))
      .catch(() => {
        auth.clear();
        setState({ status: 'anonymous', sessionExpired: false });
      });
  }, [state.status]);

  // Qualquer 401 da API (token expirado/inválido) derruba a sessão.
  useEffect(
    () => auth.onUnauthorized(() => setState({ status: 'anonymous', sessionExpired: true })),
    [],
  );

  const handleLogin = useCallback(async (username: string, password: string) => {
    const response = await api.login(username, password);
    auth.save(response);
    setState({ status: 'authenticated', user: response.user });
  }, []);

  const handleLogout = useCallback(() => {
    auth.clear();
    setState({ status: 'anonymous', sessionExpired: false });
  }, []);

  if (state.status === 'checking') {
    return (
      <div className="min-h-screen bg-[#0B0C0E] flex items-center justify-center text-zinc-400">
        <Loader2 className="w-5 h-5 animate-spin" aria-label="Carregando" />
      </div>
    );
  }

  if (state.status === 'anonymous') {
    return <LoginPage onLogin={handleLogin} sessionExpired={state.sessionExpired} />;
  }

  return <Dashboard currentUser={state.user} onLogout={handleLogout} />;
};

export default App;
