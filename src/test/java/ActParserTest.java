import org.junit.Test;
import parser.ActParser;

import java.io.File;

public class ActParserTest {
    @Test
    public void constitutionParsingTest() {

    }

    @Test
    public void linesOmittingTest() {
        File constitution = new File(getClass().getClassLoader().getResource("konstytucja.txt").getFile());

        ActParser parser = new ActParser();

        parser.parse(constitution);
    }
}
