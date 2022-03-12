package visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ast.Node;
import ast.SemanticAction;
import symboltable.Kind;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;

public class Helper {

    public static void postProcess(Node root, BufferedWriter logger) throws IOException {

        if (root.getName().equals(SemanticAction.PROG)) {
            /**
             * 1. associate impl function with struct
             */
            for (SymbolTableEntry entry : root.symbolTable.getEntries()) {
                if (entry.kind == Kind.struct) {
                    SymbolTableEntry implEntry = root.symbolTable.getEntryByNameKind(entry.name, Kind.impl);
                    if (implEntry != null) {
                        SymbolTable structSymbolTable = entry.link;
                        SymbolTable implSymbolTable = implEntry.link;
                        for (SymbolTableEntry structEntry : structSymbolTable.getEntries()) {
                            if (structEntry.kind == Kind.function) {
                                SymbolTableEntry implFuncEntry = implSymbolTable.getEntryByNameKind(structEntry.name,
                                        structEntry.kind);
                                if (implFuncEntry != null) {
                                    structEntry.link = implFuncEntry.link;
                                } else {
                                    logger.write("[warn][semantic] cannot find func " + structEntry.name + " in impl "
                                            + implSymbolTable.getName() + "\n");

                                }
                            }
                        }
                    } else {
                        logger.write("[warn][semantic] cannot find impl for strcut" + entry.name + "\n");
                    }
                }
            }
            // add inherited struct to the struct scope
            int entryNum = root.symbolTable.getEntries().size();
            for (int i = 0; i < entryNum; i++) {
                SymbolTableEntry structEntry = root.symbolTable.getEntries().get(i);
                if (structEntry.kind == Kind.struct) {
                    for (String inherited : structEntry.inherits) {
                        SymbolTableEntry inheritedEntry = root.symbolTable.getEntryByNameKind(inherited, Kind.struct);
                        if (inheritedEntry != null) {
                            for (SymbolTableEntry e : inheritedEntry.link.getEntries()) {
                                if (structEntry.link.getEntryByNameKind(e.name, e.kind) == null) {
                                    structEntry.link.addEntry(e);
                                } else {
                                    logger.write("[warn][semantic] " + e.kind + " : " + e.name + " in struct "
                                            + inheritedEntry.name +
                                            " is shadowed by struct " + structEntry.name + "\n");
                                }
                            }
                        } else {
                            logger.write(
                                    "[warn][semantic] struct " + structEntry.name + " inherits " + inherited
                                            + " not found\n");
                        }
                    }
                }
            }
            // remove impl from prog
            List<SymbolTableEntry> rootEntries = root.symbolTable.getEntries();
            Iterator<SymbolTableEntry> iterator = rootEntries.iterator();
            while (iterator.hasNext()) {
                SymbolTableEntry entry = iterator.next();
                if (entry.kind == Kind.impl) {
                    iterator.remove();
                }
            }
        }
    }

    public static void printSymbolTableToFile(Node node, BufferedWriter logger) throws IOException {
        Queue<SymbolTable> queue = new LinkedList<>();
        queue.add(node.symbolTable);
        while (queue.size() != 0) {
            SymbolTable table = queue.remove();
            logger.write(table.toString() + "\n");
            logger.write("==============================================\n");
            for (SymbolTableEntry entry : table.getEntries()) {
                if (entry.link != null) {
                    queue.add(entry.link);
                }
            }
        }
    }
}
