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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static parser.ActParserSectionPattern.CHAPTER;

public class ActParser {
    private static final String CHAPTER_REGEXP_PATTERN =
            "^\\s*(?i)(ROZDZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})|\\d+)\\s*$";

    private static final String SECTION_REGEXP_PATTERN =
            "^\\s*(?i)(DZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))([a-zA-Z])?\\s*$";

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
//            rootActElementBuilder
//                    .typeName(filteredLines.get(0))
//                    .title(filteredLines.get(1) + "\n" + filteredLines.get(2));

            List<String> actLines = filteredLines.subList(3, filteredLines.size());

            List<List<String>> sections =
                    Lists.splitIncludingDelimiterAsFirstElement(actLines, e -> e.matches(SECTION_REGEXP_PATTERN));

            return null;

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

    private ActElement parseConstitution(List<String> lines) {
        ActElementBuilder rootActElementBuilder =
                new ActElementBuilder()
                        .typeName(lines.get(0) + "\n" + lines.get(1))
                        .content(lines.get(2));

        List<List<String>> sections =
                Lists.splitIncludingDelimiterAsFirstElement(lines, e -> e.matches(SECTION_REGEXP_PATTERN));

        checkIfThereIsOnlyOneSectionAndThrowIfNot(sections);

        List<List<String>> chapters =
                Lists.splitIncludingDelimiterAsFirstElement(sections.get(0), e -> e.matches(CHAPTER_REGEXP_PATTERN));

        List<ActElement> constitutionElements = new ArrayList<>();

        ActElement preamble = createPreamble(chapters);

        constitutionElements.add(preamble);

        chapters = chapters.subList(1, chapters.size());

        ActElement actElement;
        for (List<String> chapter : chapters) {
            actElement = parseSection(chapter, CHAPTER);

            if (actElement != null) {
                constitutionElements.add(actElement);
            }
        }

        rootActElementBuilder.childrenElements(constitutionElements);

        return rootActElementBuilder.build();
    }

    private ActElement parseSection(List<String> sectionLines, ActParserSectionPattern sectionType) {
        ActElementBuilder actElementBuilder = new ActElementBuilder();

        if (sectionLines.size() == 0) {
            return null;
        }

        logger.info(String.format("Parsing section type: %s\nLine: `%s`",
                sectionType.toString(),
                sectionLines.get(0)));

        Pattern pattern = Pattern.compile(sectionType.pattern);
        Matcher matcher = pattern.matcher(sectionLines.get(0));

        if (!matcher.matches()) {
            logger.severe(new StringBuilder()
                    .append("Given section does not match pattern.\n")
                    .append(String.format("Section type: %s\nLine: `%s`",
                            sectionType.toString(),
                            sectionLines.get(0))
                    ).toString()
            );
            throw new ActParsingException("Given section does not match pattern.");
        } // TODO: Sekcja nie musi się zaczynać matchującym

        boolean firstLineProcessed = false;

        if (sectionType.typeNameGroupNumber >= 0) {
            actElementBuilder.typeName(matcher.group(sectionType.typeNameGroupNumber));
            firstLineProcessed = true;
        }

        if (sectionType.identifierGroupNumber >= 0) {
            actElementBuilder.identifier(matcher.group(sectionType.identifierGroupNumber));
            firstLineProcessed = true;
        }

        if (sectionType.titleInNextLine) {
            if (sectionLines.size() < 2) {
                String exceptionMessage = String.format("Section requires title. Line: `%`", sectionLines.get(0));
                throw new ActParsingException(exceptionMessage);
            } else {
                actElementBuilder.title(sectionLines.get(1));
            }
        }

        List<String> linesToProcess = new ArrayList(sectionLines);

        if(firstLineProcessed) {
            if(sectionType.titleInNextLine) {
                linesToProcess = linesToProcess.subList(2, linesToProcess.size());
            } else {
                linesToProcess = linesToProcess.subList(1, linesToProcess.size());
            }
        }

        if(sectionType.ordinal() + 1 >= ActParserSectionPattern.values().length) {
            actElementBuilder.content(
                    Strings.glueBrokenText(linesToProcess)
            );
        } else {
            List<List<String>> splitElements = Lists.splitIncludingDelimiterAsFirstElement(
                    linesToProcess,
                    e -> e.matches(ActParserSectionPattern.values()[sectionType.ordinal()+1].pattern)
            );

            List<ActElement> subElements = new ArrayList<>();

            ActElement actElement;
            for (List<String> lines : splitElements) {
                actElement = parseSection(lines, ActParserSectionPattern.values()[sectionType.ordinal()+1]);

                if (actElement != null) {
                    subElements.add(actElement);
                }
            }
        }

        return actElementBuilder.build();
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
