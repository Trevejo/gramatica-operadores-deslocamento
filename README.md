# GCOD - Gramática com Operadores de Deslocamento

Um analisador gramatical e parser recursivo descendente baseado em web, implementado em Java com Spring Boot.

## 🚀 Funcionalidades

- **Interface Web Interativa**: Interface moderna com tema escuro para fácil análise gramatical
- **Parser Recursivo Descendente**: Implementa um robusto analisador de expressões
- **Geração de Árvore Sintática em Tempo Real**: Representação visual das expressões analisadas
- **Tratamento de Erros**: Relatórios detalhados para expressões inválidas
- **Design Responsivo**: Funciona perfeitamente em dispositivos desktop e móveis

## 📋 Definição da Gramática

O parser implementa a seguinte gramática:

```
E → E << T | E >> T | T
T → T + F | T - F | F
F → ( E ) | id
```

## 🛠️ Stack Técnico

- **Backend**: Java com Spring Boot
- **Frontend**: Templates Thymeleaf com CSS moderno
- **Estilização**: Tema escuro personalizado com design responsivo
- **Ferramenta de Build**: Maven

## 🎨 Recursos da Interface

- Interface em modo escuro para reduzir o cansaço visual
- Destaque de sintaxe para melhor legibilidade
- Formulário interativo com feedback em tempo real
- Mensagens de erro claras e indicadores de sucesso
- Layout responsivo que funciona em todos os dispositivos

## 📦 Instalação

1. Clone o repositório:
```bash
git clone https://github.com/Trevejo/gramatica-operadores-deslocamento.git
```

2. Navegue até o diretório do projeto:
```bash
cd gramatica-operadores-deslocamento
```

3. Construa o projeto usando Maven:
```bash
mvn clean install
```

4. Execute a aplicação:
```bash
mvn spring-boot:run
```

5. Abra seu navegador e acesse:
```
http://localhost:8080
```

## 💻 Como Usar

1. Acesse a interface do parser através da aplicação web
2. Digite uma expressão para analisar (ex: `id + id << id`)
3. Clique no botão "Parse" para analisar a expressão
4. Visualize os resultados:
   - Sucesso: Veja a árvore sintática gerada
   - Erro: Revise as mensagens de erro detalhadas

## 📝 Exemplos de Expressões

Expressões válidas:
- `id + id`
- `id << id`
- `(id + id) << id`
- `id >> id - id`

## 🧪 Testes

A aplicação inclui testes abrangentes para:
- Expressões válidas
- Expressões inválidas
- Casos de borda
- Tratamento de erros

## 📚 Documentação

- [Conjuntos First e Follow](docs/first-follow.md)
- [Implementação do Parser](docs/parser-implementation.md)
- [Metodologia de Testes](docs/testing-methodology.md)

## 👥 Autores

- Fábio Gomes Celestino
- Murilo Trevejo Santos

