public class Cliente {

    private static int ultimoID = 10_000;

    private String nome;
    private int documento;

    /**
     * Construtor do cliente. Cria um novo cliente a partir do nome informado.
     */
    public Cliente(String nome) {

        setNome(nome);
        documento = ultimoID++;

    }

    /**
     * Retorna o nome do cliente.
     */
    public String getNome() {

        return nome;

    }

    /**
     * Atribui ao cliente o nome informado como parâmetro.
     * O nome deve conter, pelo menos, duas palavras.
     */
    public void setNome(String nome) {

        if (nome == null)
            throw new IllegalArgumentException("Nome inválido.");

        String[] partes = nome.trim().split("\\s+");

        if (partes.length < 2)
            throw new IllegalArgumentException("O nome deve possuir pelo menos duas palavras.");

        this.nome = nome;

    }

    /**
     * Retorna uma representação textual do cliente.
     */
    @Override
    public String toString() {

        return "DOCUMENTO: " + documento + " NOME: " + nome;

    }

    /**
     * Retorna o código hash do cliente.
     */
    @Override
    public int hashCode() {

        return documento;

    }
}