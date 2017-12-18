package parser.actparserutils;

import container.ActElement;
import container.ActElementBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used for act section parsing.
 */
public class SectionParser {

    public static final String SECTION_REGEXP_PATTERN =
            "^\\s*(?i)(DZIA[Łł])\\s*((M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))([a-zA-Z]))?\\s*$";

    /**
     * Parse section of act given as list of lines.
     *
     * @param sectionLines list of section lines.
     * @return parsed and processed act elements tree as list.
     */
    public List<ActElement> parse(List<String> sectionLines) {
        List<ActElement> sectionElements = new ArrayList<>();


        return sectionElements;
    }

    public List<ActElement> parseAll(List<List<String>> sections) {
        List<ActElement> actElements = new ArrayList<>();

        if (sections.isEmpty()) {
            return actElements;
        } else if (sections.size() == 1) {
            parseSingleSingleSectionWithPreambleChecking(sections, actElements);
        } else {
            for (List<String> sectionLines : sections) {
                if (sectionLines.size() >= 2
                        && sectionLines.get(0).matches(SECTION_REGEXP_PATTERN)) {

                    Pattern sectionPattern = Pattern.compile(SECTION_REGEXP_PATTERN);
                    Matcher sectionTypeNameMatcher = sectionPattern.matcher(sectionLines.get(0));

                    ActElement sectionElement = new ActElementBuilder()
                            .typeName(sectionTypeNameMatcher.group(1))
                            .identifier(sectionTypeNameMatcher.group(2))
                            .title(getIfNotElement(sectionLines.get(1)))
                            .build();

                }

            }
        }

        return actElements;
    }

    private void parseSingleSingleSectionWithPreambleChecking(List<List<String>> sections, List<ActElement> actElements) {
        PreambleParser preambleParser = new PreambleParser();
        ActElement preambleElement = preambleParser.parse(sections.get(0), true);

        if (preambleElement != null) {
            actElements.add(preambleElement);
        }

        actElements.addAll(parse(sections.get(0)));
    }
}
