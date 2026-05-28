import java.io.Serializable;

public class Chamado implements Serializable {

    int id;
    String descricao;
    String status;
    String prioridade;
    String usuario;

    public Chamado(int id, String descricao, String prioridade, String usuario) {

        this.id = id;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.usuario = usuario;
        this.status = "Aberto";
    }
}