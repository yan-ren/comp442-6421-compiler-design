package symboltable;

import java.util.ArrayList;
import java.util.Objects;

public class SymbolTableEntryType {
    public String name;
    public ArrayList<String> dimension;

    public SymbolTableEntryType(String name) {
        this.name = name;
        dimension = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SymbolTableEntryType)) {
            return false;
        }
        SymbolTableEntryType symbolTableEntryType = (SymbolTableEntryType) o;
        return Objects.equals(name, symbolTableEntryType.name)
                && dimension.equals(symbolTableEntryType.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dimension);
    }

    @Override
    public String toString() {
        return "{" +
                " name='" + name + "'" +
                ", dimension='" + dimension + "'" +
                "}";
    }

}
