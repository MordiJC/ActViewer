package io.github.mordijc;

import io.github.mordijc.parser.ActParser;
import io.github.mordijc.parser.actutils.ParsingException;
import picocli.CommandLine;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ActViewer {
    public static void main(String[] args) {
        ApplicationCommand applicationCommand = new ApplicationCommand();

        try {
            CommandLine commandLine = new CommandLine(applicationCommand);
            commandLine.parse(args);

            if (commandLine.isUsageHelpRequested()) {
                commandLine.usage(System.out, CommandLine.Help.Ansi.AUTO);
                return;
            }

            Act act = new Act(
                    new ActParser().parse(applicationCommand.inputFile)
            );

            switch (getSelectCommandOrThrow(applicationCommand)) {
                case ARTICLE:
                    if (applicationCommand.endArticle == null) {
                        System.out.println(
                                act.getArticleElementByIdentifier(applicationCommand.article)
                        );
                    } else {
                        act.getArticlesRange(
                                applicationCommand.article,
                                applicationCommand.endArticle
                        ).forEach(System.out::println);
                    }
                    break;
                case CHAPTER:
                    System.out.println(
                            act.getChapterByIdentifier(applicationCommand.chapter)
                    );
                    break;
                case SECTION:
                    System.out.println(
                            act.getSectionByIdentifier(applicationCommand.section)
                    );
                    break;
                case TOC:
                    if (applicationCommand.tocRequested) {
                        System.out.println(
                                act.getTableOfContents()
                        );
                    }
                    break;
            }

        } catch (CommandLine.PicocliException | IllegalArgumentException | ParsingException | NoSuchElementException e) {
            System.err.println(e.getMessage());
            CommandLine.usage(new ApplicationCommand(), System.err, CommandLine.Help.Ansi.ON);
        }
    }

    private static ApplicationCommand.SelectCommand getSelectCommandOrThrow(ApplicationCommand applicationCommand) {
        Supplier<Stream<Object>> supplier = () -> Stream.of(applicationCommand.article,
                applicationCommand.chapter,
                applicationCommand.section,
                applicationCommand.tocRequested);
        if (supplier.get()
                .filter(e -> (e == null) || (e instanceof Boolean && !((Boolean) e)))
                .count() != 3) {
            throw new IllegalArgumentException("You must provide only one of selecting commands:\n" +
                    String.join(", ", applicationCommand.getArticleOptionNames()) + "\n" +
                    String.join(", ", applicationCommand.getChapterOptionNames()) + "\n" +
                    String.join(", ", applicationCommand.getSectionOptionNames()) + "\n" +
                    String.join(", ", applicationCommand.getTocRequestedOptionNames())
            );
        }
        return ApplicationCommand.SelectCommand
                .values()
                [supplier.get()
                .map(Objects::nonNull)
                .collect(Collectors.toList())
                .indexOf(true)];
    }
}
