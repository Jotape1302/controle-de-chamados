import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { Header } from '../components/Header';
import { MetricsHUD } from '../components/MetricsHUD';
import { AnalyticsCharts } from '../components/AnalyticsCharts';
import { FilterBar } from '../components/FilterBar';
import { TicketTable } from '../components/TicketTable';
import { TicketKanban } from '../components/TicketKanban';
import { NewTicketModal } from '../components/NewTicketModal';
import { TicketDetailsModal } from '../components/TicketDetailsModal';
import { UserManagementModal } from '../components/UserManagementModal';
import { ToastContainer, ToastMessage } from '../components/Toast';
import { api, ApiError } from '../services/api';
import { Ticket, Metrics, User, FilterState, TicketStatus, TicketPriority, NewTicketInput, NewUserInput } from '../types';

const errorMessage = (err: unknown, fallback: string) => (err instanceof Error && err.message) || fallback;

interface DashboardProps {
  currentUser: User;
  onLogout: () => void;
}

export const Dashboard: React.FC<DashboardProps> = ({ currentUser, onLogout }) => {
  // Application State
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [metrics, setMetrics] = useState<Metrics | null>(null);
  const [users, setUsers] = useState<User[]>([]);

  const [loading, setLoading] = useState<boolean>(true);
  const [isRefreshing, setIsRefreshing] = useState<boolean>(false);
  const [apiConnected, setApiConnected] = useState<boolean>(true);

  // Filters State
  const [filters, setFilters] = useState<FilterState>({
    status: 'ALL',
    priority: 'ALL',
    category: 'ALL',
    search: '',
    viewMode: 'table',
  });

  // Modals & Inspection State
  const [isNewTicketOpen, setIsNewTicketOpen] = useState<boolean>(false);
  const [isUserManagementOpen, setIsUserManagementOpen] = useState<boolean>(false);
  const [selectedTicket, setSelectedTicket] = useState<Ticket | null>(null);

  // Notifications State
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const addToast = (type: 'success' | 'error' | 'info', message: string) => {
    const id = Math.random().toString(36).substring(2, 9);
    setToasts((prev) => [...prev, { id, type, message }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4000);
  };

  const removeToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  // Load Data from Spring Boot Backend
  const loadData = useCallback(async (isSilent = false) => {
    try {
      if (!isSilent) setIsRefreshing(true);

      const [ticketsData, metricsData, usersData] = await Promise.all([
        api.getTickets(),
        api.getMetrics(),
        api.getUsers(),
      ]);

      setTickets(ticketsData);
      setMetrics(metricsData);
      setUsers(usersData);
      setApiConnected(true);
    } catch (err) {
      // 401 é tratado globalmente (logout); aqui só sinalizamos falha de conexão.
      if (!(err instanceof ApiError && err.status === 401)) {
        console.error('Erro ao sincronizar com backend:', err);
        setApiConnected(false);
      }
    } finally {
      setLoading(false);
      setIsRefreshing(false);
    }
  }, []);

  useEffect(() => {
    loadData();
    // Auto-refresh every 15 seconds
    const interval = setInterval(() => loadData(true), 15000);
    return () => clearInterval(interval);
  }, [loadData]);

  // Keep selected ticket in sync with updated list
  useEffect(() => {
    if (selectedTicket) {
      const updated = tickets.find((t) => t.id === selectedTicket.id);
      if (updated) setSelectedTicket(updated);
    }
  }, [tickets, selectedTicket]);

  // Ticket Operations
  const handleCreateTicket = async (ticketData: NewTicketInput) => {
    try {
      const created = await api.createTicket(ticketData);
      addToast('success', `Chamado #${created.id} registrado com sucesso.`);
      await loadData(true);
    } catch (err) {
      addToast('error', errorMessage(err, 'Falha ao abrir chamado'));
      throw err;
    }
  };

  const handleUpdateStatus = async (id: number, newStatus: TicketStatus) => {
    try {
      await api.updateStatus(id, newStatus);
      addToast('info', `Status do chamado #${id} alterado para "${newStatus}".`);
      await loadData(true);
    } catch (err) {
      addToast('error', errorMessage(err, 'Falha ao atualizar status'));
    }
  };

  const handleUpdatePriority = async (id: number, newPriority: TicketPriority) => {
    try {
      await api.updatePriority(id, newPriority);
      addToast('info', `Prioridade do chamado #${id} alterada para "${newPriority}".`);
      await loadData(true);
    } catch (err) {
      addToast('error', errorMessage(err, 'Falha ao atualizar prioridade'));
    }
  };

  const handleDeleteTicket = async (id: number) => {
    try {
      await api.deleteTicket(id);
      addToast('success', `Chamado #${id} excluído com sucesso.`);
      if (selectedTicket?.id === id) {
        setSelectedTicket(null);
      }
      await loadData(true);
    } catch (err) {
      addToast('error', errorMessage(err, 'Falha ao excluir chamado'));
    }
  };

  const handleAddUser = async (userData: NewUserInput) => {
    try {
      const newUser = await api.createUser(userData);
      addToast('success', `Usuário ${newUser.username} cadastrado com sucesso.`);
      const updatedUsers = await api.getUsers();
      setUsers(updatedUsers);
    } catch (err) {
      addToast('error', errorMessage(err, 'Falha ao registrar usuário'));
      throw err;
    }
  };

  // Categories list calculation
  const categories = useMemo(() => {
    const set = new Set<string>();
    tickets.forEach((t) => {
      if (t.category) set.add(t.category);
    });
    return Array.from(set).sort();
  }, [tickets]);

  // Status counts calculation
  const statusCounts = useMemo(() => {
    const counts: Record<string, number> = {
      'Aberto': 0,
      'Em Análise': 0,
      'Concluído': 0,
      'Encerrado': 0,
    };
    tickets.forEach((t) => {
      if (counts[t.status] !== undefined) {
        counts[t.status]++;
      }
    });
    return counts;
  }, [tickets]);

  // Filtered Tickets
  const filteredTickets = useMemo(() => {
    return tickets.filter((t) => {
      if (filters.status !== 'ALL' && t.status !== filters.status) {
        return false;
      }
      if (filters.priority !== 'ALL' && t.priority !== filters.priority) {
        return false;
      }
      if (filters.category !== 'ALL' && t.category !== filters.category) {
        return false;
      }
      if (filters.search) {
        const query = filters.search.toLowerCase();
        const matchesId = String(t.id).includes(query);
        const matchesTitle = t.title.toLowerCase().includes(query);
        const matchesDesc = t.description.toLowerCase().includes(query);
        const matchesReq = t.requester.toLowerCase().includes(query);
        const matchesCat = t.category.toLowerCase().includes(query);
        return matchesId || matchesTitle || matchesDesc || matchesReq || matchesCat;
      }
      return true;
    });
  }, [tickets, filters]);

  return (
    <div className="min-h-screen bg-[#0B0C0E] text-zinc-100 flex flex-col selection:bg-zinc-800 selection:text-white">
      {/* Top Enterprise Header */}
      <Header
        currentUser={currentUser}
        onLogout={onLogout}
        onOpenNewTicket={() => setIsNewTicketOpen(true)}
        onOpenUserManagement={() => setIsUserManagementOpen(true)}
        onRefresh={() => loadData(false)}
        isRefreshing={isRefreshing}
        apiConnected={apiConnected}
      />

      {/* Main Dashboard Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-5 space-y-4">
        {/* 1. Metrics Overview */}
        <MetricsHUD metrics={metrics} loading={loading} />

        {/* 2. Visual Analytics Charts */}
        <AnalyticsCharts metrics={metrics} />

        {/* 3. Filters & Views Bar */}
        <FilterBar
          filters={filters}
          onFilterChange={(newFilters) => setFilters((prev) => ({ ...prev, ...newFilters }))}
          statusCounts={statusCounts}
          totalCount={tickets.length}
          filteredCount={filteredTickets.length}
          categories={categories}
        />

        {/* 4. Tickets Presentation (Table / Kanban) */}
        {filters.viewMode === 'table' ? (
          <TicketTable
            tickets={filteredTickets}
            currentUser={currentUser}
            onSelectTicket={setSelectedTicket}
            onUpdateStatus={handleUpdateStatus}
            onUpdatePriority={handleUpdatePriority}
            onDeleteTicket={handleDeleteTicket}
          />
        ) : (
          <TicketKanban
            tickets={filteredTickets}
            onSelectTicket={setSelectedTicket}
            onUpdateStatus={handleUpdateStatus}
          />
        )}
      </main>

      {/* Footer */}
      <footer className="border-t border-zinc-800/80 bg-[#090A0C] py-3.5 text-center text-xs font-mono text-zinc-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <span>Homeney Incident & SLA Management</span>
          <span className="text-zinc-400">Spring Boot 3.5 • Java 17 • React 18 • TypeScript</span>
        </div>
      </footer>

      {/* Modals */}
      <NewTicketModal
        isOpen={isNewTicketOpen}
        onClose={() => setIsNewTicketOpen(false)}
        onSubmit={handleCreateTicket}
        currentUser={currentUser}
      />

      <TicketDetailsModal
        ticket={selectedTicket}
        currentUser={currentUser}
        onClose={() => setSelectedTicket(null)}
        onUpdateStatus={handleUpdateStatus}
        onUpdatePriority={handleUpdatePriority}
        onDeleteTicket={handleDeleteTicket}
      />

      <UserManagementModal
        isOpen={isUserManagementOpen}
        onClose={() => setIsUserManagementOpen(false)}
        users={users}
        onAddUser={handleAddUser}
      />

      {/* Toast Notifications */}
      <ToastContainer toasts={toasts} onDismiss={removeToast} />
    </div>
  );
};
