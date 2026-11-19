import { Ticket, Metrics, User, NewTicketInput, TicketStatus, TicketPriority, LoginResponse, NewUserInput } from '../types';
import { auth } from './auth';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly fieldErrors?: Record<string, string>,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

interface RequestOptions extends Omit<RequestInit, 'body'> {
  body?: unknown;
  /** Mensagem usada quando a API não retorna uma mensagem própria. */
  fallbackError: string;
  /** Se false, não envia o token nem trata 401 como sessão expirada (ex.: login). */
  authenticated?: boolean;
}

async function request<T>(path: string, { body, fallbackError, authenticated = true, ...init }: RequestOptions): Promise<T> {
  const headers = new Headers(init.headers);
  headers.set('Accept', 'application/json');
  if (body !== undefined) headers.set('Content-Type', 'application/json');

  const token = authenticated ? auth.token() : null;
  if (token) headers.set('Authorization', `Bearer ${token}`);

  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    const payload = await res.json().catch(() => ({}));
    if (res.status === 401 && authenticated) {
      auth.notifyUnauthorized();
    }
    throw new ApiError(payload.message || fallbackError, res.status, payload.errors);
  }

  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export const api = {
  // Autenticação
  login(username: string, password: string): Promise<LoginResponse> {
    return request('/auth/login', {
      method: 'POST',
      body: { username, password },
      authenticated: false,
      fallbackError: 'Falha ao autenticar',
    });
  },

  me(): Promise<User> {
    return request('/auth/me', { fallbackError: 'Sessão inválida' });
  },

  // Chamados
  getTickets(filters?: { status?: string; priority?: string; category?: string; search?: string; requester?: string }): Promise<Ticket[]> {
    const params = new URLSearchParams();
    if (filters?.status && filters.status !== 'ALL') params.append('status', filters.status);
    if (filters?.priority && filters.priority !== 'ALL') params.append('priority', filters.priority);
    if (filters?.category && filters.category !== 'ALL') params.append('category', filters.category);
    if (filters?.search) params.append('search', filters.search);
    if (filters?.requester) params.append('requester', filters.requester);
    const query = params.toString() ? `?${params.toString()}` : '';
    return request(`/tickets${query}`, { fallbackError: 'Falha ao buscar chamados' });
  },

  getTicketById(id: number): Promise<Ticket> {
    return request(`/tickets/${id}`, { fallbackError: `Chamado #${id} não encontrado` });
  },

  createTicket(ticket: NewTicketInput): Promise<Ticket> {
    return request('/tickets', { method: 'POST', body: ticket, fallbackError: 'Falha ao abrir chamado' });
  },

  updateTicket(id: number, ticket: Partial<NewTicketInput>): Promise<Ticket> {
    return request(`/tickets/${id}`, { method: 'PUT', body: ticket, fallbackError: 'Falha ao atualizar chamado' });
  },

  updateStatus(id: number, status: TicketStatus): Promise<Ticket> {
    return request(`/tickets/${id}/status`, { method: 'PATCH', body: { status }, fallbackError: 'Falha ao atualizar status' });
  },

  updatePriority(id: number, priority: TicketPriority): Promise<Ticket> {
    return request(`/tickets/${id}/priority`, { method: 'PATCH', body: { priority }, fallbackError: 'Falha ao atualizar prioridade' });
  },

  deleteTicket(id: number): Promise<void> {
    return request(`/tickets/${id}`, { method: 'DELETE', fallbackError: 'Falha ao excluir chamado' });
  },

  // Métricas
  getMetrics(): Promise<Metrics> {
    return request('/metrics', { fallbackError: 'Falha ao buscar métricas' });
  },

  // Usuários
  getUsers(): Promise<User[]> {
    return request('/users', { fallbackError: 'Falha ao listar usuários' });
  },

  createUser(user: NewUserInput): Promise<User> {
    return request('/users', { method: 'POST', body: user, fallbackError: 'Falha ao cadastrar usuário' });
  },

  checkHealth(): Promise<{ status: string; version: string; service: string }> {
    return request('/health', { authenticated: false, fallbackError: 'Serviço offline' });
  },
};
