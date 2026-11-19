import React, { useState } from 'react';
import { Layers, LogIn, AlertTriangle, Loader2 } from 'lucide-react';

interface LoginPageProps {
  onLogin: (username: string, password: string) => Promise<void>;
  sessionExpired?: boolean;
}

export const LoginPage: React.FC<LoginPageProps> = ({ onLogin, sessionExpired = false }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password) {
      setError('Informe usuário e senha.');
      return;
    }
    try {
      setSubmitting(true);
      setError(null);
      await onLogin(username.trim(), password);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Falha ao autenticar.');
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0B0C0E] text-zinc-100 flex items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <div className="flex items-center gap-3 mb-6">
          <div className="w-9 h-9 bg-zinc-900 border border-zinc-700 flex items-center justify-center">
            <Layers className="w-4 h-4" />
          </div>
          <div>
            <h1 className="text-sm font-semibold tracking-tight text-white">Homeney</h1>
            <p className="text-xs text-zinc-400">Gestão de Chamados & SLA</p>
          </div>
        </div>

        <form
          onSubmit={handleSubmit}
          className="bg-[#111216] border border-zinc-800 p-5 space-y-4"
          aria-label="Formulário de login"
        >
          <div>
            <h2 className="text-sm font-medium text-white">Entrar</h2>
            <p className="text-xs text-zinc-500 mt-0.5">Use suas credenciais de operador.</p>
          </div>

          {sessionExpired && !error && (
            <div role="status" className="p-2 bg-amber-950/30 border border-amber-800 text-amber-400 text-xs font-mono">
              Sua sessão expirou. Entre novamente.
            </div>
          )}

          {error && (
            <div role="alert" className="p-2 bg-rose-950/30 border border-rose-800 text-rose-400 text-xs font-mono flex items-center gap-2">
              <AlertTriangle className="w-3.5 h-3.5 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <div className="space-y-3 text-xs font-mono">
            <div>
              <label htmlFor="username" className="block text-zinc-400 mb-1">Usuário</label>
              <input
                id="username"
                type="text"
                autoComplete="username"
                autoFocus
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full bg-[#0B0C0E] border border-zinc-800 focus:border-zinc-500 px-3 py-2 text-white focus:outline-none"
              />
            </div>
            <div>
              <label htmlFor="password" className="block text-zinc-400 mb-1">Senha</label>
              <input
                id="password"
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-[#0B0C0E] border border-zinc-800 focus:border-zinc-500 px-3 py-2 text-white focus:outline-none"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full px-3.5 py-2 text-xs font-medium bg-zinc-100 hover:bg-white text-zinc-950 flex items-center justify-center gap-1.5 transition-colors disabled:opacity-60"
          >
            {submitting ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <LogIn className="w-3.5 h-3.5" />}
            <span>{submitting ? 'Entrando...' : 'Entrar'}</span>
          </button>
        </form>
      </div>
    </div>
  );
};
