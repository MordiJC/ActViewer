package parser.actutils;

import container.ActElement;

import java.util.ArrayList;
import java.util.List;

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
}
