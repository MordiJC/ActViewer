package io.gihub.mordijc.container;

import java.util.ArrayList;
import java.util.List;

public class ActElementBuilder {
    private String title = "";
    private String identifier = "";
    private String typeName = "";
    private String content = "";
    private String summary = "";
    private List<ActElement> childrenActElements = new ArrayList<>();

    public ActElement build()
    {
        ActElement result = new ActElement(title, typeName, identifier, content, summary);
        result.setChildrenActElements(childrenActElements);
        return result;
    }

    public ActElementBuilder summary(String summary) {
        this.summary = summary;
        return this;
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

    public ActElementBuilder childrenElements(List<ActElement> childrenActElements) {
        this.childrenActElements = childrenActElements;
        return this;
    }
}
