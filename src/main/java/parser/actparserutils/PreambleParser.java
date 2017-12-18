package parser.actparserutils;

import container.ActElement;
import container.ActElementBuilder;

import java.util.List;

public class PreambleParser {

    /**
     * Checks if lines contain preamble of Polish Constitution and if it contains,
     * then parses it and returns as new <code>ActElement</code> instance. If remove
     * flag is set then preamble will be removed from given list.
     *
     * @param lines list of act lines.
     * @return element containing preamble or null if preamble was not found.
     * @since 1.0
     */
    public ActElement parse(List<String> lines, boolean remove) {
        ActElement preamble = getPreambleIfPresentOrNull(lines);

        if (remove) {
            removePreamble(lines);
        }

        return preamble;
    }

    private void removePreamble(List<String> lines) {
        lines.subList(0, new ChapterParser().getFirstChapter(lines).size()).clear();
    }

    public ActElement getPreambleIfPresentOrNull(List<String> lines) {

        List<String> preambleLines = getLinesIfPreambleOrNull(lines);

        if (preambleLines == null) {
            return null;
        }

        String preambleTitle = new StringBuilder()
                .append(preambleLines.get(0))
                .append(' ')
                .append(preambleLines.get(1))
                .toString();

        String preambleContent = WordBreakJoiningUtils.joinBrokenWordsAndJoinIntoOneString(preambleLines);

        ActElement preamble = new ActElementBuilder()
                .title(preambleTitle)
                .typeName("Preambuła")
                .content(preambleContent)
                .build();

        return preamble;
    }

    public static List<String> getLinesIfPreambleOrNull(List<String> lines) {
        if (lines.size() >= 2
                && lines.get(0).equalsIgnoreCase("KONSTYTUCJA")
                && lines.get(1).equalsIgnoreCase("RZECZYPOSPOLITEJ POLSKIEJ")) {

            List<String> preambleChapter = new ChapterParser().getFirstChapter(lines);

            preambleChapter.stream().forEach(System.out::println);
        }
        return null;
    }
}
