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
        Parser parser = new Parser();
        parser.initFirstFollowSet();
        assertEquals(parser.firstSet.size(), parser.followSet.size());
        assertEquals(parser.firstSet.get("REPTPROG0"), new HashSet<String>(Arrays.asList("struct", "impl", "func")));
        assertEquals(parser.followSet.get("REPTPROG0"), new HashSet<String>(Arrays.asList("∅")));
    }

    @Test
    void testInitParserTable() throws IOException {
        Parser parser = new Parser();
        parser.initFirstFollowSet();
        parser.initParseTable();
        assertEquals(new ArrayList<String>(List.of("dot", "id", "FUNCORVARIDNEST")),
                parser.parseTable.get("FUNCORVARIDNESTTAIL::dot"));
        assertEquals(new ArrayList<String>(),
                parser.parseTable.get("FUNCORVARIDNESTTAIL::rpar"));
    }
}
