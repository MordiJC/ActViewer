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
import static parser.ActParserSectionPattern.PARAGRAPH;

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


                actElementBuilder.content(new ParagraphsParser().getIntroduction(section.subList(1, section.size()), PARAGRAPH));
                actElementBuilder.childrenElements(elements);

                result.add(actElementBuilder.build());
            }
        }

        return result;
    }

    private List<ActElement> parseArticleContent(List<String> lines) {
        return new ParagraphsParser().getParagraphs(lines);
    }

    private void checkIfMatchesOrThrow(List<String> lines, ActParserSectionPattern enumSection) {
        if (!lines.get(0).matches(enumSection.pattern)) {
            Log.getLogger().severe("Given enumeration does not match: " + lines.get(0));
            throw new ActParsingException("Given enumeration does not match: " + lines.get(0));
        }
    }
}
