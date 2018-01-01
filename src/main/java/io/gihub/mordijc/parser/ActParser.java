package io.gihub.mordijc.parser;

import io.gihub.mordijc.container.ActElement;
import io.gihub.mordijc.container.ActElementBuilder;
import io.gihub.mordijc.parser.actutils.*;
import io.gihub.mordijc.util.Lists;
import io.gihub.mordijc.util.Log;
import io.gihub.mordijc.util.Regex;

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

    public ActElement parse(File inputFile) {
        FileReader fileReader = openFileForReadingAndHandleErrors(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        Log.getLogger().info("Attempting to parse file: " + inputFile.getAbsolutePath());

        List<String> filteredLines = new ActPreparser().prepare(bufferedReader.lines().collect(Collectors.toList()));

        filteredLines = prepareForParsing(filteredLines);

        if (filteredLines.get(0).equalsIgnoreCase("ustawa")) {
            return parseAct(filteredLines);

        } else if (filteredLines.get(0).equalsIgnoreCase("konstytucja")) {
            return parseConstitution(filteredLines);
        }

        throw new ParsingException("Invalid file. This is neither a constitution nor a act.");
    }

    private List<String> prepareForParsing(List<String> lines) {
        List<String> filteredLines = new ArrayList<>();

        Pattern pattern = Pattern.compile(ActParserSection.ARTICLE.pattern);
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
        // TODO: FIX
        if (lines.size() < 4) {
            Log.getLogger().severe("Given act is too short. It should contain at least 4 lines.");
            throw new ParsingException("Given act is too short. It should contain at least 4 lines.");
        }

        ActElementBuilder rootActElementBuilder =
                new ActElementBuilder()
                        .typeName((lines.get(0) + " " + lines.get(2))
                                .toUpperCase(Locale.forLanguageTag("pl_PL")));

        rootActElementBuilder.childrenElements(
                parseSection(lines.subList(3, lines.size()), ActParserSection.GENERAL_SECTIONS[0])
        );


        return rootActElementBuilder.build();
    }

    private List<ActElement> parseSection(List<String> lines, ActParserSection apsp) {
        if (lines == null || lines.size() == 0) {
            throw new IllegalArgumentException("Lines must not be null and have at least 1 element");
        }

        if (apsp == ActParserSection.ARTICLE) {
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

    private List<ActElement> parseSectionWithTitle(List<String> lines, ActParserSection apsp) {
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
                throw new ParsingException("Section have to be at least 2 lines long: "
                        + lines.stream().collect(Collectors.joining("\n")));
            }
        }

        actElementBuilder.childrenElements(subElements);

        List<ActElement> result = new ArrayList<>();
        result.add(actElementBuilder.build());
        return result;
    }

    private ActElement parseConstitution(List<String> lines) {
        PreambleParser preambleParser = new PreambleParser(lines);
        ActElement constitution = preambleParser.parse();

        List<String> constitutionContent = lines.subList(
                preambleParser.getPreambleLinesCount() + 3, // 3 first lines of constitution
                lines.size()
        );

        constitution.setChildrenActElements(
                new SectionsParser().parse(constitutionContent)
        );

        return constitution;
    }

    private FileReader openFileForReadingAndHandleErrors(File inputFile) throws ParsingException {
        FileReader fileReader;

        try {
            fileReader = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            Log.getLogger().severe("Unable to open file: " + inputFile.getAbsolutePath());
            throw new ParsingException("Unable to open file: " + inputFile.getAbsolutePath(), e);
        }

        Log.getLogger().severe("File opened: " + inputFile.getAbsolutePath());

        return fileReader;
    }
}
