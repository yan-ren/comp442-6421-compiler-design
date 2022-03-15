package symboltable;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableEntry {
    // name of the entry, e.g f1, f2, MyClass1
    public String name;
    // used for struct
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

    public boolean isSameFuncInputType(List<SymbolTableEntryType> funcInputType2) {
        if (funcInputType.size() != funcInputType2.size()) {
            return false;
        }

        return funcInputType.equals(funcInputType2);
    }
}
