package compiladores.GCOD.semantico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private Map<String, List<Symbol>> table;
    private String currentScope;

    public SymbolTable() {
        this.table = new HashMap<>();
        this.currentScope = "global"; // Default scope
    }

    public void enterScope(String scopeName) {
        this.currentScope = scopeName;
    }

    public void exitScope() {
        // For now, let's assume exiting a scope means returning to global.
        // More sophisticated scope management (e.g., nested scopes) can be added.
        this.currentScope = "global"; 
    }

    public String getCurrentScope() {
        return currentScope;
    }

    public void addSymbol(Symbol symbol) {
        table.computeIfAbsent(symbol.getName(), k -> new ArrayList<>()).add(symbol);
    }

    public Symbol lookup(String name, String scope) {
        List<Symbol> symbols = table.get(name);
        if (symbols != null) {
            for (Symbol sym : symbols) {
                if (sym.getScope().equals(scope)) {
                    return sym;
                }
            }
            // If not found in current scope, check global scope (if not already global)
            if (!scope.equals("global")) {
                 for (Symbol sym : symbols) {
                    if (sym.getScope().equals("global")) {
                        return sym;
                    }
                }
            }
        }
        return null;
    }

    // Lookup in current scope, then global
    public Symbol lookup(String name) {
        return lookup(name, this.currentScope);
    }

    public boolean isDeclared(String name, String scope) {
        return lookup(name, scope) != null;
    }

    public boolean isDeclaredInCurrentScope(String name) {
        List<Symbol> symbols = table.get(name);
        if (symbols != null) {
            for (Symbol sym : symbols) {
                if (sym.getScope().equals(this.currentScope)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("SymbolTable:\n");
        for (Map.Entry<String, List<Symbol>> entry : table.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(":\n");
            for (Symbol symbol : entry.getValue()) {
                sb.append("    ").append(symbol).append("\n");
            }
        }
        return sb.toString();
    }
} 