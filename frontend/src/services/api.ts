import { Ticket, Metrics, User, NewTicketInput, TicketStatus, TicketPriority } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

export const api = {
  // Chamados / Tickets
  async getTickets(filters?: { status?: string; priority?: string; category?: string; search?: string; requester?: string }): Promise<Ticket[]> {
    const params = new URLSearchParams();
    if (filters?.status && filters.status !== 'ALL') params.append('status', filters.status);
    if (filters?.priority && filters.priority !== 'ALL') params.append('priority', filters.priority);
    if (filters?.category && filters.category !== 'ALL') params.append('category', filters.category);
    if (filters?.search) params.append('search', filters.search);
    if (filters?.requester) params.append('requester', filters.requester);

    const query = params.toString() ? `?${params.toString()}` : '';
    const res = await fetch(`${API_BASE_URL}/tickets${query}`);
    if (!res.ok) throw new Error(`Falha ao buscar chamados (${res.status})`);
    return res.json();
  },

  async getTicketById(id: number): Promise<Ticket> {
    const res = await fetch(`${API_BASE_URL}/tickets/${id}`);
    if (!res.ok) throw new Error(`Chamado #${id} não encontrado`);
    return res.json();
  },

  async createTicket(ticket: NewTicketInput): Promise<Ticket> {
    const res = await fetch(`${API_BASE_URL}/tickets`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(ticket),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Falha ao abrir chamado');
    }
    return res.json();
  },

  async updateTicket(id: number, ticket: Partial<NewTicketInput>): Promise<Ticket> {
    const res = await fetch(`${API_BASE_URL}/tickets/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(ticket),
    });
    if (!res.ok) throw new Error('Falha ao atualizar chamado');
    return res.json();
  },

  async updateStatus(id: number, status: TicketStatus): Promise<Ticket> {
    const res = await fetch(`${API_BASE_URL}/tickets/${id}/status`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status }),
    });
    if (!res.ok) throw new Error('Falha ao atualizar status');
    return res.json();
  },

  async updatePriority(id: number, priority: TicketPriority): Promise<Ticket> {
    const res = await fetch(`${API_BASE_URL}/tickets/${id}/priority`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ priority }),
    });
    if (!res.ok) throw new Error('Falha ao atualizar prioridade');
    return res.json();
  },

  async deleteTicket(id: number): Promise<void> {
    const res = await fetch(`${API_BASE_URL}/tickets/${id}`, {
      method: 'DELETE',
    });
    if (!res.ok) throw new Error('Falha ao excluir chamado');
  },

  // Métricas
  async getMetrics(): Promise<Metrics> {
    const res = await fetch(`${API_BASE_URL}/metrics`);
    if (!res.ok) throw new Error(`Falha ao buscar métricas (${res.status})`);
    return res.json();
  },

  // Usuários
  async getUsers(): Promise<User[]> {
    const res = await fetch(`${API_BASE_URL}/users`);
    if (!res.ok) throw new Error('Falha ao listar usuários');
    return res.json();
  },

  async createUser(user: { username: string; password?: string; fullName?: string; email?: string; admin: boolean }): Promise<User> {
    const res = await fetch(`${API_BASE_URL}/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        ...user,
        password: user.password || '123',
        fullName: user.fullName || user.username,
        email: user.email || `${user.username.toLowerCase()}@homeney.tech`,
      }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Falha ao cadastrar usuário');
    }
    return res.json();
  },

  async checkHealth(): Promise<{ status: string; version: string; service: string }> {
    const res = await fetch(`${API_BASE_URL}/health`);
    if (!res.ok) throw new Error('Serviço offline');
    return res.json();
  },
};
