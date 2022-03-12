package symboltable;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableEntry {
    public String name; // name of the entry, e.g f1, f2, MyClass1
    public List<String> inherits;
    public Kind kind;

    public SymbolTableEntryType type;
    public SymbolTable link;

    // field only used for function
    public List<SymbolTableEntryType> funcInputType;
    public SymbolTableEntryType funcOutputType;

    public SymbolTableEntry(String name, Kind kind, SymbolTable link) {
        this.name = name;
        this.kind = kind;
        this.link = link;
        this.inherits = new ArrayList<>();
        this.funcInputType = new ArrayList<>();
    }
}
