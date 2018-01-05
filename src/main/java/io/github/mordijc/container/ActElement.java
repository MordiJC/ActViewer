package io.github.mordijc.container;

import io.github.mordijc.parser.ActParserSection;
import io.github.mordijc.util.Regex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic interface used as io.gihub.mordijc.container for title, act section content and its sub-sections.
 */
public class ActElement {
    /**
     * Title of act element.
     */
    public final String title;

    /**
     * Identifier of element.
     * E.g. "135", "XVI", "3)", "a)".
     */
    public final String identifier;
    /**
     * Type name of element.
     * E.g. "Rodzia&#x142;", "DZIA&#x141;"
     */
    public final String typeName;

    /**
     * Content of element.
     */
    public final String content;

    /**
     * Summary of children elements
     */
    public final String summary;

    /**
     * List of child <code>ActElement</code> instances.
     */
    private List<ActElement> childrenActElements = new ArrayList<>();

    /**
     * ActElement type.
     */
    public final ActParserSection type;

    /**
     * Creates new <code>ActElement</code> instance without any data and description.
     * This instance type should be used only as root element.
     */
    public ActElement() {
        this(ActParserSection.TEXT,"", "", "", "", "");
    }

    /**
     * Creates new <code>ActElement</code> instance using given title, section name and identifier.
     *
     * @param title      title of section.
     * @param typeName   type name of section.
     * @param identifier identifier of element.
     */
    public ActElement(ActParserSection type, String title, String typeName, String identifier, String content, String summary) {
        this.type = type;
        this.title = title;
        this.typeName = typeName;
        this.identifier = identifier;
        this.content = content;
        this.summary = summary;
    }

    /**
     * Returns list of child sections of this section. This list can never be null and
     * have to be ordered to prevent range operations errors.
     *
     * @return list of child sections.
     */
    public final List<ActElement> getChildrenActElements() {
        return Collections.unmodifiableList(childrenActElements);
    }

    /**
     * Set new children sections list but first removes previous.
     *
     * @param childrenActElements list of children sections.
     */
    public void setChildrenActElements(List<ActElement> childrenActElements) {
        this.childrenActElements.clear();
        this.childrenActElements = childrenActElements;
    }

    @Override
    public String toString() {
        return getStringRepresentation(true);
    }

    public String getStringRepresentation(boolean showContent) {
        if (isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        builder.append(typeName).append(typeName.isEmpty() ? "" : " ")
                .append(identifier).append(identifier.isEmpty() ? "" : "\n")
                .append(title).append(title.isEmpty() ? "" : "\n");

        if (!typeName.isEmpty() && identifier.isEmpty() && title.isEmpty()) {
            builder.append("\n");
        }

        if(showContent && !content.isEmpty()) {
            builder.append(content).append('\n');
        }

        if(childrenActElements.size() != 0) {
            childrenActElements.stream().forEach(e -> {
                Scanner sc = new Scanner(e.getStringRepresentation(showContent));
                while (sc.hasNextLine()) {
                    builder.append("\t").append(sc.nextLine()).append("\n");
                }
            });
        }

        if(!summary.isEmpty()) {
            builder.append(summary);
        }

        return builder.toString();
    }

    public boolean isEmpty() {
        return this.content.isEmpty()
                && this.identifier.isEmpty()
                && this.typeName.isEmpty()
                && this.title.isEmpty()
                && this.summary.isEmpty()
                && this.getChildrenActElements().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActElement that = (ActElement) o;

        if(!type.equals(that.type)) {
            return false;
        }

        if (!title.equals(that.title)) {
            return false;
        }
        if (!identifier.equals(that.identifier)) {
            return false;
        }
        if (!typeName.equals(that.typeName)) {
            return false;
        }
        if (!content.equals(that.content)) {
            return false;
        }
        if (!summary.equals(that.summary)) {
            return false;
        }
        return childrenActElements.equals(that.childrenActElements);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 53 * result + type.hashCode();
        result = 59 * result + identifier.hashCode();
        result = 61 * result + typeName.hashCode();
        result = 67 * result + content.hashCode();
        result = 71 * result + summary.hashCode();
        result = 73 * result + childrenActElements.hashCode();
        return result;
    }

    public String strippedIdentifier() {
        Matcher matcher = Pattern.compile(type.pattern).matcher(identifier.trim());
        if(!matcher.matches()) {
            throw new IllegalStateException("Identifier does not match type of element.");
        }

        return Regex.getGroupOrEmptyString(matcher, "identifier").replaceAll("[]).\\s]", "");
    }
}
