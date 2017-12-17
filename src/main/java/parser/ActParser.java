package parser;

import container.ActElement;
import parser.actparserutils.PreambleUtils;

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
            "^\\s*(?i)(ROZDZIA[Łł])\\s*(\\d+)\\s*$";

    private static final String SECTION_REGEXP_PATTERN =
            "^\\s*(?i)(DZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))\\s*$";

    public static Logger logger = Logger.getLogger("ActParser");

    public ActElement parse(File inputFile) {
        FileReader fileReader = openFileForReadingAndHandleErrors(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        logger.log(Level.INFO, "Attempting to parse file: " + inputFile.getAbsolutePath());

        List<String> filteredLines = bufferedReader.lines()
                .filter(line -> !line.matches("^(\\u00A9Kancelaria Sejmu.*)|(\\d{4}-\\d{2}-\\d{2})|(Dz\\.U\\..*)|(.)$"))
                .collect(Collectors.toList());

        List<List<List<String>>> sections = splitIntoSectionsWithChaptersList(filteredLines);

        List<ActElement> finalListOfActElements = createSectionsStructure(sections);

        ActElement rootActElement = new ActElement();

        rootActElement.setChildrenActElements(finalListOfActElements);

        logger.log(Level.INFO, "Parsing of file succeeded: " + inputFile.getAbsolutePath());

        return null;
    }

    // TODO: naprawic
    private List<ActElement> createSectionsStructure(List<List<List<String>>> sections) {
        List<ActElement> subsectionsList = new ArrayList<>();

        if (sections.size() == 0) {
            return subsectionsList;
        }

        if (sections.size() == 1) {
            List<List<String>> currentSection = sections.get(0);

            PreambleUtils.addPreambleIfPresent(currentSection, subsectionsList);

            PreambleUtils.removePreambleFromCurrentSectionIfPresent(currentSection);

            List<ActElement> chapters = parseChapters();

            subsectionsList.addAll(chapters);
        } else {

        }

        return subsectionsList;
    }

    private List<List<List<String>>> splitIntoSectionsWithChaptersList(List<String> filteredLines) {
        List<List<String>> sections = splitIntoSections(filteredLines);

        List<List<List<String>>> sectionsWithChapters = splitSectionsIntoChapters(sections);

        return sectionsWithChapters;
    }

    private List<List<List<String>>> splitSectionsIntoChapters(List<List<String>> sections) {
        List<List<List<String>>> sectionsWithChapters = new ArrayList<>();
        List<List<String>> currentSection = new ArrayList<>();

        for (List<String> section : sections) {
            splitSectionIntoChapters(currentSection, section);
        }
        return sectionsWithChapters;
    }

    private void splitSectionIntoChapters(List<List<String>> currentSection, List<String> section) {
        List<String> currentChapter = new ArrayList<>();
        for (String line : section) {
            if (line.matches(CHAPTER_REGEXP_PATTERN)) {
                addSublistIfNotEmpty(currentSection, currentChapter);
            }
            currentChapter.add(line);
        }
        addSublistIfNotEmpty(currentSection, currentChapter);
    }

    private List<List<String>> splitIntoSections(List<String> filteredLines) {
        List<List<String>> sections = new ArrayList<>();
        List<String> currentChapter = new ArrayList<>();

        // split by sections
        for (String line : filteredLines) {
            if (line.matches(SECTION_REGEXP_PATTERN)) {
                addSublistIfNotEmpty(sections, currentChapter);
                currentChapter = new ArrayList<>();
            }
            currentChapter.add(line);
        }
        addSublistIfNotEmpty(sections, currentChapter);

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
