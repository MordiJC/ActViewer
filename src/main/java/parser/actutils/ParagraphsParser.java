package parser.actutils;

import container.ActElement;
import container.ActElementBuilder;
import parser.ActParserSectionPattern;
import util.Lists;
import util.Regex;
import util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static parser.ActParserSectionPattern.PARAGRAPH;

public class ParagraphsParser {
    public List<ActElement> getParagraphs(List<String> lines) {
        return getChildrenElements(lines, PARAGRAPH);
    }

    private List<ActElement> getChildrenElements(List<String> lines, ActParserSectionPattern apsp) {
        // TODO: FIX
        if(lines == null || lines.size() == 0) {
            throw new IllegalArgumentException("Lines must not be null or its size have to be > 0");
        }

        List<List<String>> paragraphs =
                Lists.splitIncludingDelimiterAsFirstElement(
                        lines.subList(
                                getIntroductionLinesCount(lines, apsp),
                                lines.size()
                        ),
                        e->e.matches(apsp.pattern)
                );

        List<ActElement> results = new ArrayList<>();

        if(paragraphs.size() == 0) {
            return results;
        }

        if(paragraphs.size() == 1
                && !paragraphs.get(0).get(0).matches(apsp.pattern)) {
            return new ArrayList<>();
        }

        Pattern pattern = Pattern.compile(apsp.pattern);
        Matcher matcher;

        for(List<String> part: paragraphs) {
            matcher = pattern.matcher(part.get(0));

            matcher.matches();

            ActElementBuilder builder = new ActElementBuilder();

            List<String> contentLines = new ArrayList<>();
            contentLines.add(Regex.getGroupOrEmptyString(matcher, "content"));

//            builder.content(new ParagraphsParser().getIntroduction(part.subList(1, part.size())));

            if(part.size() > 1) {
                contentLines.addAll(part.subList(1, part.size()));
            }

            results.add(
                    new ActElementBuilder()
                    .identifier(Regex.getGroupOrEmptyString(matcher, "identifier"))
                    .content(Strings.glueBrokenText(contentLines))
                    .build()
            );
        }

        return results;
    }

    public String getIntroduction(List<String> lines, ActParserSectionPattern apsp) {
        int linesCount = getIntroductionLinesCount(lines, apsp);

        return Strings.glueBrokenText(lines.subList(0, linesCount));
    }

    private int getIntroductionLinesCount(List<String> lines, ActParserSectionPattern apsp) {
        int count = 0;

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).matches(apsp.pattern)) {
                return count;
            } else {
                count = i + 1;
            }
        }

        return count;
    }
}
