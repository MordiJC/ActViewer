import org.junit.Test;
import io.gihub.mordijc.ProgramArgumentsHandler;

import java.io.StringWriter;
import static org.junit.Assert.assertEquals;


public class ProgramArgumentsHandlerTest {

    @Test
    public void helpNoArgumentsTest() {
        StringWriter stringWriter = new StringWriter();
        String[] args = {};
        ProgramArgumentsHandler argumentsHandler = new ProgramArgumentsHandler(stringWriter);

        argumentsHandler.handle(args);

        stringWriter.flush();

        assertEquals(argumentsHandler.getHelpMessage(), stringWriter.toString());
    }

    @Test
    public void helpOptionShortTest() {
        StringWriter stringWriter = new StringWriter();
        String[] args = {"-h"};
        ProgramArgumentsHandler argumentsHandler = new ProgramArgumentsHandler(stringWriter);

        argumentsHandler.handle(args);

        stringWriter.flush();

        assertEquals(argumentsHandler.getHelpMessage(), stringWriter.toString());
    }

    @Test
    public void helpOptionLongTest() {
        StringWriter stringWriter = new StringWriter();
        String[] args = {"--help"};
        ProgramArgumentsHandler argumentsHandler = new ProgramArgumentsHandler(stringWriter);

        argumentsHandler.handle(args);

        stringWriter.flush();

        assertEquals(argumentsHandler.getHelpMessage(), stringWriter.toString());
    }
}
