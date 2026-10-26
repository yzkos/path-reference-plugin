package com.github.pathreference;

import org.jetbrains.annotations.NotNull;

import java.util.function.IntUnaryOperator;

/**
 * Converts editor selection offsets to one-based line numbers.
 *
 * <p>Kept free of platform classes so that it can be unit tested outside the IDE.</p>
 */
public final class LineRangeCalculator {

    private LineRangeCalculator() {
    }

    /**
     * @param lineOf maps a document offset to a zero-based line number
     * @return one-based {@code [startLine, endLine]}
     */
    public static int[] lineRange(int startOffset, int endOffset, int caretOffset,
                                  @NotNull IntUnaryOperator lineOf) {
        if (startOffset == endOffset) {
            int line = lineOf.applyAsInt(caretOffset);
            return new int[]{line + 1, line + 1};
        }
        if (startOffset > endOffset) {
            int swap = startOffset;
            startOffset = endOffset;
            endOffset = swap;
        }

        // A selection that ends at the very beginning of a line contains that
        // line's newline character, but the line itself is not selected.
        if (endOffset > 0) {
            int endLine = lineOf.applyAsInt(endOffset);
            int previousLine = lineOf.applyAsInt(endOffset - 1);
            if (endLine > previousLine) {
                endOffset--;
            }
        }

        int firstLine = lineOf.applyAsInt(startOffset);
        int lastLine = lineOf.applyAsInt(endOffset);
        if (lastLine < firstLine) {
            lastLine = firstLine;
        }
        return new int[]{firstLine + 1, lastLine + 1};
    }
}
