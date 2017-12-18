package container;

import java.util.ArrayList;
import java.util.List;

public class ActElementBuilder {
    private String title = "";
    private String identifier = "";
    private String typeName = "";
    private String content = "";
    private List<ActElement> childrenActElements = new ArrayList<>();

    public ActElement build() {
        return new ActElement(title, typeName, identifier, content);
    }

    public ActElementBuilder content(String content) {
        this.content = content;
        return this;
    }

    public ActElementBuilder identifier(String sectionIdentifier) {
        this.identifier = sectionIdentifier;
        return this;
    }

    public ActElementBuilder typeName(String sectionName) {
        this.typeName = sectionName;
        return this;
    }

    public ActElementBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ActElementBuilder childrenSections(List<ActElement> childrenActElements) {
        this.childrenActElements = childrenActElements;
        return this;
    }
}
