import React, { useState, useEffect } from 'react';
import { 
  Plus, 
  RefreshCw, 
  Users, 
  ShieldCheck,
  Layers,
  LogOut
} from 'lucide-react';
import { User } from '../types';

interface HeaderProps {
  currentUser: User;
  onLogout: () => void;
  onOpenNewTicket: () => void;
  onOpenUserManagement: () => void;
  onRefresh: () => void;
  isRefreshing: boolean;
  apiConnected: boolean;
}

export const Header: React.FC<HeaderProps> = ({
  currentUser,
  onLogout,
  onOpenNewTicket,
  onOpenUserManagement,
  onRefresh,
  isRefreshing,
  apiConnected,
}) => {
  const [time, setTime] = useState<string>('');

  useEffect(() => {
    const update = () => {
      const now = new Date();
      setTime(now.toLocaleTimeString('pt-BR', { hour12: false }));
    };
    update();
    const interval = setInterval(update, 1000);
    return () => clearInterval(interval);
  }, []);

  return (
    <header className="border-b border-zinc-800 bg-[#0B0C0E] sticky top-0 z-30">
      {/* Top Status Bar - Minimal Enterprise Telemetry */}
      <div className="px-4 lg:px-8 py-1.5 bg-[#090A0C] border-b border-zinc-800/80 flex items-center justify-between text-[11px] font-mono text-zinc-400">
        <div className="flex items-center space-x-4">
          <span className="flex items-center gap-1.5">
            <span className={`w-1.5 h-1.5 ${apiConnected ? 'bg-emerald-500' : 'bg-rose-500'}`} />
            <span className="text-zinc-300">
              {apiConnected ? 'API Online' : 'API Desconectada'}
            </span>
          </span>
        </div>

        <div className="flex items-center space-x-3 text-zinc-500">
          <span className="font-mono text-zinc-400">{time}</span>
        </div>
      </div>

      {/* Main Header */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3 flex flex-col md:flex-row md:items-center justify-between gap-3">
        {/* Brand & Subtitle */}
        <div className="flex items-center space-x-3">
          <div className="w-8 h-8 bg-zinc-900 border border-zinc-700 flex items-center justify-center text-zinc-100">
            <Layers className="w-4 h-4" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-sm font-semibold tracking-tight text-white">
                Homeney
              </h1>
              <span className="text-[10px] px-1.5 py-0.2 bg-zinc-900 text-zinc-400 border border-zinc-800 font-mono">
                v2.0
              </span>
            </div>
            <p className="text-xs text-zinc-400">
              Enterprise Incident & SLA Management
            </p>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex flex-wrap items-center gap-2">
          {/* Usuário autenticado */}
          <div className="flex items-center bg-zinc-900/80 border border-zinc-800 text-xs">
            <div className="flex items-center gap-1.5 text-zinc-300 px-2 py-1.5">
              <ShieldCheck className={`w-3.5 h-3.5 ${currentUser.admin ? 'text-zinc-200' : 'text-zinc-400'}`} />
              <span className="font-medium">{currentUser.fullName || currentUser.username}</span>
              <span className={`text-[10px] font-mono px-1 py-0.2 ${
                currentUser.admin ? 'bg-zinc-800 text-zinc-200 border border-zinc-700' : 'text-zinc-500'
              }`}>
                {currentUser.admin ? 'ADMIN' : 'USER'}
              </span>
            </div>
            <button
              onClick={onLogout}
              className="px-2 py-1.5 border-l border-zinc-800 text-zinc-400 hover:text-white hover:bg-zinc-800 transition-colors"
              title="Sair"
              aria-label="Sair"
            >
              <LogOut className="w-3.5 h-3.5" />
            </button>
          </div>

          {/* User Management (Admin only) */}
          {currentUser.admin && (
            <button
              onClick={onOpenUserManagement}
              className="px-2.5 py-1.5 text-xs font-medium bg-zinc-900 hover:bg-zinc-800 text-zinc-300 hover:text-white border border-zinc-800 transition-colors flex items-center gap-1.5"
              title="Gerenciar Usuários e Permissões"
            >
              <Users className="w-3.5 h-3.5 text-zinc-400" />
              <span>Usuários</span>
            </button>
          )}

          {/* Refresh Button */}
          <button
            onClick={onRefresh}
            disabled={isRefreshing}
            className="p-1.5 text-xs bg-zinc-900 hover:bg-zinc-800 text-zinc-400 hover:text-zinc-200 border border-zinc-800 transition-colors disabled:opacity-50"
            title="Atualizar Dados"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isRefreshing ? 'animate-spin text-zinc-200' : ''}`} />
          </button>

          {/* Open New Ticket CTA Button */}
          <button
            onClick={onOpenNewTicket}
            className="px-3.5 py-1.5 text-xs font-medium bg-zinc-100 hover:bg-white text-zinc-950 flex items-center gap-1.5 border border-zinc-200 transition-colors active:scale-[0.99]"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Novo Chamado</span>
          </button>
        </div>
      </div>
    </header>
  );
};
