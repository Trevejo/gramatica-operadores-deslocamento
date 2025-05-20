package compiladores.GCOD.parser;

import org.springframework.stereotype.Service;
import compiladores.GCOD.parser.ast.ExpressionNode;
import compiladores.GCOD.semantico.SemanticAnalyzer;
import compiladores.GCOD.semantico.SymbolTable;
import java.util.List;
import java.util.ArrayList;

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
        String parserErrors = parser.getErrors();
        
        String syntaxTree = null;
        String symbolTableStr = null;
        List<String> semanticErrors = new ArrayList<>();
        boolean analysisSuccess = false;

        if (ast != null && (parserErrors == null || parserErrors.isEmpty())) {
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            semanticAnalyzer.analyze(ast);
            
            syntaxTree = ast.toTreeString();
            symbolTableStr = semanticAnalyzer.getSymbolTable().toString();
            semanticErrors.addAll(semanticAnalyzer.getSemanticErrors());
            analysisSuccess = semanticErrors.isEmpty();
        } else {
            if (ast != null) {
                 syntaxTree = ast.toTreeString();
            }
        }
        
        boolean overallSuccess = (parserErrors == null || parserErrors.isEmpty()) && ast != null && analysisSuccess;
        
        List<String> allErrors = new ArrayList<>();
        if (parserErrors != null && !parserErrors.isEmpty()) {
            allErrors.add("Parser Errors:\n" + parserErrors);
        }
        if (!semanticErrors.isEmpty()) {
            allErrors.add("Semantic Errors:"); 
            for(String err : semanticErrors){
                allErrors.add(err);
            }
        }

        return new ParserResult(overallSuccess, syntaxTree, String.join("\n", allErrors), symbolTableStr);
    }
    
    /**
     * A class to hold the results of parsing an input.
     */
    public static class ParserResult {
        private final boolean success;
        private final String syntaxTree;
        private final String errors;
        private final String symbolTable;
        
        public ParserResult(boolean success, String syntaxTree, String errors, String symbolTable) {
            this.success = success;
            this.syntaxTree = syntaxTree;
            this.errors = errors;
            this.symbolTable = symbolTable;
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

        public String getSymbolTable() {
            return symbolTable;
        }
    }
} 