package parser.actparserutils;

import container.ActElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used for act section parsing.
 */
public class SectionParser {

    /**
     * Parse section of act given as list of lines.
     *
     * @param sectionLines list of section lines.
     * @return parsed and processed act elements tree as list.
     */
    public List<ActElement> parse(List<String> sectionLines) {
        List<ActElement> sectionElements = new ArrayList<>();

        // check if it is constitution
        if(sectionLines.size() >= 2
                && sectionLines.get(0).equalsIgnoreCase("KONSTYTUCJA")
                && sectionLines.get(1).equalsIgnoreCase("RZECZYPOSPOLITEJ POLSKIEJ")) {
            // it is constitution
            // split by chapters
            // add preamble, threat chapters as section and parse it like everything else

            

        } else {

        }

        return sectionElements;
    }
}
