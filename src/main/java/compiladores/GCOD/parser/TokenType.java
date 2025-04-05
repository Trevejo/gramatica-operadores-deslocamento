package compiladores.GCOD.parser;

public enum TokenType {
    // Literals
    ID,         // Identifier
    
    // Operators
    PLUS,       // +
    MINUS,      // -
    LEFT_SHIFT, // <<
    RIGHT_SHIFT,// >>
    
    // Punctuation
    LPAREN,     // (
    RPAREN,     // )
    
    // Special tokens
    EOF,        // End of file
    ERROR       // Invalid token
} 