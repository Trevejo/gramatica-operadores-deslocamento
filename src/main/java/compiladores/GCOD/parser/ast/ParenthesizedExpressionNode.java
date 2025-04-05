package compiladores.GCOD.parser.ast;

public class ParenthesizedExpressionNode extends ExpressionNode {
    private final ExpressionNode expression;

    public ParenthesizedExpressionNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    @Override
    public String toTreeString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Parenthesized\n");
        sb.append(expression.toTreeString(indent + "  └─ "));
        return sb.toString();
    }
} 