package io.github.mordijc.parser.actutils;

import io.github.mordijc.container.ActElement;
import io.github.mordijc.container.ActElementBuilder;
import io.github.mordijc.parser.ActParserSection;
import io.github.mordijc.util.Lists;
import io.github.mordijc.util.Regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.github.mordijc.parser.ActParserSection.ARTICLE;
import static io.github.mordijc.parser.ActParserSection.GENERAL_SECTIONS;

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

        return parseSections(lines, GENERAL_SECTIONS[0]);
    }

    private List<ActElement> parseSections(List<String> lines, ActParserSection actParserSection) throws ParsingException {
        assert (lines != null && lines.size() != 0);
        assert (actParserSection != null);

        ActParserSection detectedSection = getMatchingSectionStartingFrom(lines.get(0), actParserSection);

        if (detectedSection == ARTICLE) {
            return new ArticlesParser().parse(lines);
        } else if (Arrays.asList(GENERAL_SECTIONS).indexOf(detectedSection) == -1) {
//            Log.getLogger().severe(lines.get(0));
//            Log.getLogger().severe(detectedSection.name());
            throw new IllegalArgumentException("ActParserSection is invalid");
        }

        List<List<String>> sections =
                Lists.splitIncludingDelimiterAsFirstElement(lines, e -> e.matches(detectedSection.pattern));

        List<ActElement> result = new ArrayList<>();

        ActElement actElement;

        for (List<String> section : sections) {
            actElement = parseSection(section, detectedSection);

            if (actElement != null) {
                result.add(actElement);
            }
        }

        return result;
    }

    private ActElement parseSection(List<String> lines, ActParserSection actParserSection) throws ParsingException {
        assert (lines != null && lines.size() != 0);
        assert (actParserSection != null && actParserSection.ordinal() < ARTICLE.ordinal());

        ActElementBuilder actElementBuilder = new ActElementBuilder();

        ActParserSection currentSection = getMatchingSectionStartingFrom(lines.get(0), actParserSection);

        Pattern pattern = Pattern.compile(currentSection.pattern);
        Matcher matcher = pattern.matcher(lines.get(0));

        if (!matcher.matches()) {
//            Log.getLogger().severe(lines.get(0));
            throw new ParsingException("Given section is not valid.");
        }

        actElementBuilder.typeName(
                Regex.getGroupOrEmptyString(matcher, "typeName")
        ).identifier(
                Regex.getGroupOrEmptyString(matcher, "identifier")
        );

        List<String> sectionContent;

        if (currentSection.hasTitle) {
            List<String> title =
                    lines.stream().skip(1).takeWhile(
                            e -> {
                                for (ActParserSection parserSection : ActParserSection.ELEMENT_SECTIONS) {
                                    if (e.matches(parserSection.pattern)) {
                                        return false;
                                    }
                                }
                                return true;
                            }
                    ).collect(Collectors.toList());

            actElementBuilder.title(
                title.stream().collect(Collectors.joining(" "))
            );

            sectionContent = lines.subList(1 + title.size(), lines.size());
        } else {
            sectionContent = lines.subList(1, lines.size());
        }

        actElementBuilder.childrenElements(parseSections(sectionContent, currentSection.next()))
                .type(currentSection);

        return actElementBuilder.build();
    }

    private ActParserSection getMatchingSectionStartingFrom(String firstLine, ActParserSection actParserSection) {
        for (int i = actParserSection.ordinal(); i < ActParserSection.values().length; ++i) {
            if (firstLine.matches(ActParserSection.values()[i].pattern)) {
                return ActParserSection.values()[i];
            }
        }

//        Log.getLogger().severe(firstLine);
        throw new ParsingException("No matching pattern for this section.");
    }
}
