package compiladores.GCOD.parser.ast;

public abstract class ExpressionNode implements ASTNode {
    @Override
    public String toTreeString() {
        return toTreeString("");
    }
} 