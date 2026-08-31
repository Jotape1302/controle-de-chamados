export type TicketStatus = 'Aberto' | 'Em Análise' | 'Concluído' | 'Encerrado';
export type TicketPriority = 'Baixa' | 'Média' | 'Alta' | 'Crítica';

export interface Ticket {
  id: number;
  title: string;
  description: string;
  status: TicketStatus;
  statusKey: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
  priority: TicketPriority;
  priorityKey: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  requester: string;
  category: string;
  assignedTo?: string | null;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string | null;
  slaDeadline?: string | null;
  slaBreached: boolean;
  slaRemainingMinutes: number;
}

export interface DailyVolume {
  date: string;
  opened: number;
  resolved: number;
}

export interface Metrics {
  totalTickets: number;
  openTickets: number;
  inProgressTickets: number;
  resolvedTickets: number;
  closedTickets: number;
  activeTickets: number;
  criticalTickets: number;
  criticalActiveTickets: number;
  avgResolutionTimeHours: number;
  slaComplianceRate: number;
  statusDistribution: Record<string, number>;
  priorityDistribution: Record<string, number>;
  categoryDistribution: Record<string, number>;
  volumeHistory: DailyVolume[];
}

export interface User {
  id: number;
  username: string;
  fullName: string;
  email: string;
  admin: boolean;
}

export interface NewTicketInput {
  title: string;
  description: string;
  priority: TicketPriority;
  category: string;
  requester: string;
  assignedTo?: string;
}

export interface FilterState {
  status: string;
  priority: string;
  category: string;
  search: string;
  viewMode: 'table' | 'kanban';
}
