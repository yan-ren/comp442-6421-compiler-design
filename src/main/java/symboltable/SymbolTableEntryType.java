package symboltable;

import java.util.ArrayList;

public class SymbolTableEntryType {
    private String name;
    private ArrayList<Integer> dimension;

    public SymbolTableEntryType(String name) {
        this.name = name;
        dimension = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getDimension() {
        return this.dimension;
    }

    public void setDimension(ArrayList<Integer> dimension) {
        this.dimension = dimension;
    }

    public void addDimension(int dimension) {
        this.dimension.add(dimension);
    }

    @Override
    public String toString() {
        return "{" +
                " name='" + getName() + "'" +
                ", dimension='" + getDimension() + "'" +
                "}";
    }

}
