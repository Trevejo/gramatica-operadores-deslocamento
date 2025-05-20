package compiladores.GCOD.parser;

import compiladores.GCOD.parser.ast.*;

/**
 * Recursive descent parser for the grammar:
 * E → E << T | E >> T | T
 * T → T + F | T - F | F
 * F → ( E ) | id
 * 
 * Note: The grammar has been rewritten to eliminate left recursion:
 * E → T E'
 * E' → << T E' | >> T E' | ε
 * T → F T'
 * T' → + F T' | - F T' | ε
 * F → ( E ) | id
 */
public class Parser {
    private final Lexer lexer;
    private Token currentToken;
    private StringBuilder parseErrors;

    public Parser(String input) {
        this.lexer = new Lexer(input);
        this.currentToken = lexer.getCurrentToken();
        this.parseErrors = new StringBuilder();
    }

    public ExpressionNode parse() {
        try {
            ExpressionNode result = parseE();
            if (currentToken.getType() != TokenType.EOF) {
                addError("Expected end of input, but found: " + currentToken.getValue());
                return null;
            }
            return result;
        } catch (SyntaxError e) {
            addError(e.getMessage());
            return null;
        }
    }

    public String getErrors() {
        return parseErrors.toString();
    }

    private void addError(String message) {
        parseErrors.append(String.format("Error at line %d, column %d: %s\n", 
                currentToken.getLine(), currentToken.getColumn(), message));
    }

    private void consume(TokenType type) throws SyntaxError {
        if (currentToken.getType() == type) {
            lexer.nextToken();
            currentToken = lexer.getCurrentToken();
        } else {
            throw new SyntaxError(String.format("Expected '%s', found '%s'", 
                    type, currentToken.getValue()));
        }
    }

    // E → T E'
    private ExpressionNode parseE() throws SyntaxError {
        ExpressionNode left = parseT();
        if (left == null) return null;
        return parseEPrime(left);
    }

    // E' → << T E' | >> T E' | ε
    private ExpressionNode parseEPrime(ExpressionNode left) throws SyntaxError {
        if (left == null) return null;

        switch (currentToken.getType()) {
            case LEFT_SHIFT:
            case RIGHT_SHIFT:
                Token opToken = currentToken;
                consume(currentToken.getType());
                ExpressionNode right = parseT();
                if (right == null) return null;
                ExpressionNode newLeft = new BinaryOperationNode(left, opToken, right);
                return parseEPrime(newLeft);
            default:
                return left;
        }
    }

    // T → F T'
    private ExpressionNode parseT() throws SyntaxError {
        ExpressionNode left = parseF();
        if (left == null) return null;
        return parseTPrime(left);
    }

    // T' → + F T' | - F T' | ε
    private ExpressionNode parseTPrime(ExpressionNode left) throws SyntaxError {
        if (left == null) return null;

        switch (currentToken.getType()) {
            case PLUS:
            case MINUS:
                Token opToken = currentToken;
                consume(currentToken.getType());
                ExpressionNode right = parseF();
                if (right == null) return null;
                ExpressionNode newLeft = new BinaryOperationNode(left, opToken, right);
                return parseTPrime(newLeft);
            default:
                return left;
        }
    }

    // F → ( E ) | id
    private ExpressionNode parseF() throws SyntaxError {
        switch (currentToken.getType()) {
            case LPAREN:
                consume(TokenType.LPAREN);
                ExpressionNode expr = parseE();
                if (expr == null) return null;
                if (currentToken.getType() != TokenType.RPAREN) {
                    throw new SyntaxError("Expected ')', found: " + currentToken.getValue());
                }
                consume(TokenType.RPAREN);
                return new ParenthesizedExpressionNode(expr);
            
            case ID:
                Token idToken = currentToken;
                String id = currentToken.getValue();
                consume(TokenType.ID);
                return new IdentifierNode(id, idToken);
            
            case ERROR:
                throw new SyntaxError("Invalid token: " + currentToken.getValue());
            
            default:
                throw new SyntaxError("Expected '(' or identifier, found: " + currentToken.getValue());
        }
    }

    // Custom exception for syntax errors
    private static class SyntaxError extends Exception {
        public SyntaxError(String message) {
            super(message);
        }
    }
} 