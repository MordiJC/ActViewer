package parser.actutils;

import container.ActElement;

import java.util.ArrayList;
import java.util.List;

public class ChapterParser {
    public static final String CHAPTER_REGEXP_PATTERN =
            "^\\s*(?i)(ROZDZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3}))([a-zA-Z])?\\s*$";

    public static boolean isChapter(List<String> lines) {
        if (lines.size() >= 2
                && lines.get(0).matches(CHAPTER_REGEXP_PATTERN)) {
            return true;
        }
        return false;
    }

    public ActElement parse(List<String> lines, boolean remove) {
        ActElement article = null;

        if (isChapter(lines)) {

        }

        return article;
    }

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
