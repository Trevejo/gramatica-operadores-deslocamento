package compiladores.GCOD.parser.ast;

import compiladores.GCOD.parser.Token;

public class BinaryOperationNode extends ExpressionNode {
    private final ExpressionNode left;
    private final Token operatorToken;
    private final ExpressionNode right;

    public BinaryOperationNode(ExpressionNode left, Token operatorToken, ExpressionNode right) {
        this.left = left;
        this.operatorToken = operatorToken;
        this.right = right;
    }

    public ExpressionNode getLeft() {
        return left;
    }

    public Token getOperatorToken() {
        return operatorToken;
    }

    public String getOperator() {
        return operatorToken.getValue();
    }

    public ExpressionNode getRight() {
        return right;
    }

    @Override
    public String toTreeString(String indent) {
        StringBuilder sb = new StringBuilder();
        String typeInfo = resolvedType != null ? " [type: " + resolvedType + "]" : "";
        sb.append(indent).append("BinaryOp(").append(getOperator()).append(")").append(typeInfo).append("\n");
        
        sb.append(left.toTreeString(indent + "  ├─ "));
        sb.append(right.toTreeString(indent + "  └─ "));
        
        return sb.toString();
    }
} 