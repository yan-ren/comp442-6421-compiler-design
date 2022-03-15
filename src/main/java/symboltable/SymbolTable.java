package symboltable;

import java.util.ArrayList;

public class SymbolTable {
    private String name;
    private ArrayList<SymbolTableEntry> entries;
    public SymbolTable upperTable;

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
