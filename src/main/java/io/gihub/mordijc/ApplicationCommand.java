package io.gihub.mordijc;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;

@CommandLine.Command(description = "Polish constitution and act viewer.")
public class ApplicationCommand {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message and exit.")
    Boolean helpRequested = false;

    @Parameters(paramLabel = "FILE", description = "File to parse.")
    File inputFile = null;

    @Option(names = {"-a", "--article"}, paramLabel = "ARTICLE NUMBER",
            description = "Display article or its element.\n" +
                    "Path format: <article no.>[.<element 1>[.<...>]]\n" +
                    "Example: 55.3.a")
    String article;

    @Option(names = {"-r", "--range"}, paramLabel = "ARTICLE NUMBER",
            description = "Specify end of articles range to display.")
    String endArticle;

    @Option(names = {"-c", "--chapter"}, paramLabel = "IDENTIFIER",
            description = "Show chapters' contents.")
    String chapter;

    @Option(names = {"-s", "--section"}, paramLabel = "IDENTIFIER",
            description = "Show sections' contents.")
    String section;

    @Option(names = {"-t", "--toc"}, description = "Show table of contents")
    Boolean tocRequested = false;

    public String[] getArticleOptionNames() {
        try {
            return getFieldOptionAnnotation("article").names();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public String[] getChapterOptionNames() {
        try {
            return getFieldOptionAnnotation("chapter").names();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public String[] getSectionOptionNames() {
        try {
            return getFieldOptionAnnotation("section").names();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public String[] getTocRequestedOptionNames() {
        try {
            return getFieldOptionAnnotation("tocRequested").names();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    private Option getFieldOptionAnnotation(String field) throws NoSuchFieldException {
        return this.getClass()
                .getDeclaredField(field)
                .getAnnotation(Option.class);
    }

    @Override
    public String toString() {
        return "ApplicationCommand{" +
                "helpRequested=" + helpRequested +
                ", inputFile=" + inputFile +
                ", article='" + article + '\'' +
                ", endArticle='" + endArticle + '\'' +
                ", chapter='" + chapter + '\'' +
                ", section='" + section + '\'' +
                ", tocRequested=" + tocRequested +
                '}';
    }
}
