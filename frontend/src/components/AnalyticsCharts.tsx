import React from 'react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  Cell,
} from 'recharts';
import { Metrics } from '../types';

interface ChartTooltipEntry {
  name?: string;
  value?: number | string;
  color?: string;
  stroke?: string;
}

interface ChartTooltipProps {
  active?: boolean;
  payload?: ChartTooltipEntry[];
  label?: string | number;
}

interface AnalyticsChartsProps {
  metrics: Metrics | null;
}

const PRIORITY_COLORS: Record<string, string> = {
  'Crítica': '#ef4444',
  'Alta': '#a855f7',
  'Média': '#eab308',
  'Baixa': '#3b82f6',
};

const CATEGORY_COLORS = ['#3b82f6', '#6366f1', '#a855f7', '#ec4899', '#f59e0b', '#10b981'];

export const AnalyticsCharts: React.FC<AnalyticsChartsProps> = ({ metrics }) => {
  if (!metrics) return null;

  // Prepare Priority Data
  const priorityData = Object.entries(metrics.priorityDistribution || {}).map(([name, count]) => ({
    name,
    count,
    color: PRIORITY_COLORS[name] || '#3b82f6',
  }));

  // Prepare Category Data
  const categoryData = Object.entries(metrics.categoryDistribution || {}).map(([name, count], index) => ({
    name,
    count,
    color: CATEGORY_COLORS[index % CATEGORY_COLORS.length],
  }));

  const CustomTooltip = ({ active, payload, label }: ChartTooltipProps) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-[#16181D] border border-zinc-700 p-2.5 shadow-lg text-xs font-mono">
          <p className="text-zinc-300 font-semibold mb-1 border-b border-zinc-700/80 pb-1">{label}</p>
          {payload.map((entry, index) => (
            <div key={index} className="flex items-center justify-between gap-3 text-zinc-300">
              <span className="flex items-center gap-1.5">
                <span className="w-1.5 h-1.5" style={{ backgroundColor: entry.color || entry.stroke }} />
                <span>{entry.name}:</span>
              </span>
              <span className="font-semibold text-white">{entry.value}</span>
            </div>
          ))}
        </div>
      );
    }
    return null;
  };

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-3">
      {/* CHART 1: FLUXO TEMPORAL */}
      <div className="lg:col-span-2 bg-[#111216] border border-zinc-800 p-4">
        <div className="flex items-center justify-between mb-3">
          <div>
            <h3 className="text-xs font-mono font-medium text-zinc-300 uppercase tracking-wider">
              Volumetria de Chamados (Últimos 7 Dias)
            </h3>
            <p className="text-[11px] text-zinc-500">Taxa diária de abertura e resolução</p>
          </div>
          <div className="flex items-center gap-3 text-[11px] font-mono">
            <span className="flex items-center gap-1.5 text-zinc-300">
              <span className="w-2 h-2 bg-blue-500"></span> Abertos
            </span>
            <span className="flex items-center gap-1.5 text-zinc-300">
              <span className="w-2 h-2 bg-emerald-500"></span> Concluídos
            </span>
          </div>
        </div>

        <div className="h-52 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={metrics.volumeHistory} margin={{ top: 10, right: 10, left: -25, bottom: 0 }}>
              <defs>
                <linearGradient id="gradientOpen" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.2} />
                  <stop offset="95%" stopColor="#3b82f6" stopOpacity={0.0} />
                </linearGradient>
                <linearGradient id="gradientResolved" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#22c55e" stopOpacity={0.2} />
                  <stop offset="95%" stopColor="#22c55e" stopOpacity={0.0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="2 2" stroke="rgba(255,255,255,0.04)" />
              <XAxis dataKey="date" stroke="#52525b" tick={{ fontSize: 11, fill: '#71717a' }} />
              <YAxis allowDecimals={false} stroke="#52525b" tick={{ fontSize: 11, fill: '#71717a' }} />
              <Tooltip content={<CustomTooltip />} />
              <Area
                type="monotone"
                dataKey="opened"
                name="Abertos"
                stroke="#3b82f6"
                strokeWidth={1.5}
                fillOpacity={1}
                fill="url(#gradientOpen)"
              />
              <Area
                type="monotone"
                dataKey="resolved"
                name="Concluídos"
                stroke="#22c55e"
                strokeWidth={1.5}
                fillOpacity={1}
                fill="url(#gradientResolved)"
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* CHART 2: DISTRIBUIÇÃO POR SEVERIDADE */}
      <div className="bg-[#111216] border border-zinc-800 p-4 flex flex-col justify-between">
        <div>
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-xs font-mono font-medium text-zinc-300 uppercase tracking-wider">
              Distribuição por Prioridade
            </h3>
            <span className="text-[10px] font-mono text-zinc-500 px-1.5 py-0.2 bg-zinc-900 border border-zinc-800">
              SEVERIDADE
            </span>
          </div>
          <p className="text-[11px] text-zinc-500 mb-3">Volume segregado por nível de criticidade</p>
        </div>

        <div className="h-40 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={priorityData} layout="vertical" margin={{ top: 0, right: 15, left: 5, bottom: 0 }}>
              <CartesianGrid strokeDasharray="2 2" stroke="rgba(255,255,255,0.04)" horizontal={false} />
              <XAxis type="number" allowDecimals={false} stroke="#52525b" tick={{ fontSize: 10, fill: '#71717a' }} />
              <YAxis dataKey="name" type="category" stroke="#52525b" tick={{ fontSize: 11, fill: '#d4d4d8' }} width={55} />
              <Tooltip content={<CustomTooltip />} />
              <Bar dataKey="count" name="Chamados" radius={[0, 0, 0, 0]}>
                {priorityData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Categories list */}
        <div className="mt-3 pt-2 border-t border-zinc-800/80">
          <div className="text-[10px] font-mono text-zinc-500 mb-1.5 flex items-center justify-between uppercase">
            <span>Categorias Ativas</span>
            <span className="text-zinc-400 font-semibold">{categoryData.length} categorias</span>
          </div>
          <div className="flex flex-wrap gap-1">
            {categoryData.slice(0, 4).map((cat, i) => (
              <span key={i} className="text-[10px] font-mono px-1.5 py-0.5 bg-zinc-900 border border-zinc-800 text-zinc-300 flex items-center gap-1">
                <span className="w-1.5 h-1.5" style={{ backgroundColor: cat.color }} />
                <span>{cat.name}</span>
                <span className="text-zinc-500">({cat.count})</span>
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};
