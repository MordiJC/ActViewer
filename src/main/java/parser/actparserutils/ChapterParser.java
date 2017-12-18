package parser.actparserutils;

import java.util.ArrayList;
import java.util.List;

public class ChapterParser {
    public static final String CHAPTER_REGEXP_PATTERN =
            "^\\s*(?i)(ROZDZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))([a-zA-Z])?\\s*$";

    public List<String> getFirstChapter(List<String> lines) {
        List<String> chapter = new ArrayList<>();
        for (String line : lines) {
            if (line.matches(CHAPTER_REGEXP_PATTERN)) {
                return chapter;
            }
            chapter.add(line);
        }

        return chapter;
    }
}
