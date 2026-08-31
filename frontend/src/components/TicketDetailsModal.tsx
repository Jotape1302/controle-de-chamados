import React, { useState } from 'react';
import { 
  X, 
  Clock, 
  Calendar, 
  User as UserIcon, 
  Tag, 
  ShieldCheck, 
  CheckCircle2, 
  Trash2,
  Copy,
  Check
} from 'lucide-react';
import { Ticket, TicketStatus, TicketPriority, User } from '../types';

interface TicketDetailsModalProps {
  ticket: Ticket | null;
  currentUser: User;
  onClose: () => void;
  onUpdateStatus: (id: number, status: TicketStatus) => void;
  onUpdatePriority: (id: number, priority: TicketPriority) => void;
  onDeleteTicket: (id: number) => void;
}

export const TicketDetailsModal: React.FC<TicketDetailsModalProps> = ({
  ticket,
  currentUser,
  onClose,
  onUpdateStatus,
  onUpdatePriority,
  onDeleteTicket,
}) => {
  const [copied, setCopied] = useState(false);

  if (!ticket) return null;

  const handleCopyId = () => {
    navigator.clipboard.writeText(`#${ticket.id} - ${ticket.title}`);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const statuses: TicketStatus[] = ['Aberto', 'Em Análise', 'Concluído', 'Encerrado'];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-xs">
      <div className="w-full max-w-3xl bg-[#111216] border border-zinc-700 shadow-2xl flex flex-col overflow-hidden max-h-[90vh]">
        {/* Header */}
        <div className="px-5 py-3.5 border-b border-zinc-800 bg-[#0D0E11] flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="px-2 py-0.5 bg-zinc-900 text-zinc-300 font-mono text-xs font-semibold border border-zinc-700">
              #{String(ticket.id).padStart(4, '0')}
            </span>
            <div>
              <span className="text-[10px] font-mono text-zinc-500 block uppercase tracking-wider">
                Detalhes do Chamado
              </span>
              <h3 className="text-sm font-semibold text-white line-clamp-1">
                {ticket.title}
              </h3>
            </div>
          </div>

          <div className="flex items-center gap-1.5">
            <button
              onClick={handleCopyId}
              className="p-1 text-zinc-400 hover:text-zinc-200 hover:bg-zinc-800 transition-colors"
              title="Copiar referência do chamado"
            >
              {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
            </button>
            <button
              onClick={onClose}
              className="p-1 text-zinc-400 hover:text-white hover:bg-zinc-800 transition-colors"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Content Body */}
        <div className="p-5 space-y-5 overflow-y-auto flex-1">
          {/* Status Stepper */}
          <div className="p-3.5 bg-[#16181D] border border-zinc-800 space-y-2.5">
            <div className="flex items-center justify-between text-xs font-mono">
              <span className="text-zinc-400 uppercase tracking-wider">Status do Chamado</span>
              <span className="text-zinc-300 font-medium">{ticket.status}</span>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-1.5">
              {statuses.map((st) => {
                const isActive = ticket.status === st;

                return (
                  <button
                    key={st}
                    onClick={() => onUpdateStatus(ticket.id, st)}
                    className={`py-1.5 px-2 text-xs font-mono text-center border transition-colors ${
                      isActive
                        ? 'bg-zinc-800 border-zinc-600 text-white font-medium'
                        : 'bg-[#111216] border-zinc-800 text-zinc-400 hover:text-zinc-200 hover:border-zinc-700'
                    }`}
                  >
                    {st}
                  </button>
                );
              })}
            </div>
          </div>

          {/* Metadata Grid */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5 text-xs font-mono">
            {/* Priority */}
            <div className="p-2.5 bg-[#16181D] border border-zinc-800">
              <span className="text-[10px] text-zinc-500 block mb-1">PRIORIDADE</span>
              <div className="flex items-center justify-between">
                <span className={`font-semibold ${
                  ticket.priority === 'Crítica' ? 'text-rose-400' :
                  ticket.priority === 'Alta' ? 'text-purple-300' :
                  ticket.priority === 'Média' ? 'text-amber-300' : 'text-zinc-300'
                }`}>
                  {ticket.priority}
                </span>
                {currentUser.admin && (
                  <select
                    value={ticket.priority}
                    onChange={(e) => onUpdatePriority(ticket.id, e.target.value as TicketPriority)}
                    className="bg-zinc-900 text-[10px] text-zinc-400 border border-zinc-700 px-1 py-0.5 cursor-pointer"
                  >
                    <option value="Baixa">Baixa</option>
                    <option value="Média">Média</option>
                    <option value="Alta">Alta</option>
                    <option value="Crítica">Crítica</option>
                  </select>
                )}
              </div>
            </div>

            {/* Category */}
            <div className="p-2.5 bg-[#16181D] border border-zinc-800">
              <span className="text-[10px] text-zinc-500 block mb-1">CATEGORIA</span>
              <span className="font-medium text-zinc-200 flex items-center gap-1">
                <Tag className="w-3 h-3 text-zinc-500" />
                {ticket.category}
              </span>
            </div>

            {/* Requester */}
            <div className="p-2.5 bg-[#16181D] border border-zinc-800">
              <span className="text-[10px] text-zinc-500 block mb-1">SOLICITANTE</span>
              <span className="font-medium text-zinc-200 flex items-center gap-1">
                <UserIcon className="w-3 h-3 text-zinc-500" />
                {ticket.requester}
              </span>
            </div>

            {/* Assigned To */}
            <div className="p-2.5 bg-[#16181D] border border-zinc-800">
              <span className="text-[10px] text-zinc-500 block mb-1">RESPONSÁVEL</span>
              <span className="font-medium text-zinc-200 flex items-center gap-1">
                <ShieldCheck className="w-3 h-3 text-zinc-500" />
                {ticket.assignedTo || 'Não atribuído'}
              </span>
            </div>
          </div>

          {/* Description Box */}
          <div className="space-y-1.5">
            <span className="text-xs font-mono text-zinc-400 uppercase tracking-wider block">
              Descrição do Chamado
            </span>
            <div className="p-3.5 bg-[#0D0E11] border border-zinc-800 text-xs text-zinc-200 leading-relaxed whitespace-pre-wrap font-sans">
              {ticket.description}
            </div>
          </div>

          {/* Timestamps */}
          <div className="p-3 bg-[#16181D]/60 border border-zinc-800/80 flex flex-wrap items-center justify-between gap-3 text-[11px] font-mono text-zinc-500">
            <div className="flex items-center gap-1.5">
              <Calendar className="w-3.5 h-3.5 text-zinc-500" />
              <span>Aberto em: <b className="text-zinc-300 font-normal">{new Date(ticket.createdAt).toLocaleString('pt-BR')}</b></span>
            </div>

            {ticket.resolvedAt && (
              <div className="flex items-center gap-1.5 text-emerald-400">
                <CheckCircle2 className="w-3.5 h-3.5" />
                <span>Resolvido em: <b>{new Date(ticket.resolvedAt).toLocaleString('pt-BR')}</b></span>
              </div>
            )}

            <div className="flex items-center gap-1.5">
              <Clock className="w-3.5 h-3.5 text-zinc-500" />
              <span>Atualizado: <b className="text-zinc-300 font-normal">{new Date(ticket.updatedAt).toLocaleString('pt-BR')}</b></span>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="px-5 py-3 border-t border-zinc-800 bg-[#0D0E11] flex items-center justify-between">
          {(currentUser.admin || currentUser.username === ticket.requester) ? (
            <button
              onClick={() => {
                if (window.confirm(`Deseja realmente excluir o chamado #${ticket.id}?`)) {
                  onDeleteTicket(ticket.id);
                  onClose();
                }
              }}
              className="px-3 py-1.5 bg-zinc-900 hover:bg-rose-950/40 text-zinc-500 hover:text-rose-400 border border-zinc-800 hover:border-rose-800/60 text-xs font-mono flex items-center gap-1.5 transition-colors"
            >
              <Trash2 className="w-3.5 h-3.5" />
              <span>Excluir Chamado</span>
            </button>
          ) : <div />}

          <button
            onClick={onClose}
            className="px-4 py-1.5 bg-zinc-800 hover:bg-zinc-700 text-zinc-200 text-xs font-medium transition-colors"
          >
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
};
