package compiladores.GCOD.parser.ast;

public class IdentifierNode extends ExpressionNode {
    private final String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toTreeString(String indent) {
        return indent + "ID(" + name + ")\n";
    }
} 