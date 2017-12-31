import org.junit.Test;
import parser.ActParserSectionPattern;
import parser.actutils.ParagraphsParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParagraphsParserTest {

    @Test
    public void getIntroductionSingleLineTest() {
        List<String> lines = Collections.singletonList("Ustawa wchodzi w życie po upływie 30 dni od dnia ogłoszenia.");

        assertEquals(lines.get(0),
                new ParagraphsParser().getIntroduction(lines, ActParserSectionPattern.PARAGRAPH));
    }

    @Test
    public void getIntroductionMultiLineTest() {
        List<String> lines = Arrays.asList(
                ("Organy samorządu terytorialnego oraz terenowe organy administracji rządowej, na\n" +
                        "podstawie i w granicach upoważnień zawartych w ustawie, ustanawiają akty prawa\n" +
                        "miejscowego obowiązujące na obszarze działania tych organów. Zasady i tryb wy-\n" +
                        "dawania aktów prawa miejscowego określa ustawa.").split("\n")
                );

        String expected = "Organy samorządu terytorialnego oraz terenowe organy administracji rządowej, na " +
                "podstawie i w granicach upoważnień zawartych w ustawie, ustanawiają akty prawa " +
                "miejscowego obowiązujące na obszarze działania tych organów. Zasady i tryb wy" +
                "dawania aktów prawa miejscowego określa ustawa.";

        assertEquals(expected, new ParagraphsParser().getIntroduction(lines, ActParserSectionPattern.PARAGRAPH));
    }
}
