package io.gihub.mordijc.parser;

import io.gihub.mordijc.container.ActElement;
import io.gihub.mordijc.container.ActElementBuilder;
import io.gihub.mordijc.parser.actutils.ActPreparser;
import io.gihub.mordijc.parser.actutils.ParsingException;
import io.gihub.mordijc.parser.actutils.PreambleParser;
import io.gihub.mordijc.parser.actutils.SectionsParser;
import io.gihub.mordijc.util.Log;

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

//        Log.getLogger().info("Attempting to parse file: " + inputFile.getAbsolutePath());

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
        if (lines.size() < 4) {
//            Log.getLogger().severe("Given act is too short. It should contain at least 4 lines.");
            throw new ParsingException("Given act is too short. It should contain at least 4 lines.");
        }

        ActElementBuilder rootActElementBuilder =
                new ActElementBuilder()
                        .title((lines.get(0).trim() + " " + lines.get(2).trim())
                                .toUpperCase(Locale.forLanguageTag("pl_PL")));

        List<String> actContent = lines.subList(3, lines.size());

        rootActElementBuilder.childrenElements(
                new SectionsParser().parse(actContent)
        );

        return rootActElementBuilder.build();
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
//            Log.getLogger().severe("Unable to open file: " + inputFile.getAbsolutePath());
            throw new ParsingException("Unable to open file: " + inputFile.getAbsolutePath(), e);
        }

//        Log.getLogger().severe("File opened: " + inputFile.getAbsolutePath());

        return fileReader;
    }
}
