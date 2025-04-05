package compiladores.GCOD.parser.ast;

public interface ASTNode {
    String toTreeString();
    String toTreeString(String indent);
} 