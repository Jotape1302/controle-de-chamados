import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Usuario[] usuarios = new Usuario[100];
        Chamado[] chamados = new Chamado[100];

        int totalUsuarios = 0;
        int totalChamados = 0;

        // ADMIN PADRÃO
        usuarios[totalUsuarios] =
                new Usuario("admin", "123", true);

        totalUsuarios++;

        Usuario usuarioLogado = null;
        boolean loginValido = false;

        // LOGIN INICIAL
        while (!loginValido) {

            System.out.println("\n===== LOGIN =====");

            System.out.print("Usuário: ");
            String usuarioLogin = sc.nextLine();

            System.out.print("Senha: ");
            String senhaLogin = sc.nextLine();

            for (int i = 0; i < totalUsuarios; i++) {

                if (usuarioLogin.equals(usuarios[i].nome)
                        && senhaLogin.equals(usuarios[i].senha)) {

                    loginValido = true;
                    usuarioLogado = usuarios[i];

                    break;
                }
            }

            if (!loginValido) {

                System.out.println("Usuário ou senha inválidos!");
            }
        }

        System.out.println("Login realizado com sucesso!");

        int opcao = 0;

        while (opcao != 10) {

            System.out.println("\n===== MENU =====");

            System.out.println("1 - Abrir chamado");
            System.out.println("2 - Listar chamados em aberto/em andamento");
            System.out.println("3 - Listar chamados concluídos/encerrados");
            System.out.println("4 - Consultar chamado");

            if (usuarioLogado.admin) {

                System.out.println("5 - Cadastrar usuário");
                System.out.println("6 - Alterar status");
                System.out.println("7 - Alterar prioridade");
                System.out.println("8 - Listar usuários");
            }

            System.out.println("9 - Logout");
            System.out.println("10 - Encerrar");

            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {

                // ABRIR CHAMADO
                case 1 -> {

                    System.out.println("\n===== NOVO CHAMADO =====");

                    System.out.print("Descrição: ");
                    String descricao = sc.nextLine();

                    chamados[totalChamados] =
                            new Chamado(
                                    totalChamados + 1,
                                    descricao,
                                    "Baixa",
                                    usuarioLogado.nome
                            );

                    System.out.println("Chamado aberto com sucesso!");
                    System.out.println("Número do chamado: "
                            + chamados[totalChamados].id);

                    totalChamados++;
                }

                // LISTAR CHAMADOS ATIVOS
                case 2 -> {

                    System.out.println("\n===== CHAMADOS ATIVOS =====");

                    boolean encontrou = false;

                    for (int i = 0; i < totalChamados; i++) {

                        boolean ativo =
                                chamados[i].status.equals("Aberto")
                                        || chamados[i].status.equals("Andamento");

                        if (ativo) {

                            if (usuarioLogado.admin ||
                                    chamados[i].usuario.equals(usuarioLogado.nome)) {

                                System.out.println("ID: " + chamados[i].id);
                                System.out.println("Usuário: " + chamados[i].usuario);
                                System.out.println("Descrição: " + chamados[i].descricao);
                                System.out.println("Prioridade: " + chamados[i].prioridade);
                                System.out.println("Status: " + chamados[i].status);

                                System.out.println();

                                encontrou = true;
                            }
                        }
                    }

                    if (!encontrou) {

                        System.out.println("Nenhum chamado encontrado.");
                    }
                }

                // LISTAR CHAMADOS FINALIZADOS
                case 3 -> {

                    System.out.println("\n===== CHAMADOS FINALIZADOS =====");

                    boolean encontrou = false;

                    for (int i = 0; i < totalChamados; i++) {

                        boolean finalizado =
                                chamados[i].status.equals("Concluído")
                                        || chamados[i].status.equals("Encerrado");

                        if (finalizado) {

                            if (usuarioLogado.admin ||
                                    chamados[i].usuario.equals(usuarioLogado.nome)) {

                                System.out.println("ID: " + chamados[i].id);
                                System.out.println("Usuário: " + chamados[i].usuario);
                                System.out.println("Descrição: " + chamados[i].descricao);
                                System.out.println("Prioridade: " + chamados[i].prioridade);
                                System.out.println("Status: " + chamados[i].status);

                                System.out.println();

                                encontrou = true;
                            }
                        }
                    }

                    if (!encontrou) {

                        System.out.println("Nenhum chamado encontrado.");
                    }
                }

                // CONSULTAR CHAMADO
                case 4 -> {

                    System.out.print("Digite o ID do chamado: ");
                    int busca = sc.nextInt();
                    sc.nextLine();

                    boolean encontrado = false;

                    for (int i = 0; i < totalChamados; i++) {

                        if (chamados[i].id == busca) {

                            if (usuarioLogado.admin ||
                                    chamados[i].usuario.equals(usuarioLogado.nome)) {

                                System.out.println("\n===== CHAMADO =====");

                                System.out.println("ID: " + chamados[i].id);
                                System.out.println("Usuário: " + chamados[i].usuario);
                                System.out.println("Descrição: " + chamados[i].descricao);
                                System.out.println("Prioridade: " + chamados[i].prioridade);
                                System.out.println("Status: " + chamados[i].status);

                                encontrado = true;
                            }
                        }
                    }

                    if (!encontrado) {

                        System.out.println("Chamado não encontrado.");
                    }
                }

                // CADASTRAR USUÁRIO
                case 5 -> {

                    if (usuarioLogado.admin) {

                        System.out.println("\n===== NOVO USUÁRIO =====");

                        System.out.print("Nome: ");
                        String nome = sc.nextLine();

                        System.out.print("Senha: ");
                        String senha = sc.nextLine();

                        System.out.print("É admin? (true/false): ");
                        boolean admin = sc.nextBoolean();
                        sc.nextLine();

                        usuarios[totalUsuarios] =
                                new Usuario(nome, senha, admin);

                        totalUsuarios++;

                        System.out.println("Usuário cadastrado com sucesso!");

                    } else {

                        System.out.println("Acesso negado!");
                    }
                }

                // ALTERAR STATUS
                case 6 -> {

                    if (usuarioLogado.admin) {

                        System.out.print("Digite o ID do chamado: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        boolean alterado = false;

                        for (int i = 0; i < totalChamados; i++) {

                            if (chamados[i].id == id) {

                                System.out.println("\n1 - Aberto");
                                System.out.println("2 - Andamento");
                                System.out.println("3 - Concluído");
                                System.out.println("4 - Encerrado");

                                System.out.print("Escolha: ");
                                int status = sc.nextInt();
                                sc.nextLine();

                                switch (status) {

                                    case 1 -> chamados[i].status = "Aberto";
                                    case 2 -> chamados[i].status = "Andamento";
                                    case 3 -> chamados[i].status = "Concluído";
                                    case 4 -> chamados[i].status = "Encerrado";

                                    default -> {
                                        System.out.println("Opção inválida!");
                                        break;
                                    }
                                }

                                alterado = true;

                                System.out.println("Status alterado!");
                            }
                        }

                        if (!alterado) {

                            System.out.println("Chamado não encontrado.");
                        }

                    } else {

                        System.out.println("Acesso negado!");
                    }
                }

                // ALTERAR PRIORIDADE
                case 7 -> {

                    if (usuarioLogado.admin) {

                        System.out.print("Digite o ID do chamado: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        boolean alterado = false;

                        for (int i = 0; i < totalChamados; i++) {

                            if (chamados[i].id == id) {

                                System.out.println("\n1 - Baixa");
                                System.out.println("2 - Média");
                                System.out.println("3 - Alta");
                                System.out.println("4 - Crítica");

                                System.out.print("Escolha: ");
                                int prioridade = sc.nextInt();
                                sc.nextLine();

                                switch (prioridade) {

                                    case 1 -> chamados[i].prioridade = "Baixa";
                                    case 2 -> chamados[i].prioridade = "Média";
                                    case 3 -> chamados[i].prioridade = "Alta";
                                    case 4 -> chamados[i].prioridade = "Crítica";

                                    default -> {
                                        System.out.println("Opção inválida!");
                                        break;
                                    }
                                }

                                alterado = true;

                                System.out.println("Prioridade alterada!");
                            }
                        }

                        if (!alterado) {

                            System.out.println("Chamado não encontrado.");
                        }

                    } else {

                        System.out.println("Acesso negado!");
                    }
                }

                // LISTAR USUÁRIOS
                case 8 -> {

                    if (usuarioLogado.admin) {

                        System.out.println("\n===== USUÁRIOS =====");

                        for (int i = 0; i < totalUsuarios; i++) {

                            System.out.println("Nome: " + usuarios[i].nome);

                            if (usuarios[i].admin) {

                                System.out.println("Tipo: Admin");

                            } else {

                                System.out.println("Tipo: Usuário");
                            }

                            System.out.println();
                        }

                    } else {

                        System.out.println("Acesso negado!");
                    }
                }

                // LOGOUT
                case 9 -> {

                    loginValido = false;
                    usuarioLogado = null;

                    System.out.println("\n===== LOGOUT =====");

                    while (!loginValido) {

                        System.out.print("Usuário: ");
                        String novoUsuario = sc.nextLine();

                        System.out.print("Senha: ");
                        String novaSenha = sc.nextLine();

                        for (int i = 0; i < totalUsuarios; i++) {

                            if (novoUsuario.equals(usuarios[i].nome)
                                    && novaSenha.equals(usuarios[i].senha)) {

                                loginValido = true;
                                usuarioLogado = usuarios[i];

                                break;
                            }
                        }

                        if (!loginValido) {

                            System.out.println("Usuário ou senha inválidos!");
                        }
                    }

                    System.out.println("Login realizado com sucesso!");
                }

                // ENCERRAR
                case 10 -> {

                    System.out.println("Sistema encerrado.");
                }

                default -> {

                    if (opcao < 1 || opcao > 10) {

                        System.out.println("Opção inválida!");
                    }
                }
            }
        }

        sc.close();
    }
}