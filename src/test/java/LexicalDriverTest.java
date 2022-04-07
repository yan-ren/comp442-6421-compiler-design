import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import lexicalanalyzer.LexerDriver;

public class LexicalDriverTest {
    @Test
    void test1() throws Exception {
        assertDoesNotThrow(() -> {
            LexerDriver.main(null);
        });
    }
}
