package com.homeney.ticketsystem.config;

import com.homeney.ticketsystem.model.Ticket;
import com.homeney.ticketsystem.model.TicketPriority;
import com.homeney.ticketsystem.model.TicketStatus;
import com.homeney.ticketsystem.model.User;
import com.homeney.ticketsystem.repository.TicketRepository;
import com.homeney.ticketsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Popula o banco com usuários e chamados de exemplo. Roda apenas no perfil {@code dev}.
 */
@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, TicketRepository ticketRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed Users if empty
        if (userRepository.count() == 0) {
            // Credenciais de desenvolvimento (documentadas no README). Não existem em produção.
            User admin = new User("admin", passwordEncoder.encode("admin123"), "Administrador do Sistema", "admin@homeney.tech", true);
            User operator = new User("operador", passwordEncoder.encode("operador123"), "Operador de Suporte", "operador@homeney.tech", false);
            User techLead = new User("jotape", passwordEncoder.encode("jotape123"), "João Paulo (Engenheiro)", "jotape@homeney.tech", true);

            userRepository.save(admin);
            userRepository.save(operator);
            userRepository.save(techLead);
        }

        // Seed Sample Tickets if empty
        if (ticketRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            // 1. Critical active ticket
            Ticket t1 = new Ticket(
                    "Instabilidade no Cluster Kubernetes (Worker Nodes Off)",
                    "Múltiplos pods em status CrashLoopBackOff após atualização de nó worker na região sa-east-1. Alerta crítico de latência gerado pelo Prometheus.",
                    TicketPriority.CRITICAL,
                    "jotape",
                    "Infraestrutura"
            );
            t1.setStatus(TicketStatus.IN_PROGRESS);
            t1.setAssignedTo("admin");
            t1.setCreatedAt(now.minusHours(1));
            t1.setUpdatedAt(now.minusMinutes(15));
            t1.setSlaDeadline(now.minusHours(1).plusHours(TicketPriority.CRITICAL.getSlaHours()));
            ticketRepository.save(t1);

            // 2. High priority open ticket
            Ticket t2 = new Ticket(
                    "Bloqueio de IP suspeito no Firewall Edge / WAF",
                    "Detecção de tráfego volumétrico anômalo vindo de sub-redes não mapeadas tentando bruteforce em /api/auth. Necessário aplicar regra DROP na Cloudflare.",
                    TicketPriority.HIGH,
                    "operador",
                    "Segurança"
            );
            t2.setStatus(TicketStatus.OPEN);
            t2.setCreatedAt(now.minusHours(3));
            t2.setUpdatedAt(now.minusHours(3));
            t2.setSlaDeadline(now.minusHours(3).plusHours(TicketPriority.HIGH.getSlaHours()));
            ticketRepository.save(t2);

            // 3. Medium priority in progress
            Ticket t3 = new Ticket(
                    "Configuração de Replicação do Banco PostgreSQL",
                    "Necessário provisionar réplica de leitura para aliviar carga de relatórios gerenciais sobre a base primária de produção.",
                    TicketPriority.MEDIUM,
                    "jotape",
                    "Banco de Dados"
            );
            t3.setStatus(TicketStatus.IN_PROGRESS);
            t3.setAssignedTo("jotape");
            t3.setCreatedAt(now.minusHours(12));
            t3.setUpdatedAt(now.minusHours(2));
            t3.setSlaDeadline(now.minusHours(12).plusHours(TicketPriority.MEDIUM.getSlaHours()));
            ticketRepository.save(t3);

            // 4. Low priority open
            Ticket t4 = new Ticket(
                    "Solicitação de Acesso VPN para Novo Desenvolvedor",
                    "Liberar credenciais OpenVPN e certificado de acesso aos repositórios internos da stack de telemetria.",
                    TicketPriority.LOW,
                    "operador",
                    "Acesso"
            );
            t4.setStatus(TicketStatus.OPEN);
            t4.setCreatedAt(now.minusDays(1));
            t4.setUpdatedAt(now.minusDays(1));
            t4.setSlaDeadline(now.minusDays(1).plusHours(TicketPriority.LOW.getSlaHours()));
            ticketRepository.save(t4);

            // 5. Resolved Ticket (within SLA)
            Ticket t5 = new Ticket(
                    "Falha no Gateway de Pagamentos em Staging",
                    "Webhook do gateway retornando HTTP 502 por timeout de rota TLS.",
                    TicketPriority.HIGH,
                    "jotape",
                    "Software"
            );
            t5.setStatus(TicketStatus.RESOLVED);
            t5.setAssignedTo("jotape");
            t5.setCreatedAt(now.minusDays(2).minusHours(4));
            t5.setResolvedAt(now.minusDays(2).minusHours(1));
            t5.setUpdatedAt(now.minusDays(2).minusHours(1));
            t5.setSlaDeadline(t5.getCreatedAt().plusHours(TicketPriority.HIGH.getSlaHours()));
            ticketRepository.save(t5);

            // 6. Closed Ticket (historic)
            Ticket t6 = new Ticket(
                    "Renovação de Certificado Wildcard SSL (*.homeney.tech)",
                    "Substituição do certificado Let's Encrypt expirando em 48 horas nas instâncias NGINX Reverse Proxy.",
                    TicketPriority.MEDIUM,
                    "admin",
                    "Redes"
            );
            t6.setStatus(TicketStatus.CLOSED);
            t6.setAssignedTo("admin");
            t6.setCreatedAt(now.minusDays(3));
            t6.setResolvedAt(now.minusDays(3).plusHours(4));
            t6.setUpdatedAt(now.minusDays(3).plusHours(4));
            t6.setSlaDeadline(t6.getCreatedAt().plusHours(TicketPriority.MEDIUM.getSlaHours()));
            ticketRepository.save(t6);

            // 7. Resolved Ticket
            Ticket t7 = new Ticket(
                    "Otimização de Query no Endpoint de Extrato",
                    "Adicionado índice composto na tabela transactions (user_id, created_at) reduzindo tempo de resposta de 1.4s para 18ms.",
                    TicketPriority.LOW,
                    "jotape",
                    "Software"
            );
            t7.setStatus(TicketStatus.RESOLVED);
            t7.setAssignedTo("jotape");
            t7.setCreatedAt(now.minusDays(4));
            t7.setResolvedAt(now.minusDays(4).plusHours(6));
            t7.setUpdatedAt(now.minusDays(4).plusHours(6));
            t7.setSlaDeadline(t7.getCreatedAt().plusHours(TicketPriority.LOW.getSlaHours()));
            ticketRepository.save(t7);
        }
    }
}
