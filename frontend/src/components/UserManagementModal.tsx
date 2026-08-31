import React, { useState } from 'react';
import { 
  X, 
  Users, 
  UserPlus, 
  ShieldCheck, 
  User as UserIcon, 
  AlertTriangle,
  CheckCircle2
} from 'lucide-react';
import { User } from '../types';

interface UserManagementModalProps {
  isOpen: boolean;
  onClose: () => void;
  users: User[];
  onAddUser: (userData: { username: string; password?: string; fullName?: string; email?: string; admin: boolean }) => Promise<void>;
}

export const UserManagementModal: React.FC<UserManagementModalProps> = ({
  isOpen,
  onClose,
  users,
  onAddUser,
}) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('123');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim()) {
      setError('Nome de usuário é obrigatório.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      await onAddUser({
        username: username.trim(),
        password: password.trim(),
        fullName: fullName.trim() || username.trim(),
        email: email.trim() || `${username.trim().toLowerCase()}@homeney.tech`,
        admin: isAdmin,
      });

      setSuccess(`Usuário '${username}' cadastrado com sucesso!`);
      setUsername('');
      setPassword('123');
      setFullName('');
      setEmail('');
      setIsAdmin(false);
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.message || 'Erro ao cadastrar usuário.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-xs">
      <div className="w-full max-w-2xl bg-[#111216] border border-zinc-700 shadow-2xl flex flex-col overflow-hidden max-h-[90vh]">
        {/* Header */}
        <div className="px-5 py-3.5 border-b border-zinc-800 bg-[#0D0E11] flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Users className="w-4 h-4 text-zinc-400" />
            <div>
              <h3 className="text-sm font-medium text-white">
                Gestão de Usuários & Operadores
              </h3>
              <p className="text-[11px] text-zinc-400">
                Controle de acessos e permissões do sistema
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1 text-zinc-400 hover:text-white hover:bg-zinc-800 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Content Body */}
        <div className="p-5 space-y-5 overflow-y-auto flex-1">
          {/* User Registration Form */}
          <div className="p-4 bg-[#16181D] border border-zinc-800 space-y-3">
            <div className="flex items-center gap-2 text-xs font-mono font-medium text-zinc-300 uppercase tracking-wider">
              <UserPlus className="w-3.5 h-3.5" />
              <span>Cadastrar Novo Usuário</span>
            </div>

            {error && (
              <div className="p-2 bg-rose-950/30 border border-rose-800 text-rose-400 text-xs font-mono flex items-center gap-2">
                <AlertTriangle className="w-3.5 h-3.5 shrink-0" />
                <span>{error}</span>
              </div>
            )}

            {success && (
              <div className="p-2 bg-emerald-950/30 border border-emerald-800 text-emerald-400 text-xs font-mono flex items-center gap-2">
                <CheckCircle2 className="w-3.5 h-3.5 shrink-0" />
                <span>{success}</span>
              </div>
            )}

            <form onSubmit={handleSubmit} className="grid grid-cols-1 sm:grid-cols-2 gap-2.5 text-xs font-mono">
              <div>
                <label className="block text-zinc-400 mb-1">Nome de Usuário (Login) *</label>
                <input
                  type="text"
                  required
                  placeholder="ex: operador2"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full bg-[#111216] border border-zinc-800 focus:border-zinc-500 px-3 py-1.5 text-white focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-zinc-400 mb-1">Senha *</label>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full bg-[#111216] border border-zinc-800 focus:border-zinc-500 px-3 py-1.5 text-white focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-zinc-400 mb-1">Nome Completo</label>
                <input
                  type="text"
                  placeholder="Nome do Operador"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="w-full bg-[#111216] border border-zinc-800 focus:border-zinc-500 px-3 py-1.5 text-white focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-zinc-400 mb-1">E-mail</label>
                <input
                  type="email"
                  placeholder="operador@homeney.tech"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full bg-[#111216] border border-zinc-800 focus:border-zinc-500 px-3 py-1.5 text-white focus:outline-none"
                />
              </div>

              <div className="sm:col-span-2 flex items-center justify-between pt-1.5">
                <label className="flex items-center gap-2 cursor-pointer text-zinc-300">
                  <input
                    type="checkbox"
                    checked={isAdmin}
                    onChange={(e) => setIsAdmin(e.target.checked)}
                    className="border-zinc-700 bg-[#111216] text-zinc-100 focus:ring-0 w-3.5 h-3.5 cursor-pointer"
                  />
                  <span>Permissão de Administrador</span>
                </label>

                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="px-3.5 py-1.5 bg-zinc-100 hover:bg-white text-zinc-950 font-medium transition-colors disabled:opacity-50"
                >
                  {isSubmitting ? 'Salvando...' : 'Adicionar Usuário'}
                </button>
              </div>
            </form>
          </div>

          {/* Users List */}
          <div className="space-y-2">
            <div className="flex items-center justify-between text-xs font-mono">
              <span className="text-zinc-400 uppercase tracking-wider">Usuários Registrados ({users.length})</span>
            </div>

            <div className="divide-y divide-zinc-800 border border-zinc-800 bg-[#0E1014]">
              {users.map((user) => (
                <div key={user.id || user.username} className="p-2.5 flex items-center justify-between text-xs font-mono hover:bg-[#16181D] transition-colors">
                  <div className="flex items-center gap-2.5">
                    <div className={`p-1 ${user.admin ? 'text-zinc-200' : 'text-zinc-500'}`}>
                      {user.admin ? <ShieldCheck className="w-4 h-4" /> : <UserIcon className="w-4 h-4" />}
                    </div>
                    <div>
                      <div className="flex items-center gap-1.5">
                        <span className="font-medium text-white">{user.username}</span>
                        <span className="text-zinc-500 text-[11px]">({user.fullName || user.username})</span>
                      </div>
                      <span className="text-[10px] text-zinc-500">{user.email || `${user.username}@homeney.tech`}</span>
                    </div>
                  </div>

                  <span className={`text-[10px] font-mono px-1.5 py-0.2 border ${
                    user.admin ? 'bg-zinc-800 text-zinc-200 border-zinc-700' : 'bg-zinc-900 text-zinc-500 border-zinc-800'
                  }`}>
                    {user.admin ? 'ADMIN' : 'USUÁRIO'}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="px-5 py-3 border-t border-zinc-800 bg-[#0D0E11] flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-1.5 bg-zinc-800 hover:bg-zinc-700 text-zinc-200 text-xs font-medium"
          >
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
};
