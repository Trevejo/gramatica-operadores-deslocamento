# Conjuntos First e Follow

## 📋 Definição da Gramática

A gramática utilizada no projeto é:

```
E → E << T | E >> T | T
T → T + F | T - F | F
F → ( E ) | id
```

## 🔍 Conjunto First

O conjunto First de um símbolo X, denotado por First(X), é o conjunto de terminais que podem iniciar as strings derivadas de X.

### Cálculo do First

1. **First(F) = {'(', id}**
   - F pode derivar em (E) ou id

2. **First(T) = First(F) = {'(', id}**
   - T pode derivar em T + F, T - F ou F

3. **First(E) = First(T) = {'(', id}**
   - E pode derivar em E << T, E >> T ou T

## 🔍 Conjunto Follow

O conjunto Follow de um símbolo X, denotado por Follow(X), é o conjunto de terminais que podem aparecer imediatamente após X em alguma forma sentencial.

### Cálculo do Follow

1. **Follow(E) = {'<<', '>>', $, ')'}**
   - E é o símbolo inicial, então $ está em seu Follow
   - E aparece antes de ) na produção F → (E)
   - E pode ser seguido por << e >> nas produções E → E << T e E → E >> T

2. **Follow(T) = {$, ')', '<<', '>>', '+', '-'}**
   - T aparece antes de << e >> em E → E << T e E → E >> T
   - T aparece antes de + e - em T → T + F e T → T - F
   - T pode ser seguido por $ ou ) quando é o último símbolo

3. **Follow(F) = {$, ')', '<<', '>>', '+', '-'}**
   - F pode ser seguido pelos mesmos símbolos que T

## 📊 Tabela de Análise

| Símbolo | First        | Follow                    |
|---------|--------------|---------------------------|
| E       | {'(', id}    | {'<<', '>>', $, ')'}      |
| T       | {'(', id}    | {$, ')', '<<', '>>', '+', '-'} |
| F       | {'(', id}    | {$, ')', '<<', '>>', '+', '-'} |

## 🔄 Implementação

A implementação dos conjuntos First e Follow foi realizada na classe `GrammarAnalyzer`, que contém métodos para calcular e armazenar estes conjuntos. A análise é feita de forma automática durante a inicialização do parser.

### Exemplo de Uso

```java
GrammarAnalyzer analyzer = new GrammarAnalyzer();
Set<Token> firstSet = analyzer.getFirst(nonTerminal);
Set<Token> followSet = analyzer.getFollow(nonTerminal);
```

## 📝 Observações

1. A gramática é não-ambígua e não possui recursão à esquerda
2. Os conjuntos First e Follow são utilizados para:
   - Verificar se a gramática é LL(1)
   - Construir a tabela de análise sintática
   - Auxiliar na implementação do parser recursivo descendente
3. A implementação atual suporta a análise de expressões com operadores de deslocamento (<< e >>) 