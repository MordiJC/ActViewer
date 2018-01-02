package io.gihub.mordijc.container;

import io.gihub.mordijc.parser.ActParserSection;

import java.util.ArrayList;
import java.util.List;

import static io.gihub.mordijc.parser.ActParserSection.TEXT;

public class ActElementBuilder {
    private ActParserSection type = TEXT;
    private String title = "";
    private String identifier = "";
    private String typeName = "";
    private String content = "";
    private String summary = "";
    private List<ActElement> childrenActElements = new ArrayList<>();

    public ActElement build()
    {
        ActElement result = new ActElement(type, title, typeName, identifier, content, summary);
        result.setChildrenActElements(childrenActElements);
        return result;
    }

    public ActElementBuilder type(ActParserSection type) {
        this.type = type;
        return this;
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
