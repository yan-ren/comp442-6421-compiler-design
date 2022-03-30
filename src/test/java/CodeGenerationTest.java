import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class CodeGenerationTest {
    @Test
    void test_function_call() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/function_call.src" });
        });
    }

    @Test
    void test_simple_main() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/simple_main.src" });
        });
    }

    @Test
    void test_write() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/write.src" });
        });
    }

    @Test
    void test_code_gen_bubblesort() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/code_gen_bubblesort.src" });
        });
    }
}
