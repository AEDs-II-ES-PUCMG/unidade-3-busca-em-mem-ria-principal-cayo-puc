import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

public class TestePreAtividade {

    private static final int N = 10_000;
    private static final int M = 1_000;
    private static final int LIMITE_VALORES = 100_000;

    public static void main(String[] args) {
        List<Integer> valoresAleatorios = gerarValoresDistintos(N, new Random(42));
        List<Integer> buscas = gerarBuscas(M, new Random(42));
        List<Integer> valoresOrdenados = new ArrayList<>(valoresAleatorios);
        Collections.sort(valoresOrdenados);

        System.out.println("Estrutura/cenario                 | Total comparacoes | Tempo total (ns)");
        System.out.println("----------------------------------|-------------------|-----------------");
        testar("ABB/insercao aleatoria", new ABB<>(), valoresAleatorios, buscas);
        testar("AVL/insercao aleatoria", new AVL<>(), valoresAleatorios, buscas);
        testar("ABB/insercao ordenada", new ABB<>(), valoresOrdenados, buscas);
        testar("AVL/insercao ordenada", new AVL<>(), valoresOrdenados, buscas);

        System.out.println();
        System.out.println("Respostas:");
        System.out.println("1. Na insercao aleatoria, a ABB tende a ficar mais parecida com a AVL, porque a ordem de entrada costuma distribuir melhor os nos.");
        System.out.println("2. Na insercao ordenada, a diferenca fica mais evidente: a ABB degenera para uma estrutura parecida com uma lista encadeada.");
        System.out.println("3. Como produtos.txt nao garante ordem favoravel, a AVL em produtosPorId evita que uma ordem ruim degrade as buscas.");
    }

    private static List<Integer> gerarValoresDistintos(int quantidade, Random random) {
        Set<Integer> valores = new LinkedHashSet<>();

        while (valores.size() < quantidade)
            valores.add(random.nextInt(LIMITE_VALORES));

        return new ArrayList<>(valores);
    }

    private static List<Integer> gerarBuscas(int quantidade, Random random) {
        List<Integer> buscas = new ArrayList<>();

        for (int i = 0; i < quantidade; i++)
            buscas.add(random.nextInt(LIMITE_VALORES));

        return buscas;
    }

    private static void testar(String nome, ABB<Integer, Integer> arvore, List<Integer> valores, List<Integer> buscas) {
        long comparacoes = 0;
        double tempo = 0;

        try {
            for (Integer valor : valores)
                arvore.inserir(valor, valor);

            for (Integer busca : buscas) {
                try {
                    arvore.pesquisar(busca);
                } catch (NoSuchElementException excecao) {
                    // Buscas sem sucesso tambem fazem parte do experimento.
                } finally {
                    comparacoes += arvore.getComparacoes();
                    tempo += arvore.getTempo();
                }
            }

            System.out.printf("%-33s | %17d | %15.0f%n", nome, comparacoes, tempo);
        } catch (StackOverflowError erro) {
            System.out.printf("%-33s | %17s | %15s%n", nome, "estouro", "estouro");
        }
    }
}
