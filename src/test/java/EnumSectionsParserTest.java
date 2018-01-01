import org.junit.Test;
import io.gihub.mordijc.parser.actutils.EnumSectionsParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EnumSectionsParserTest {

    @Test
    public void getIntroductionSingleLineTest() {
        List<String> lines = Collections.singletonList("Ustawa wchodzi w życie po upływie 30 dni od dnia ogłoszenia.");

        assertEquals(lines.get(0),
                new EnumSectionsParser(lines).getIntroduction());
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

        assertEquals(expected, new EnumSectionsParser(lines).getIntroduction());
    }

    @Test
    public void getIntroductionMultiLineWithParagraphTest() {
        List<String> lines = Arrays.asList(
                ("Organy samorządu terytorialnego oraz terenowe organy administracji rządowej, na\n" +
                        "podstawie i w granicach upoważnień zawartych w ustawie, ustanawiają akty prawa\n" +
                        "miejscowego obowiązujące na obszarze działania tych organów. Zasady i tryb wy-\n" +
                        "dawania aktów prawa miejscowego określa ustawa.\n" +
                        "1. bla bla").split("\n")
                );

        String expected = "Organy samorządu terytorialnego oraz terenowe organy administracji rządowej, na " +
                "podstawie i w granicach upoważnień zawartych w ustawie, ustanawiają akty prawa " +
                "miejscowego obowiązujące na obszarze działania tych organów. Zasady i tryb wy" +
                "dawania aktów prawa miejscowego określa ustawa.";

        assertEquals(expected, new EnumSectionsParser(lines).getIntroduction());
    }

    @Test
    public void getIntroductionMultiLineWithPointTest() {
        List<String> lines = Arrays.asList(
                ("Organy samorządu terytorialnego oraz terenowe organy administracji rządowej, na\n" +
                        "podstawie i w granicach upoważnień zawartych w ustawie, ustanawiają akty prawa\n" +
                        "miejscowego obowiązujące na obszarze działania tych organów. Zasady i tryb wy-\n" +
                        "dawania aktów prawa miejscowego określa ustawa.\n" +
                        "1a) bla bla").split("\n")
                );

        String expected = "Organy samorządu terytorialnego oraz terenowe organy administracji rządowej, na " +
                "podstawie i w granicach upoważnień zawartych w ustawie, ustanawiają akty prawa " +
                "miejscowego obowiązujące na obszarze działania tych organów. Zasady i tryb wy" +
                "dawania aktów prawa miejscowego określa ustawa.";

        assertEquals(expected, new EnumSectionsParser(lines).getIntroduction());
    }

    @Test
    public void getIntroductionMultiLineWithLetterTest() {
        List<String> lines = Arrays.asList(
                ("Organy samorządu terytorialnego oraz terenowe organy administracji rządowej, na\n" +
                        "podstawie i w granicach upoważnień zawartych w ustawie, ustanawiają akty prawa\n" +
                        "miejscowego obowiązujące na obszarze działania tych organów. Zasady i tryb wy-\n" +
                        "dawania aktów prawa miejscowego określa ustawa.\n" +
                        "a) bla bla").split("\n")
                );

        String expected = "Organy samorządu terytorialnego oraz terenowe organy administracji rządowej, na " +
                "podstawie i w granicach upoważnień zawartych w ustawie, ustanawiają akty prawa " +
                "miejscowego obowiązujące na obszarze działania tych organów. Zasady i tryb wy" +
                "dawania aktów prawa miejscowego określa ustawa.";

        assertEquals(expected, new EnumSectionsParser(lines).getIntroduction());
    }
}
