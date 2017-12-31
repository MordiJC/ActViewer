import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ActViewer {

    static List<String> helpOptionNames = Arrays.asList("h", "help");
    static List<String> fileOptionNames = Arrays.asList("f", "file");
    static List<String> localizationOptionNames = Arrays.asList("l", "localization", "show");
    static List<String> rangeEndOptionNames = Arrays.asList("r", "range");
    static List<String> tableOfContentsOptionNames = Arrays.asList("t", "toc", "table-of-contents");

    public static void main(String[] args) {


        OptionParser parser = new OptionParser() {
            {
                // Basic options

                acceptsAll(helpOptionNames, "Display help")
                        .forHelp();


                acceptsAll(fileOptionNames)
                        .withRequiredArg()
                        .required()
                        .ofType(File.class);

                // Act display mode options
                acceptsAll(localizationOptionNames, "Specify part of act.")
                        .requiredUnless(tableOfContentsOptionNames.get(0))
                        .withRequiredArg()
                        .withValuesSeparatedBy(".")
                        .describedAs("Level1.Level2.Level3....")
                        .ofType(String.class);


//                acceptsAll(rangeEndOptionNames,
//                        "Display range of chapters, articles, etc.")
//                        .withRequiredArg()
//                        .withValuesSeparatedBy(".")
//                        .describedAs("Level1.Level2.Level3....")
//                        .withValuesConvertedBy(new ActLocationPathValueConverter())
//                        .ofType(String.class);

                // Table of contents mode.
                acceptsAll(tableOfContentsOptionNames, "Show table of contents");
            }
        };

        OptionSet options = parser.parse(args);

        if (options.has("help") || options.has("h") || options.asMap().size() == 1) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: remove if no debug
            }
            // Exit
        } else {
            // Check params

        }
    }
}
