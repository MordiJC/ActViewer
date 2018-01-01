package io.gihub.mordijc.parser.actutils;

import io.gihub.mordijc.container.ActElement;
import io.gihub.mordijc.container.ActElementBuilder;
import io.gihub.mordijc.util.Lists;
import io.gihub.mordijc.util.Strings;

import java.util.List;

import static io.gihub.mordijc.parser.ActParserSection.CHAPTER;

public class PreambleParser {
    private List<String> parsingTarget;

    public PreambleParser(List<String> lines) {
        reset(lines);
    }

    public void reset(List<String> lines) {
        parsingTarget = lines;
    }

    public ActElement parse() throws ParsingException {
        if (parsingTarget == null || parsingTarget.size() == 0) {
            throw new IllegalArgumentException("Argument must not be a null and have to have elements");
        }

        ActElementBuilder actElementBuilder = new ActElementBuilder();

        checkIfConstitutionOrThrow();

        actElementBuilder.title(
                parsingTarget.get(0).trim()
                        + "\n"
                        + parsingTarget.get(1).trim()
                        + "\n"
                        + parsingTarget.get(2).trim()
        );

        actElementBuilder.content(
                Strings.glueBrokenTextAndJoinWith(
                        getPreamble(),
                        "\n"
                )
        );

        return actElementBuilder.build();
    }

    public int getPreambleLinesCount() {
        return getPreamble().size();
    }

    private List<String> getPreamble() {
        return Lists.getFirstOfSplitIncludingDelimiterAsFirstElement(
                parsingTarget.subList(3, parsingTarget.size()),
                e -> e.matches(CHAPTER.pattern)
        );
    }

    private void checkIfConstitutionOrThrow() {
        if (!parsingTarget.get(0).matches("(?i)\\s*KONSTYTUCJA\\s*")
                && !parsingTarget.get(1).matches("(?i)\\s*RZECZYPOSPOLITEJ\\s*POLSKIEJ\\s*")
                && !parsingTarget.get(2).matches("(?i)\\s*z dnia.*")) {
            throw new ParsingException("Given text is not constitution");
        }
    }
}
