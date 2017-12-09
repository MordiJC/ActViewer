package parser;

import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

import java.util.Arrays;
import java.util.List;

public class ActLocationPathValueConverter implements ValueConverter<ActLocation> {

    public static final String ROMAN_NUMBERS_REGEXP = "M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})";
    public static final String NUMBERS_REGEXP = "\\d+";
    public static final String LETTER_REGEXP = "[a-zA-Z]+";
    public static final String NUMBERS_WITH_LETTERS_REGEXP = "\\d+[a-zA-Z]+";
    public static final String FULL_SECTION_ID_PATTERN
            = String.format("(%s|%s|%s|%s)",
            ROMAN_NUMBERS_REGEXP, NUMBERS_REGEXP, LETTER_REGEXP, NUMBERS_WITH_LETTERS_REGEXP);
    public static final String SECTIONS_SEPARATOR_REGEXP = "\\.";

    @Override
    public ActLocation convert(String value) {
        String[] parts = value.split(SECTIONS_SEPARATOR_REGEXP);

        for (String s : parts) {
            if(!s.matches(FULL_SECTION_ID_PATTERN)) {
                throw new ValueConversionException(String.format("Invalid format of `%s` int `%s`", s, value));
            }
        }

        return new ActLocation(parts);
    }

    @Override
    public Class<ActLocation> valueType() {
        return ActLocation.class;
    }

    @Override
    public String valuePattern() {
        return null; // Nothing interesting
    }
}
