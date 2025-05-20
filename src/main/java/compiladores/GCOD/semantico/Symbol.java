package compiladores.GCOD.semantico;

public class Symbol {
    private String name;
    private String type;
    private String scope;
    private int line;
    private int column;

    public Symbol(String name, String type, String scope, int line, int column) {
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Symbol{" +
               "name='" + name + "'" +
               ", type='" + type + "'" +
               ", scope='" + scope + "'" +
               ", line=" + line +
               ", column=" + column +
               '}';
    }
} 