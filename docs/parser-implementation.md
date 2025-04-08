# Implementação do Parser Recursivo Descendente

## 📋 Visão Geral

O parser recursivo descendente implementado neste projeto é responsável por analisar expressões matemáticas que incluem operadores de deslocamento (<< e >>). A implementação segue a abordagem top-down, onde a análise começa pelo símbolo inicial da gramática e tenta derivar a entrada através das regras de produção.

## 🔧 Estrutura do Parser

O parser é composto pelas seguintes classes principais:

1. **Parser**: Classe principal que coordena o processo de análise
2. **GrammarAnalyzer**: Responsável pelo cálculo dos conjuntos First e Follow
3. **Token**: Representa os tokens da linguagem
4. **SyntaxTree**: Estrutura que armazena a árvore sintática gerada

## 📝 Implementação dos Métodos

### Método E()

```java
private SyntaxTree E() {
    SyntaxTree tree = new SyntaxTree("E");
    
    // Tenta derivar E → E << T
    if (currentToken.isType("id") || currentToken.isType("(")) {
        tree.addChild(E());
        if (currentToken.isType("<<")) {
            tree.addChild(new SyntaxTree("<<"));
            advance();
            tree.addChild(T());
        }
        // Tenta derivar E → E >> T
        else if (currentToken.isType(">>")) {
            tree.addChild(new SyntaxTree(">>"));
            advance();
            tree.addChild(T());
        }
    }
    // Deriva E → T
    else {
        tree.addChild(T());
    }
    
    return tree;
}
```

### Método T()

```java
private SyntaxTree T() {
    SyntaxTree tree = new SyntaxTree("T");
    
    // Tenta derivar T → T + F
    if (currentToken.isType("id") || currentToken.isType("(")) {
        tree.addChild(T());
        if (currentToken.isType("+")) {
            tree.addChild(new SyntaxTree("+"));
            advance();
            tree.addChild(F());
        }
        // Tenta derivar T → T - F
        else if (currentToken.isType("-")) {
            tree.addChild(new SyntaxTree("-"));
            advance();
            tree.addChild(F());
        }
    }
    // Deriva T → F
    else {
        tree.addChild(F());
    }
    
    return tree;
}
```

### Método F()

```java
private SyntaxTree F() {
    SyntaxTree tree = new SyntaxTree("F");
    
    // Deriva F → ( E )
    if (currentToken.isType("(")) {
        tree.addChild(new SyntaxTree("("));
        advance();
        tree.addChild(E());
        if (!currentToken.isType(")")) {
            throw new ParseException("Esperado ')'");
        }
        tree.addChild(new SyntaxTree(")"));
        advance();
    }
    // Deriva F → id
    else if (currentToken.isType("id")) {
        tree.addChild(new SyntaxTree("id"));
        advance();
    }
    else {
        throw new ParseException("Token inesperado: " + currentToken);
    }
    
    return tree;
}
```

## 🔍 Tratamento de Erros

O parser implementa um sistema robusto de tratamento de erros que inclui:

1. **Verificação de Tokens Esperados**: Cada método verifica se o token atual é o esperado
2. **Mensagens de Erro Descritivas**: Fornece mensagens claras sobre o erro encontrado
3. **Recuperação de Erros**: Tenta continuar a análise após encontrar um erro

### Exemplo de Tratamento de Erro

```java
try {
    SyntaxTree tree = parser.parse(input);
    System.out.println("Análise bem-sucedida!");
    System.out.println(tree.toString());
} catch (ParseException e) {
    System.out.println("Erro de análise: " + e.getMessage());
}
```

## 📊 Geração da Árvore Sintática

A árvore sintática é gerada durante o processo de análise e representa a estrutura hierárquica da expressão. Cada nó da árvore contém:

1. O símbolo (terminal ou não-terminal)
2. Os filhos (subárvores)
3. Informações sobre a posição na expressão

### Exemplo de Árvore Sintática

Para a expressão `id + id << id`:

```
E
├── E
│   └── T
│       ├── T
│       │   └── F
│       │       └── id
│       ├── +
│       └── F
│           └── id
├── <<
└── T
    └── F
        └── id
```

## 🔄 Fluxo de Execução

1. O parser recebe uma string de entrada
2. O analisador léxico converte a string em uma sequência de tokens
3. O parser inicia a análise a partir do símbolo inicial E
4. A árvore sintática é construída durante a análise
5. Se a análise for bem-sucedida, a árvore é retornada
6. Se ocorrer um erro, uma exceção é lançada

## 📝 Observações

1. O parser implementa uma estratégia de backtracking para lidar com múltiplas produções
2. A análise é feita de forma recursiva, seguindo a estrutura da gramática
3. A implementação atual suporta:
   - Operadores de deslocamento (<< e >>)
   - Operadores aritméticos (+ e -)
   - Parênteses para agrupamento
   - Identificadores (id) 