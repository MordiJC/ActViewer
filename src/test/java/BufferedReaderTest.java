import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class BufferedReaderTest {

    @Test
    public void checksBufferedReaderCopyConstructor() throws IOException {
        String pseudoFile =
                "1. blah blah\n" +
                "2. blah blah halb\n" +
                "3. hahahfsd\n" +
                "4. blblasd";

        StringReader stringReader = new StringReader(pseudoFile);
        BufferedReader bufferedReader = new BufferedReader(stringReader);

        System.out.println(bufferedReader.readLine());

        BufferedReader bufferedReader2 = new BufferedReader(bufferedReader);

        System.out.println(bufferedReader2.readLine());

        System.out.println(bufferedReader.readLine());

    }
}
