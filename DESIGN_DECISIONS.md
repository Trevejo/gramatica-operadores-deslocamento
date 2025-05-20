# Design Decisions for Semantic Analyzer

This document outlines the key design decisions, assumptions, and implemented rules for the Semantic Analyzer component of the GCOD compiler.

## 1. Core Responsibilities

The Semantic Analyzer complements the existing syntactic parser by:
- Building and managing a Symbol Table.
- Performing Type Checking for expressions and operations.
- Validating Declarations (in a limited capacity due to current grammar).
- Managing Scopes (currently a single global scope).
- Annotating the Abstract Syntax Tree (AST) with semantic information (types).

## 2. Symbol Table (`Symbol.java`, `SymbolTable.java`)

- **`Symbol`**: Stores information for each identifier:
    - `name` (String): The identifier name.
    - `type` (String): The resolved type of the identifier (e.g., "int", "float").
    - `scope` (String): The scope in which the identifier is declared (currently "global").
    - `line` (int): Line number of declaration/first use.
    - `column` (int): Column number of declaration/first use.
- **`SymbolTable`**:
    - Uses a `Map<String, List<Symbol>>` to store symbols. This allows for potential future support of symbol overloading or different symbols with the same name in different scopes (though scope management is currently basic).
    - `currentScope`: Manages the current scope, defaulting to "global". `enterScope` and `exitScope` methods are present for future extension but currently only toggle between a named scope and "global".
    - `lookup(name, scope)`: Searches for a symbol first in the specified scope, then in the "global" scope if the specified scope is not "global".
    - `lookup(name)`: Convenience method to search in `currentScope` then "global".

## 3. Type System (`SemanticAnalyzer.java`)

- **Defined Types**:
    - `TYPE_INT ("int")`: Represents integer values.
    - `TYPE_FLOAT ("float")`: Represents floating-point values.
    - `TYPE_BOOL ("bool")`: Represents boolean values (intended for results of comparisons, though comparison operators are not yet in grammar).
    - `TYPE_UNKNOWN ("unknown")`: Represents an undetermined type.
    - `TYPE_ERROR ("error_type")`: Represents a type error encountered during analysis.

- **Implicit Declaration & Default Type**:
    - Due to the current parser's limitations (no explicit variable declaration syntax, no typed literals), an identifier is implicitly "declared" when first encountered during semantic analysis.
    - **Assumption**: Upon first encounter, an identifier is assigned `TYPE_INT`. This is a simplification to allow basic expression analysis. For robust float type usage, the grammar would need to be extended for float literals or explicit float declarations.

## 4. AST Node Annotations

- `ExpressionNode` (and its subclasses `IdentifierNode`, `BinaryOperationNode`, `ParenthesizedExpressionNode`):
    - `resolvedType` (String): Stores the type of the expression after semantic analysis.
- `IdentifierNode`:
    - `token` (Token): Stores the original token for access to line/column information.
    - `symbol` (Symbol): A reference to its entry in the symbol table.
- `BinaryOperationNode`:
    - `operatorToken` (Token): Stores the token for the operator (e.g., "+", "<<") for line/column information and its string value.

## 5. Operational Rules and Type Checking (`SemanticAnalyzer.visitBinaryOperation`)

Rules are applied based on the types of the left and right operands:

- **Operators: `+`, `-`**
    - `int, int -> int`
    - `float, float -> float`
    - `int, float -> float` (Result is promoted to float)
    - `float, int -> float` (Result is promoted to float)
    - Any other combination results in `TYPE_ERROR`.

- **Operators: `<<`, `>>` (Bitwise Shift)**
    - `int, int -> int`
    - Any other combination results in `TYPE_ERROR` (Floats or other types are not valid for bitwise shifts).

- **Error Handling**:
    - If an operand's type is `TYPE_ERROR` or `TYPE_UNKNOWN`, the operation generally results in `TYPE_ERROR`.
    - Specific error messages, including line and column numbers (from the operator token), are generated and collected.

## 6. Scope Management

- Currently, only a single "global" scope is effectively used because the language grammar (as parsed) lacks constructs like functions or blocks that would introduce new scopes.
- The `SymbolTable` has basic support for `enterScope` and `exitScope`, which can be enhanced if the grammar evolves.

## 7. Limitations & Future Extensions

- **Explicit Declarations**: The lack of explicit variable declarations (e.g., `var x: int;`) means "undeclared variable" errors are not truly caught; variables are declared on first use with a default type.
- **Type Literals**: The parser currently only identifies `id`. Support for `123` (int literal), `3.14` (float literal), `true`/`false` (boolean literals) would be needed for richer type inference and testing.
- **Assignments**: No assignment statement in the current grammar, so type checking of assignments (e.g., `x = y + z`) is not yet implemented.
- **Control Flow**: No `if`, `while`, etc., so type checking for conditions (expecting `TYPE_BOOL`) is not present.
- **Functions**: No function declarations or calls, limiting scope management and function-related semantic checks.
- **More Operators**: The system can be extended with more operators (e.g., `*`, `/`, `%`, comparison operators `==`, `!=`, `<`, `>`, logical `&&`, `||`).

## 8. Integration with Parser (`SemanticAnalyzer.analyzeCode`)

- The `SemanticAnalyzer` includes a utility method `analyzeCode(String code)` that:
    1. Invokes the `Parser`.
    2. If parsing is successful, traverses the AST to perform semantic analysis.
    3. Prints AST before and after analysis, the symbol table, and any semantic errors.
    4. Collects both parser and semantic errors. 