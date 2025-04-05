package compiladores.GCOD.parser;

import org.springframework.stereotype.Service;
import compiladores.GCOD.parser.ast.ExpressionNode;

@Service
public class ParserService {
    
    /**
     * Parse the input string and return the syntax tree.
     * 
     * @param input The input string to parse
     * @return The result of the parsing operation
     */
    public ParserResult parse(String input) {
        Parser parser = new Parser(input);
        ExpressionNode ast = parser.parse();
        
        String syntaxTree = null;
        if (ast != null) {
            syntaxTree = ast.toTreeString();
        }
        
        String errors = parser.getErrors();
        boolean success = errors.isEmpty() && ast != null;
        
        return new ParserResult(success, syntaxTree, errors);
    }
    
    /**
     * A class to hold the results of parsing an input.
     */
    public static class ParserResult {
        private final boolean success;
        private final String syntaxTree;
        private final String errors;
        
        public ParserResult(boolean success, String syntaxTree, String errors) {
            this.success = success;
            this.syntaxTree = syntaxTree;
            this.errors = errors;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getSyntaxTree() {
            return syntaxTree;
        }
        
        public String getErrors() {
            return errors;
        }
    }
} 