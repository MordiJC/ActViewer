package io.gihub.mordijc.parser.actutils;

import io.gihub.mordijc.container.ActElement;
import io.gihub.mordijc.container.ActElementBuilder;
import io.gihub.mordijc.parser.ActParserSection;
import io.gihub.mordijc.util.Lists;
import io.gihub.mordijc.util.Regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.gihub.mordijc.parser.ActParserSection.ARTICLE;
import static io.gihub.mordijc.parser.ActParserSection.GENERAL_SECTIONS;

public class SectionsParser {
    /**
     * Parses given content as general sections from {@code ActParserSection#GENERAL_SECTIONS}
     *
     * @param lines lines to parse.
     * @return List of sections
     * @throws ParsingException
     */
    public List<ActElement> parse(List<String> lines) throws ParsingException {
        if (lines == null || lines.size() == 0) {
            throw new IllegalArgumentException("Argument must not be a null and have to have elements");
        }

        List<ActElement> result = new ArrayList<>();


        return result;
    }

    private List<ActElement> parseSections(List<String> lines, ActParserSection actParserSection) throws ParsingException {
        assert (lines != null && lines.size() != 0);
        assert (actParserSection != null);

        if (actParserSection == ARTICLE) {
            return new ArticlesParser().parse(lines);
        } else if (Arrays.asList(GENERAL_SECTIONS).indexOf(actParserSection) == -1) {
            throw new IllegalArgumentException("ActParserSection is invalid");
        }

        List<List<String>> sections =
                Lists.splitIncludingDelimiterAsFirstElement(lines, e -> e.matches(actParserSection.pattern));

        List<ActElement> result = new ArrayList<>();

        ActElement actElement;

        for (List<String> section : sections) {
            actElement = parseSection(section, actParserSection);

            if (actElement != null) {
                result.add(actElement);
            }
        }

        return result;
    }

    private ActParserSection getMatchingSectionStartingFrom(String firstLine, ActParserSection actParserSection) {
        for (int i = actParserSection.ordinal(); i < ActParserSection.values().length; ++i) {
            if (firstLine.matches(ActParserSection.values()[i].pattern)) {
                return actParserSection;
            }
        }

        throw new ParsingException("No matching pattern for this section.");
    }

    private ActElement parseSection(List<String> lines, ActParserSection actParserSection) throws ParsingException {
        assert (lines != null && lines.size() != 0);
        assert (actParserSection != null && actParserSection.ordinal() < ARTICLE.ordinal());

        ActElementBuilder actElementBuilder = new ActElementBuilder();

        ActParserSection currentSection = getMatchingSectionStartingFrom(lines.get(0), actParserSection);

        Pattern pattern = Pattern.compile(currentSection.pattern);
        Matcher matcher = pattern.matcher(lines.get(0));

        matcher.matches();

        actElementBuilder.typeName(
                Regex.getGroupOrEmptyString(matcher, "typeName")
        ).identifier(
                Regex.getGroupOrEmptyString(matcher, "identifier")
        );

        List<String> sectionContent;

        if (currentSection.hasTitle) {
            actElementBuilder.title(lines.get(1));
            sectionContent = lines.subList(2, lines.size());
        } else {
            sectionContent = lines.subList(1, lines.size());
        }

        actElementBuilder.childrenElements(parseSections(sectionContent, currentSection.next()));

        return actElementBuilder.build();
    }
}
