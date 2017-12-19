package parser;

import container.ActElement;
import container.ActElementBuilder;
import parser.actutils.ChapterParser;
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
import java.util.stream.Collectors;

public class ActParser {
    private static final String CHAPTER_REGEXP_PATTERN =
            "^\\s*(?i)(ROZDZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})|\\d+)\\s*$";

    private static final String SECTION_REGEXP_PATTERN =
            "^\\s*(?i)(DZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))([a-zA-Z])?\\s*$";

    private static String INCORRECT_LINES_REGEXP_PATTERN =
            "^(\\u00A9Kancelaria Sejmu.*)|(\\d{4}-\\d{2}-\\d{2})|(Dz\\.U\\..*)|(?:.)$";

    private static Logger logger = Logger.getLogger("ActParser");

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

        if (filteredLines.get(0).equalsIgnoreCase("ustawa")) {
            rootActElementBuilder
                    .typeName(filteredLines.get(0))
                    .title(filteredLines.get(1) + "\n" + filteredLines.get(2));

            List<String> actLines = filteredLines.subList(3, filteredLines.size());

            List<List<String>> sections =
                    Lists.splitIncludingDelimiterAsFirstElement(actLines, e -> e.matches(SECTION_REGEXP_PATTERN));

            sections.forEach(section -> {
                section.forEach(System.out::println);
                System.out.println("\n--------------------------------------------------\n");
            });

        } else if (filteredLines.get(0).equalsIgnoreCase("konstytucja")) {
            return parseConstitution(filteredLines);
        }

        throw new ActParsingException("Invalid file. This is neither a constitution nor a act.");
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

        constitutionElements.addAll(ChapterParser.parseAll(chapters.subList(1, chapters.size())));

        return rootActElementBuilder.build();
    }

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
