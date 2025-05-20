package compiladores.GCOD.semantico;

import compiladores.GCOD.parser.Token;
import compiladores.GCOD.parser.ast.*;
import compiladores.GCOD.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {
    public static final String TYPE_INT = "int";
    public static final String TYPE_FLOAT = "float";
    public static final String TYPE_BOOL = "bool";
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

    public SymbolTable getSymbolTable() {
        return symbolTable;
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
    }

    private void addError(String message, int line, int column) {
        semanticErrors.add(String.format("Semantic Error (line %d, col %d): %s", line, column, message));
    }

    private void visitIdentifier(IdentifierNode node) {
        String varName = node.getName();
        Token token = node.getToken();
        Symbol symbol = symbolTable.lookup(varName);

        if (symbol == null) {
            // For this simple grammar, assume first encounter is a declaration of TYPE_INT.
            symbol = new Symbol(varName, TYPE_INT, symbolTable.getCurrentScope(), token.getLine(), token.getColumn());
            symbolTable.addSymbol(symbol);
            
            node.setResolvedType(TYPE_INT);
            node.setSymbol(symbol);
            System.out.println("Implicitly declared identifier '" + varName + "' as " + TYPE_INT + " in scope '" + symbol.getScope() + "' at line " + token.getLine());
        } else {
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
                if (TYPE_INT.equals(leftType) && TYPE_INT.equals(rightType)) {
                    node.setResolvedType(TYPE_INT);
                } else if (TYPE_FLOAT.equals(leftType) && TYPE_FLOAT.equals(rightType)) {
                    node.setResolvedType(TYPE_FLOAT);
                } else if ((TYPE_INT.equals(leftType) && TYPE_FLOAT.equals(rightType)) || (TYPE_FLOAT.equals(leftType) && TYPE_INT.equals(rightType))) {
                    node.setResolvedType(TYPE_FLOAT);
                } else {
                    addError("Type mismatch for operator '" + operator + ". Expected int/int, float/float, or int/float, but got " + leftType + " and " + rightType + ".", line, col);
                    node.setResolvedType(TYPE_ERROR);
                }
                break;
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
        if (!TYPE_ERROR.equals(node.getResolvedType())) {
            System.out.println("BinaryOp '" + operator + "' [L"+line+",C"+col+"] with types (" + leftType + ", " + rightType + ") -> result type: " + node.getResolvedType());
        }
    }

    private void visitParenthesizedExpression(ParenthesizedExpressionNode node) {
        visit(node.getExpression());
        node.setResolvedType(node.getExpression().getResolvedType());
        System.out.println("ParenthesizedExpression -> result type: " + node.getResolvedType());
    }

    public void analyzeCode(String code) {
        Parser parser = new Parser(code);
        ASTNode astRoot = parser.parse();
        String parseErrorsStr = parser.getErrors();

        if (parseErrorsStr != null && !parseErrorsStr.isEmpty()) {
            semanticErrors.add("Parser Errors:\n" + parseErrorsStr);
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