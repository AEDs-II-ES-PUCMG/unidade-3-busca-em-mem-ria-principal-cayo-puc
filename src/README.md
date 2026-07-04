# Construtor `AVL(AVL<?, V> original, Function<V, K> funcaoChave)`

Este arquivo explica o construtor de `AVL` usado em `App.main()`, na linha:

```java
produtosCadastradosPorId = new AVL<Integer, Produto>(produtosCadastradosPorNome, (p -> p.idProduto));
```

## O que esse construtor faz

Ele **não lê nenhum arquivo e não recebe itens novos**. Ele recebe uma árvore AVL já existente
(`original`) e cria uma **segunda árvore, com os mesmos itens**, mas indexada por uma chave
diferente, extraída de cada item por meio da função `funcaoChave`.

Ou seja: se você já tem uma árvore com todos os itens organizados por uma chave A (por exemplo,
descrição do produto), e precisa também conseguir buscar esses **mesmos itens** por uma chave B
(por exemplo, o id do produto), você **não** precisa reler o arquivo de dados nem reconstruir os
objetos do zero. Basta pedir para o construtor percorrer a árvore original e reinserir cada item
na nova árvore, usando a chave B.

Internamente, o construtor:
1. Percorre a árvore `original` em ordem (menor para maior, segundo a chave antiga).
2. Para cada item encontrado, aplica `funcaoChave` para obter a nova chave.
3. Insere o par (nova chave, item) na nova árvore.

A árvore `original` **não é alterada** — o construtor cria uma árvore totalmente nova.

## Assinatura

```java
public AVL(AVL<?, V> original, Function<V, K> funcaoChave)
```

- `V`: o tipo dos itens armazenados (o mesmo em ambas as árvores — os itens não mudam, só a chave).
- `K`: o tipo da **nova** chave, extraída pela função.
- `original`: a árvore já existente, cujos itens serão copiados.
- `funcaoChave`: uma função (geralmente uma expressão lambda) que, dado um item `V`, devolve a
  nova chave `K` para esse item.

## Cuidados importantes

- **As novas chaves precisam ser únicas.** Se `funcaoChave` gerar a mesma chave para dois itens
  diferentes da árvore original, a inserção do segundo item lança `IllegalArgumentException`
  ("O item já foi inserido anteriormente na árvore"). Antes de usar esse construtor, garanta que
  o atributo escolhido como nova chave realmente identifica cada item de forma única.
- **O tipo `K` precisa ter uma ordem natural** (implementar `Comparable`), pois a nova árvore usa
  `Comparator.naturalOrder()` internamente. Tipos como `Integer`, `String` e `Double` já servem
  para isso sem nenhum trabalho extra.

## Exemplos de uso

### Exemplo 1 — já usado no projeto (produtos: descrição → id)

```java
// produtosCadastradosPorNome já existe: AVL<String, Produto>, indexada pela descrição do produto.
AVL<Integer, Produto> produtosCadastradosPorId =
        new AVL<Integer, Produto>(produtosCadastradosPorNome, (p -> p.idProduto));
```

Aqui, `p.idProduto` é único para cada produto, então a nova árvore é construída sem problemas.
Agora é possível buscar o mesmo produto tanto pelo nome (`produtosCadastradosPorNome`) quanto
pelo id (`produtosCadastradosPorId`), sem duplicar a leitura do arquivo `produtos.txt`.

### Exemplo 2 — hipotético (clientes: documento → nome)

Suponha que você já tenha `clientesPorId` (`AVL<Integer, Cliente>`, indexada pelo documento) e
quisesse também uma árvore indexada pelo nome do cliente:

```java
AVL<String, Cliente> clientesPorNome = new AVL<String, Cliente>(clientesPorId, (c -> c.getNome()));
```

**Atenção:** esse exemplo só funciona se não houver dois clientes com exatamente o mesmo nome no
arquivo `clientes.txt`. Como nomes podem se repetir (duas pessoas podem se chamar igual), essa
reindexação pode lançar `IllegalArgumentException` em tempo de execução. Esse é exatamente o tipo
de cuidado que você deve ter antes de escolher uma chave para reindexar uma árvore.

### Exemplo 3 — genérico (didático)

```java
class Aluno {
    int matricula;
    String nome;
}

// alunosPorMatricula: AVL<Integer, Aluno>, já populada.
AVL<String, Aluno> alunosPorNome = new AVL<String, Aluno>(alunosPorMatricula, (a -> a.nome));
```

Se cada aluno tiver um nome distinto, essa reindexação funciona da mesma forma: percorre
`alunosPorMatricula` em ordem, extrai o nome de cada aluno e o insere na nova árvore.
