import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		String[] descricao = new String[100];
		String[] status = new String[100];
		String[] prioridade = new String[100];

		String[] usuarios = new String[100];
		String[] senhas = new String[100];

		int totalUsuarios = 0;
		int consultaChamado = 0;
		int totalChamados = 0;

		int opcao = 0;
		int opcaoTI;

		String usuarioLogin;
		String senhaLogin;

		boolean admin = false;
		boolean loginValido = false;

		usuarios[totalUsuarios] = "admin";
		senhas[totalUsuarios] = "12345678";
		totalUsuarios++;

		System.out.println("========================================");
		System.out.println("Bem vindo à central de chamado do T.I");
		System.out.println("========================================");

		System.out.println("Usuário:");
		usuarioLogin = sc.nextLine();

		System.out.println("Senha:");
		senhaLogin = sc.nextLine();

		for (int i = 0; i < totalUsuarios; i++) {

			if (usuarioLogin.equals(usuarios[i]) &&
					senhaLogin.equals(senhas[i])) {

				loginValido = true;

				if (usuarioLogin.equals("admin")) {
					admin = true;
				}
			}
		}

		if (!loginValido) {

			System.out.println("Usuário não cadastrado!");
			System.out.println("Solicite ao setor de T.I que realize seu cadastro.");

			sc.close();
			return;
		}

		System.out.println("Login realizado com sucesso!");

		while (opcao != 5) {

			
			if (admin) {

				do {

					System.out.println("\n1 - Abrir chamado.");
					System.out.println("2 - Consultar andamento de chamado.");
					System.out.println("3 - Atualizar chamado (T.I)");
					System.out.println("4 - Cadastrar usuário");
					System.out.println("5 - Sair");
					System.out.println("Escolha a opção desejada:");

					opcao = sc.nextInt();
					sc.nextLine();

					if (opcao < 1 || opcao > 5) {
						System.out.println("Opção inválida!");
					}

				} while (opcao < 1 || opcao > 5);

			}

			else {

				do {

					System.out.println("\n1 - Abrir chamado.");
					System.out.println("2 - Consultar andamento de chamado.");
					System.out.println("5 - Sair");
					System.out.println("Escolha a opção desejada:");

					opcao = sc.nextInt();
					sc.nextLine();

					if (opcao != 1 && opcao != 2 && opcao != 5) {
						System.out.println("Opção inválida!");
					}

				} while (opcao != 1 && opcao != 2 && opcao != 5);

			}

			switch (opcao) {

			case 1 -> {

				System.out.println("Descreva o problema:");
				descricao[totalChamados] = sc.nextLine();

				status[totalChamados] = "Em aberto";
				prioridade[totalChamados] = "Baixa";

				System.out.println("========================================");
				System.out.println("Chamado Nº " + totalChamados + " aberto com sucesso!");
				System.out.println("========================================");

				totalChamados++;
			}

			case 2 -> {

				System.out.println("Digite o número do chamado:");
				consultaChamado = sc.nextInt();
				sc.nextLine();

				if (consultaChamado >= 0 &&
						consultaChamado < totalChamados) {

					System.out.println("========================================");
					System.out.println("Descrição: "
							+ descricao[consultaChamado]);

					System.out.println("Status: "
							+ status[consultaChamado]);

					System.out.println("Prioridade: "
							+ prioridade[consultaChamado]);

					System.out.println("========================================");

				} else {

					System.out.println("Chamado inexistente!");
				}
			}

			case 3 -> {

				if (admin) {

					do {

						System.out.println("1 - Alterar status");
						System.out.println("2 - Alterar prioridade");
						System.out.println("Escolha a opção:");

						opcaoTI = sc.nextInt();
						sc.nextLine();

						if (opcaoTI < 1 || opcaoTI > 2) {
							System.out.println("Opção inválida!");
						}

					} while (opcaoTI < 1 || opcaoTI > 2);

					switch (opcaoTI) {

					case 1 -> {

						System.out.println("Digite o número do chamado:");
						consultaChamado = sc.nextInt();
						sc.nextLine();

						if (consultaChamado >= 0 &&
								consultaChamado < totalChamados) {

							System.out.println("Digite o novo status:");
							status[consultaChamado] = sc.nextLine();

							System.out.println("Status atualizado!");

						} else {

							System.out.println("Chamado inexistente!");
						}
					}

					case 2 -> {

						System.out.println("Digite o número do chamado:");
						consultaChamado = sc.nextInt();
						sc.nextLine();

						if (consultaChamado >= 0 &&
								consultaChamado < totalChamados) {

							System.out.println("Digite a nova prioridade:");
							prioridade[consultaChamado] = sc.nextLine();

							System.out.println("Prioridade atualizada!");

						} else {

							System.out.println("Chamado inexistente!");
						}
					}
					}
				}
			}

			case 4 -> {

				if (admin) {

					System.out.println("Digite o nome do novo usuário:");
					usuarios[totalUsuarios] = sc.nextLine();

					System.out.println("Digite a senha do novo usuário:");
					senhas[totalUsuarios] = sc.nextLine();

					System.out.println("Usuário cadastrado com sucesso!");

					totalUsuarios++;

					System.out.println("\n1 - Continuar como admin");
					System.out.println("2 - Voltar para login");

					int escolhaLogin = sc.nextInt();
					sc.nextLine();

					if (escolhaLogin == 2) {

						loginValido = false;
						admin = false;

						System.out.println("Usuário:");
						usuarioLogin = sc.nextLine();

						System.out.println("Senha:");
						senhaLogin = sc.nextLine();

						for (int i = 0; i < totalUsuarios; i++) {

							if (usuarioLogin.equals(usuarios[i]) &&
									senhaLogin.equals(senhas[i])) {

								loginValido = true;

								if (usuarioLogin.equals("admin")) {
									admin = true;
								}
							}
						}

						if (!loginValido) {

							System.out.println("Usuário ou senha inválidos!");
							opcao = 5;
						}

						else {

							System.out.println("Login realizado com sucesso!");
						}
					}
				}
			}

			case 5 -> {
				System.out.println("Programa encerrado.");
			}
			}
		}

		sc.close();
	}
}