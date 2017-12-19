package parser;

public enum ActParserSectionPattern {
    SECTION("^\\s*(?i)(DZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true, 2, 1),
    CHAPTER("^\\s*(?i)(ROZDZIA[Łł])\\s*(M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})|\\d+)\\s*$",
            true, 2, 1),
    ARTICLE("^(?i)((Art\\.)\\s*(\\d+\\.))\\s*(.*)$",
            false, 3, 2),
    ENUM_DOT("^(\\d+)\\..*$",
            false, 1, -1),
    ENUM_NUMBER_AND_PARENTHESIS("^(\\d+[a-z]+)\\).*$",
            false, 1, -1),
    ENUM_LETTER_AND_PARENTHESIS("^([a-z]+)\\).*$",
            false, 1, -1);

    public final String pattern;
    public final boolean titleInNextLine;
    public final int identifierGroupNumber;
    public final int typeNameGroupNumber;

    ActParserSectionPattern(String pattern, boolean titleInNextLine, int identifierGroupNumber, int typeNameGroupNumber) {
        this.pattern = pattern;
        this.titleInNextLine = titleInNextLine;
        this.identifierGroupNumber = identifierGroupNumber;
        this.typeNameGroupNumber = typeNameGroupNumber;
    }
}
