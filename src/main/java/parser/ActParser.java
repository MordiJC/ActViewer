package parser;

import container.ActElement;
import parser.actparserutils.SectionParser;

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
    public static final String CHAPTER_REGEXP_PATTERN =
            "^\\s*(?i)(ROZDZIA[Łł])\\s*(\\d+)\\s*$";

    public static final String SECTION_REGEXP_PATTERN =
            "^\\s*(?i)(DZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))([a-zA-Z])?\\s*$";

    private static String INCORRECT_LINES_REGEXP_PATTERN =
            "^(\\u00A9Kancelaria Sejmu.*)|(\\d{4}-\\d{2}-\\d{2})|(Dz\\.U\\..*)|(.)$";

    public static Logger logger = Logger.getLogger("ActParser");

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

        List<List<String>> sections = splitIntoSections(filteredLines);

        List<ActElement> actElements = new SectionParser().parseAll(sections);

        ActElement rootActElement = new ActElement();

        rootActElement.setChildrenActElements(actElements);

        logger.log(Level.INFO, "Parsing of file succeeded: " + inputFile.getAbsolutePath());

        return rootActElement;
    }

    private List<List<String>> splitIntoSections(List<String> filteredLines) {
        List<List<String>> sections = new ArrayList<>();
        List<String> currentSection = new ArrayList<>();

        for (String line : filteredLines) {
            if (line.matches(SECTION_REGEXP_PATTERN)) {
                addSublistIfNotEmpty(sections, currentSection);
                currentSection = new ArrayList<>();
            }
            currentSection.add(line);
        }

        addSublistIfNotEmpty(sections, currentSection);

        return sections;
    }

    private void addSublistIfNotEmpty(List<List<String>> mainList, List<String> sublist) {
        if (sublist != null || sublist.size() != 0) {
            mainList.add(sublist);
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
