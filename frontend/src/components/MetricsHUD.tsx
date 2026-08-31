import React from 'react';
import { Metrics } from '../types';

interface MetricsHUDProps {
  metrics: Metrics | null;
  loading: boolean;
}

export const MetricsHUD: React.FC<MetricsHUDProps> = ({ metrics, loading }) => {
  if (loading || !metrics) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
        {[1, 2, 3, 4, 5].map((i) => (
          <div key={i} className="h-24 bg-[#111216] border border-zinc-800 animate-pulse p-3.5 flex flex-col justify-between">
            <div className="h-3 bg-zinc-800 w-20"></div>
            <div className="h-7 bg-zinc-800 w-12"></div>
            <div className="h-2.5 bg-zinc-800 w-28"></div>
          </div>
        ))}
      </div>
    );
  }

  const {
    totalTickets,
    openTickets,
    inProgressTickets,
    activeTickets,
    resolvedTickets,
    closedTickets,
    criticalTickets,
    criticalActiveTickets,
    slaComplianceRate,
    avgResolutionTimeHours,
  } = metrics;

  return (
    <section className="space-y-2">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
        {/* CARD 1: TOTAL */}
        <div className="bg-[#111216] border border-zinc-800 p-4 transition-colors hover:border-zinc-700">
          <p className="text-[11px] font-mono uppercase text-zinc-400 tracking-wider">
            Total de Chamados
          </p>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-mono font-semibold text-zinc-100">{totalTickets}</span>
            <span className="text-[11px] font-mono text-zinc-500">tickets</span>
          </div>
          <div className="mt-2 pt-2 border-t border-zinc-800/80 flex items-center justify-between text-[11px] font-mono text-zinc-500">
            <span>Encerrados: <b className="text-zinc-300 font-normal">{closedTickets}</b></span>
            <span className="text-zinc-400">100% Sincronizado</span>
          </div>
        </div>

        {/* CARD 2: ATIVOS */}
        <div className="bg-[#111216] border border-zinc-800 p-4 transition-colors hover:border-zinc-700">
          <p className="text-[11px] font-mono uppercase text-zinc-400 tracking-wider">
            Em Atendimento
          </p>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-mono font-semibold text-amber-400">{activeTickets}</span>
            <span className="text-[11px] font-mono text-zinc-500">ativos</span>
          </div>
          <div className="mt-2 pt-2 border-t border-zinc-800/80 flex items-center justify-between text-[11px] font-mono text-zinc-500">
            <span>Abertos: <b className="text-zinc-300 font-normal">{openTickets}</b></span>
            <span>Análise: <b className="text-zinc-300 font-normal">{inProgressTickets}</b></span>
          </div>
        </div>

        {/* CARD 3: CRÍTICOS */}
        <div className={`bg-[#111216] border p-4 transition-colors ${
          criticalActiveTickets > 0 
            ? 'border-rose-900/80 bg-rose-950/10' 
            : 'border-zinc-800 hover:border-zinc-700'
        }`}>
          <div className="flex items-center justify-between">
            <p className="text-[11px] font-mono uppercase text-zinc-400 tracking-wider">
              Incidentes Críticos
            </p>
            {criticalActiveTickets > 0 && (
              <span className="w-2 h-2 bg-rose-500" title="Alerta Crítico Ativo" />
            )}
          </div>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className={`text-2xl font-mono font-semibold ${criticalActiveTickets > 0 ? 'text-rose-400' : 'text-zinc-100'}`}>
              {criticalActiveTickets}
            </span>
            <span className="text-[11px] font-mono text-zinc-500">urgentes</span>
          </div>
          <div className="mt-2 pt-2 border-t border-zinc-800/80 flex items-center justify-between text-[11px] font-mono text-zinc-500">
            <span>Total Histórico: <b className="text-zinc-300 font-normal">{criticalTickets}</b></span>
            <span className={criticalActiveTickets > 0 ? 'text-rose-400 font-medium' : 'text-zinc-500'}>
              {criticalActiveTickets > 0 ? 'Ação Requerida' : 'Estável'}
            </span>
          </div>
        </div>

        {/* CARD 4: SLA COMPLIANCE */}
        <div className="bg-[#111216] border border-zinc-800 p-4 transition-colors hover:border-zinc-700">
          <p className="text-[11px] font-mono uppercase text-zinc-400 tracking-wider">
            Conformidade SLA
          </p>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className={`text-2xl font-mono font-semibold ${
              slaComplianceRate >= 95 ? 'text-emerald-400' : slaComplianceRate >= 85 ? 'text-amber-400' : 'text-rose-400'
            }`}>
              {slaComplianceRate.toFixed(1)}%
            </span>
            <span className="text-[11px] font-mono text-zinc-500">meta: 95%</span>
          </div>
          <div className="mt-2 pt-2 border-t border-zinc-800/80 flex items-center justify-between text-[11px] font-mono text-zinc-500">
            <span>Resolvidos: <b className="text-zinc-300 font-normal">{resolvedTickets}</b></span>
            <span className="text-emerald-500 font-normal">Dentro do Prazo</span>
          </div>
        </div>

        {/* CARD 5: MTTR / TEMPO MÉDIO */}
        <div className="bg-[#111216] border border-zinc-800 p-4 transition-colors hover:border-zinc-700">
          <p className="text-[11px] font-mono uppercase text-zinc-400 tracking-wider">
            Tempo Médio (MTTR)
          </p>
          <div className="mt-2 flex items-baseline gap-1.5">
            <span className="text-2xl font-mono font-semibold text-zinc-100">
              {avgResolutionTimeHours}h
            </span>
            <span className="text-[11px] font-mono text-zinc-500">resolução</span>
          </div>
          <div className="mt-2 pt-2 border-t border-zinc-800/80 flex items-center justify-between text-[11px] font-mono text-zinc-500">
            <span>Desempenho: <b className="text-zinc-300 font-normal">{avgResolutionTimeHours <= 4 ? 'Excelente' : 'Estável'}</b></span>
            <span className="text-zinc-400 font-mono">KPI Operacional</span>
          </div>
        </div>
      </div>
    </section>
  );
};
