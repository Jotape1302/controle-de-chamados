import React from 'react';
import { CheckCircle2, AlertTriangle, Info, X } from 'lucide-react';

export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'info';
  message: string;
}

interface ToastProps {
  toasts: ToastMessage[];
  onDismiss: (id: string) => void;
}

export const ToastContainer: React.FC<ToastProps> = ({ toasts, onDismiss }) => {
  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-1.5 pointer-events-none">
      {toasts.map((t) => (
        <div
          key={t.id}
          className={`pointer-events-auto p-3 border font-mono text-xs shadow-lg flex items-center justify-between gap-3 min-w-[280px] max-w-md ${
            t.type === 'success'
              ? 'bg-[#101713] border-emerald-800 text-emerald-300'
              : t.type === 'error'
              ? 'bg-[#1C1113] border-rose-800 text-rose-300'
              : 'bg-[#111318] border-zinc-700 text-zinc-200'
          }`}
        >
          <div className="flex items-center gap-2">
            {t.type === 'success' && <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 shrink-0" />}
            {t.type === 'error' && <AlertTriangle className="w-3.5 h-3.5 text-rose-400 shrink-0" />}
            {t.type === 'info' && <Info className="w-3.5 h-3.5 text-zinc-400 shrink-0" />}
            <span>{t.message}</span>
          </div>

          <button
            onClick={() => onDismiss(t.id)}
            className="text-zinc-500 hover:text-zinc-300 transition-colors"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      ))}
    </div>
  );
};
