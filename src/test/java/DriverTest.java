import org.junit.jupiter.api.Test;

public class DriverTest {

    @Test
    void test1() throws Exception {
        Driver.main(new String[] { "./input/src/debug.src" });
    }

    @Test
    void test2() throws Exception {
        Driver.main(new String[] { "./input/src/bubblesort.src" });
    }

    @Test
    void test3() throws Exception {
        Driver.main(new String[] { "./input/src/polynomial.src" });
    }

    @Test
    void test4() throws Exception {
        Driver.main(new String[] { "./input/src/parser_test_idnest.src" });
    }

    @Test
    void test5() throws Exception {
        Driver.main(new String[] { "./input/src/parser_test_supplement.src" });
    }

    @Test
    void test6() throws Exception {
        Driver.main(new String[] { "./input/src/polynomialsemanticerrors.src" });
    }

    // @Test
    // void test6() throws Exception {
    // Driver.main(new String[] { "./input/skiperrors.src" });
    // }
}
