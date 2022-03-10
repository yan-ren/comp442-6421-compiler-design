package symboltable;

public class SymbolTableEntry {
    private String name; // name of the entry, e.g f1, f2, MyClass1
    private Kind kind;
    private SymbolTableEntryType type;
    public SymbolTable link;

    public SymbolTableEntry(String name, Kind kind, SymbolTableEntryType type) {
        this.name = name;
        this.kind = kind;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Kind getKind() {
        return this.kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public SymbolTableEntryType getType() {
        return this.type;
    }

    public void setType(SymbolTableEntryType type) {
        this.type = type;
    }
}