import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                FileUtils.readFileToString(new File("./output/bubblesort/bubblesort.outsemanticerrors"), encoding)
                        .contains(semanticError),
                "bubblesort.outsemanticerrors should not contain semantic error");
        assertEquals(0,
                StringUtils.countMatches(
                        FileUtils.readFileToString(new File("./output/bubblesort/bubblesort.outsemanticerrors"),
                                encoding),
                        semanticWarning),
                "bubblesort.outsemanticerrors should not contain semantic warn");
    }

    @Test
    void test3() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/src/polynomial.src" });
        });

        assertEquals(false,
                FileUtils.readFileToString(new File("./output/polynomial/polynomial.outsemanticerrors"), encoding)
                        .contains(semanticError),
                "polynomial.outsemanticerrors should not contain semantic errors");
        assertEquals(2,
                StringUtils.countMatches(
                        FileUtils.readFileToString(new File("./output/polynomial/polynomial.outsemanticerrors"),
                                encoding),
                        semanticWarning),
                "polynomial.outsemanticerrors should contain 2 semantic warn");
    }

    /**
     * 14.1 Circular class dependency
     * 
     * @throws Exception
     */
    @Test
    void test4() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/src/semantic_circular_dep.src" });
        });
        assertEquals(3,
                StringUtils.countMatches(
                        FileUtils.readFileToString(
                                new File("./output/semantic_circular_dep/semantic_circular_dep.outsemanticerrors"),
                                encoding),
                        "Circular class dependency"),
                "semantic_circular_dep.outsemanticerrors should contain 3 Circular class dependency semantic errors");
    }

    /*
     * 8.5 shadowed inherited data member
     */
    @Test
    void test5() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/src/semantic_shadowed_data.src" });
        });
        assertEquals(2,
                StringUtils.countMatches(
                        FileUtils.readFileToString(
                                new File("./output/semantic_shadowed_data/semantic_shadowed_data.outsemanticerrors"),
                                encoding),
                        "is shadowed by struct"),
                "semantic_shadowed_data.outsemanticerrors should contain 2 semantic warn: shadowed by struct");
    }

    /**
     * 13.1 Use of array with wrong number of dimensions
     * 13.2 Array index is not an integer
     * 
     * @throws Exception
     */
    @Test
    void test6() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/src/semantic_array.src" });
        });

        assertEquals(3,
                StringUtils.countMatches(
                        FileUtils.readFileToString(new File("./output/semantic_array/semantic_array.outsemanticerrors"),
                                encoding),
                        "array index is not an integer"),
                "semantic_array.outsemanticerrors should contain 3 semantic errors: array index is not an integers");
        assertEquals(1,
                StringUtils.countMatches(
                        FileUtils.readFileToString(new File("./output/semantic_array/semantic_array.outsemanticerrors"),
                                encoding),
                        "use of array with wrong number of dimensions"),
                "semantic_array.outsemanticerrors should contain 1 semantic error: use of array with wrong number of dimensions");
    }

    /**
     * 10.1 Type error in expression
     * 10.2 Type error in assignment statement
     * 10.3 Type error in return statement
     * 
     * @throws Exception
     */
    @Test
    void test7() throws Exception {
        assertThrows(Exception.class, () -> {
            Driver.main(new String[] { "./input/src/semantic_type.src" });
        });
        assertEquals(1,
                StringUtils.countMatches(
                        FileUtils.readFileToString(new File("./output/semantic_type/semantic_type.outsemanticerrors"),
                                encoding),
                        "type error in expression: *, line"),
                "semantic_type.outsemanticerrors should contain 1 semantic error: [error][semantic] type error in expression: *");
    }
}
