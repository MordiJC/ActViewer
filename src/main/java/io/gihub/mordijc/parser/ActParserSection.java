package io.gihub.mordijc.parser;

import java.util.NoSuchElementException;

// TODO: Add formatting string
public enum ActParserSection {
    PART("^\\s*(?i)(?<typeName>CZ[\u0118\u0119][\u015a\u015b][\u0106\u0107])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true),
    BOOK("^\\s*(?i)(?<typeName>KSI[\u0118\u0119]GA)\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true),
    TITLE("^\\s*(?i)(?<typeName>TYTU[\u0141\u0142])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true),
    SECTION("^\\s*(?i)(?<typeName>DZIA[\u0141\u0142])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})[a-zA-Z])?\\s*$",
            true),
    CHAPTER("^\\s*(?i)(?<typeName>ROZDZIA[\u0141\u0142])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})|\\d+)\\s*$",
            true),
    BRANCH("^\\s*(?i)(?<typeName>ODDZIA[\u0141\u0142])\\s*(?<identifier>M{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})|\\d+)\\s*$",
            true),
    ARTICLE("^(?i)(?<id>(?<typeName>Art\\.)\\s*(?<identifier>\\d+[a-z]?\\.))\\s*(?<content>.*)$",
            false),
    PARAGRAPH("^(?<id>(?<identifier>\\d+\\.))\\s*(?<content>.*)$",
            false),
    POINT("^(?<id>(?<identifier>\\d+[a-z]*\\)))\\s*(?<content>.*)$",
            false),
    LETTER("^(?<id>(?<identifier>[a-z]+\\)))\\s*(?<content>.{5,})",
            false),
    TEXT(".*",
            false),
    NONE ("",
            false);
    /**
     * Array of systematization units. Last one is ARTICLE and is here only because it needs to be split as the others.
     */
    public static final ActParserSection[] GENERAL_SECTIONS = {PART, BOOK, TITLE, SECTION, CHAPTER, BRANCH, ARTICLE};
    /**
     * Array of enumerate units that can be found in ARTICLE.
     */
    public static final ActParserSection[] ENUM_SECTIONS = {PARAGRAPH, POINT, LETTER};
    public static final ActParserSection[] ELEMENT_SECTIONS = {PART, BOOK, TITLE, SECTION, CHAPTER, BRANCH, ARTICLE, PARAGRAPH, POINT, LETTER};
    /**
     * Regexp pattern.
     */
    public final String pattern;
    /**
     * Tells if section has title in next line(s).
     */
    public final boolean hasTitle;

    /**
     * Constructs <code>ActParserSection</code> using pattern and title
     * presence flag.
     * <p>
     * <p><code>pattern</code> will be used to match one line of text. It can
     * have two catch groups: `typeName` and `identifier`. This groups will be
     * used to get their content and place them in <code>ActElement</code>
     * fields.</p>
     *
     * @param pattern  regular expression pattern.
     * @param hasTitle tells if io.gihub.mordijc.parser should look for title in next lines.
     */
    ActParserSection(String pattern, boolean hasTitle) {
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
    public ActParserSection next() throws NoSuchElementException {
        int ord = this.ordinal() + 1;
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        return ActParserSection.values()[ord];
    }

    /**
     * Returns previous element of this enum or throws NoSuchElementException.
     *
     * @return predecessor of current enum element.
     * @throws NoSuchElementException if there is no predecessor (current
     *                                element is first in enum order)
     */
    public ActParserSection previous() throws NoSuchElementException {
        int ord = this.ordinal() - 1;
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }

        return ActParserSection.values()[ord];
    }

    /**
     * Tells if current element has successor in enum order.
     *
     * @return {@code true} if element has successor.
     */
    public boolean hasNext() {
        int ord = this.ordinal() + 1;
        if (ord >= ActParserSection.values().length
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
        if (ord >= ActParserSection.values().length
                || ord < 0) {
            return false;
        }
        return true;
    }
}
