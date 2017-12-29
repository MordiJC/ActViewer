package parser.actutils;

import container.ActElement;
import util.Strings;

import java.util.List;

import static parser.ActParserSectionPattern.PARAGRAPH;

public class ParagraphsParser {
    public List<ActElement> getParagraphs(List<String> lines) {
        return null;
    }

    public String getIntroduction(List<String> lines) {
        int linesCount = getIntroductionLinesCount(lines);

        return Strings.glueBrokenText(lines.subList(0, linesCount));
    }

    public String getSummary(List<String> lines) {
        // TODO complete getSummary
        return "";
    }

    private int getIntroductionLinesCount(List<String> lines) {
        int count = 0;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).matches(PARAGRAPH.pattern)) {
                return count;
            } else {
                count = i + 1;
            }
        }

        return count;
    }

    // TODO: getParagraphs
    // TODO: getSummary
}
