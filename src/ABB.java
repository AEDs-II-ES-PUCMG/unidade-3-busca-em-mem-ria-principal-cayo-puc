import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ABB<K, V> implements IMapeamento<K, V>{

	private No<K, V> raiz; // referência à raiz da árvore.
	private Comparator<K> comparador; //comparador empregado para definir "menores" e "maiores".
	private int tamanho;

    private long comparacoes;
    private double tempo;
	
	/**
	 * Método auxiliar para inicialização da árvore binária de busca.
	 * 
	 * Este método define a raiz da árvore como {@code null} e seu tamanho como 0.
	 * Se o comparador fornecido for {@code null}, o comparador padrão de ordem natural
	 * será utilizado.
	 * 
	 * @param comparador o comparador para organizar os elementos da árvore.
	 */
	@SuppressWarnings("unchecked")
	private void init(Comparator<K> comparador) {
		raiz = null;
		tamanho = 0;
		if (comparador == null) {
			comparador = (Comparator<K>) Comparator.naturalOrder();
		}
		this.comparador = comparador;
	}
	
	/**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária de busca vazia. Para isso, esse método atribui null à raiz da árvore.
     */
    public ABB() {
        init(null);
    }

    /**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária de busca vazia utilizando o
     * comparador fornecido para definir a organização dos elementos na árvore.
     * Para isso, esse método atribui null à raiz da árvore.
     *  
     * @param comparador o comparador a ser utilizado para organizar os elementos da árvore.  
     */
    public ABB(Comparator<K> comparador) {
        init(comparador);
    }
    
    /**
     * Construtor da classe.
     * Esse construtor cria uma nova árvore binária a partir de uma outra árvore binária de busca,
     * com os mesmos itens, mas usando uma nova chave.
     * @param original a árvore binária de busca original.
     * @param funcaoChave a função que irá extrair a nova chave de cada item para a nova árvore.
     */
    public ABB(ABB<?,V> original, Function<V,K> funcaoChave, Comparator<K> comparador) {
        ABB<K,V> nova = new ABB<>();
        nova = copiarArvore(original.raiz, funcaoChave, nova);
        this.raiz = nova.raiz;
        this.comparador = comparador;
    
    }
    
    /**
     * Recursivamente, copia os elementos da árvore original para esta, num processo análogo ao caminhamento em ordem.
     * @param <T> Tipo da nova chave.
     * @param raizArvore raiz da árvore original que será copiada.
     * @param funcaoChave função extratora da nova chave para cada item da árvore.
     * @param novaArvore Nova árvore. Parâmetro usado para permitir o retorno da recursividade.
     * @return A nova árvore com os itens copiados e usando a chave indicada pela função extratora.
     */
    private <T> ABB<T,V> copiarArvore(No<?,V> raizArvore, Function<V,T> funcaoChave, ABB<T,V> novaArvore) {
    	
        if (raizArvore != null) {
    		novaArvore = copiarArvore(raizArvore.getEsquerda(), funcaoChave, novaArvore);
            V item = raizArvore.getItem();
            T chave = funcaoChave.apply(item);
    		novaArvore.inserir(chave, item);
    		novaArvore = copiarArvore(raizArvore.getDireita(), funcaoChave, novaArvore);
    	}
        return novaArvore;
    }

    /**
     * Método booleano que indica se a árvore está vazia ou não.
     * @return
     * verdadeiro: se a raiz da árvore for null, o que significa que a árvore está vazia.
     * falso: se a raiz da árvore não for null, o que significa que a árvore não está vazia.
     */
    public Boolean vazia() {
        return (this.raiz == null);
    }
    
    @Override
    /**
     * Método que encapsula a pesquisa recursiva de itens na árvore.
     * @param chave a chave do item que será pesquisado na árvore.
     * @return o valor associado à chave.
     */
	public V pesquisar(K chave) {	
        comparacoes = 0;
        LocalDateTime inicio = LocalDateTime.now();
        try {
            return pesquisar(raiz, chave);
        } finally {
            LocalDateTime fim = LocalDateTime.now();
            tempo = Duration.between(inicio, fim).toNanos();
        }
    }
    
    private V pesquisar(No<K, V> raizArvore, K procurado) {
    	
    	int comparacao;
    	
        comparacoes++;
    	if (raizArvore == null)
    		/// Se a raiz da árvore ou sub-árvore for null, a árvore/sub-árvore está vazia e então o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	
    	comparacao = comparador.compare(procurado, raizArvore.getChave());
    	
        comparacoes++;   
    	if (comparacao == 0)
    		/// O item procurado foi encontrado.
    		return raizArvore.getItem();
    	else if (comparacao < 0)
    		/// Se o item procurado for menor do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore esquerda.    
    		return pesquisar(raizArvore.getEsquerda(), procurado);
    	else
    		/// Se o item procurado for maior do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore direita.
    		return pesquisar(raizArvore.getDireita(), procurado);
    }
    
    @Override
    /**
     * Método que encapsula a adição recursiva de itens à árvore, associando-o à chave fornecida.
     * @param chave a chave associada ao item que será inserido na árvore.
     * @param item o item que será inserido na árvore.
     * 
     * @return o tamanho atualizado da árvore após a execução da operação de inserção.
     */
	public int inserir(K chave, V item) {
        comparacoes = 0;
        LocalDateTime inicio = LocalDateTime.now();
        raiz = inserir(raiz, chave, item);
        LocalDateTime fim = LocalDateTime.now();
        tempo = Duration.between(inicio, fim).toNanos();
        tamanho++;
		return tamanho;
	}
    
    /**
     * Método recursivo responsável por adicionar um item à árvore.
     * @param raizArvore a raiz da árvore ou sub-árvore em que o item será adicionado.
     * @param chave a chave associada ao item que deverá ser inserido.
     * @param item o item que deverá ser adicionado à árvore.
     * @return a raiz atualizada da árvore ou sub-árvore em que o item foi adicionado.
     * @throws RuntimeException se um item com a mesma chave já estiver presente na árvore.
     */
    protected No<K, V> inserir(No<K, V> raizArvore, K chave, V item) {
    	comparacoes++;
        if(raizArvore==null)
            return new No<K,V>(chave, item);
        
        int comparacao = comparador.compare(chave, raizArvore.getChave());

        comparacoes++;
        if(comparacao > 0)
           raizArvore.setDireita(inserir(raizArvore.getDireita(), chave, item));
        else if(comparacao < 0)
                raizArvore.setEsquerda(inserir(raizArvore.getEsquerda(), chave, item));
    	else  //comparacao == 0, ou seja, chave existe
            throw new IllegalArgumentException("Elemento já existe na árvore");
        return raizArvore;
    }

    @Override 
    public String toString(){
    	return percorrer();
    }

    @Override
	public String percorrer() {
    	if (vazia())
    		throw new IllegalStateException("A árvore está vazia!");
    	
    	return caminhamentoEmOrdem(raiz);
	}
    
    private String caminhamentoEmOrdem(No<K,V> raizArvore) {
    	
        if (raizArvore != null) {
    		String resposta = caminhamentoEmOrdem(raizArvore.getEsquerda());
    		resposta += raizArvore.getItem()+"\n";
    		resposta += caminhamentoEmOrdem(raizArvore.getDireita());
            return resposta;
    	}
        else return "";
    }
    
    public Lista<V> recortar(K chaveInicio, K chaveFinal){
        Lista<V> listaRecorte = new Lista<>();
        recortarRecursivo(raiz, chaveInicio, chaveFinal, listaRecorte);
        return listaRecorte;
    }

    private void recortarRecursivo(No<K,V> raizSubArvore, K chaveInicio, K chaveFinal, Lista<V> listaRecorte){
        if(raizSubArvore == null)
            return;
        if( comparador.compare(chaveInicio, raizSubArvore.getChave()) > 0)
                recortarRecursivo(raizSubArvore.getDireita(), chaveInicio, chaveFinal, listaRecorte);
        else if( comparador.compare(chaveFinal, raizSubArvore.getChave()) < 0)
                recortarRecursivo(raizSubArvore.getEsquerda(), chaveInicio, chaveFinal, listaRecorte);
        else {
            recortarRecursivo(raizSubArvore.getEsquerda(), chaveInicio, chaveFinal, listaRecorte);
            listaRecorte.inserir(raizSubArvore.getItem());
            recortarRecursivo(raizSubArvore.getDireita(), chaveInicio, chaveFinal, listaRecorte);
        }
    }

	@Override
	/**
     * Método que encapsula a remoção recursiva de um item da árvore.
     * @param chave a chave do item que deverá ser localizado e removido da árvore.
     * @return o valor associado ao item removido.
	 */
	public V remover(K chave) {
		V elemento = pesquisar(raiz, chave);
        //marcar tempo e comparações
        raiz = remover(raiz, chave);

		return elemento;
	}
    
    protected No<K,V> remover(No<K,V> raizSubArvore, K chave) {
        int comparacao;
    	
        comparacoes++;
    	if (raizSubArvore == null)
    		/// Se a raiz da árvore ou sub-árvore for null, a árvore/sub-árvore está vazia e então o item não foi encontrado.
    		throw new NoSuchElementException("O item não foi localizado na árvore!");
    	
    	comparacao = comparador.compare(chave, raizSubArvore.getChave());
    	
        comparacoes++;   
    	if (comparacao == 0){
    		/// O item procurado foi encontrado.
            int grau = raizSubArvore.grau();
            switch (grau) {
                case 0 -> {return null;}
                case 1 -> {return raizSubArvore.getDireita();}
                case -1 -> {return raizSubArvore.getEsquerda();}
                case 2 -> removerAntecessor(raizSubArvore);
            }   
        }	
    	else if (comparacao < 0)
    		/// Se o item procurado for menor do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore esquerda.    
    		raizSubArvore.setEsquerda(remover(raizSubArvore.getEsquerda(), chave));
    	else
    		/// Se o item procurado for maior do que o item armazenado na raiz da árvore:
            /// pesquise esse item na sub-árvore direita.
    		raizSubArvore.setDireita(remover(raizSubArvore.getDireita(), chave));
    
        return raizSubArvore;
    }

    private void removerAntecessor(No<K,V> raizSubArvore){
        //descobrindo o antecessor
        No<K,V> antecessor = raizSubArvore.getEsquerda();
        while (antecessor.getDireita()!=null) {
            antecessor = antecessor.getDireita();            
        }

        //copiar dados do antecessor para a raiz grau 2
        raizSubArvore.setItem(antecessor.getItem());
        raizSubArvore.setChave(antecessor.getChave());

        //remover o antecessor duplicado do lado esquerdo
        raizSubArvore.setEsquerda(remover(raizSubArvore.getEsquerda(), raizSubArvore.getChave()));
    }

	@Override
	public int tamanho() {
		return tamanho;
	}
	
	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return tempo;
	}

    public boolean contem(K chave) {
        try {
            pesquisar(chave);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public int contarPorGrau(int grau) {
        return contarPorGrau(raiz, grau);
    }

    private int contarPorGrau(No<K,V> no, int grau) {
        if (no == null) {
            return 0;
        }

        int total = 0;

        if (no.grau() == grau) {
            total = 1;
        }

        return total
            + contarPorGrau(no.getEsquerda(), grau)
            + contarPorGrau(no.getDireita(), grau);
    }

    public boolean estaBalanceada() {
        return estaBalanceada(raiz);
    }

    private boolean estaBalanceada(No<K,V> no) {
        if (no == null) {
            return true;
        }

        int fb = altura(no.getEsquerda()) - altura(no.getDireita());

        if (fb < -1 || fb > 1) {
            return false;
        }

        return estaBalanceada(no.getEsquerda())
            && estaBalanceada(no.getDireita());
    }

    public String listarIntervalo(K inicio, K fim) {
        StringBuilder sb = new StringBuilder();
        listarIntervalo(raiz, inicio, fim, sb);
        return sb.toString();
    }

    private void listarIntervalo(No<K,V> no, K inicio, K fim, StringBuilder sb) {
        if (no == null) {
            return;
        }

        if (comparador.compare(no.getChave(), inicio) > 0) {
            listarIntervalo(no.getEsquerda(), inicio, fim, sb);
        }

        if (comparador.compare(no.getChave(), inicio) >= 0 &&
            comparador.compare(no.getChave(), fim) <= 0) {
            sb.append(no.getItem()).append("\n");
        }

        if (comparador.compare(no.getChave(), fim) < 0) {
            listarIntervalo(no.getDireita(), inicio, fim, sb);
        }
    }

    public int contarIntervalo(K inicio, K fim) {
        return contarIntervalo(raiz, inicio, fim);
    }

    private int contarIntervalo(No<K,V> no, K inicio, K fim) {
        if (no == null) {
            return 0;
        }

        if (comparador.compare(no.getChave(), inicio) < 0) {
            return contarIntervalo(no.getDireita(), inicio, fim);
        }

        if (comparador.compare(no.getChave(), fim) > 0) {
            return contarIntervalo(no.getEsquerda(), inicio, fim);
        }

        return 1
            + contarIntervalo(no.getEsquerda(), inicio, fim)
            + contarIntervalo(no.getDireita(), inicio, fim);
    }

    public V maior() {
        if (raiz == null) {
            throw new NoSuchElementException("Árvore vazia.");
        }

        return maior(raiz).getItem();
    }

    private No<K,V> maior(No<K,V> no) {
        if (no.getDireita() == null) {
            return no;
        }

        return maior(no.getDireita());
    }

    public V menor() {
        if (raiz == null) {
            throw new NoSuchElementException("Árvore vazia.");
        }

        return menor(raiz).getItem();
    }

    private No<K,V> menor(No<K,V> no) {
        if (no.getEsquerda() == null) {
            return no;
        }

        return menor(no.getEsquerda());
    }

    public Lista<V> filtrarPorValorMaiorQue(double valorMinimo) {
        Lista<V> resultado = new Lista<>();
        filtrarPorValorMaiorQue(raiz, valorMinimo, resultado);
        return resultado;
    }

    private void filtrarPorValorMaiorQue(No<K, V> no, double valorMinimo, Lista<V> resultado) {
        if (no == null) {
            return;
        }

        filtrarPorValorMaiorQue(no.getEsquerda(), valorMinimo, resultado);

        Produto produto = (Produto) no.getItem();

        if (produto.valorDeVenda() > valorMinimo) {
            resultado.inserir(no.getItem());
        }

        filtrarPorValorMaiorQue(no.getDireita(), valorMinimo, resultado);
    }

    public Lista<V> buscarPorTrechoDescricao(String trecho) {
        Lista<V> resultado = new Lista<>();
        buscarPorTrechoDescricao(raiz, trecho.toLowerCase(), resultado);
        return resultado;
    }

    private void buscarPorTrechoDescricao(No<K, V> no, String trecho, Lista<V> resultado) {
        if (no == null) {
            return;
        }

        buscarPorTrechoDescricao(no.getEsquerda(), trecho, resultado);

        Produto produto = (Produto) no.getItem();

        if (produto.getDescricao().toLowerCase().contains(trecho)) {
            resultado.inserir(no.getItem());
        }

        buscarPorTrechoDescricao(no.getDireita(), trecho, resultado);
    }

    public Lista<V> filtrarPorValorMenorQue(double valorMaximo) {
        Lista<V> resultado = new Lista<>();
        filtrarPorValorMenorQue(raiz, valorMaximo, resultado);
        return resultado;
    }

    private void filtrarPorValorMenorQue(No<K,V> no, double valorMaximo, Lista<V> resultado) {
        if (no == null) return;

        filtrarPorValorMenorQue(no.getEsquerda(), valorMaximo, resultado);

        Produto produto = (Produto) no.getItem();

        if (produto.valorDeVenda() < valorMaximo) {
            resultado.inserir(no.getItem());
        }

        filtrarPorValorMenorQue(no.getDireita(), valorMaximo, resultado);
    }

    public Lista<V> filtrarPorFaixaDePreco(double minimo, double maximo) {
        Lista<V> resultado = new Lista<>();
        filtrarPorFaixaDePreco(raiz, minimo, maximo, resultado);
        return resultado;
    }

    private void filtrarPorFaixaDePreco(No<K,V> no, double minimo, double maximo, Lista<V> resultado) {
        if (no == null) return;

        filtrarPorFaixaDePreco(no.getEsquerda(), minimo, maximo, resultado);

        Produto produto = (Produto) no.getItem();
        double valor = produto.valorDeVenda();

        if (valor >= minimo && valor <= maximo) {
            resultado.inserir(no.getItem());
        }

        filtrarPorFaixaDePreco(no.getDireita(), minimo, maximo, resultado);
    }

    public int contarProdutosAcimaDe(double valorMinimo) {
        return contarProdutosAcimaDe(raiz, valorMinimo);
    }

    private int contarProdutosAcimaDe(No<K,V> no, double valorMinimo) {
        if (no == null) return 0;

        Produto produto = (Produto) no.getItem();

        int total = produto.valorDeVenda() > valorMinimo ? 1 : 0;

        return total
            + contarProdutosAcimaDe(no.getEsquerda(), valorMinimo)
            + contarProdutosAcimaDe(no.getDireita(), valorMinimo);
    }

    public double somarValorDeVenda() {
        return somarValorDeVenda(raiz);
    }

    private double somarValorDeVenda(No<K,V> no) {
        if (no == null) return 0.0;

        Produto produto = (Produto) no.getItem();

        return produto.valorDeVenda()
            + somarValorDeVenda(no.getEsquerda())
            + somarValorDeVenda(no.getDireita());
    }

    public double mediaValorDeVenda() {
        if (tamanho == 0) {
            return 0.0;
        }

        return somarValorDeVenda() / tamanho;
    }

    public V produtoMaisCaro() {
        if (raiz == null) {
            throw new NoSuchElementException("Árvore vazia.");
        }

        return produtoMaisCaro(raiz, raiz.getItem());
    }

    private V produtoMaisCaro(No<K,V> no, V maiorAtual) {
        if (no == null) return maiorAtual;

        Produto produtoAtual = (Produto) no.getItem();
        Produto produtoMaior = (Produto) maiorAtual;

        if (produtoAtual.valorDeVenda() > produtoMaior.valorDeVenda()) {
            maiorAtual = no.getItem();
        }

        maiorAtual = produtoMaisCaro(no.getEsquerda(), maiorAtual);
        maiorAtual = produtoMaisCaro(no.getDireita(), maiorAtual);

        return maiorAtual;
    }

    public Lista<V> buscarDescricaoComecandoCom(String prefixo) {
        Lista<V> resultado = new Lista<>();
        buscarDescricaoComecandoCom(raiz, prefixo.toLowerCase(), resultado);
        return resultado;
    }

    private void buscarDescricaoComecandoCom(No<K,V> no, String prefixo, Lista<V> resultado) {
        if (no == null) return;

        buscarDescricaoComecandoCom(no.getEsquerda(), prefixo, resultado);

        Produto produto = (Produto) no.getItem();

        if (produto.getDescricao().toLowerCase().startsWith(prefixo)) {
            resultado.inserir(no.getItem());
        }

        buscarDescricaoComecandoCom(no.getDireita(), prefixo, resultado);
    }

    public Lista<V> filtrarPorMargemMaiorQue(double margemMinima) {
        Lista<V> resultado = new Lista<>();
        filtrarPorMargemMaiorQue(raiz, margemMinima, resultado);
        return resultado;
    }

    private void filtrarPorMargemMaiorQue(No<K,V> no, double margemMinima, Lista<V> resultado) {
        if (no == null) return;

        filtrarPorMargemMaiorQue(no.getEsquerda(), margemMinima, resultado);

        Produto produto = (Produto) no.getItem();

        if (produto.getMargemLucro() > margemMinima) {
            resultado.inserir(no.getItem());
        }

        filtrarPorMargemMaiorQue(no.getDireita(), margemMinima, resultado);
    }

    public String relatorioProdutos() {
        StringBuilder sb = new StringBuilder();
        relatorioProdutos(raiz, sb);
        return sb.toString();
    }

    private void relatorioProdutos(No<K,V> no, StringBuilder sb) {
        if (no == null) return;

        relatorioProdutos(no.getEsquerda(), sb);

        Produto produto = (Produto) no.getItem();

        sb.append("Produto: ").append(produto.getDescricao()).append("\n");
        sb.append("Valor de venda: R$ ")
        .append(String.format("%.2f", produto.valorDeVenda()))
        .append("\n\n");

        relatorioProdutos(no.getDireita(), sb);
    }

    public int altura() {
        return altura(raiz);
    }

    /**
     * Calcula recursivamente a altura de um nó.
     */
    private int altura(No<K, V> no) {
        if (no == null) {
            return -1;
        }

        int alturaEsquerda = altura(no.getEsquerda());
        int alturaDireita = altura(no.getDireita());

        return 1 + Math.max(alturaEsquerda, alturaDireita);
    }

    public V produtoMaisBarato() {

    if (raiz == null)
        throw new IllegalStateException("Árvore vazia.");

    return produtoMaisBarato(raiz);
}

    private V produtoMaisBarato(No<K,V> no) {

        Produto menor = (Produto) no.getItem();

        if (no.getEsquerda() != null) {
            Produto aux = (Produto) produtoMaisBarato(no.getEsquerda());
            if (aux.valorDeVenda() < menor.valorDeVenda())
                menor = aux;
        }

        if (no.getDireita() != null) {
            Produto aux = (Produto) produtoMaisBarato(no.getDireita());
            if (aux.valorDeVenda() < menor.valorDeVenda())
                menor = aux;
        }

        return (V) menor;
    }

    public V maiorMargemLucro() {

    if (raiz == null)
        throw new IllegalStateException();

    return maiorMargemLucro(raiz);
}

private V maiorMargemLucro(No<K,V> no) {

    Produto maior = (Produto) no.getItem();

    if (no.getEsquerda() != null) {
        Produto aux = (Produto) maiorMargemLucro(no.getEsquerda());
        if (aux.getMargemLucro() > maior.getMargemLucro())
            maior = aux;
    }

    if (no.getDireita() != null) {
        Produto aux = (Produto) maiorMargemLucro(no.getDireita());
        if (aux.getMargemLucro() > maior.getMargemLucro())
            maior = aux;
    }

    return (V) maior;
}

public V menorMargemLucro() {

    if (raiz == null)
        throw new IllegalStateException();

    return menorMargemLucro(raiz);
}

private V menorMargemLucro(No<K,V> no) {

    Produto menor = (Produto) no.getItem();

    if (no.getEsquerda() != null) {
        Produto aux = (Produto) menorMargemLucro(no.getEsquerda());
        if (aux.getMargemLucro() < menor.getMargemLucro())
            menor = aux;
    }

    if (no.getDireita() != null) {
        Produto aux = (Produto) menorMargemLucro(no.getDireita());
        if (aux.getMargemLucro() < menor.getMargemLucro())
            menor = aux;
    }

    return (V) menor;
}

public double somaValorVenda() {
    return somaValorVenda(raiz);
}

private double somaValorVenda(No<K,V> no) {

    if (no == null)
        return 0;

    Produto p = (Produto) no.getItem();

    return p.valorDeVenda()
            + somaValorVenda(no.getEsquerda())
            + somaValorVenda(no.getDireita());
}

public double somaPrecoCusto() {
    return somaPrecoCusto(raiz);
}

private double somaPrecoCusto(No<K,V> no) {

    if (no == null)
        return 0;

    Produto p = (Produto) no.getItem();

    return p.getPrecoCusto()
            + somaPrecoCusto(no.getEsquerda())
            + somaPrecoCusto(no.getDireita());
}

public double mediaPrecoCusto() {

    if (tamanho == 0)
        return 0;

    return somaPrecoCusto() / tamanho;
}

public double mediaValorVenda() {

    if (tamanho == 0)
        return 0;

    return somaValorVenda() / tamanho;
}

public V primeiroProduto() {

    if (raiz == null)
        throw new IllegalStateException();

    No<K,V> atual = raiz;

    while (atual.getEsquerda() != null)
        atual = atual.getEsquerda();

    return atual.getItem();
}

public V ultimoProduto() {

    if (raiz == null)
        throw new IllegalStateException();

    No<K,V> atual = raiz;

    while (atual.getDireita() != null)
        atual = atual.getDireita();

    return atual.getItem();
}

public Lista<V> produtosComMargemMaiorQue(double margem) {

    Lista<V> lista = new Lista<>();

    produtosComMargemMaiorQue(raiz, margem, lista);

    return lista;
}

private void produtosComMargemMaiorQue(No<K,V> no, double margem, Lista<V> lista) {

    if (no == null)
        return;

    produtosComMargemMaiorQue(no.getEsquerda(), margem, lista);

    Produto p = (Produto) no.getItem();

    if (p.getMargemLucro() > margem)
        lista.inserirFinal(no.getItem());

    produtosComMargemMaiorQue(no.getDireita(), margem, lista);
}

public Lista<V> produtosComMargemMenorQue(double margem) {

    Lista<V> lista = new Lista<>();

    produtosComMargemMenorQue(raiz, margem, lista);

    return lista;
}

private void produtosComMargemMenorQue(No<K,V> no, double margem, Lista<V> lista) {

    if (no == null)
        return;

    produtosComMargemMenorQue(no.getEsquerda(), margem, lista);

    Produto p = (Produto) no.getItem();

    if (p.getMargemLucro() < margem)
        lista.inserirFinal(no.getItem());

    produtosComMargemMenorQue(no.getDireita(), margem, lista);
}

public Lista<V> produtosComecandoCom(char letra) {

    Lista<V> lista = new Lista<>();

    produtosComecandoCom(raiz, Character.toUpperCase(letra), lista);

    return lista;
}

private void produtosComecandoCom(No<K,V> no, char letra, Lista<V> lista) {

    if (no == null)
        return;

    produtosComecandoCom(no.getEsquerda(), letra, lista);

    Produto p = (Produto) no.getItem();

    if (Character.toUpperCase(p.getDescricao().charAt(0)) == letra)
        lista.inserirFinal(no.getItem());

    produtosComecandoCom(no.getDireita(), letra, lista);
}

public Lista<V> descricaoContem(String texto) {

    Lista<V> lista = new Lista<>();

    descricaoContem(raiz, texto.toLowerCase(), lista);

    return lista;
}

private void descricaoContem(No<K,V> no, String texto, Lista<V> lista) {

    if (no == null)
        return;

    descricaoContem(no.getEsquerda(), texto, lista);

    Produto p = (Produto) no.getItem();

    if (p.getDescricao().toLowerCase().contains(texto))
        lista.inserirFinal(no.getItem());

    descricaoContem(no.getDireita(), texto, lista);
}

public Lista<V> descricaoMaiorQue(int tamanho) {

    Lista<V> lista = new Lista<>();

    descricaoMaiorQue(raiz, tamanho, lista);

    return lista;
}

private void descricaoMaiorQue(No<K,V> no, int tamanho, Lista<V> lista) {

    if (no == null)
        return;

    descricaoMaiorQue(no.getEsquerda(), tamanho, lista);

    Produto p = (Produto) no.getItem();

    if (p.getDescricao().length() > tamanho)
        lista.inserirFinal(no.getItem());

    descricaoMaiorQue(no.getDireita(), tamanho, lista);
}

public int contarPereciveis() {

    return contarPereciveis(raiz);
}

private int contarPereciveis(No<K,V> no) {

    if (no == null)
        return 0;

    int cont = 0;

    if (no.getItem() instanceof ProdutoPerecivel)
        cont++;

    cont += contarPereciveis(no.getEsquerda());

    cont += contarPereciveis(no.getDireita());

    return cont;
}

public int contarNaoPereciveis() {

    return contarNaoPereciveis(raiz);
}

private int contarNaoPereciveis(No<K,V> no) {

    if (no == null)
        return 0;

    int cont = 0;

    if (no.getItem() instanceof ProdutoNaoPerecivel)
        cont++;

    cont += contarNaoPereciveis(no.getEsquerda());

    cont += contarNaoPereciveis(no.getDireita());

    return cont;
}

public Lista<V> produtosFolhas() {

    Lista<V> lista = new Lista<>();

    produtosFolhas(raiz, lista);

    return lista;
}

private void produtosFolhas(No<K,V> no, Lista<V> lista){

    if(no == null)
        return;

    produtosFolhas(no.getEsquerda(), lista);

    if(no.grau() == 0)
        lista.inserirFinal(no.getItem());

    produtosFolhas(no.getDireita(), lista);
}

public Lista<V> produtosInternos(){

    Lista<V> lista = new Lista<>();

    produtosInternos(raiz, lista);

    return lista;
}

private void produtosInternos(No<K,V> no, Lista<V> lista){

    if(no == null)
        return;

    produtosInternos(no.getEsquerda(), lista);

    if(no.grau() > 0)
        lista.inserirFinal(no.getItem());

    produtosInternos(no.getDireita(), lista);
}

public Lista<V> produtosGrauDois(){

    Lista<V> lista = new Lista<>();

    produtosGrauDois(raiz, lista);

    return lista;
}

private void produtosGrauDois(No<K,V> no, Lista<V> lista){

    if(no == null)
        return;

    produtosGrauDois(no.getEsquerda(), lista);

    if(no.grau() == 2)
        lista.inserirFinal(no.getItem());

    produtosGrauDois(no.getDireita(), lista);
}

public int quantidadeFolhas(){

    return quantidadeFolhas(raiz);
}

private int quantidadeFolhas(No<K,V> no){

    if(no == null)
        return 0;

    if(no.grau() == 0)
        return 1;

    return quantidadeFolhas(no.getEsquerda()) +
           quantidadeFolhas(no.getDireita());
}

public int quantidadeInternos(){

    return quantidadeInternos(raiz);
}

private int quantidadeInternos(No<K,V> no){

    if(no == null)
        return 0;

    int cont = 0;

    if(no.grau() > 0)
        cont++;

    cont += quantidadeInternos(no.getEsquerda());
    cont += quantidadeInternos(no.getDireita());

    return cont;
}

public Lista<V> produtosNivel(int nivel){

    Lista<V> lista = new Lista<>();

    produtosNivel(raiz,0,nivel,lista);

    return lista;
}

private void produtosNivel(No<K,V> no,
                           int atual,
                           int nivel,
                           Lista<V> lista){

    if(no == null)
        return;

    if(atual == nivel)
        lista.inserirFinal(no.getItem());

    produtosNivel(no.getEsquerda(),
                  atual+1,
                  nivel,
                  lista);

    produtosNivel(no.getDireita(),
                  atual+1,
                  nivel,
                  lista);
}

public int quantidadeNivel(int nivel){

    return quantidadeNivel(raiz,0,nivel);
}

private int quantidadeNivel(No<K,V> no,
                            int atual,
                            int nivel){

    if(no == null)
        return 0;

    int cont = 0;

    if(atual == nivel)
        cont++;

    cont += quantidadeNivel(no.getEsquerda(),
                            atual+1,
                            nivel);

    cont += quantidadeNivel(no.getDireita(),
                            atual+1,
                            nivel);

    return cont;
}

public Lista<V> produtosNivelPar(){

    Lista<V> lista = new Lista<>();

    produtosNivelPar(raiz,0,lista);

    return lista;
}

private void produtosNivelPar(No<K,V> no,
                              int nivel,
                              Lista<V> lista){

    if(no == null)
        return;

    if(nivel % 2 == 0)
        lista.inserirFinal(no.getItem());

    produtosNivelPar(no.getEsquerda(),
                     nivel+1,
                     lista);

    produtosNivelPar(no.getDireita(),
                     nivel+1,
                     lista);
}

public Lista<V> produtosNivelImpar(){

    Lista<V> lista = new Lista<>();

    produtosNivelImpar(raiz,0,lista);

    return lista;
}

private void produtosNivelImpar(No<K,V> no,
                                int nivel,
                                Lista<V> lista){

    if(no == null)
        return;

    if(nivel % 2 != 0)
        lista.inserirFinal(no.getItem());

    produtosNivelImpar(no.getEsquerda(),
                       nivel+1,
                       lista);

    produtosNivelImpar(no.getDireita(),
                       nivel+1,
                       lista);
}

public V produtoMaisProfundo(){

    return produtoMaisProfundo(raiz,0).produto;
}

private class Resultado{

    V produto;
    int nivel;

    Resultado(V produto,int nivel){
        this.produto = produto;
        this.nivel = nivel;
    }
}

private Resultado produtoMaisProfundo(No<K,V> no,int nivel){

    if(no == null)
        return new Resultado(null,-1);

    Resultado melhor = new Resultado(no.getItem(),nivel);

    Resultado esq = produtoMaisProfundo(no.getEsquerda(),nivel+1);

    Resultado dir = produtoMaisProfundo(no.getDireita(),nivel+1);

    if(esq.nivel > melhor.nivel)
        melhor = esq;

    if(dir.nivel > melhor.nivel)
        melhor = dir;

    return melhor;
}
}
