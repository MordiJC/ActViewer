package parser.actparserutils;

import java.util.List;
import java.util.stream.Collectors;

public class WordBreakJoiningUtils {

    public static String joinBrokenWordsAndJoinIntoOneString(List<String> preambleLines) {
        List<String> linesPreparedForWordBreakJoining = prepareLinesForWordBreakJoining(preambleLines);

        return String.join(" ", linesPreparedForWordBreakJoining)
                .replace("-\n", "");
    }

    private static List<String> prepareLinesForWordBreakJoining(List<String> preambleLines) {
        return preambleLines.subList(2, preambleLines.size())
                .stream()
                .map(line -> line.replaceAll("\\-$", "-\n"))
                .collect(Collectors.toList());
    }
}
