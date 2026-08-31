import React from 'react';
import { 
  CheckCircle2, 
  ArrowRight, 
  Tag, 
  User as UserIcon
} from 'lucide-react';
import { Ticket, TicketStatus, TicketPriority } from '../types';

interface TicketKanbanProps {
  tickets: Ticket[];
  onSelectTicket: (ticket: Ticket) => void;
  onUpdateStatus: (id: number, status: TicketStatus) => void;
}

export const TicketKanban: React.FC<TicketKanbanProps> = ({
  tickets,
  onSelectTicket,
  onUpdateStatus,
}) => {
  const columns: { title: string; status: TicketStatus; color: string; border: string; bg: string }[] = [
    {
      title: 'Aberto',
      status: 'Aberto',
      color: 'text-blue-400',
      border: 'border-zinc-800',
      bg: 'bg-[#0E1015]/40',
    },
    {
      title: 'Em Análise',
      status: 'Em Análise',
      color: 'text-amber-400',
      border: 'border-zinc-800',
      bg: 'bg-[#12110D]/40',
    },
    {
      title: 'Concluído',
      status: 'Concluído',
      color: 'text-emerald-400',
      border: 'border-zinc-800',
      bg: 'bg-[#0E1410]/40',
    },
    {
      title: 'Encerrado',
      status: 'Encerrado',
      color: 'text-zinc-400',
      border: 'border-zinc-800',
      bg: 'bg-zinc-900/20',
    },
  ];

  const getPriorityBorder = (priority: TicketPriority) => {
    switch (priority) {
      case 'Crítica':
        return 'border-l-2 border-l-rose-500';
      case 'Alta':
        return 'border-l-2 border-l-purple-500';
      case 'Média':
        return 'border-l-2 border-l-amber-500';
      case 'Baixa':
      default:
        return 'border-l-2 border-l-zinc-600';
    }
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-3">
      {columns.map((col) => {
        const columnTickets = tickets.filter((t) => t.status === col.status);

        return (
          <div
            key={col.status}
            className={`border ${col.border} ${col.bg} p-3 flex flex-col min-h-[480px] bg-[#111216]`}
          >
            {/* Column Header */}
            <div className="flex items-center justify-between pb-2.5 mb-2.5 border-b border-zinc-800">
              <span className={`text-xs font-mono font-medium uppercase tracking-wider ${col.color}`}>
                {col.title}
              </span>
              <span className="text-[10px] font-mono font-medium px-1.5 py-0.2 bg-zinc-900 text-zinc-400 border border-zinc-800">
                {columnTickets.length}
              </span>
            </div>

            {/* Cards List */}
            <div className="space-y-2 flex-1 overflow-y-auto max-h-[640px] pr-0.5">
              {columnTickets.length === 0 ? (
                <div className="h-28 border border-dashed border-zinc-800 flex items-center justify-center text-[11px] font-mono text-zinc-600">
                  Nenhum chamado
                </div>
              ) : (
                columnTickets.map((ticket) => (
                  <div
                    key={ticket.id}
                    className={`p-3 bg-[#16181D] border border-zinc-800 hover:border-zinc-700 transition-colors cursor-pointer group ${getPriorityBorder(ticket.priority)}`}
                    onClick={() => onSelectTicket(ticket)}
                  >
                    {/* Card Top: ID + Priority */}
                    <div className="flex items-center justify-between text-[11px] font-mono mb-1.5">
                      <span className="font-semibold text-zinc-300">
                        #{String(ticket.id).padStart(4, '0')}
                      </span>
                      <span className={`text-[10px] font-mono px-1.5 py-0.2 border ${
                        ticket.priority === 'Crítica' ? 'bg-rose-950/30 text-rose-400 border-rose-800/60' :
                        ticket.priority === 'Alta' ? 'bg-purple-950/30 text-purple-300 border-purple-800/60' :
                        ticket.priority === 'Média' ? 'bg-amber-950/30 text-amber-300 border-amber-800/60' :
                        'bg-zinc-900 text-zinc-400 border-zinc-700'
                      }`}>
                        {ticket.priority}
                      </span>
                    </div>

                    {/* Card Title */}
                    <h4 className="text-xs font-medium text-zinc-100 group-hover:text-white transition-colors line-clamp-2 mb-1">
                      {ticket.title}
                    </h4>

                    {/* Description preview */}
                    <p className="text-[11px] text-zinc-500 line-clamp-2 mb-2 font-sans">
                      {ticket.description}
                    </p>

                    {/* Metadata: Category + Requester */}
                    <div className="flex items-center justify-between text-[10px] font-mono text-zinc-500 border-t border-zinc-800/80 pt-2 mb-1.5">
                      <span className="flex items-center gap-1 text-zinc-400">
                        <Tag className="w-2.5 h-2.5 text-zinc-500" />
                        {ticket.category}
                      </span>
                      <span className="flex items-center gap-1">
                        <UserIcon className="w-2.5 h-2.5 text-zinc-500" />
                        {ticket.requester}
                      </span>
                    </div>

                    {/* Quick Move Action Buttons */}
                    <div
                      className="flex items-center justify-end gap-1 pt-1"
                      onClick={(e) => e.stopPropagation()}
                    >
                      {col.status === 'Aberto' && (
                        <button
                          onClick={() => onUpdateStatus(ticket.id, 'Em Análise')}
                          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 text-amber-300 border border-zinc-700 text-[10px] font-mono flex items-center gap-1 transition-colors"
                          title="Mover para Em Análise"
                        >
                          <span>Analisar</span>
                          <ArrowRight className="w-2.5 h-2.5" />
                        </button>
                      )}

                      {col.status === 'Em Análise' && (
                        <button
                          onClick={() => onUpdateStatus(ticket.id, 'Concluído')}
                          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 text-emerald-300 border border-zinc-700 text-[10px] font-mono flex items-center gap-1 transition-colors"
                          title="Concluir Chamado"
                        >
                          <CheckCircle2 className="w-2.5 h-2.5" />
                          <span>Concluir</span>
                        </button>
                      )}

                      {col.status === 'Concluído' && (
                        <button
                          onClick={() => onUpdateStatus(ticket.id, 'Encerrado')}
                          className="px-2 py-0.5 bg-zinc-900 hover:bg-zinc-800 text-zinc-400 border border-zinc-700 text-[10px] font-mono flex items-center gap-1 transition-colors"
                          title="Encerrar Chamado"
                        >
                          <span>Encerrar</span>
                          <ArrowRight className="w-2.5 h-2.5" />
                        </button>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};
