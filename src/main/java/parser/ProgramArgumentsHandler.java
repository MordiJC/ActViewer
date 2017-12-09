package parser;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;

public class ProgramArgumentsHandler {
    public ProgramArgumentsHandler(Writer writer) {
        this.writer = writer;
    }

    public ProgramArgumentsHandler() {
        this.writer = new PrintWriter(System.out, true);
    }

    public void handle(String[] args) {
        options = optionParser.parse(args);

        checkHelpOptionsAndPrintHelpIfNeeded();

    }

    private void checkHelpOptionsAndPrintHelpIfNeeded() {
        if(options.has("help") ||
                options.has("h") ||
                !options.hasOptions()) {
            printHelp();
        }
    }

    private void printHelp() {
        try {
            optionParser.printHelpOn(writer);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Flag that app can quit.
        }
    }

    public String getHelpMessage() {
        StringWriter stringWriter = new StringWriter();

        try {
            optionParser.printHelpOn(stringWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // No need for closing StringWriter. (see docs)
        return stringWriter.toString();
    }

    private void createCommands() {
        optionParser.acceptsAll(Arrays.asList("h", "help")).forHelp();
        optionParser.acceptsAll(Arrays.asList("r", "range"));
    }

    private Writer writer;
    private OptionParser optionParser = new OptionParser();
    private OptionSet options = null;
}
