import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class SymbolTableSemanticCheckingTest {

        String encoding = null;
        String semanticErrorTag = "[error][semantic]";
        String semanticWarningTag = "[warn][semantic]";

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
                                FileUtils.readFileToString(new File("./output/bubblesort/bubblesort.outsemanticerrors"),
                                                encoding)
                                                .contains(semanticErrorTag),
                                "bubblesort.outsemanticerrors should not contain semantic error");
                assertEquals(0,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/bubblesort/bubblesort.outsemanticerrors"),
                                                                encoding),
                                                semanticWarningTag),
                                "bubblesort.outsemanticerrors should not contain semantic warn");
        }

        @Test
        void test3() throws Exception {
                assertDoesNotThrow(() -> {
                        Driver.main(new String[] { "./input/src/polynomial.src" });
                });

                assertEquals(false,
                                FileUtils.readFileToString(new File("./output/polynomial/polynomial.outsemanticerrors"),
                                                encoding)
                                                .contains(semanticErrorTag),
                                "polynomial.outsemanticerrors should not contain semantic errors");
                assertEquals(2,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/polynomial/polynomial.outsemanticerrors"),
                                                                encoding),
                                                semanticWarningTag),
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
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_array/semantic_array.outsemanticerrors"),
                                                                encoding),
                                                "array index is not an integer"),
                                "semantic_array.outsemanticerrors should contain 3 semantic errors: array index is not an integers");
                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_array/semantic_array.outsemanticerrors"),
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
                /*
                 * throws exception because error in expression blocks the rest of semantic
                 * checks
                 */
                assertThrows(Exception.class, () -> {
                        Driver.main(new String[] { "./input/src/semantic_type.src" });
                });
                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_type/semantic_type.outsemanticerrors"),
                                                                encoding),
                                                "type error in expression: *, line"),
                                "semantic_type.outsemanticerrors should contain 1 semantic error: [error][semantic] type error in expression: *");
        }

        /**
         * 6.1 undeclared member function definition
         * 6.2 undefined member function declaration
         * 
         * @throws Exception
         */
        @Test
        void test8() throws Exception {
                assertDoesNotThrow(() -> {
                        Driver.main(new String[] { "./input/src/semantic_undeclared_undefined_func.src" });
                });
                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared_undefined_func/semantic_undeclared_undefined_func.outsemanticerrors"),
                                                                encoding),
                                                "undefined member function"),
                                "semantic_undeclared_undefined_func.outsemanticerrors should contain 1 semantic error: [error][semantic] undefined member function");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared_undefined_func/semantic_undeclared_undefined_func.outsemanticerrors"),
                                                                encoding),
                                                "undeclared member function"),
                                "semantic_undeclared_undefined_func.outsemanticerrors should contain 1 semantic error: [error][semantic] undeclared member function");
        }

        /**
         * 8.1 multiply declared class
         * 8.2 multiply defined free function
         * 8.3 multiply declared identifier in class
         * 8.4 multiply declared identifier in function
         * 
         * 9.3 Overridden inherited member function
         * 
         * @throws Exception
         */
        @Test
        void test9() throws Exception {
                assertDoesNotThrow(() -> {
                        Driver.main(new String[] { "./input/src/semantic_multiple_declared.src" });
                });
                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_multiple_declared/semantic_multiple_declared.outsemanticerrors"),
                                                                encoding),
                                                "multiple declaration for struct: POLYNOMIAL"),
                                "semantic_multiple_declared.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_multiple_declared/semantic_multiple_declared.outsemanticerrors"),
                                                                encoding),
                                                "multiple declaration for variable: b in scope: LINEAR"),
                                "semantic_multiple_declared.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_multiple_declared/semantic_multiple_declared.outsemanticerrors"),
                                                                encoding),
                                                "multiple declaration for function: f in scope: global"),
                                "semantic_multiple_declared.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_multiple_declared/semantic_multiple_declared.outsemanticerrors"),
                                                                encoding),
                                                "multiple declaration for variable: counter in scope: main"),
                                "semantic_multiple_declared.outsemanticerrors should contain 1 semantic error");

                assertEquals(3,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_multiple_declared/semantic_multiple_declared.outsemanticerrors"),
                                                                encoding),
                                                "unimplemented struct"),
                                "semantic_multiple_declared.outsemanticerrors should contain 3 semantic errors");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_multiple_declared/semantic_multiple_declared.outsemanticerrors"),
                                                                encoding),
                                                "function: evaluate in struct POLYNOMIAL is shadowed by struct LINEAR"),
                                "semantic_multiple_declared.outsemanticerrors should contain 1 semantic error");

        }

        /**
         * 11.1 Undeclared local variable
         * 
         * 11.2.1 Undeclared variable (if function is member function, check in the
         * class for member used as variable)
         * 11.2.2 Undeclared variable (if function is member function, and its class
         * inherits search in all super classes' tables for member used as variable)
         * 
         * 11.4 Undeclared free function
         * 11.5 Undeclared class
         * 
         * @throws Exception
         */
        @Test
        void test10() throws Exception {
                assertDoesNotThrow(() -> {
                        Driver.main(new String[] { "./input/src/semantic_undeclared.src" });
                });
                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared/semantic_undeclared.outsemanticerrors"),
                                                                encoding),
                                                "Undeclared class: a"),
                                "semantic_undeclared.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared/semantic_undeclared.outsemanticerrors"),
                                                                encoding),
                                                "Undeclared free function: undefined"),
                                "semantic_undeclared.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared/semantic_undeclared.outsemanticerrors"),
                                                                encoding),
                                                "[error][semantic] Undeclared local variable: counter, line: 24"),
                                "semantic_undeclared.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared/semantic_undeclared.outsemanticerrors"),
                                                                encoding),
                                                "[error][semantic] Undeclared local variable: counter, line: 33"),
                                "semantic_undeclared.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared/semantic_undeclared.outsemanticerrors"),
                                                                encoding),
                                                "[error][semantic] Undeclared local variable: counter, line: 43"),
                                "semantic_undeclared.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared/semantic_undeclared.outsemanticerrors"),
                                                                encoding),
                                                "Undeclared local variable: d"),
                                "semantic_undeclared.outsemanticerrors should contain 1 semantic error");
        }

        /**
         * 12.1 Function call with wrong number of parameters
         * 12.2 Function call with wrong type of parameters
         * 
         * 13.3 Array parameter using wrong number of dimensions
         * 
         * @throws Exception
         */
        @Test
        void test11() throws Exception {
                assertDoesNotThrow(() -> {
                        Driver.main(new String[] { "./input/src/semantic_fcall.src" });
                });
                assertEquals(2,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_fcall/semantic_fcall.outsemanticerrors"),
                                                                encoding),
                                                "Function call with wrong number of parameters"),
                                "semantic_fcall.outsemanticerrors should contain 2 semantic errors");

                assertEquals(2,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_fcall/semantic_fcall.outsemanticerrors"),
                                                                encoding),
                                                "Function call with wrong type of parameters"),
                                "semantic_fcall.outsemanticerrors should contain 2 semantic errors");
        }

        /**
         * 8.6 multiply declared function in class
         * 
         * 9.1 Overloaded free function
         * 9.2 Overloaded member function
         * 
         * @throws Exception
         */
        @Test
        void test12() throws Exception {
                assertDoesNotThrow(() -> {
                        Driver.main(new String[] { "./input/src/semantic_overloaded_func.src" });
                });

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_overloaded_func/semantic_overloaded_func.outsemanticerrors"),
                                                                encoding),
                                                "[error][semantic] multiple declaration for function: f in scope: POLYNOMIAL"),
                                "semantic_overloaded_func.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_overloaded_func/semantic_overloaded_func.outsemanticerrors"),
                                                                encoding),
                                                "[warn][semantic] Overloaded function for function: evaluate in scope: POLYNOMIAL"),
                                "semantic_overloaded_func.outsemanticerrors should contain 1 semantic warn");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_overloaded_func/semantic_overloaded_func.outsemanticerrors"),
                                                                encoding),
                                                "[warn][semantic] Overloaded function for function: f in scope: global"),
                                "semantic_overloaded_func.outsemanticerrors should contain 1 semantic warn");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_overloaded_func/semantic_overloaded_func.outsemanticerrors"),
                                                                encoding),
                                                "[warn][semantic] unimplemented struct, cannot find impl for strcut POLYNOMIAL"),
                                "semantic_overloaded_func.outsemanticerrors should contain 1 semantic warn");
        }

        /**
         * 11.2.3 Undeclared data member (search in class table)
         * 11.2.4 Undeclared data member (if class inherits from other classes, search
         * in all super classes' tables)
         * 
         * 11.3.1 Undeclared member function (search in class table)
         * 11.3.2 Undeclared member function (if class ihherits from other classes,
         * search in all super classes' tables)
         * 
         * 12.1 Function call with wrong number of parameters
         * 
         * @throws Exception
         */
        @Test
        void test13() throws Exception {
                assertDoesNotThrow(() -> {
                        Driver.main(new String[] { "./input/src/semantic_undeclared_member.src" });
                });

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared_member/semantic_undeclared_member.outsemanticerrors"),
                                                                encoding),
                                                "[error][semantic] Undeclared data member: b in C"),
                                "semantic_undeclared_member.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared_member/semantic_undeclared_member.outsemanticerrors"),
                                                                encoding),
                                                "[error][semantic] Undeclared member function: f in C"),
                                "semantic_undeclared_member.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared_member/semantic_undeclared_member.outsemanticerrors"),
                                                                encoding),
                                                "[error][semantic] Function call with wrong number of parameters"),
                                "semantic_undeclared_member.outsemanticerrors should contain 1 semantic error");

                assertEquals(1,
                                StringUtils.countMatches(
                                                FileUtils.readFileToString(new File(
                                                                "./output/semantic_undeclared_member/semantic_undeclared_member.outsemanticerrors"),
                                                                encoding),
                                                "[error][semantic] dot operator used on non-class type counter type"),
                                "semantic_undeclared_member.outsemanticerrors should contain 1 semantic error");
        }
}
