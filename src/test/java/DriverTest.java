import org.junit.jupiter.api.Test;

public class DriverTest {

    @Test
    void test1() throws Exception {
        Driver.main(new String[] { "./input/debug.src" });
    }

    @Test
    void test2() throws Exception {
        Driver.main(new String[] { "./input/bubblesort.src" });
    }

    @Test
    void test3() throws Exception {
        Driver.main(new String[] { "./input/polynomial.src" });
    }

    @Test
    void test4() throws Exception {
        Driver.main(new String[] { "./input/idnest.src" });
    }

    @Test
    void test5() throws Exception {
        Driver.main(new String[] { "./input/supplement.src" });
    }

    @Test
    void test6() throws Exception {
        Driver.main(new String[] { "./input/skiperrors.src" });
    }
}
