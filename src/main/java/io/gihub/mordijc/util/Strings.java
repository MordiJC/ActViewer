package io.gihub.mordijc.util;

import java.util.List;

public class Strings {

    /**
     * Joins wrapped words and lines.
     *
     * @param lines list of lines to glue.
     * @return glued text.
     */
    public static String glueBrokenText(List<String> lines) {
        return glueBrokenTextAndJoinWith(lines, " ");
    }

    public static String glueBrokenTextAndJoinWith(List<String> lines, String separator) {
        if (lines.isEmpty()) {
            return "";
        } else if (lines.size() == 1) {
            return lines.get(0);
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String line : lines.subList(0, lines.size()-1)) {
            if (line.matches(".*-$")) {
                stringBuilder.append(line.replaceAll("-$", ""));
            } else {
                stringBuilder.append(line).append(separator);
            }
        }

        stringBuilder.append(lines.get(lines.size()-1));

        return stringBuilder.toString();
    }
}
