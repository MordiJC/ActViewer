package util;

import java.util.List;
import java.util.stream.Collectors;

public class Strings {

    public static String glueBrokenText(List<String> lines) {
        List<String> preparedLines = prepareLinesForGluing(lines);

        return String.join(" ", preparedLines)
                .replace("-\n", "");
    }

    private static List<String> prepareLinesForGluing(List<String> lines) {
        return lines.subList(2, lines.size())
                .stream()
                .map(line -> line.replaceAll("\\-$", "-\n"))
                .collect(Collectors.toList());
    }
}
