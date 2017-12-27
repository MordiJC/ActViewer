package parser;

import java.util.NoSuchElementException;

public enum ActParserSectionPattern {
    PART("^\\s*(?i)(?<typeName>CZ[Ęę][Śś][Ćć])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true),
    BOOK("^\\s*(?i)(?<typeName>KSI[Ęę]GA)\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true),
    TITLE("^\\s*(?i)(?<typeName>TYTU[Łł])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true),
    SECTION("^\\s*(?i)(?<typeName>DZIA[Łł])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true),
    CHAPTER("^\\s*(?i)(?<typeName>ROZDZIA[Łł])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})|\\d+)\\s*$",
            true),
    BRANCH("^\\s*(?i)(?<typeName>ODDZIA[Łł])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})|\\d+)\\s*$",
            true),
    ARTICLE("^(?i)((?<typeName>Art\\.)\\s*(?<identifier>\\d+[a-z]?\\.))\\s*(.*)$",
            false),
    ENUM_DOT("^(?<identifier>\\d+)\\..*$",
            false),
    ENUM_NUMBER_AND_PARENTHESIS("^(?<identifier>\\d+[a-z]+)\\).*$",
            false),
    ENUM_LETTER_AND_PARENTHESIS("^(?<identifier>[a-z]+)\\).*$",
            false);

    /**
     * Regexp pattern.
     */
    public final String pattern;

    /**
     * Tells if section has title in next line(s).
     */
    public final boolean hasTitle;

    /**
     * Array of systematization units. Last one is ARTICLE and is here only because it needs to be split as the others.
     */
    public static final ActParserSectionPattern[] GENERAL_SECTIONS = {PART, BOOK, TITLE, SECTION, CHAPTER, BRANCH, ARTICLE};
    /**
     * Array of enumerate units that can be found in ARTICLE.
     */
    public static final ActParserSectionPattern[] ENUM_SECTIONS = {ENUM_DOT, ENUM_NUMBER_AND_PARENTHESIS, ENUM_LETTER_AND_PARENTHESIS};

    /**
     * Constructs <code>ActParserSectionPattern</code> using pattern and title
     * presence flag.
     * <p>
     * <p><code>pattern</code> will be used to match one line of text. It can
     * have two catch groups: `typeName` and `identifier`. This groups will be
     * used to get their content and place them in <code>ActElement</code>
     * fields.</p>
     *
     * @param pattern  regular expression pattern.
     * @param hasTitle tells if parser should look for title in next lines.
     */
    ActParserSectionPattern(String pattern, boolean hasTitle) {
        this.pattern = pattern;
        this.hasTitle = hasTitle;
    }

    /**
     * Returns next element of this enum or throws NoSuchElementException.
     *
     * @return successor of current enum element.
     * @throws NoSuchElementException if there is no successor (current
     *                                element is last in enum order)
     */
    public ActParserSectionPattern next() throws NoSuchElementException {
        int ord = this.ordinal() + 1;
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return ActParserSectionPattern.values()[ord];
    }

    /**
     * Returns previous element of this enum or throws NoSuchElementException.
     *
     * @return predecessor of current enum element.
     * @throws NoSuchElementException if there is no predecessor (current
     *                                element is first in enum order)
     */
    public ActParserSectionPattern previous() throws NoSuchElementException {
        int ord = this.ordinal() - 1;
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }

        return ActParserSectionPattern.values()[ord];
    }

    /**
     * Tells if current element has successor in enum order.
     *
     * @return {@code true} if element has successor.
     */
    public boolean hasNext() {
        int ord = this.ordinal() + 1;
        if (ord >= ActParserSectionPattern.values().length
                || ord < 0) {
            return false;
        }
        return true;
    }

    /**
     * Tells if current element has predecessor in enum order.
     *
     * @return {@code true} if element has predecessor.
     */
    public boolean hasPrevious() {
        int ord = this.ordinal() - 1;
        if (ord >= ActParserSectionPattern.values().length
                || ord < 0) {
            return false;
        }
        return true;
    }
}
