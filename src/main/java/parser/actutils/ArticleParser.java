package parser.actutils;

import container.ActElement;
import container.ActElementBuilder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleParser {

    public static String ARTICLE_REGEXP_PATTERN = "^(?i)(Art)\\.\\s*(\\d+)\\.\\s*(.*)$";

    public static boolean isArticle(String line) {
        if (line.matches(ARTICLE_REGEXP_PATTERN)) {
            return true;
        }
        return false;
    }

    public ActElement parse(List<String> lines) throws ParsingException {
        ActElement article = null;

        if (lines.size() == 0) {
            throw new ParsingException("parser requires minimum of 1 line");
        }

        if (isArticle(lines.get(0))) {
            Pattern articlePattern = Pattern.compile(ARTICLE_REGEXP_PATTERN);
            Matcher articleMatcher = articlePattern.matcher(lines.get(0));

            ActElementBuilder actElementBuilder = new ActElementBuilder();

            actElementBuilder.typeName(articleMatcher.group(1))
                    .identifier(articleMatcher.group(2));

            if (!articleMatcher.group(3).isEmpty()){

            } else {
                // parse next line
            }
        }

        return article;
    }
}
