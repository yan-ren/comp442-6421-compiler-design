import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
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

        executeProgram("./output/code_gen_debug/code_gen_debug.m", UTIL, "18", true);
    }

    @Test
    void test_read_write() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/read_write.src" });
        });
        // pass 1 to console input, expect print 2
        executeProgram("./output/read_write/read_write.m", UTIL, "1", true);
    }

    @Test
    void test_simple_main() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/simple_main.src" });
        });
        // pass 1 to console input, expect print 2
        // then loop print 0 to 10 inclusice
        executeProgram("./output/simple_main/simple_main.m", UTIL, "1", true);
    }

    @Test
    void test_function_call() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/function_call.src" });
        });
        // expect print 7
        // expect print 17
        executeProgram("./output/function_call/function_call.m", UTIL, "", true);
    }

    @Test
    void test_basic_array() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/basic_array.src" });
        });
    }

    @Test
    void test_code_gen_bubblesort() throws Exception {
        assertDoesNotThrow(() -> {
            Driver.main(new String[] { "./input/code_gen/code_gen_bubblesort.src" });
        });
    }

    /**
     * Run moon code
     * 
     * @param src
     * @param lib
     * @param run if true, then run moon code, if false, print command for user to
     *            try themself
     * @throws IOException
     * @throws InterruptedException
     */
    void executeProgram(String src, String lib, String input, boolean run) throws IOException, InterruptedException {
        String command = "";
        // pass input through echo
        if (input != null && !input.isEmpty()) {
            command = "echo " + input + " | " + MOON + " " + src + " " + lib;
        } else {
            command = MOON + " " + src + " " + lib;
        }
        String[] cmd = { "/bin/sh", "-c", command };
        System.out.println("[execute] $ " + Arrays.toString(cmd));
        if (run) {
            Process process = Runtime.getRuntime()
                    .exec(cmd);
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            process.waitFor();
        }
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
