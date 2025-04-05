package compiladores.GCOD.parser.ast;

public class BinaryOperationNode extends ExpressionNode {
    private final ExpressionNode left;
    private final String operator;
    private final ExpressionNode right;

    public BinaryOperationNode(ExpressionNode left, String operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public ExpressionNode getRight() {
        return right;
    }

    @Override
    public String toTreeString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("BinaryOp(").append(operator).append(")\n");
        
        // Left child with increased indentation
        sb.append(left.toTreeString(indent + "  ├─ "));
        
        // Right child with increased indentation
        sb.append(right.toTreeString(indent + "  └─ "));
        
        return sb.toString();
    }
} 