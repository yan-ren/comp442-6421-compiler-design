package visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import ast.Node;
import ast.SemanticAction;
import symboltable.Kind;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;

public class Helper {

    public static void postProcess(Node node, BufferedWriter logger) throws IOException {

        if (node.getName().equals(SemanticAction.PROG)) {
            /**
             * 1. associate impl function with struct
             */
            for (SymbolTableEntry entry : node.symbolTable.getEntries()) {
                if (entry.kind == Kind.struct) {
                    SymbolTableEntry implEntry = node.symbolTable.getEntryByNameKind(entry.name, Kind.impl);
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
            int entryNum = node.symbolTable.getEntries().size();
            for (int i = 0; i < entryNum; i++) {
                SymbolTableEntry entry = node.symbolTable.getEntries().get(i);
                if (entry.kind == Kind.struct) {
                    for (String inherited : entry.inherits) {
                        SymbolTableEntry inheritedEntry = node.symbolTable.getEntryByNameKind(inherited, Kind.struct);
                        if (inheritedEntry != null) {
                            for (SymbolTableEntry e : inheritedEntry.link.getEntries()) {
                                if (node.symbolTable.getEntryByNameKind(e.name, e.kind) == null) {
                                    node.symbolTable.addEntry(e);
                                } else {
                                    logger.write("[warn][semantic] " + e.kind + " : " + e.name + " in struct "
                                            + inheritedEntry.name +
                                            " is shadowed by struct " + entry.kind + "\n");
                                }
                            }
                        } else {
                            logger.write(
                                    "[warn][semantic] struct " + entry.name + " inherits " + inherited
                                            + " not found\n");
                        }
                    }
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
