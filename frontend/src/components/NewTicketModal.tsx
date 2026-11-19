import React, { useState } from 'react';
import { 
  X, 
  AlertTriangle, 
  Send 
} from 'lucide-react';
import { NewTicketInput, TicketPriority, User } from '../types';

interface NewTicketModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (ticket: NewTicketInput) => Promise<void>;
  currentUser: User;
}

const CATEGORIES = [
  'Infraestrutura',
  'Segurança',
  'Banco de Dados',
  'Redes',
  'Software',
  'Acesso & Permissões',
  'Hardware',
  'Geral',
];

const PRIORITIES: { value: TicketPriority; label: string; sla: string; border: string; activeBorder: string; activeBg: string; activeText: string }[] = [
  {
    value: 'Baixa',
    label: 'Baixa',
    sla: 'SLA: 72h',
    border: 'border-zinc-800',
    activeBorder: 'border-zinc-500',
    activeBg: 'bg-zinc-800/80',
    activeText: 'text-zinc-100',
  },
  {
    value: 'Média',
    label: 'Média',
    sla: 'SLA: 24h',
    border: 'border-zinc-800',
    activeBorder: 'border-amber-600',
    activeBg: 'bg-amber-950/20',
    activeText: 'text-amber-400',
  },
  {
    value: 'Alta',
    label: 'Alta',
    sla: 'SLA: 8h',
    border: 'border-zinc-800',
    activeBorder: 'border-purple-600',
    activeBg: 'bg-purple-950/20',
    activeText: 'text-purple-300',
  },
  {
    value: 'Crítica',
    label: 'Crítica',
    sla: 'SLA: 2h',
    border: 'border-zinc-800',
    activeBorder: 'border-rose-600',
    activeBg: 'bg-rose-950/20',
    activeText: 'text-rose-400',
  },
];

export const NewTicketModal: React.FC<NewTicketModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  currentUser,
}) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('Infraestrutura');
  const [priority, setPriority] = useState<TicketPriority>('Baixa');
  const [requester, setRequester] = useState(currentUser.username || 'admin');
  const [assignedTo, setAssignedTo] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) {
      setError('O título do chamado é obrigatório.');
      return;
    }
    if (!description.trim()) {
      setError('A descrição detalhada é obrigatória.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      await onSubmit({
        title: title.trim(),
        description: description.trim(),
        category,
        priority,
        requester: requester.trim() || currentUser.username,
        assignedTo: assignedTo.trim() || undefined,
      });
      // Reset form
      setTitle('');
      setDescription('');
      setCategory('Infraestrutura');
      setPriority('Baixa');
      setAssignedTo('');
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erro ao criar chamado.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-xs">
      <div className="w-full max-w-2xl bg-[#111216] border border-zinc-700 shadow-2xl flex flex-col overflow-hidden">
        {/* Header */}
        <div className="px-5 py-3.5 border-b border-zinc-800 bg-[#0D0E11] flex items-center justify-between">
          <div>
            <h3 className="text-sm font-medium text-white">
              Abertura de Chamado
            </h3>
            <p className="text-[11px] text-zinc-400">
              Preencha as informações do chamado para inclusão no sistema
            </p>
          </div>

          <button
            onClick={onClose}
            className="p-1 text-zinc-400 hover:text-white hover:bg-zinc-800 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Form Body */}
        <form onSubmit={handleSubmit} className="p-5 space-y-4">
          {error && (
            <div className="p-2.5 bg-rose-950/30 border border-rose-800/80 text-rose-400 text-xs font-mono flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {/* Title */}
          <div>
            <label className="block text-xs font-mono text-zinc-400 mb-1">
              Título do Chamado <span className="text-rose-500">*</span>
            </label>
            <input
              type="text"
              required
              placeholder="Ex: Falha de conectividade no Cluster Kubernetes"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full bg-[#16181D] border border-zinc-800 focus:border-zinc-500 px-3 py-2 text-xs text-white placeholder-zinc-500 focus:outline-none transition-colors"
            />
          </div>

          {/* Priority Selector Grid */}
          <div>
            <label className="block text-xs font-mono text-zinc-400 mb-1">
              Nível de Prioridade & SLA <span className="text-rose-500">*</span>
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
              {PRIORITIES.map((p) => {
                const isSelected = priority === p.value;

                return (
                  <button
                    type="button"
                    key={p.value}
                    onClick={() => setPriority(p.value)}
                    className={`p-2.5 border text-left transition-colors font-mono ${
                      isSelected
                        ? `${p.activeBorder} ${p.activeBg}`
                        : 'border-zinc-800 bg-[#16181D] text-zinc-400 hover:border-zinc-700'
                    }`}
                  >
                    <div className="flex items-center justify-between mb-0.5">
                      <span className={`text-xs font-medium ${isSelected ? p.activeText : 'text-zinc-300'}`}>
                        {p.label}
                      </span>
                    </div>
                    <span className="text-[10px] text-zinc-500 block">{p.sla}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Category & Solicitante Row */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-mono text-zinc-400 mb-1">
                Categoria
              </label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full bg-[#16181D] border border-zinc-800 focus:border-zinc-500 px-3 py-2 text-xs text-white focus:outline-none transition-colors cursor-pointer"
              >
                {CATEGORIES.map((cat) => (
                  <option key={cat} value={cat} className="bg-[#111216]">
                    {cat}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-mono text-zinc-400 mb-1">
                Solicitante
              </label>
              <input
                type="text"
                value={requester}
                onChange={(e) => setRequester(e.target.value)}
                placeholder="Nome do solicitante"
                className="w-full bg-[#16181D] border border-zinc-800 focus:border-zinc-500 px-3 py-2 text-xs text-white focus:outline-none transition-colors font-mono"
              />
            </div>
          </div>

          {/* Detailed Description */}
          <div>
            <label className="block text-xs font-mono text-zinc-400 mb-1">
              Descrição Detalhada <span className="text-rose-500">*</span>
            </label>
            <textarea
              required
              rows={4}
              placeholder="Descreva o problema observado, etapas para reprodução ou detalhes técnicos..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full bg-[#16181D] border border-zinc-800 focus:border-zinc-500 p-3 text-xs text-white placeholder-zinc-500 focus:outline-none transition-colors"
            />
          </div>

          {/* Assigned Technician */}
          <div>
            <label className="block text-xs font-mono text-zinc-400 mb-1">
              Responsável / Técnico Designado (Opcional)
            </label>
            <input
              type="text"
              value={assignedTo}
              onChange={(e) => setAssignedTo(e.target.value)}
              placeholder="Ex: jotape, operador"
              className="w-full bg-[#16181D] border border-zinc-800 focus:border-zinc-500 px-3 py-1.5 text-xs text-white focus:outline-none transition-colors font-mono"
            />
          </div>

          {/* Footer Actions */}
          <div className="pt-3 border-t border-zinc-800 flex items-center justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              className="px-3.5 py-1.5 text-xs font-medium text-zinc-400 hover:text-zinc-200 border border-zinc-800 hover:bg-zinc-800 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-1.5 text-xs font-medium bg-zinc-100 hover:bg-white text-zinc-950 flex items-center gap-1.5 transition-colors disabled:opacity-50"
            >
              <Send className="w-3 h-3" />
              <span>{isSubmitting ? 'Registrando...' : 'Registrar Chamado'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
