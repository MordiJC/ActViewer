package parser;

import container.Section;
import container.SectionBuilder;

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

    private static final int BUFFER_SIZE = 1024;
    private static final String CHAPTER_REGEXP_PATTERN
            = "^\\s*(?i)((?:ROZ)?(?:DZIAŁ))\\s*[A-Za-z]+\\s*$";
    private static final String CHAPTER_WITH_NUMBER_REGEXP_PATTERN =
            "^\\s*(?i)((?:ROZ)?(?:DZIAŁ))\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))\\s*$";
    public static Logger logger = Logger.getLogger("ActParser");

    public Section parse(File inputFile) {
        FileReader fileReader = openFileForReadingAndHandleErrors(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        logger.log(Level.INFO, "Attempting to parse file: " + inputFile.getAbsolutePath());

        List<String> filteredLines = bufferedReader.lines()
                .filter(line -> !line.matches("^(\\u00A9Kancelaria Sejmu.*)|(\\d{4}-\\d{2}-\\d{2})|(Dz\\.U\\..*)|(.)$"))
                .collect(Collectors.toList());

        List<List<String>> chapters = splitIntoChaptersList(filteredLines);

        List<Section> sections = createSectionsStructure(chapters);

        Section rootSection = new Section();

        rootSection.setChildrenSections(sections);

        logger.log(Level.INFO, "Parsing of file succeeded: " + inputFile.getAbsolutePath());

        return null;
    }

    private List<Section> createSectionsStructure(List<List<String>> chapters) {
        List<Section> sections = new ArrayList<>();

        if (chapters.size() == 0) {
            return sections;
        }

        Section preamble = getPreambleIfPresentOrNull(chapters);

        if (preamble != null) {
            sections.add(preamble);
        }

        for (List<String> chapter : chapters) {
            String title;
            String sectionName;
            String sectionIdentifier;

            title = "";
        }

        return sections;
    }

    private Section getPreambleIfPresentOrNull(List<List<String>> chapters) {

        List<String> preambleLines = getLinesIfPreambleOrNull(chapters);

        if (preambleLines == null) {
            return null;
        }

        String preambleTitle = new StringBuilder()
                .append(preambleLines.get(0))
                .append(' ')
                .append(preambleLines.get(1))
                .toString();

        String preambleContent = joinBrokenWordsAndJoinIntoOneString(preambleLines);

        Section preamble = new SectionBuilder()
                .title(preambleTitle)
                .sectionName("Preambuła")
                .content(preambleContent)
                .build();

        return preamble;
    }

    private List<String> getLinesIfPreambleOrNull(List<List<String>> chapters) {
        return chapters.stream()
                .filter(chapter -> chapter.size() >= 2
                        && chapter.get(0).equalsIgnoreCase("KONSTYTUCJA")
                        && chapter.get(1).equalsIgnoreCase("RZECZYPOSPOLITEJ POLSKIEJ"))
                .findFirst()
                .orElse(null);
    }

    private String joinBrokenWordsAndJoinIntoOneString(List<String> preambleLines) {
        List<String> linesPreparedForWordBreakJoining = prepareLinesForWordBreakJoining(preambleLines);

        return String.join(" ", linesPreparedForWordBreakJoining)
                .replace("-\n", "");
    }

    private List<String> prepareLinesForWordBreakJoining(List<String> preambleLines) {
        return preambleLines.subList(2, preambleLines.size())
                .stream()
                .map(line -> line.replaceAll("\\-$", "-\n"))
                .collect(Collectors.toList());
    }

    private List<List<String>> splitIntoChaptersList(List<String> filteredLines) {
        List<List<String>> chapters = new ArrayList<>();
        List<String> currentChapter = new ArrayList<>();

        for (String line : filteredLines) {

            if (line.matches(CHAPTER_WITH_NUMBER_REGEXP_PATTERN)) {
                addChapterIfNotEmpty(chapters, currentChapter);
                currentChapter = new ArrayList<>();
            }

            currentChapter.add(line);
        }
        addChapterIfNotEmpty(chapters, currentChapter);

        return chapters;
    }

    private void addChapterIfNotEmpty(List<List<String>> chapters, List<String> currentChapter) {
        if (currentChapter != null || currentChapter.size() != 0) {
            chapters.add(currentChapter);
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
