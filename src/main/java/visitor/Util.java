package visitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ast.Node;
import ast.SemanticAction;
import lexicalanalyzer.Constants.LA_TYPE;
import symboltable.Kind;
import symboltable.SymbolTable;
import symboltable.SymbolTableEntry;

public class Util {

    public static void processSymbolTable(Node root, BufferedWriter logger) throws IOException {

        if (root.getName().equals(SemanticAction.PROG)) {
            /**
             * 14.1 Circular class dependency
             */
            for (SymbolTableEntry structEntry : root.symbolTable.getEntries()) {
                boolean circularDependency = false;
                if (structEntry.kind == Kind.struct) {
                    Queue<SymbolTableEntry> entryQueue = new LinkedList<>();
                    entryQueue.add(structEntry);

                    ArrayList<String> inherits = new ArrayList<>();
                    while (entryQueue.size() != 0) {
                        SymbolTableEntry current = entryQueue.remove();
                        for (String parent : current.inherits) {
                            SymbolTableEntry parentEntry = root.symbolTable.getEntryByNameKind(parent, Kind.struct);
                            if (parentEntry == null) {
                                logger.write(
                                        "[error][semantic] inherited struct " + parent + " is not founded for struct "
                                                + current.name + "\n");
                            } else if (inherits.contains(parentEntry.name)) {
                                circularDependency = true;
                                logger.write("[error][semantic] Circular class dependency " + current.name + "\n");
                            } else {
                                inherits.add(parentEntry.name);
                                entryQueue.add(parentEntry);
                            }
                        }
                    }

                    /**
                     * add inherited struct to the struct scope
                     * 
                     * 8.5 shadowed inherited data member
                     */
                    if (!circularDependency) {
                        entryQueue = new LinkedList<>();
                        entryQueue.add(structEntry);
                        int inheritedLevel = 1;

                        while (entryQueue.size() != 0) {
                            SymbolTableEntry current = entryQueue.remove();

                            for (String currentInherited : current.inherits) {
                                SymbolTableEntry currentInheritedEntry = root.symbolTable.getEntryByNameKind(
                                        currentInherited,
                                        Kind.struct);
                                if (currentInheritedEntry != null) {
                                    entryQueue.add(currentInheritedEntry);

                                    for (SymbolTableEntry e : currentInheritedEntry.link.getEntries()) {
                                        if (structEntry.link.getEntryByNameKind(e.name, e.kind) == null) {
                                            structEntry.link.appendEntry(e);
                                        } else if (inheritedLevel == 1) {
                                            logger.write(
                                                    "[warn][semantic] " + e.kind + ": " + e.name + " in struct "
                                                            + currentInheritedEntry.name +
                                                            " is shadowed by struct " + structEntry.name + "\n");
                                        }
                                    }
                                } else {
                                    logger.write(
                                            "[error][semantic] struct " + structEntry.name + " inherits "
                                                    + currentInherited
                                                    + " not found\n");
                                }
                            }
                            inheritedLevel++;
                        }
                    }
                }
            }
            /**
             * 1. associate impl function with struct
             * 
             * 6.2 undefined member function declaration, e.g impl doesn't implement the
             * function declared in strct
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
                                    structEntry.link.upperTable = structSymbolTable;
                                } else {
                                    logger.write("[error][semantic] undefined member function declaration "
                                            + structEntry.name + " in impl "
                                            + implSymbolTable.getName() + "\n");
                                }
                            }
                        }
                    } else {
                        logger.write("[warn][semantic] unimplemented struct, cannot find impl for strcut " + entry.name
                                + "\n");
                    }
                }
            }
            /**
             * check for each impl if a struct exists and func in impl is defined in struct
             * 
             * 6.1 undeclared member function definition, e.g. impl has function that is not
             * declared in struct
             */
            for (SymbolTableEntry entry : root.symbolTable.getEntries()) {
                if (entry.kind == Kind.impl) {
                    SymbolTableEntry structEntry = root.symbolTable.getEntryByNameKind(entry.name, Kind.struct);
                    if (structEntry != null) {
                        SymbolTable implSymbolTable = entry.link;
                        SymbolTable structSymbolTable = structEntry.link;
                        for (SymbolTableEntry implEntry : implSymbolTable.getEntries()) {
                            if (implEntry.kind == Kind.function) {
                                SymbolTableEntry structFuncEntry = structSymbolTable.getEntryByNameKind(implEntry.name,
                                        implEntry.kind);
                                if (structFuncEntry == null) {
                                    logger.write("[error][semantic] undeclared member function definition "
                                            + implEntry.name + " in impl "
                                            + implSymbolTable.getName() + "\n");

                                }
                            }
                        }
                    } else {
                        logger.write("[warn][semantic] unknown impl " + entry.name + " cannot find struct for impl \n");
                    }
                }
            }
            /**
             * remove impl from prog symbol table
             */
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

    // TODO: get struct type size
    public static int getTypeSize(String typeName) {
        if (typeName.equals(LA_TYPE.INTEGER) || typeName.equals(LA_TYPE.INTNUM)) {
            return ComputeMemSizeVisitor.INT_SIZE;
        } else if (typeName.equals(LA_TYPE.FLOAT) || typeName.equals(LA_TYPE.FLOATNUM)) {
            return ComputeMemSizeVisitor.FLOAT_SIZE;
        }

        return 0;
    }

    // TODO: get struct type size
    public static int getNodeSize(Node node) {
        int totalDim = 1;
        // for array size dimension, e.g. a[7], or a[] -> a[0]
        for (String dim : node.symbolTableEntry.type.dimension) {
            totalDim += Integer.parseInt(dim);
        }

        return totalDim * Util.getTypeSize(node.symbolTableEntry.type.name);
    }

    public static boolean isPrimaryType(String typeName) {
        if (typeName.equals(LA_TYPE.INTEGER) || typeName.equals(LA_TYPE.INTNUM) || typeName.equals(LA_TYPE.FLOAT)
                || typeName.equals(LA_TYPE.FLOATNUM)) {
            return true;
        }

        return false;
    }

    public static boolean isDotNode(Node node) {
        if (node.getName().equals(SemanticAction.DOT)) {
            return true;
        }

        return false;
    }
}
