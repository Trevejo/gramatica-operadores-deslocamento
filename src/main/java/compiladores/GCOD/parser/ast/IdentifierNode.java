package compiladores.GCOD.parser.ast;

import compiladores.GCOD.parser.Token;
import compiladores.GCOD.semantico.Symbol;

public class IdentifierNode extends ExpressionNode {
    private final String name;
    private Token token;
    private Symbol symbol;

    public IdentifierNode(String name, Token token) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public Token getToken() {
        return token;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toTreeString(String indent) {
        String typeInfo = resolvedType != null ? " [type: " + resolvedType + "]" : "";
        String symInfo = symbol != null ? " (sym: " + symbol.getName() + ")" : "";
        return indent + "ID(" + name + ")" + typeInfo + symInfo + "\n";
    }
} 