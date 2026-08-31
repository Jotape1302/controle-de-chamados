import React from 'react';
import { 
  Search, 
  Filter, 
  LayoutGrid, 
  Table as TableIcon, 
  X,
  SlidersHorizontal 
} from 'lucide-react';
import { FilterState } from '../types';

interface FilterBarProps {
  filters: FilterState;
  onFilterChange: (filters: Partial<FilterState>) => void;
  statusCounts: Record<string, number>;
  totalCount: number;
  filteredCount: number;
  categories: string[];
}

export const FilterBar: React.FC<FilterBarProps> = ({
  filters,
  onFilterChange,
  statusCounts,
  totalCount,
  filteredCount,
  categories,
}) => {
  const statusTabs: { label: string; value: string }[] = [
    { label: 'Todos', value: 'ALL' },
    { label: 'Aberto', value: 'Aberto' },
    { label: 'Em Análise', value: 'Em Análise' },
    { label: 'Concluído', value: 'Concluído' },
    { label: 'Encerrado', value: 'Encerrado' },
  ];

  return (
    <div className="space-y-2.5">
      {/* Top Filter Controls */}
      <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-2">
        {/* Search Field */}
        <div className="relative flex-1">
          <Search className="w-3.5 h-3.5 text-zinc-500 absolute left-3 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Filtrar por protocolo ID, título, descrição, solicitante..."
            value={filters.search}
            onChange={(e) => onFilterChange({ search: e.target.value })}
            className="w-full bg-[#111216] border border-zinc-800 focus:border-zinc-500 pl-8 pr-7 py-1.5 text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none transition-colors font-mono"
          />
          {filters.search && (
            <button
              onClick={() => onFilterChange({ search: '' })}
              className="absolute right-2.5 top-1/2 -translate-y-1/2 text-zinc-500 hover:text-zinc-300"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          )}
        </div>

        {/* Filters & View Mode */}
        <div className="flex flex-wrap items-center gap-2">
          {/* Priority Select */}
          <div className="flex items-center bg-[#111216] border border-zinc-800 px-2 py-1 text-xs text-zinc-300">
            <SlidersHorizontal className="w-3.5 h-3.5 text-zinc-500 mr-1.5" />
            <select
              value={filters.priority}
              onChange={(e) => onFilterChange({ priority: e.target.value })}
              className="bg-transparent text-zinc-200 focus:outline-none cursor-pointer text-xs"
            >
              <option value="ALL" className="bg-[#111216]">Prioridade: Todas</option>
              <option value="Baixa" className="bg-[#111216]">Baixa</option>
              <option value="Média" className="bg-[#111216]">Média</option>
              <option value="Alta" className="bg-[#111216]">Alta</option>
              <option value="Crítica" className="bg-[#111216]">Crítica</option>
            </select>
          </div>

          {/* Category Select */}
          <div className="flex items-center bg-[#111216] border border-zinc-800 px-2 py-1 text-xs text-zinc-300">
            <Filter className="w-3.5 h-3.5 text-zinc-500 mr-1.5" />
            <select
              value={filters.category}
              onChange={(e) => onFilterChange({ category: e.target.value })}
              className="bg-transparent text-zinc-200 focus:outline-none cursor-pointer text-xs"
            >
              <option value="ALL" className="bg-[#111216]">Categoria: Todas</option>
              {categories.map((cat) => (
                <option key={cat} value={cat} className="bg-[#111216]">
                  {cat}
                </option>
              ))}
            </select>
          </div>

          {/* View Mode (Table / Kanban) */}
          <div className="flex items-center bg-[#111216] border border-zinc-800 p-0.5">
            <button
              onClick={() => onFilterChange({ viewMode: 'table' })}
              className={`p-1.5 text-xs flex items-center gap-1 transition-colors ${
                filters.viewMode === 'table'
                  ? 'bg-zinc-800 text-white font-medium'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
              title="Visualização em Tabela"
            >
              <TableIcon className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">Tabela</span>
            </button>
            <button
              onClick={() => onFilterChange({ viewMode: 'kanban' })}
              className={`p-1.5 text-xs flex items-center gap-1 transition-colors ${
                filters.viewMode === 'kanban'
                  ? 'bg-zinc-800 text-white font-medium'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
              title="Quadro Kanban"
            >
              <LayoutGrid className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">Quadro</span>
            </button>
          </div>
        </div>
      </div>

      {/* Segmented Status Tabs */}
      <div className="flex items-center justify-between border-b border-zinc-800 pb-2 overflow-x-auto">
        <div className="flex items-center gap-1">
          {statusTabs.map((tab) => {
            const count = tab.value === 'ALL' ? totalCount : statusCounts[tab.value] || 0;
            const active = filters.status === tab.value;

            return (
              <button
                key={tab.value}
                onClick={() => onFilterChange({ status: tab.value })}
                className={`px-3 py-1 text-xs font-mono transition-colors flex items-center gap-2 whitespace-nowrap border ${
                  active
                    ? 'bg-zinc-800 text-white border-zinc-700 font-medium'
                    : 'bg-[#111216] text-zinc-400 border-zinc-800 hover:text-zinc-200 hover:border-zinc-700'
                }`}
              >
                <span>{tab.label}</span>
                <span className={`text-[10px] px-1 py-0.2 font-mono ${
                  active ? 'bg-zinc-700 text-zinc-100' : 'bg-zinc-900 text-zinc-500'
                }`}>
                  {count}
                </span>
              </button>
            );
          })}
        </div>

        <div className="text-[11px] font-mono text-zinc-500 hidden sm:block whitespace-nowrap pl-4">
          Mostrando <span className="text-zinc-300 font-semibold">{filteredCount}</span> de <span className="text-zinc-300">{totalCount}</span>
        </div>
      </div>
    </div>
  );
};
