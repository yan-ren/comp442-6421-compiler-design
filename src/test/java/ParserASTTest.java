import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import parser.ParserDriver;

public class ParserASTTest {

    @Test
    void test_bubblesort() throws Exception {
        assertDoesNotThrow(() -> {
            ParserDriver.main(new String[] { "./input/parser/bubblesort.src" });
        });
    }

    @Test
    void test_polynomial() throws Exception {
        assertDoesNotThrow(() -> {
            ParserDriver.main(new String[] { "./input/parser/polynomial.src" });
        });
    }

    @Test
    void test_parser_test_idnest() throws Exception {
        assertDoesNotThrow(() -> {
            ParserDriver.main(new String[] { "./input/parser/parser_test_idnest.src" });
        });
    }

    @Test
    void test_parser_test_supplement() throws Exception {
        assertDoesNotThrow(() -> {
            ParserDriver.main(new String[] { "./input/parser/parser_test_supplement.src" });
        });
    }

    @Test
    void test_skiperrors() throws Exception {
        assertDoesNotThrow(() -> {
            ParserDriver.main(new String[] { "./input/parser/skiperrors.src" });
        });
    }
}
