package symboltable;

import java.util.ArrayList;

public class SymbolTable {
    private String name;
    private ArrayList<SymbolTableEntry> entries;
    public SymbolTable upperTable;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SymbolTableEntry> getEntries() {
        return this.entries;
    }

    public void setEntries(ArrayList<SymbolTableEntry> entries) {
        this.entries = entries;
    }

    public void addEntry(SymbolTableEntry entry) {
        this.entries.add(entry);
    }
}
