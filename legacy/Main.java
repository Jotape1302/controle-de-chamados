import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    // ================= SALVAR USUÁRIOS =================

    public static void salvarUsuarios(ArrayList<Usuario> usuarios) {

        try {

            FileOutputStream arquivo =
                    new FileOutputStream("usuarios.dat");

            ObjectOutputStream salvar =
                    new ObjectOutputStream(arquivo);

            salvar.writeObject(usuarios);

            salvar.close();

        } catch (Exception e) {

            System.out.println("Erro ao salvar usuários.");
        }
    }

    // ================= CARREGAR USUÁRIOS =================

    public static ArrayList<Usuario> carregarUsuarios() {

        try {

            FileInputStream arquivo =
                    new FileInputStream("usuarios.dat");

            ObjectInputStream leitura =
                    new ObjectInputStream(arquivo);

            return (ArrayList<Usuario>) leitura.readObject();

        } catch (Exception e) {

            return new ArrayList<Usuario>();
        }
    }

    // ================= SALVAR CHAMADOS =================

    public static void salvarChamados(ArrayList<Chamado> chamados) {

        try {

            FileOutputStream arquivo =
                    new FileOutputStream("chamados.dat");

            ObjectOutputStream salvar =
                    new ObjectOutputStream(arquivo);

            salvar.writeObject(chamados);

            salvar.close();

        } catch (Exception e) {

            System.out.println("Erro ao salvar chamados.");
        }
    }

    // ================= CARREGAR CHAMADOS =================

    public static ArrayList<Chamado> carregarChamados() {

        try {

            FileInputStream arquivo =
                    new FileInputStream("chamados.dat");

            ObjectInputStream leitura =
                    new ObjectInputStream(arquivo);

            return (ArrayList<Chamado>) leitura.readObject();

        } catch (Exception e) {

            return new ArrayList<Chamado>();
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ArrayList<Usuario> usuarios = carregarUsuarios();
        ArrayList<Chamado> chamados = carregarChamados();

        // CRIA ADMIN PADRÃO SE NÃO EXISTIR NINGUÉM
        if (usuarios.isEmpty()) {

            usuarios.add(new Usuario("admin", "123", true));

            salvarUsuarios(usuarios);
        }

        Usuario usuarioLogado = null;
        boolean loginValido = false;

        // LOGIN
        while (!loginValido) {

            System.out.println("\n===== LOGIN =====");

            System.out.print("Usuário: ");
            String usuarioLogin = sc.nextLine();

            System.out.print("Senha: ");
            String senhaLogin = sc.nextLine();

            for (Usuario usuario : usuarios) {

                if (usuarioLogin.equals(usuario.nome)
                        && senhaLogin.equals(usuario.senha)) {

                    loginValido = true;
                    usuarioLogado = usuario;

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

                    int novoId = chamados.size() + 1;

                    Chamado novoChamado =
                            new Chamado(
                                    novoId,
                                    descricao,
                                    "Baixa",
                                    usuarioLogado.nome
                            );

                    chamados.add(novoChamado);

                    salvarChamados(chamados);

                    System.out.println("Chamado aberto com sucesso!");
                    System.out.println("Número do chamado: " + novoChamado.id);
                }

                // LISTAR CHAMADOS ATIVOS
                case 2 -> {

                    System.out.println("\n===== CHAMADOS ATIVOS =====");

                    boolean encontrou = false;

                    for (Chamado chamado : chamados) {

                        boolean ativo =
                                chamado.status.equals("Aberto")
                                        || chamado.status.equals("Andamento");

                        if (ativo) {

                            if (usuarioLogado.admin
                                    || chamado.usuario.equals(usuarioLogado.nome)) {

                                System.out.println("ID: " + chamado.id);
                                System.out.println("Usuário: " + chamado.usuario);
                                System.out.println("Descrição: " + chamado.descricao);
                                System.out.println("Prioridade: " + chamado.prioridade);
                                System.out.println("Status: " + chamado.status);

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

                    for (Chamado chamado : chamados) {

                        boolean finalizado =
                                chamado.status.equals("Concluído")
                                        || chamado.status.equals("Encerrado");

                        if (finalizado) {

                            if (usuarioLogado.admin
                                    || chamado.usuario.equals(usuarioLogado.nome)) {

                                System.out.println("ID: " + chamado.id);
                                System.out.println("Usuário: " + chamado.usuario);
                                System.out.println("Descrição: " + chamado.descricao);
                                System.out.println("Prioridade: " + chamado.prioridade);
                                System.out.println("Status: " + chamado.status);

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

                    for (Chamado chamado : chamados) {

                        if (chamado.id == busca) {

                            if (usuarioLogado.admin
                                    || chamado.usuario.equals(usuarioLogado.nome)) {

                                System.out.println("\n===== CHAMADO =====");

                                System.out.println("ID: " + chamado.id);
                                System.out.println("Usuário: " + chamado.usuario);
                                System.out.println("Descrição: " + chamado.descricao);
                                System.out.println("Prioridade: " + chamado.prioridade);
                                System.out.println("Status: " + chamado.status);

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

                        usuarios.add(new Usuario(nome, senha, admin));

                        salvarUsuarios(usuarios);

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

                        for (Chamado chamado : chamados) {

                            if (chamado.id == id) {

                                System.out.println("\n1 - Aberto");
                                System.out.println("2 - Andamento");
                                System.out.println("3 - Concluído");
                                System.out.println("4 - Encerrado");

                                System.out.print("Escolha: ");
                                int status = sc.nextInt();
                                sc.nextLine();

                                switch (status) {

                                    case 1 -> chamado.status = "Aberto";
                                    case 2 -> chamado.status = "Andamento";
                                    case 3 -> chamado.status = "Concluído";
                                    case 4 -> chamado.status = "Encerrado";

                                    default -> {
                                        System.out.println("Opção inválida!");
                                        break;
                                    }
                                }

                                salvarChamados(chamados);

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

                        for (Chamado chamado : chamados) {

                            if (chamado.id == id) {

                                System.out.println("\n1 - Baixa");
                                System.out.println("2 - Média");
                                System.out.println("3 - Alta");
                                System.out.println("4 - Crítica");

                                System.out.print("Escolha: ");
                                int prioridade = sc.nextInt();
                                sc.nextLine();

                                switch (prioridade) {

                                    case 1 -> chamado.prioridade = "Baixa";
                                    case 2 -> chamado.prioridade = "Média";
                                    case 3 -> chamado.prioridade = "Alta";
                                    case 4 -> chamado.prioridade = "Crítica";

                                    default -> {
                                        System.out.println("Opção inválida!");
                                        break;
                                    }
                                }

                                salvarChamados(chamados);

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

                        for (Usuario usuario : usuarios) {

                            System.out.println("Nome: " + usuario.nome);

                            if (usuario.admin) {

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

                        for (Usuario usuario : usuarios) {

                            if (novoUsuario.equals(usuario.nome)
                                    && novaSenha.equals(usuario.senha)) {

                                loginValido = true;
                                usuarioLogado = usuario;

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