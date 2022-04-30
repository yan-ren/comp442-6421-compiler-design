# Compiler Design

## Overview

A simple compiler implemented in Java to utilize the main compilation concept. Following phases are implemented
- Lexical Analyzer
- Syntactic Analyzer
- Intemediate Representation (Abstract Syntax Tree)
- Semantic Translation
- Object Code (Moon assembly code)

## Repository structure

| Path / File               | Description                           |
| ------------------------- | ------------------------------------- |
| src/main/                 | Source code                           |
| src/maun/java/Driver.java | Main compilier driver                 |
| src/test/                 | Unit tests and driver tests           |
| doc/                      | Reports for each phases, grammar, etc |
| input/                    | Input files for compiler driver       |
| output/                   | Compiler output files                 |
| pom.xml                   | Maven build file                      |

## Summary for each compilation phases
- Lexical Analyzer
  - Table driven scanner approach, where the state transition table is implemented to represent the DFA.
- Sytactic Analyzer
  - Table driven predictive parsing
- Intemediate Representation (Abstract Syntax Tree)
- Semantic Translation
  - Visitor pattern
- Code generation
  - label approach/stack approach

## Buid with
- Java 11
- Maven

## Contact
Contact repository owner for any inquiry
