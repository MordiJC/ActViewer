import org.junit.Test;
import parser.ActParser;
import parser.ActPreparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

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

    @Test
    public void constitutionPreparsingManualTest() throws FileNotFoundException {
        File constitution = new File(getClass().getClassLoader().getResource("konstytucja.txt").getFile());
        FileReader fileReader = new FileReader(constitution);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        new ActPreparser()
                .prepare(bufferedReader.lines().collect(Collectors.toList()))
                .stream()
                .forEach(System.out::println);
    }
}
