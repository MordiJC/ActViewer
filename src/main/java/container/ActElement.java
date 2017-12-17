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

    public final String title;
    public final String sectionIdentifier;
    public final String sectionName;
    public final String content;
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
     * Creates new <code>ActElement</code> instance using given title,  section name and identifier.
     * ActElement name examples: "135", "XVI", "3)", "a)".
     *
     * @param title             title of section.
     * @param sectionName       name of section.
     * @param sectionIdentifier identifier of section.
     * @since 1.0
     */
    public ActElement(String title, String sectionName, String sectionIdentifier, String content) {
        this.title = title;
        this.sectionName = sectionName;
        this.sectionIdentifier = sectionIdentifier;
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
}
