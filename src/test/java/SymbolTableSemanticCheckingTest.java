import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class SymbolTableSemanticCheckingTest {

    String encoding = null;
    String semanticError = "[error][semantic]";
    String semanticWarning = "[warn][semantic]";

    @Test
    void test1() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/src/debug.src" });
        });
    }

    @Test
    void test2() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/src/bubblesort.src" });
        });
        assertEquals(false,
                FileUtils.readFileToString(new File("./output/bubblesort.outsemanticerrors"), encoding)
                        .contains(semanticError),
                "bubblesort.src should not contain semantic error");
        assertEquals(0,
                StringUtils.countMatches(
                        FileUtils.readFileToString(new File("./output/bubblesort.outsemanticerrors"), encoding),
                        semanticWarning),
                "bubblesort.src should not contain semantic warn");
    }

    @Test
    void test3() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/src/polynomial.src" });
        });

        assertEquals(false,
                FileUtils.readFileToString(new File("./output/polynomial.outsemanticerrors"), encoding)
                        .contains(semanticError),
                "polynomial.src should not contain semantic errors");
        assertEquals(2,
                StringUtils.countMatches(
                        FileUtils.readFileToString(new File("./output/polynomial.outsemanticerrors"), encoding),
                        semanticWarning),
                "polynomial.src should contain 2 semantic warn");
    }

    @Test
    void test4() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/src/polynomialsemanticerrors.src" });
        });
    }
}
