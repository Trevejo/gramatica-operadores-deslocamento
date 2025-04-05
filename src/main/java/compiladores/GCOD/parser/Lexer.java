package compiladores.GCOD.parser;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int position;
    private int line;
    private int column;
    private Token currentToken;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
        nextToken(); // Initialize with the first token
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public void nextToken() {
        skipWhitespace();

        if (position >= input.length()) {
            currentToken = new Token(TokenType.EOF, "", line, column);
            return;
        }

        char c = input.charAt(position);

        switch (c) {
            case '(':
                currentToken = new Token(TokenType.LPAREN, "(", line, column);
                advance();
                break;
            case ')':
                currentToken = new Token(TokenType.RPAREN, ")", line, column);
                advance();
                break;
            case '+':
                currentToken = new Token(TokenType.PLUS, "+", line, column);
                advance();
                break;
            case '-':
                currentToken = new Token(TokenType.MINUS, "-", line, column);
                advance();
                break;
            case '<':
                if (peek(1) == '<') {
                    currentToken = new Token(TokenType.LEFT_SHIFT, "<<", line, column);
                    advance();
                    advance();
                } else {
                    currentToken = new Token(TokenType.ERROR, String.valueOf(c), line, column);
                    advance();
                }
                break;
            case '>':
                if (peek(1) == '>') {
                    currentToken = new Token(TokenType.RIGHT_SHIFT, ">>", line, column);
                    advance();
                    advance();
                } else {
                    currentToken = new Token(TokenType.ERROR, String.valueOf(c), line, column);
                    advance();
                }
                break;
            default:
                if (Character.isLetter(c)) {
                    int startPos = position;
                    int startCol = column;
                    
                    while (position < input.length() && 
                          (Character.isLetterOrDigit(input.charAt(position)) || 
                           input.charAt(position) == '_')) {
                        advance();
                    }
                    
                    String identifier = input.substring(startPos, position);
                    currentToken = new Token(TokenType.ID, identifier, line, startCol);
                } else {
                    currentToken = new Token(TokenType.ERROR, String.valueOf(c), line, column);
                    advance();
                }
                break;
        }
    }

    private void skipWhitespace() {
        while (position < input.length()) {
            char c = input.charAt(position);
            if (c == ' ' || c == '\t') {
                advance();
            } else if (c == '\n') {
                advance();
                line++;
                column = 1;
            } else {
                break;
            }
        }
    }

    private void advance() {
        position++;
        column++;
    }

    private char peek(int offset) {
        int pos = position + offset;
        if (pos >= input.length()) {
            return '\0';
        }
        return input.charAt(pos);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        // Reset position to start tokenizing from the beginning
        position = 0;
        line = 1;
        column = 1;
        nextToken();
        
        while (currentToken.getType() != TokenType.EOF) {
            tokens.add(currentToken);
            nextToken();
        }
        tokens.add(currentToken); // Add EOF token
        
        return tokens;
    }
} 