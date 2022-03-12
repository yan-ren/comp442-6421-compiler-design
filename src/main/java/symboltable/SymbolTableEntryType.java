package symboltable;

import java.util.ArrayList;

public class SymbolTableEntryType {
    public String name;
    public ArrayList<String> dimension;

    public SymbolTableEntryType(String name) {
        this.name = name;
        dimension = new ArrayList<>();
    }
}
