package com.devvault.util;

import java.util.ArrayList;
import java.util.List;

/** Parses the "code fragment ==> explanation" text used by lessons and problems into displayable rows. */
public final class LineByLine {

    private static final String SEPARATOR = "==>";

    private LineByLine() {
    }

    /** One displayed row: a piece of code and the reason it is there. */
    public record Row(String code, String why) {
    }

    /**
     * Splits the text into lines and each line at the first {@code ==>}. Lines without a separator are
     * ignored. Never returns null, so templates can loop over the result without a null check.
     */
    public static List<Row> parse(String text) {
        List<Row> rows = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return rows;
        }
        for (String line : text.split("\\R")) {
            int at = line.indexOf(SEPARATOR);
            if (at < 0) {
                continue;
            }
            String code = line.substring(0, at).strip();
            String why = line.substring(at + SEPARATOR.length()).strip();
            if (!code.isEmpty() || !why.isEmpty()) {
                rows.add(new Row(code, why));
            }
        }
        return rows;
    }
}
