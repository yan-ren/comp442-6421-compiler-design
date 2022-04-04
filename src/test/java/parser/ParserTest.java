package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void testInitFirstFollowSet() throws IOException {
        Parser parser = new Parser(null, null, null, null, null);
        parser.initFirstFollowSet();
        assertEquals(parser.firstSet.size(), parser.followSet.size());
        assertEquals(new HashSet<String>(Arrays.asList("struct", "impl", "func")),
                parser.firstSet.get("REPTPROG0"));
        assertEquals(new HashSet<String>(Arrays.asList("intnum", "floatnum", "openpar", "not", "id",
                "plus", "minus")), parser.firstSet.get("ARITHEXPR"));
        assertEquals(new HashSet<String>(Arrays.asList("opensqbr")), parser.firstSet.get("ARRAYSIZE"));
        assertEquals(new HashSet<String>(Arrays.asList("epsilon")), parser.followSet.get("REPTPROG0"));
        assertEquals(new HashSet<String>(Arrays.asList("mult", "div", "and", "closesqbr", "eq", "noteq", "lt",
                "gt", "leq", "geq", "plus", "minus", "or", "comma", "semi", "closepar")),
                parser.followSet.get("FUNCORVAR"));
        assertEquals(new HashSet<String>(Arrays.asList("intnum", "floatnum", "openpar", "not", "id",
                "plus", "minus")), parser.followSet.get("MULTOP"));
        assertEquals(new HashSet<String>(Arrays.asList("epsilon")), parser.followSet.get("ASSIGNSTAT"));
    }

    @Test
    void testInitParserTable() throws IOException {
        Parser parser = new Parser(null, null, null, null, null);
        parser.initFirstFollowSet();
        parser.initParseTable();
        assertEquals(new ArrayList<String>(List.of("dot", "id", "FUNCORVARIDNEST")),
                parser.parseTable.get("FUNCORVARIDNESTTAIL::dot"));

        // FUNCORVARIDNESTTAIL::closepar → &epsilon
        assertEquals(new ArrayList<String>(),
                parser.parseTable.get("FUNCORVARIDNESTTAIL::closepar"));
        assertEquals(new ArrayList<String>(List.of("intnum", "closesqbr")),
                parser.parseTable.get("ARRAYSIZE1::intnum"));
        assertEquals(
                new ArrayList<String>(List.of("func", "id", "openpar", "FPARAMS", "closepar", "arrow",
                        "RETURNTYPE")),
                parser.parseTable.get("FUNCHEAD::func"));
    }
}
