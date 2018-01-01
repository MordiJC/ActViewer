package io.gihub.mordijc.parser.actutils;

import io.gihub.mordijc.parser.ActParserSection;
import io.gihub.mordijc.util.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActPreparser {
    private static final Pattern[] SPLIT_PATTERNS = new Pattern[]{
            Pattern.compile(ActParserSection.ARTICLE.pattern),
            Pattern.compile(ActParserSection.PARAGRAPH.pattern),
            Pattern.compile(ActParserSection.POINT.pattern),
            Pattern.compile(ActParserSection.LETTER.pattern)
    };

    private static final Pattern[] INVALID_PATTERNS = new Pattern[]{
            Pattern.compile("\\u00A9Kancelaria Sejmu.*"),
            Pattern.compile("\\d{4}-\\d{2}-\\d{2}"),
            Pattern.compile("Dz\\.U\\..*"),
            Pattern.compile("."),
            Pattern.compile(".*\\(uchylony\\)\\s*"),
            Pattern.compile(".*\\(pominięty\\)\\s*")
    };

    public List<String> prepare(List<String> lines) {
        return lines.stream()
                .filter(this::isLineValid)
                .flatMap(this::prepareLine)
                .collect(Collectors.toList());
    }

    private boolean isLineValid(String line) {
        for (Pattern pattern : INVALID_PATTERNS) {
            if (pattern.matcher(line).matches()) {
                return false;
            }
        }
        return true;
    }

    private Stream<String> prepareLine(String line) {
        List<String> preparedLineParts = new ArrayList<>();

        Matcher matcher;

        for (Pattern pattern : SPLIT_PATTERNS) {
            matcher = pattern.matcher(line);

            if (matcher.matches()) {
                preparedLineParts.add(Regex.getGroupOrEmptyString(matcher, "id"));
                String content = Regex.getGroupOrEmptyString(matcher, "content");

                if (!content.isEmpty()) {
                    preparedLineParts.add(content);
                }

                return preparedLineParts.stream();
            }
        }

        preparedLineParts.add(line);

        return preparedLineParts.stream();
    }
}
