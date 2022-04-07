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

    public void appendEntry(SymbolTableEntry entry) {
        this.entries.add(entry);
    }

    public void addAtBeginning(SymbolTableEntry entry) {
        this.entries.add(0, entry);
    }

    public SymbolTableEntry getEntryByName(String name) {
        for (SymbolTableEntry entry : this.entries) {
            if (entry.name.equals(name)) {
                return entry;
            }
        }
        return null;
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
        // "Table: " + this.name + ", scope size: " + this.scopeSize + ", table id: " +
        // this.hashCode();
        String result = String.format("%1$-10s| %2$-10s| %3$-10s| ", "Table: " + this.name,
                "scope size: " + this.scopeSize,
                "table id: " + this.hashCode());
        if (this.upperTable != null) {
            result += String.format("Upper table: %1$-10s", this.upperTable.hashCode());
        }
        result += "\n";
        //
        result += String.format("%1$-10s| %2$-10s| %3$-30s| %4$-10s| %5$-10s| %6$-10s", "name", "kind", "type", "link",
                "size", "offset");
        result += "\n___________________________________________________________________________________\n";
        for (SymbolTableEntry entry : this.entries) {
            // result += entry.name + "," + entry.kind + ",";
            result += String.format("%1$-10s| %2$-10s| ", entry.name, entry.kind);
            if (entry.type != null) {
                String tmp = entry.type.name;
                if (entry.type.dimension.size() != 0) {
                    // result += String.format("%1$-10s| ", "[" + String.join("][",
                    // entry.type.dimension) + "]");
                    tmp += "[" + String.join("][", entry.type.dimension) + "]";
                }
                result += String.format("%1$-30s| ", tmp);
            } else if (entry.kind == Kind.function) {
                String tmp = "(";
                // result += "(";
                for (SymbolTableEntryType input : entry.funcInputType) {
                    // result += input.name;
                    tmp += input.name;
                    if (input.dimension.size() != 0) {
                        tmp += "[" + String.join("][", input.dimension) + "]";
                    }
                    tmp += ",";
                }
                tmp += "):";
                tmp += entry.funcOutputType.name;
                if (entry.funcOutputType.dimension.size() != 0) {
                    tmp += "[" + String.join("][", entry.funcOutputType.dimension) + "]";
                }

                result += String.format("%1$-30s| ", tmp);
            } else {
                result += String.format("%1$-30s| ", "");
            }

            if (entry.link != null) {
                result += String.format("%1$-10s| ", entry.link.hashCode());
            } else {
                result += String.format("%1$-10s| ", "");
            }

            result += String.format("%1$-10s| ", entry.size);
            result += String.format("%1$-10s| ", entry.offset);
            result += "\n___________________________________________________________________________________\n";
        }

        return result;
    }

    /**
     * From input table look for entry with given name and kind in table and all
     * it's parent table
     * 
     * @param table
     * @param name
     * @param kind
     * @return
     */
    public static SymbolTableEntry lookupEntryInTableAndUpperTable(SymbolTable table, String name, Kind kind) {
        while (table != null
                && table.getEntryByNameKind(name,
                        kind) == null) {
            table = table.upperTable;
        }
        if (table != null) {
            return table.getEntryByNameKind(name, kind);
        }

        return null;
    }

    public static SymbolTableEntry lookupEntryInTableAndUpperTable(SymbolTable table, String name, Kind[] kinds) {
        SymbolTableEntry result = null;
        for (Kind kind : kinds) {
            result = lookupEntryInTableAndUpperTable(table, name, kind);
            if (result != null) {
                return result;
            }
        }

        return result;
    }

    /**
     * Look for entry from current table or upper table, then calculate offset
     * 
     * @param table
     * @param name
     * @return
     */
    public static String getOffsetByName(SymbolTable table, String name) {
        while (table != null
                && table.getEntryByName(name) == null) {
            table = table.upperTable;
        }
        if (table != null) {
            return String.valueOf(table.getEntryByName(name).offset);
        }

        return null;
    }

    /**
     * Return the offset from current table for i th parameter
     * 
     * @param link
     * @param i
     * @return
     */
    public static String getParamOffsetByIndex(SymbolTable table, int i) {
        int count = 0;
        for (SymbolTableEntry entry : table.entries) {
            if (entry.kind == Kind.parameter) {
                count++;
            }
            if (count == i) {
                return String.valueOf(entry.offset);
            }
        }
        return null;
    }

    public static String getFieldSize(SymbolTable symbolTable, String structName, String fieldName) {
        SymbolTableEntry strctEntry = SymbolTable.lookupEntryInTableAndUpperTable(symbolTable, structName, Kind.struct);
        if (strctEntry != null) {
            return String.valueOf(-strctEntry.link.getEntryByName(fieldName).offset);
        }

        return null;
    }
}
