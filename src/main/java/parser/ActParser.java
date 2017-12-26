package parser;

import container.ActElement;
import container.ActElementBuilder;
import util.Lists;
import util.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ActParser {
    private static String INCORRECT_LINES_REGEXP_PATTERN =
            "^(\\u00A9Kancelaria Sejmu.*)|(\\d{4}-\\d{2}-\\d{2})|(Dz\\.U\\..*)|(?:.)$";

    private Logger logger = Logger.getLogger("ActParser");

    private List<String> removeIncorrectLines(List<String> lines) {
        return lines.stream()
                .filter(line -> !line.matches(INCORRECT_LINES_REGEXP_PATTERN))
                .collect(Collectors.toList());
    }

    public ActElement parse(File inputFile) {
        FileReader fileReader = openFileForReadingAndHandleErrors(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        logger.log(Level.INFO, "Attempting to parse file: " + inputFile.getAbsolutePath());

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
            logger.severe("Given act is too short. It should contain at least 4 lines.");
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
            // TODO: parse article

            List<List<String>> sectionsLines =
                    Lists.splitIncludingDelimiterAsFirstElement(
                            lines,
                            e -> e.matches(apsp.pattern));

            Pattern pattern = Pattern.compile(apsp.pattern);
            Matcher matcher;
            List<ActElement> result = new ArrayList<>();

            for (List<String> section : sectionsLines) {
                matcher = pattern.matcher(section.get(0));
                if (!matcher.matches()) {
                    result.add(new ActElementBuilder().content(Strings.glueBrokenText(section)).build());
                } else {
                    result.add(
                            new ActElementBuilder()
                                    .identifier(getGroupOrEmptyString(matcher, "identifier"))
                                    .typeName(getGroupOrEmptyString(matcher, "typeName"))
                                    .content(Strings.glueBrokenText(section.subList(1, section.size())))
                                    .build()
                    );
                }
            }
            return result;
        }

        List<List<String>> sectionsLines =
                Lists.splitIncludingDelimiterAsFirstElement(
                        lines,
                        e -> e.matches(apsp.pattern));

        if (sectionsLines.size() == 0) {
            throw new IllegalStateException();
        }

        if (sectionsLines.size() == 1) {
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

        actElementBuilder.identifier(getGroupOrEmptyString(matcher, "identifier"))
                .typeName(getGroupOrEmptyString(matcher, "typeName"));

        List<ActElement> subElements =
                parseSection(lines.subList(1, lines.size()), apsp.next());

        if (apsp.hasTitle) {
            if (subElements.size() >= 2) {
                actElementBuilder.title(lines.get(1));

                subElements = subElements.subList(1, subElements.size());
            } else {
                logger.severe(("Section have to be at least 2 lines long: "
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

    private String getGroupOrEmptyString(Matcher matcher, String groupName) {
        try {
            return matcher.group(groupName);
        } catch (IllegalArgumentException e) {
            return "";
        }
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


    /**
     * Create preamble from first chapter. GIVEN CHAPTER HAVE TO BE PREAMBLE.
     *
     * @param chapters list of chapters.
     * @return <code>ActElement</code> containing preamble.
     */
    private ActElement createPreamble(List<List<String>> chapters) {
        return new ActElementBuilder()
                .typeName("Preambuła")
                .content(Strings.glueBrokenText(chapters.get(0).subList(3, chapters.get(0).size())))
                .build();
    }

    private void checkIfThereIsOnlyOneSectionAndThrowIfNot(List<List<String>> sections) {
        if (sections.size() != 1) {
            throw new ActParsingException("Given file is not constitution.");
        }
    }

    private FileReader openFileForReadingAndHandleErrors(File inputFile) throws ActParsingException {
        FileReader fileReader;

        try {
            fileReader = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Unable to open file: " + inputFile.getAbsolutePath());
            throw new ActParsingException("Unable to open file: " + inputFile.getAbsolutePath(), e);
        }

        logger.log(Level.INFO, "File opened: " + inputFile.getAbsolutePath());

        return fileReader;
    }
}
