package parser;

import container.ActElement;
import container.ActElementBuilder;
import parser.actutils.ArticlesParser;
import util.Lists;
import util.Log;
import util.Regex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ActParser {
    private static String INCORRECT_LINES_REGEXP_PATTERN =
            "^(\\u00A9Kancelaria Sejmu.*)|" +
                    "(\\d{4}-\\d{2}-\\d{2})|" +
                    "(Dz\\.U\\..*)|" +
                    "(?:.)|" +
                    "(.*\\(uchylony\\)\\s*)|" +
                    "(.*\\(pominięty\\)\\s*)$";

    private List<String> removeIncorrectLines(List<String> lines) {
        return lines.stream()
                .filter(line -> !line.matches(INCORRECT_LINES_REGEXP_PATTERN))
                .collect(Collectors.toList());
    }

    public ActElement parse(File inputFile) {
        FileReader fileReader = openFileForReadingAndHandleErrors(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        Log.getLogger().info("Attempting to parse file: " + inputFile.getAbsolutePath());

        List<String> filteredLines = removeIncorrectLines(bufferedReader.lines().collect(Collectors.toList()));

        filteredLines = prepareForParsing(filteredLines);

        if (filteredLines.get(0).equalsIgnoreCase("ustawa")) {
            return parseAct(filteredLines);

        } else if (filteredLines.get(0).equalsIgnoreCase("konstytucja")) {
            return parseConstitution(filteredLines);
        }

        throw new ActParsingException("Invalid file. This is neither a constitution nor a act.");
    }

    private List<String> prepareForParsing(List<String> lines) {
        List<String> filteredLines = new ArrayList<>();

        Pattern pattern = Pattern.compile(ActParserSectionPattern.ARTICLE.pattern);
        Matcher matcher = pattern.matcher("");

        for (String s : lines) {
            matcher.reset(s);

            if (matcher.matches()) {
                filteredLines.add(matcher.group(1));
                if (!matcher.group(4).isEmpty()) {
                    filteredLines.add(matcher.group(4));
                }
            } else {
                filteredLines.add(s);
            }
        }

        return filteredLines;
    }

    private ActElement parseAct(List<String> lines) {
        if (lines.size() < 4) {
            Log.getLogger().severe("Given act is too short. It should contain at least 4 lines.");
            throw new ActParsingException("Given act is too short. It should contain at least 4 lines.");
        }

        ActElementBuilder rootActElementBuilder =
                new ActElementBuilder()
                        .typeName((lines.get(0) + " " + lines.get(2))
                                .toUpperCase(Locale.forLanguageTag("pl_PL")));

        rootActElementBuilder.childrenElements(
                parseSection(lines.subList(3, lines.size()), ActParserSectionPattern.GENERAL_SECTIONS[0])
        );


        return rootActElementBuilder.build();
    }

    private List<ActElement> parseSection(List<String> lines, ActParserSectionPattern apsp) {
        if (lines == null || lines.size() == 0) {
            throw new IllegalArgumentException("Lines must not be null and have at least 1 element");
        }

        if (apsp == ActParserSectionPattern.ARTICLE) {
            return new ArticlesParser().parse(lines);
        }

        List<List<String>> sectionsLines =
                Lists.splitIncludingDelimiterAsFirstElement(
                        lines,
                        e -> e.matches(apsp.pattern));

        if (sectionsLines.size() == 0) {
            throw new IllegalStateException();
        } else if (sectionsLines.size() == 1) {
            if (sectionsLines.get(0).get(0).matches(apsp.pattern)) {
                return parseSectionWithTitle(sectionsLines.get(0), apsp);
            } else {
                if (apsp.hasNext()) {
                    return parseSection(sectionsLines.get(0), apsp.next());
                } else {
                    throw new IllegalStateException("Something strange happened during parsing.");
                }
            }
        } else {
            List<ActElement> actElements = new ArrayList<>();

            for (List<String> ls : sectionsLines) {
                actElements.addAll(parseSectionWithTitle(ls, apsp));
            }

            return actElements;
        }
    }

    private List<ActElement> parseSectionWithTitle(List<String> lines, ActParserSectionPattern apsp) {
        Pattern pattern = Pattern.compile(apsp.pattern);
        Matcher matcher = pattern.matcher(lines.get(0));

        ActElementBuilder actElementBuilder = new ActElementBuilder();

        if (!matcher.matches()) {
            return parseSection(lines, apsp.next());
        }

        actElementBuilder.identifier(Regex.getGroupOrEmptyString(matcher, "identifier"))
                .typeName(Regex.getGroupOrEmptyString(matcher, "typeName"));

        List<ActElement> subElements =
                parseSection(lines.subList(1, lines.size()), apsp.next());

        if (apsp.hasTitle) {
            if (subElements.size() >= 2) {
                actElementBuilder.title(lines.get(1));

                if (subElements.size() > 0) {
                    subElements.remove(0);
                }
            } else {
                Log.getLogger().severe(("Section have to be at least 2 lines long: "
                        + lines.stream().collect(Collectors.joining("\n"))));
                throw new ActParsingException("Section have to be at least 2 lines long: "
                        + lines.stream().collect(Collectors.joining("\n")));
            }
        }

        actElementBuilder.childrenElements(subElements);

        List<ActElement> result = new ArrayList<>();
        result.add(actElementBuilder.build());
        return result;
    }

    private ActElement parseConstitution(List<String> lines) {
        ActElementBuilder rootActElementBuilder =
                new ActElementBuilder()
                        .typeName(lines.get(0))
                        .title(lines.get(1))
                        .content(lines.get(2));

        rootActElementBuilder.childrenElements(
                parseSection(lines.subList(3, lines.size()), ActParserSectionPattern.GENERAL_SECTIONS[0])
        );

        return rootActElementBuilder.build();
    }

    private FileReader openFileForReadingAndHandleErrors(File inputFile) throws ActParsingException {
        FileReader fileReader;

        try {
            fileReader = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            Log.getLogger().severe("Unable to open file: " + inputFile.getAbsolutePath());
            throw new ActParsingException("Unable to open file: " + inputFile.getAbsolutePath(), e);
        }

        Log.getLogger().severe("File opened: " + inputFile.getAbsolutePath());

        return fileReader;
    }
}
