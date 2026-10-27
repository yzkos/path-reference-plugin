package com.github.pathreference;

import java.util.function.IntUnaryOperator;

public final class ReferenceFormatterTest {

    public static void main(String[] args) {
        formatting();
        lineRanges();
        relativizing();
        System.out.println("ReferenceFormatterTest: OK");
    }

    private static void formatting() {
        // default: path ":" line, range "-"
        assertEquals("src/App.php:5", ReferenceFormatter.format("src/App.php", 5, 5, ":", "-", "", false));
        assertEquals("@src/App.php:5-9", ReferenceFormatter.format("src/App.php", 5, 9, ":", "-", "@", false));
        assertEquals("`@src/App.php:5~9`", ReferenceFormatter.format("src/App.php", 5, 9, ":", "~", "@", true));
        // custom path/line separator, e.g. "#"
        assertEquals("@src/App.php#5", ReferenceFormatter.format("src/App.php", 5, 5, "#", "-", "@", false));
        assertEquals("@src/App.php#5-9", ReferenceFormatter.format("src/App.php", 5, 9, "#", "-", "@", false));
        assertEquals("@src/App.php#5..9", ReferenceFormatter.format("src/App.php", 5, 9, "#", "..", "@", false));
        // multi-character separators, spaces are part of the separator
        assertEquals("@src/App.php #5", ReferenceFormatter.format("src/App.php", 5, 5, " #", "-", "@", false));
        assertEquals("@src/App.php #5-9", ReferenceFormatter.format("src/App.php", 5, 9, " #", "-", "@", false));
        assertEquals("@src/App.php <- 5", ReferenceFormatter.format("src/App.php", 5, 5, " <- ", "-", "@", false));
        assertEquals("#src/App.php", ReferenceFormatter.formatPath("src/App.php", "#", false));
        assertEquals("php", ReferenceFormatter.fenceLanguage("php"));
        assertEquals("javascript", ReferenceFormatter.fenceLanguage("js"));
        assertEquals("typescript", ReferenceFormatter.fenceLanguage("TS"));
        assertEquals("", ReferenceFormatter.fenceLanguage(null));
    }

    private static void lineRanges() {
        // line1 = 0..4, line2 = 6..10, line3 = 12..16, line4 = 18..22
        String text = "line1\nline2\nline3\nline4\n";
        IntUnaryOperator lineOf = lineOf(text);

        assertRange(new int[]{1, 1}, LineRangeCalculator.lineRange(0, 0, 0, lineOf));
        assertRange(new int[]{2, 2}, LineRangeCalculator.lineRange(6, 6, 8, lineOf));
        assertRange(new int[]{2, 3}, LineRangeCalculator.lineRange(6, 17, 6, lineOf));
        assertRange(new int[]{2, 3}, LineRangeCalculator.lineRange(6, 18, 6, lineOf));
        assertRange(new int[]{1, 4}, LineRangeCalculator.lineRange(0, text.length(), 0, lineOf));
        assertRange(new int[]{2, 3}, LineRangeCalculator.lineRange(18, 6, 18, lineOf));
        assertRange(new int[]{1, 1}, LineRangeCalculator.lineRange(0, 0, 0, lineOf("")));
    }

    private static void relativizing() {
        assertEquals("app/Http/Kernel.php", ReferenceFormatter.relativize(
                "/home/user/project/app/Http/Kernel.php", "/home/user/project"));
        assertEquals("app/Http/Kernel.php", ReferenceFormatter.relativize(
                "/home/user/project/app/Http/Kernel.php", "/home/user/project/"));
        assertEquals("", ReferenceFormatter.relativize("/home/user/project", "/home/user/project"));
        assertEquals("home/user/app.php", ReferenceFormatter.relativize("/home/user/app.php", "/"));
        assertNull(ReferenceFormatter.relativize("/home/user/other/app.php", "/home/user/project"));
        assertNull(ReferenceFormatter.relativize("/home/user/projectx/app.php", "/home/user/project"));
    }

    private static void assertNull(String actual) {
        if (actual != null) {
            throw new AssertionError("expected <null> but was <" + actual + ">");
        }
    }

    private static IntUnaryOperator lineOf(String text) {
        return offset -> {
            int clamped = Math.max(0, Math.min(offset, text.length()));
            int line = 0;
            for (int i = 0; i < clamped; i++) {
                if (text.charAt(i) == '\n') {
                    line++;
                }
            }
            return line;
        };
    }

    private static void assertRange(int[] expected, int[] actual) {
        if (expected[0] != actual[0] || expected[1] != actual[1]) {
            throw new AssertionError("expected <" + expected[0] + "," + expected[1]
                    + "> but was <" + actual[0] + "," + actual[1] + ">");
        }
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }
}
