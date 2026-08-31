import React from 'react';
import { 
  AlertCircle, 
  Clock, 
  Trash2, 
  Eye, 
  User as UserIcon, 
  Tag
} from 'lucide-react';
import { Ticket, TicketPriority, TicketStatus, User } from '../types';

interface TicketTableProps {
  tickets: Ticket[];
  currentUser: User;
  onSelectTicket: (ticket: Ticket) => void;
  onUpdateStatus: (id: number, status: TicketStatus) => void;
  onUpdatePriority: (id: number, priority: TicketPriority) => void;
  onDeleteTicket: (id: number) => void;
}

export const TicketTable: React.FC<TicketTableProps> = ({
  tickets,
  currentUser,
  onSelectTicket,
  onUpdateStatus,
  onUpdatePriority,
  onDeleteTicket,
}) => {
  const getPriorityBadge = (priority: TicketPriority) => {
    switch (priority) {
      case 'Crítica':
        return (
          <span className="inline-flex items-center gap-1.5 px-2 py-0.5 text-[11px] font-mono font-medium bg-rose-950/30 text-rose-400 border border-rose-800/60">
            <span className="w-1.5 h-1.5 bg-rose-500" />
            Crítica
          </span>
        );
      case 'Alta':
        return (
          <span className="inline-flex items-center gap-1.5 px-2 py-0.5 text-[11px] font-mono font-medium bg-purple-950/30 text-purple-300 border border-purple-800/60">
            <span className="w-1.5 h-1.5 bg-purple-400" />
            Alta
          </span>
        );
      case 'Média':
        return (
          <span className="inline-flex items-center gap-1.5 px-2 py-0.5 text-[11px] font-mono font-medium bg-amber-950/30 text-amber-300 border border-amber-800/60">
            <span className="w-1.5 h-1.5 bg-amber-400" />
            Média
          </span>
        );
      case 'Baixa':
      default:
        return (
          <span className="inline-flex items-center gap-1.5 px-2 py-0.5 text-[11px] font-mono font-medium bg-zinc-900 text-zinc-300 border border-zinc-700">
            <span className="w-1.5 h-1.5 bg-zinc-400" />
            Baixa
          </span>
        );
    }
  };

  const getStatusBadgeClass = (status: TicketStatus) => {
    switch (status) {
      case 'Aberto':
        return 'bg-blue-950/30 text-blue-300 border-blue-800/60';
      case 'Em Análise':
        return 'bg-amber-950/30 text-amber-300 border-amber-800/60';
      case 'Concluído':
        return 'bg-emerald-950/30 text-emerald-300 border-emerald-800/60';
      case 'Encerrado':
      default:
        return 'bg-zinc-900 text-zinc-400 border-zinc-700';
    }
  };

  const formatRelativeTime = (dateStr: string) => {
    if (!dateStr) return '-';
    try {
      const date = new Date(dateStr);
      return date.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' });
    } catch {
      return dateStr;
    }
  };

  if (tickets.length === 0) {
    return (
      <div className="bg-[#111216] border border-zinc-800 p-12 text-center space-y-2">
        <AlertCircle className="w-8 h-8 text-zinc-600 mx-auto" />
        <h3 className="text-xs font-mono font-semibold text-zinc-300 uppercase">Nenhum chamado encontrado</h3>
        <p className="text-xs text-zinc-500 max-w-sm mx-auto">
          Nenhum registro corresponde aos filtros selecionados.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-[#111216] border border-zinc-800 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs border-collapse">
          {/* Table Header */}
          <thead className="bg-[#0D0E11] text-zinc-400 font-mono text-[11px] uppercase tracking-wider border-b border-zinc-800">
            <tr>
              <th className="py-2.5 px-4 font-medium">ID</th>
              <th className="py-2.5 px-4 font-medium">Chamado / Descrição</th>
              <th className="py-2.5 px-4 font-medium">Categoria</th>
              <th className="py-2.5 px-4 font-medium">Prioridade</th>
              <th className="py-2.5 px-4 font-medium">Status</th>
              <th className="py-2.5 px-4 font-medium">Solicitante</th>
              <th className="py-2.5 px-4 font-medium">Data / SLA</th>
              <th className="py-2.5 px-4 font-medium text-right">Ações</th>
            </tr>
          </thead>

          {/* Table Body */}
          <tbody className="divide-y divide-zinc-800/80">
            {tickets.map((ticket) => {
              const isOverdue = ticket.slaBreached;

              return (
                <tr
                  key={ticket.id}
                  className="hover:bg-[#16181D] transition-colors group"
                >
                  {/* ID */}
                  <td className="py-3 px-4 font-mono font-medium text-zinc-300 whitespace-nowrap">
                    #{String(ticket.id).padStart(4, '0')}
                  </td>

                  {/* Title & Description preview */}
                  <td className="py-3 px-4 max-w-xs md:max-w-md">
                    <button
                      onClick={() => onSelectTicket(ticket)}
                      className="text-left font-medium text-zinc-200 hover:text-white transition-colors line-clamp-1 group-hover:underline"
                    >
                      {ticket.title}
                    </button>
                    <p className="text-[11px] text-zinc-500 line-clamp-1 mt-0.5 font-sans">
                      {ticket.description}
                    </p>
                  </td>

                  {/* Category */}
                  <td className="py-3 px-4 whitespace-nowrap">
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 bg-zinc-900 text-zinc-400 border border-zinc-800 text-[10px] font-mono">
                      <Tag className="w-2.5 h-2.5 text-zinc-500" />
                      {ticket.category || 'Geral'}
                    </span>
                  </td>

                  {/* Priority */}
                  <td className="py-3 px-4 whitespace-nowrap">
                    {currentUser.admin ? (
                      <div className="relative inline-block">
                        <select
                          value={ticket.priority}
                          onChange={(e) => onUpdatePriority(ticket.id, e.target.value as TicketPriority)}
                          className="bg-transparent text-xs font-mono cursor-pointer focus:outline-none appearance-none pr-4"
                        >
                          <option value="Baixa" className="bg-[#111216] text-zinc-300">Baixa</option>
                          <option value="Média" className="bg-[#111216] text-amber-300">Média</option>
                          <option value="Alta" className="bg-[#111216] text-purple-300">Alta</option>
                          <option value="Crítica" className="bg-[#111216] text-rose-400">Crítica</option>
                        </select>
                        {getPriorityBadge(ticket.priority)}
                      </div>
                    ) : (
                      getPriorityBadge(ticket.priority)
                    )}
                  </td>

                  {/* Status with inline selector */}
                  <td className="py-3 px-4 whitespace-nowrap">
                    <select
                      value={ticket.status}
                      onChange={(e) => onUpdateStatus(ticket.id, e.target.value as TicketStatus)}
                      className={`text-[11px] font-mono font-medium px-2 py-0.5 border cursor-pointer focus:outline-none transition-colors ${getStatusBadgeClass(ticket.status)}`}
                    >
                      <option value="Aberto" className="bg-[#111216] text-blue-300">Aberto</option>
                      <option value="Em Análise" className="bg-[#111216] text-amber-300">Em Análise</option>
                      <option value="Concluído" className="bg-[#111216] text-emerald-300">Concluído</option>
                      <option value="Encerrado" className="bg-[#111216] text-zinc-400">Encerrado</option>
                    </select>
                  </td>

                  {/* Requester */}
                  <td className="py-3 px-4 whitespace-nowrap">
                    <div className="flex items-center gap-1.5 text-zinc-400 text-xs font-mono">
                      <UserIcon className="w-3 h-3 text-zinc-500" />
                      <span>{ticket.requester}</span>
                    </div>
                  </td>

                  {/* Created At / SLA */}
                  <td className="py-3 px-4 whitespace-nowrap font-mono text-[11px]">
                    <div className="flex flex-col">
                      <span className="text-zinc-300">{formatRelativeTime(ticket.createdAt)}</span>
                      {ticket.status !== 'Concluído' && ticket.status !== 'Encerrado' && (
                        <span className={`text-[10px] flex items-center gap-1 mt-0.5 ${
                          isOverdue ? 'text-rose-400 font-semibold' : 'text-zinc-500'
                        }`}>
                          <Clock className="w-2.5 h-2.5" />
                          {isOverdue ? 'SLA Expirado' : `SLA: ${ticket.priority === 'Crítica' ? '2h' : ticket.priority === 'Alta' ? '8h' : ticket.priority === 'Média' ? '24h' : '72h'}`}
                        </span>
                      )}
                    </div>
                  </td>

                  {/* Actions */}
                  <td className="py-3 px-4 text-right whitespace-nowrap">
                    <div className="flex items-center justify-end gap-1">
                      <button
                        onClick={() => onSelectTicket(ticket)}
                        className="p-1 bg-zinc-900 hover:bg-zinc-800 text-zinc-400 hover:text-zinc-200 border border-zinc-800 transition-colors"
                        title="Ver Detalhes"
                      >
                        <Eye className="w-3.5 h-3.5" />
                      </button>

                      {(currentUser.admin || currentUser.username === ticket.requester) && (
                        <button
                          onClick={() => {
                            if (window.confirm(`Deseja realmente excluir o chamado #${ticket.id}?`)) {
                              onDeleteTicket(ticket.id);
                            }
                          }}
                          className="p-1 bg-zinc-900 hover:bg-rose-950/40 text-zinc-500 hover:text-rose-400 border border-zinc-800 hover:border-rose-800/60 transition-colors"
                          title="Excluir Chamado"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};
