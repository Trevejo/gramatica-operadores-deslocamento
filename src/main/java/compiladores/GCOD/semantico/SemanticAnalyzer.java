package compiladores.GCOD.semantico;

import compiladores.GCOD.parser.Token;
import compiladores.GCOD.parser.ast.*;
import compiladores.GCOD.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {
    public static final String TYPE_INT = "int";
    public static final String TYPE_BOOL = "bool"; // For results of comparisons, conditions
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String TYPE_ERROR = "error_type";

    private SymbolTable symbolTable;
    private List<String> semanticErrors;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.semanticErrors = new ArrayList<>();
    }

    public List<String> getSemanticErrors() {
        return semanticErrors;
    }

    public void analyze(ASTNode node) {
        visit(node);
    }

    private void visit(ASTNode node) {
        if (node == null) return;

        if (node instanceof IdentifierNode) {
            visitIdentifier((IdentifierNode) node);
        } else if (node instanceof BinaryOperationNode) {
            visitBinaryOperation((BinaryOperationNode) node);
        } else if (node instanceof ParenthesizedExpressionNode) {
            visitParenthesizedExpression((ParenthesizedExpressionNode) node);
        }
        // Add more else if blocks for other ASTNode types as the grammar expands
        // e.g., DeclarationNode, AssignmentNode, IfStatementNode, etc.
    }

    private void addError(String message, int line, int column) {
        semanticErrors.add(String.format("Semantic Error (line %d, col %d): %s", line, column, message));
    }

    private void visitIdentifier(IdentifierNode node) {
        String varName = node.getName();
        Token token = node.getToken();

        Symbol symbol = symbolTable.lookup(varName); // Looks in current then global

        if (symbol == null) {
            // For this simple grammar, assume first encounter is a declaration of TYPE_INT.
            // In a real language, this would be an undeclared variable error if not on LHS of assignment or in a decl.
            // For now, we are auto-declaring it to allow expressions like "a + b" to be analyzable.
            // This fulfills "construção da tabela de símbolos" by adding it.
            // It also touches on "validação de declarações" by implicitly declaring.
            symbol = new Symbol(varName, TYPE_INT, symbolTable.getCurrentScope(), token.getLine(), token.getColumn());
            symbolTable.addSymbol(symbol);
            // For this example, we don't add an error for "undeclared", because we auto-declare.
            // If the language had explicit declarations, here we'd error: addError("Variable '" + varName + "' not declared.", token.getLine(), token.getColumn());
            
            // Annotate AST node
            node.setResolvedType(TYPE_INT); // Defaulting to int for this simple expression grammar
            node.setSymbol(symbol);
            System.out.println("Implicitly declared identifier '" + varName + "' as " + TYPE_INT + " in scope '" + symbol.getScope() + "' at line " + token.getLine());
        } else {
            // Variable already in symbol table (either from previous implicit or explicit declaration)
            node.setResolvedType(symbol.getType());
            node.setSymbol(symbol);
            System.out.println("Identifier '" + varName + "' found in symbol table. Type: " + symbol.getType() + ", Scope: " + symbol.getScope());
        }
    }

    private void visitBinaryOperation(BinaryOperationNode node) {
        visit(node.getLeft());
        visit(node.getRight());

        String leftType = node.getLeft().getResolvedType();
        String rightType = node.getRight().getResolvedType();
        Token operatorToken = node.getOperatorToken();
        String operator = operatorToken.getValue();
        int line = operatorToken.getLine();
        int col = operatorToken.getColumn();

        // Default to error type if operands have errors
        if (TYPE_ERROR.equals(leftType) || TYPE_ERROR.equals(rightType)) {
            node.setResolvedType(TYPE_ERROR);
            return;
        }
        
        if (TYPE_UNKNOWN.equals(leftType) || TYPE_UNKNOWN.equals(rightType)) {
            addError("Cannot perform operation '" + operator + "' on operands with unknown types ('" + leftType + "', '" + rightType + "').", line, col);
            node.setResolvedType(TYPE_ERROR);
            return;
        }

        switch (operator) {
            case "+":
            case "-":
            case "<<":
            case ">>":
                if (TYPE_INT.equals(leftType) && TYPE_INT.equals(rightType)) {
                    node.setResolvedType(TYPE_INT);
                } else {
                    addError("Type mismatch for operator '" + operator + ". Expected two integers, but got " + leftType + " and " + rightType + ".", line, col);
                    node.setResolvedType(TYPE_ERROR);
                }
                break;
            default:
                addError("Unsupported operator: " + operator, line, col);
                node.setResolvedType(TYPE_ERROR);
                break;
        }
        System.out.println("BinaryOp '" + operator + "' [L"+line+",C"+col+"] with types " + leftType + ", " + rightType + " -> result type: " + node.getResolvedType());
    }

    private void visitParenthesizedExpression(ParenthesizedExpressionNode node) {
        visit(node.getExpression());
        node.setResolvedType(node.getExpression().getResolvedType());
        System.out.println("ParenthesizedExpression -> result type: " + node.getResolvedType());
    }

    // Placeholder for integrating with a ParserService or similar
    public void analyzeCode(String code) {
        Parser parser = new Parser(code);
        ASTNode astRoot = parser.parse();
        String parseErrorsStr = parser.getErrors();

        if (parseErrorsStr != null && !parseErrorsStr.isEmpty()) {
            semanticErrors.add("Parser Errors:\n" + parseErrorsStr);
            // Optionally, don't proceed with semantic analysis if parsing failed badly
            // return;
        }

        if (astRoot != null) {
            System.out.println("AST Before Semantic Analysis:\n" + astRoot.toTreeString());
            analyze(astRoot);
            System.out.println("\nAST After Semantic Analysis:\n" + astRoot.toTreeString());
            System.out.println("\nSymbol Table:\n" + symbolTable.toString());
        } else if (parseErrorsStr == null || parseErrorsStr.isEmpty()){
            semanticErrors.add("Parser did not produce an AST and reported no errors.");
        }

        if (!semanticErrors.isEmpty()) {
            System.out.println("\nSemantic Errors Found:");
            for (String error : semanticErrors) {
                System.out.println(error);
            }
        } else {
            System.out.println("\nNo semantic errors found.");
        }
    }
} 