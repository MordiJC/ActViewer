package parser;

import java.io.*;

public class ActParser {
    public ActParser() {
    }

    public Object parse(File inputFile) {
        FileReader fileReader = openFileForReadingAndHandleErrors(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // Omit introduction and go to beginning of first chapter first chapter
        String line = "";

        try {
            bufferedReader.mark(1024);

            while ()

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean goToNextChapterAndSayIfExists(BufferedReader bufferedReader) {
        return true;
    }

    private FileReader openFileForReadingAndHandleErrors(File inputFile) throws ActParsingException {
        FileReader fileReader;

        try {
            fileReader = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            throw new ActParsingException("Unable to open file: " + inputFile.getAbsolutePath(), e);
        }

        return fileReader;
    }

    private static final int BUFFER_SIZE = 1024;
    private static final String CHAPTER_REGEXP_PATTERN
            = "^\\s*(?i)((?:ROZ)?(?:DZIAŁ))\\s(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))\\s*$";
}
