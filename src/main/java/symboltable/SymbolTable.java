package symboltable;

import java.util.ArrayList;

public class SymbolTable {
    private String name;
    private ArrayList<SymbolTableEntry> entries;
    public SymbolTable upperTable;

    // code generation
    public int scopeSize = 0;

    public SymbolTable(String name, SymbolTable upperTable) {
        this.name = name;
        this.upperTable = upperTable;
        entries = new ArrayList<>();
    }

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

    public SymbolTableEntry getEntryByNameKind(String name, Kind kind) {
        for (SymbolTableEntry entry : this.entries) {
            if (entry.name.equals(name) && entry.kind == kind) {
                return entry;
            }
        }
        return null;
    }

    // return the first found symbol table entry index based on name and kind
    public int getEntryIndexByNameKind(String name, Kind kind) {
        for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i).name.equals(name) && this.entries.get(i).kind == kind) {
                return i;
            }
        }

        return -1;
    }

    // replace entry with specific index
    public void replaceEntry(int index, SymbolTableEntry newEntry) {
        this.entries.set(index, newEntry);
    }

    @Override
    public String toString() {
        String result = "Table: " + this.name + ", Object id: " + this.hashCode();
        if (this.upperTable != null) {
            result += ", Upper table: " + this.upperTable.hashCode();
        }
        result += "\n";
        result += "name,kind,type,link";
        result += "\n_________________________________________\n";
        for (SymbolTableEntry entry : this.entries) {
            result += entry.name + "," + entry.kind + ",";
            if (entry.type != null) {
                result += entry.type.name;
                if (entry.type.dimension.size() != 0) {
                    result += "[" + String.join("][", entry.type.dimension) + "]";
                }
            }
            if (entry.kind == Kind.function) {
                result += "(";
                for (SymbolTableEntryType input : entry.funcInputType) {
                    result += input.name;
                    if (input.dimension.size() != 0) {
                        result += "[" + String.join("][", input.dimension) + "]";
                    }
                    result += ",";
                }
                result += "):";
                result += entry.funcOutputType.name;
                if (entry.funcOutputType.dimension.size() != 0) {
                    result += "[" + String.join("][", entry.funcOutputType.dimension) + "]";
                }
            }
            if (entry.link != null) {
                result += "," + entry.link.hashCode();
            }
            result += "\n_________________________________________\n";
        }

        return result;
    }
}
