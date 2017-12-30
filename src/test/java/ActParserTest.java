import org.junit.Test;
import parser.ActParser;

import java.io.File;

public class ActParserTest {

    @Test
    public void constitutionParsingTest() {
        File constitution = new File(getClass().getClassLoader().getResource("konstytucja.txt").getFile());

        ActParser parser = new ActParser();

        System.out.println(parser.parse(constitution));
    }

    @Test
    public void uokikChaptersTest(){
        File uokik = new File(getClass().getClassLoader().getResource("uokik.txt").getFile());

        ActParser parser = new ActParser();

        System.out.println(parser.parse(uokik));
    }
}
