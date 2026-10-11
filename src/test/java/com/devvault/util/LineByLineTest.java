package com.devvault.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineByLineTest {

    @Test
    void parse_splitsEachLineAtTheFirstSeparator() {
        List<LineByLine.Row> rows = LineByLine.parse("int a = 1; ==> declares a\nreturn a; ==> gives it back ==> really");

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).code()).isEqualTo("int a = 1;");
        assertThat(rows.get(0).why()).isEqualTo("declares a");
        assertThat(rows.get(1).why()).isEqualTo("gives it back ==> really");
    }

    @Test
    void parse_keepsTheLettersOfTheExplanationIntact() {
        List<LineByLine.Row> rows = LineByLine.parse("x ==> Both operands are int, so the result is not decimal.");

        assertThat(rows.get(0).why()).isEqualTo("Both operands are int, so the result is not decimal.");
    }

    @Test
    void parse_ignoresLinesWithoutSeparatorAndHandlesWindowsLineEndings() {
        List<LineByLine.Row> rows = LineByLine.parse("just a note\r\nfoo ==> bar\r\n\r\n");

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).code()).isEqualTo("foo");
    }

    @Test
    void parse_returnsEmptyListForNullOrBlank() {
        assertThat(LineByLine.parse(null)).isEmpty();
        assertThat(LineByLine.parse("  ")).isEmpty();
    }
}
