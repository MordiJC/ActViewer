package io.gihub.mordijc;

import picocli.CommandLine;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;


public class ActViewer {
    public static void main(String[] args) {
        ApplicationCommand applicationCommand = new ApplicationCommand();

        System.out.println(Arrays.toString(ApplicationCommand.class.getDeclaredFields()));

        try {
            CommandLine commandLine = new CommandLine(applicationCommand);
            commandLine.parse(args);

            if(commandLine.isUsageHelpRequested()) {
                commandLine.usage(System.out, CommandLine.Help.Ansi.ON);
                return;
            }

            if (!checkSelectCommandsState(applicationCommand)) {
                return;
            }
        } catch (CommandLine.MissingParameterException e) {
            System.err.println(e.getMessage());
            CommandLine.usage(new ApplicationCommand(), System.err, CommandLine.Help.Ansi.ON);
        }

        System.out.println(applicationCommand);
    }

    private static boolean checkSelectCommandsState(ApplicationCommand applicationCommand) {
        if(Stream.of(applicationCommand.article,
                applicationCommand.chapter,
                applicationCommand.section,
                applicationCommand.tocRequested)
                .filter(Objects::isNull)
                .count() != 3) {
            System.err.println("You must provide only one of selecting commands:\n" +
                   String.join(", ", applicationCommand.getArticleOptionNames()) + "\n" +
                    String.join(", ", applicationCommand.getChapterOptionNames()) + "\n" +
                    String.join(", ", applicationCommand.getSectionOptionNames()) + "\n" +
                    String.join(", ", applicationCommand.getTocRequestedOptionNames())
            );
            return false;
        }
        return true;
    }
}
