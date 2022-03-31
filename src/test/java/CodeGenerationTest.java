import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

public class CodeGenerationTest {

    private final String MOON = "./moon/moon";
    private final String UTIL = "./moon/samples/util.m";

    @Test
    void test_code_gen_debug() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/code_gen_debug.src" });
        });
        executeProgram("./output/code_gen_debug/code_gen_debug.m", UTIL);
    }

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

    void executeProgram(String src, String lib) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime()
                .exec(MOON + " " + src + " " + lib);
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        process.waitFor();
    }
}

class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
    }
}
