import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);

		String[] descricao = new String[100];
		String[] status = new String[100];
		String[] prioridade = new String[100];

		int consultaChamado = 0;
		int totalChamados = 0;
		int opcao, opcaoTI;
		int senhaTI;

		System.out.println("========================================");
		System.out.println("Bem vindo à central de chamado do T.I");
		System.out.println("========================================");

		System.out.println("1 - Abrir chamado.");
		System.out.println("2 - Consultar andamento de chamado.");
		System.out.println("3 - Atualizar chamado (T.I)");
		System.out.println("4 - Sair.");
		System.out.println("Escolha a opção desejada: ");
		opcao = sc.nextInt();
		sc.nextLine();

		while (opcao >= 1 && opcao < 4) {

			switch (opcao) {
			case 1 -> {
				System.out.println("Descreva o problema: ");
				descricao[totalChamados] = sc.nextLine();

				status[totalChamados] = "Em aberto";
				prioridade[totalChamados] = "Baixa";

				System.out.println("========================================");
				System.out.println("Chamado Nº " + (totalChamados) + " aberto com sucesso!");
				System.out.println("Guarde este número para consultas futuras.");
				System.out.println("========================================");

				totalChamados++;

				System.out.println("1 - Abrir chamado.");
				System.out.println("2 - Consultar andamento de chamado.");
				System.out.println("3 - Atualizar chamado (T.I)");
				System.out.println("4 - Sair.");
				System.out.println("Escolha a opção desejada: ");
				opcao = sc.nextInt();
				sc.nextLine();
			}
			case 2 -> {
				System.out.println("Digite o número do seu chamado: ");
				consultaChamado = sc.nextInt();
				sc.nextLine();

				System.out.println("========================================");
				System.out.printf("A descrição do chamado é: %s\n", descricao[consultaChamado]);
				System.out.printf("O status do chamado é: %s\n", status[consultaChamado]);
				System.out.printf("A prioridade do chamado é: %s\n", prioridade[consultaChamado]);
				System.out.println("========================================");

				System.out.println("1 - Abrir chamado.");
				System.out.println("2 - Consultar andamento de chamado.");
				System.out.println("3 - Atualizar chamado (T.I)");
				System.out.println("4 - Sair.");
				System.out.println("Escolha a opção desejada: ");
				opcao = sc.nextInt();
				sc.nextLine();
			}
			case 3 -> {
				do {
					System.out.println("Digite a senha de administrador: ");
					senhaTI = sc.nextInt();
					sc.nextLine();

					if (senhaTI != 12345678) {
						System.out.println("Senha incorreta, tente novamente!");
					}
				} while (senhaTI != 12345678);

				System.out.println("1 - Alterar status do chamado.");
				System.out.println("2 - Alterar prioridade do chamado.");
				System.out.println("Digite a opção desejada: ");
				opcaoTI = sc.nextInt();
				sc.nextLine();

				switch (opcaoTI) {
				case 1 -> {
					System.out.println("Digite o número do chamado que deseja alterar status: ");
					consultaChamado = sc.nextInt();
					sc.nextLine();

					System.out.printf("Digite o novo status do chamado n°%d: ", consultaChamado);
					status[consultaChamado] = sc.nextLine();

					System.out.println("========================================");
					System.out.printf("O novo status do chamado n°%d é %s\n", consultaChamado, status[consultaChamado]);
					System.out.println("========================================");
				}
				case 2 -> {
					System.out.println("Digite o número do chamado que deseja alterar prioridade: ");
					consultaChamado = sc.nextInt();
					sc.nextLine();

					System.out.printf("Digite a nova prioridade do chamado n°%d: ", consultaChamado);
					prioridade[consultaChamado] = sc.nextLine();

					System.out.println("========================================");
					System.out.printf("A nova prioridade do chamado n°%d é %s\n", consultaChamado,
							prioridade[consultaChamado]);
					System.out.println("========================================");
				}
				}

				System.out.println("1 - Abrir chamado.");
				System.out.println("2 - Consultar andamento de chamado.");
				System.out.println("3 - Atualizar chamado (T.I)");
				System.out.println("4 - Sair.");
				System.out.println("Escolha a opção desejada: ");
				opcao = sc.nextInt();
				sc.nextLine();
			}
			}
		}
		System.out.println("Programa encerrado.");

		sc.close();
	}
}