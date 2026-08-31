import java.io.Serializable;

public class Usuario implements Serializable {

    String nome;
    String senha;
    boolean admin;

    public Usuario(String nome, String senha, boolean admin) {

        this.nome = nome;
        this.senha = senha;
        this.admin = admin;
    }
}