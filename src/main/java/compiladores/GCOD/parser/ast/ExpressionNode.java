package compiladores.GCOD.parser.ast;

public abstract class ExpressionNode implements ASTNode {
    protected String resolvedType;

    @Override
    public String toTreeString() {
        return toTreeString("");
    }

    public String getResolvedType() {
        return resolvedType;
    }

    public void setResolvedType(String resolvedType) {
        this.resolvedType = resolvedType;
    }
} 