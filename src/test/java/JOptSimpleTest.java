import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;

import java.util.Arrays;

public class JOptSimpleTest {
    @Test(expected = OptionException.class)
    public void optsTest() {
        OptionParser parser = new OptionParser(){
            {
                acceptsAll(Arrays.asList("h", "help")).forHelp();
                acceptsAll(Arrays.asList("r", "range"));
                accepts("x").requiredIf("r");
            }
        };

        String[] args = {"--range", "123", "fad"};

        OptionSet options = parser.parse(args);

        System.out.println(options.valueOf("r"));
        System.out.println(options.has("r"));
        System.out.println(options.nonOptionArguments());
    }
}
