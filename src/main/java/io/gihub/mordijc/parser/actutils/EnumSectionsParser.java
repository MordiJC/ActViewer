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
    private String introduction = "";
    private List<ActElement> sections = new ArrayList<>();
    private String summary = "";

    public EnumSectionsParser(List<String> lines) {
        reset(lines);
        parse();
    }

    public void reset(List<String> lines) {
        if (lines == null) {
            throw new IllegalArgumentException("Lines must not be null");
        }
        this.lines = lines;
    }

    public void parse() {
        this.introduction = parseIntroduction();
        this.sections = getEnumSections();
        this.summary = getSummary();
    }

    private List<ActElement> getEnumSections() {
        if (lines.size() == 0) {
            return new ArrayList<>();
        }

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

        for (List<String> section :
                sections.subList(0, sections.size() != 0 ? sections.size() - 1 : 0)) {
            results.add(
                    createElement(matcher, section)
            );
        }

        List<String> lastSection = sections.get(sections.size() - 1);

        List<List<String>> splitSection =
                Lists.splitIncludingDelimiterAsFirstElement(
                        lastSection,
                        e -> e.matches("^[-\\u2012\\u2013\\u2014\\u2015].*")
                );

        results.add(createElement(matcher, splitSection.get(0)));

        if (splitSection.size() == 2) {
            this.summary = Strings.glueBrokenText(splitSection.get(1));
        } else if (splitSection.size() > 2) {
            throw new ParsingException("Given section is invalid or has multiple lines starting with dash.");
        }

        return results;
    }

    private ActElement createElement(Matcher matcher, List<String> section) {
        matcher.reset(section.get(0));

        if (!matcher.matches()) {
            throw new ParsingException("Given section is not enum section.");
        }

        EnumSectionsParser enumSectionsParser =
                new EnumSectionsParser(section.subList(1, section.size()));

        return new ActElementBuilder()
                .identifier(
                        Regex.getGroupOrEmptyString(matcher, "identifier")
                )
                .content(
                        enumSectionsParser.getIntroduction()
                )
                .summary(
                        enumSectionsParser.getSummary()
                )
                .childrenElements(
                        enumSectionsParser.getSections()
                )
                .build();
    }

    private String parseIntroduction() {
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

    public String getIntroduction() {
        return introduction;
    }

    public List<ActElement> getSections() {
        return sections;
    }

    public String getSummary() {
        return summary;
    }
}
