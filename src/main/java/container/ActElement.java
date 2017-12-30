package container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Basic interface used as container for title, act section content and its sub-sections.
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
     * E.g. "Rodział", "DZIAŁ"
     */
    public final String typeName;

    /**
     * Content of element.
     */
    public final String content;

    /**
     * List of child <code>ActElement</code> instances.
     */
    private List<ActElement> childrenActElements = new ArrayList<>();

    /**
     * Creates new <code>ActElement</code> instance without any data and description.
     * This instance type should be used only as root element.
     */
    public ActElement() {
        this("", "", "", "");
    }

    /**
     * Creates new <code>ActElement</code> instance using given title, section name and identifier.
     *
     * @param title      title of section.
     * @param typeName   type name of section.
     * @param identifier identifier of element.
     */
    public ActElement(String title, String typeName, String identifier, String content) {
        this.title = title;
        this.typeName = typeName;
        this.identifier = identifier;
        this.content = content;
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
        if(isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        builder.append(typeName).append(typeName.isEmpty() ? "" : " ")
                .append(identifier).append(identifier.isEmpty() ? "" : "\n")
                .append("[TITLE]\n")
                .append(title).append(title.isEmpty() ? "" : "\n");

        if(!typeName.isEmpty() && identifier.isEmpty() && title.isEmpty()) {
            builder.append("\n");
        }

        builder.append("[CONTENT]\n");
        builder.append(content);

        childrenActElements.stream().forEach(e-> {
            Scanner sc = new Scanner(e.toString());
            while(sc.hasNextLine()) {
                builder.append("\t").append(sc.nextLine()).append("\n");
            }
        });

        return builder.toString();
    }

    private boolean isEmpty() {
        return this.content.isEmpty()
                && this.identifier.isEmpty()
                && this.typeName.isEmpty()
                && this.title.isEmpty()
                && this.getChildrenActElements().isEmpty();
    }
}
