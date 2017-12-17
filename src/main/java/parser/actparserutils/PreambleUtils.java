package parser.actparserutils;

import container.ActElement;
import container.SectionBuilder;

import java.util.List;

public class PreambleUtils {

    public static void removePreambleFromCurrentSectionIfPresent(List<List<String>> currentSection) {
        if (currentSection.size() == 0) {
            return;
        }

        List<String> chapter = currentSection.get(0);

        if (chapter.size() > 2
                && chapter.get(0).equalsIgnoreCase("KONSTYTUCJA")
                && chapter.get(1).equalsIgnoreCase("RZECZYPOSPOLITEJ POLSKIEJ")) {
            currentSection.remove(0);
        }
    }

    public static void addPreambleIfPresent(List<List<String>> chapters, List<ActElement> actElements) {
        ActElement preamble = getPreambleIfPresentOrNull(chapters);

        if (preamble != null) {
            actElements.add(preamble);
        }
    }

    public static ActElement getPreambleIfPresentOrNull(List<List<String>> chapters) {

        List<String> preambleLines = getLinesIfPreambleOrNull(chapters);

        if (preambleLines == null) {
            return null;
        }

        String preambleTitle = new StringBuilder()
                .append(preambleLines.get(0))
                .append(' ')
                .append(preambleLines.get(1))
                .toString();

        String preambleContent = WordBreakJoiningUtils.joinBrokenWordsAndJoinIntoOneString(preambleLines);

        ActElement preamble = new SectionBuilder()
                .title(preambleTitle)
                .sectionName("Preambuła")
                .content(preambleContent)
                .build();

        return preamble;
    }

    public static List<String> getLinesIfPreambleOrNull(List<List<String>> chapters) {
        return chapters.stream()
                .filter(chapter -> chapter.size() >= 2
                        && chapter.get(0).equalsIgnoreCase("KONSTYTUCJA")
                        && chapter.get(1).equalsIgnoreCase("RZECZYPOSPOLITEJ POLSKIEJ"))
                .findFirst()
                .orElse(null);
    }
}
