package symboltable;

public class SymbolTableFuncEntry extends SymbolTableEntry {

    private SymbolTableEntryType funcInput;
    private SymbolTableEntryType funcOutput;

    public SymbolTableFuncEntry(String name, Kind kind, SymbolTableEntryType type) {
        super(name, kind, type);
    }

    public SymbolTableEntryType getFuncInput() {
        return this.funcInput;
    }

    public void setFuncInput(SymbolTableEntryType funcInput) {
        this.funcInput = funcInput;
    }

    public SymbolTableEntryType getFuncOutput() {
        return this.funcOutput;
    }

    public void setFuncOutput(SymbolTableEntryType funcOutput) {
        this.funcOutput = funcOutput;
    }
}
