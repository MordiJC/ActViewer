package container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basic interface used as container for title, act section content and its sub-sections.
 *
 * @since 1.0
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
     *
     * @since 1.0
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
     * @since 1.0
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
     * @since 1.0
     */
    public final List<ActElement> getChildrenActElements() {
        return Collections.unmodifiableList(childrenActElements);
    }

    /**
     * Set new children sections list but first removes previous.
     *
     * @param childrenActElements list of children sections.
     * @since 1.0
     */
    public void setChildrenActElements(List<ActElement> childrenActElements) {
        this.childrenActElements.clear();
        this.childrenActElements = childrenActElements;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(identifier).append(identifier.isEmpty()?"":" ")
                .append(typeName).append(typeName.isEmpty()?"":"\n")
                .append(title).append(title.isEmpty()?"":"\n");

        for(ActElement ae: childrenActElements) {
            builder.append(ae.toString()).append("\n");
        }

        return builder.toString();
    }
}
