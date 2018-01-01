package io.gihub.mordijc.parser.actutils;

import io.gihub.mordijc.container.ActElement;
import io.gihub.mordijc.container.ActElementBuilder;
import io.gihub.mordijc.parser.ActParserSection;
import io.gihub.mordijc.util.Lists;
import io.gihub.mordijc.util.Regex;
import io.gihub.mordijc.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.gihub.mordijc.parser.ActParserSection.ENUM_SECTIONS;

public class EnumSectionsParser {
    private List<String> lines;

    public EnumSectionsParser(List<String> lines) {
        reset(lines);
    }

    public void reset(List<String> lines) {
        if (lines == null || lines.size() == 0) {
            throw new IllegalArgumentException("Lines must not be null or its size have to be > 0");
        }
        this.lines = lines;
    }

    public List<ActElement> getEnumSections() {

        List<String> currentSection = lines.subList(
                getIntroductionLines().size(),
                lines.size()
        );

        List<ActElement> results = new ArrayList<>();

        if (currentSection.size() == 0) {
            return results;
        }

        ActParserSection currentActParserSection =
                getMatchingOrNull(currentSection.get(0), ENUM_SECTIONS);

        if (currentActParserSection == null) {
            throw new ParsingException("This should never happen.");
        }

        List<List<String>> sections =
                Lists.splitIncludingDelimiterAsFirstElement(
                        currentSection,
                        e -> e.matches(currentActParserSection.pattern)
                );

        Matcher matcher =
                Pattern.compile(currentActParserSection.pattern).matcher("");

        for (List<String> section : sections) {
            matcher.reset(section.get(0));

            if (!matcher.matches()) {
                throw new ParsingException("Given section is not enum section.");
            }

            EnumSectionsParser enumSectionsParser =
                    new EnumSectionsParser(section.subList(1, section.size()));

            results.add(
                    new ActElementBuilder()
                            .identifier(
                                    Regex.getGroupOrEmptyString(matcher, "identifier")
                            )
                            .content(
                                    enumSectionsParser.getIntroduction()
                            )
                            .childrenElements(
                                    enumSectionsParser.getEnumSections()
                            )
                            .build()
            );
        }

        return results;
    }

    public String getIntroduction() {
        return Strings.glueBrokenText(
                getIntroductionLines()
        );
    }

    private List<String> getIntroductionLines() {
        return lines.stream().takeWhile(e -> {
            for (ActParserSection actParserSection : ENUM_SECTIONS) {
                if (e.matches(actParserSection.pattern)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    private ActParserSection getMatchingOrNull(String line, ActParserSection[] actParserSections) {
        for (ActParserSection actParserSection : actParserSections) {
            if (line.matches(actParserSection.pattern)) {
                return actParserSection;
            }
        }

        return null;
    }
}
