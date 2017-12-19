package parser;

public enum ActElementType {
    SECTION                     ("^\\s*(?i)(DZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$"),
    CHAPTER                     ("^\\s*(?i)(ROZDZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})|\\d+)\\s*$"),
    ARTICLE                     ("^(?i)(Art)\\.\\s*(\\d+)\\.\\s*(.*)$"),
    ENUM_DOT                    ("^(\\d+)\\..*$"),
    ENUM_NUMBER_AND_PARENTHESIS ("^(\\d+[a-z]+)\\).*$"),
    ENUM_LETTER_AND_PARENTHESIS ("^([a-z]+)\\).*$");



    private final String pattern;

    ActElementType(String pattern) {
        this.pattern = pattern;
    }
}
