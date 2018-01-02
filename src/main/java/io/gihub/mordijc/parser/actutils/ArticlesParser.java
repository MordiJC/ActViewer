package io.gihub.mordijc.parser.actutils;

import io.gihub.mordijc.container.ActElement;
import io.gihub.mordijc.container.ActElementBuilder;
import io.gihub.mordijc.util.Lists;
import io.gihub.mordijc.util.Log;
import io.gihub.mordijc.util.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.gihub.mordijc.parser.ActParserSection.ARTICLE;

public class ArticlesParser {
    public List<ActElement> parse(List<String> lines) {
        List<List<String>> articles =
                Lists.splitIncludingDelimiterAsFirstElement(
                        lines,
                        e -> e.matches(ARTICLE.pattern)
                );

        List<ActElement> result = new ArrayList<>();

        for (List<String> article : articles) {
            result.add(parseArticleContent(article));
        }

        return result;
    }

    private ActElement parseArticleContent(List<String> lines) {
        Pattern pattern = Pattern.compile(ARTICLE.pattern);
        Matcher matcher = pattern.matcher("");

        matcher.reset(lines.get(0));

        if (!matcher.matches()) {
            Log.getLogger().severe(lines.get(0));
            throw new ParsingException("Given text is not an article.");
        }

        ActElementBuilder actElementBuilder = new ActElementBuilder()
                .typeName(Regex.getGroupOrEmptyString(matcher, "typeName"))
                .identifier(Regex.getGroupOrEmptyString(matcher, "identifier"));

        EnumSectionsParser enumSectionsParser =
                new EnumSectionsParser(lines.subList(1, lines.size()));

        actElementBuilder
                .content(enumSectionsParser.getIntroduction())
                .childrenElements(enumSectionsParser.getSections())
                .summary(enumSectionsParser.getSummary());

        return actElementBuilder.build();
    }
}
