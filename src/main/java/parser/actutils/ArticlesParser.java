package parser.actutils;

import container.ActElement;
import container.ActElementBuilder;
import parser.ActParserSectionPattern;
import parser.ActParsingException;
import util.Lists;
import util.Log;
import util.Regex;
import util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static parser.ActParserSectionPattern.ARTICLE;

public class ArticlesParser {
    public List<ActElement> parse(List<String> lines) {
        List<List<String>> sectionsLines =
                Lists.splitIncludingDelimiterAsFirstElement(
                        lines,
                        e -> e.matches(ARTICLE.pattern));

        Pattern pattern = Pattern.compile(ARTICLE.pattern);
        Matcher matcher;
        List<ActElement> result = new ArrayList<>();

        for (List<String> section : sectionsLines) {
            matcher = pattern.matcher(section.get(0));
            if (!matcher.matches()) {
                result.add(new ActElementBuilder().content(Strings.glueBrokenText(section)).build());
            } else {
                List<ActElement> elements = parseArticleContent(section.subList(1, section.size()));

                ActElementBuilder actElementBuilder = new ActElementBuilder()
                        .identifier(Regex.getGroupOrEmptyString(matcher, "identifier"))
                        .typeName(Regex.getGroupOrEmptyString(matcher, "typeName"));

                if (elements.size() == 1 && elements.get(0).identifier.isEmpty()) {
                    actElementBuilder.content(elements.get(0).content);
                } else {
                    actElementBuilder.childrenElements(elements);
                }

                result.add(actElementBuilder.build());
            }
        }
        return result;
    }



    private List<ActElement> parseArticleContent(List<String> lines) {
        List<List<String>> articleContentSections =
                Lists.splitIncludingDelimiterAsFirstElement(lines,
                        e -> e.matches(ActParserSectionPattern.PARAGRAPH.pattern)
                );

        List<ActElement> result = new ArrayList<>();
        ActElement supposedSummary = null;

        // TODO: Nie zadziała, bo jeżeli jest podsumowanie, to ostatni punkt nie ma znaku końca,
        // TODO: a poprzednie mają przecinki, trzeba to obsłużyć
        if (!articleContentSections.get(0).get(0).matches(ActParserSectionPattern.PARAGRAPH.pattern)) {
            result.add(
                    new ActElementBuilder().content(
                            Strings.glueBrokenText(articleContentSections.get(0))
                    ).build()
            );
            if (articleContentSections.size() >= 1) {
                articleContentSections.remove(0);
            }
        }

        if (articleContentSections.size() > 0
                && !articleContentSections
                .get(articleContentSections.size() - 1)
                .get(0).matches(ActParserSectionPattern.PARAGRAPH.pattern)) {

            supposedSummary = new ActElementBuilder()
                    .content(
                            Strings.glueBrokenText(
                                    articleContentSections
                                            .get(articleContentSections.size() - 1)
                            )
                    ).build();

            articleContentSections.remove(articleContentSections.size() - 1);
        }

        for (List<String> l : articleContentSections) {
            result.add(new ActElementBuilder().content(Strings.glueBrokenText(l)).build());
        }

        if (supposedSummary != null) {
            result.add(supposedSummary);
        }

        return result;
    }

    private void checkIfMatchesOrThrow(List<String> lines, ActParserSectionPattern enumSection) {
        if (!lines.get(0).matches(enumSection.pattern)) {
            Log.getLogger().severe("Given enumeration does not match: " + lines.get(0));
            throw new ActParsingException("Given enumeration does not match: " + lines.get(0));
        }
    }
}
