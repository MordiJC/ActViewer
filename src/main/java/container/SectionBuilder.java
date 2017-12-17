package container;

import java.util.ArrayList;
import java.util.List;

public class SectionBuilder {
    private String title = "";
    private String sectionName = "";
    private String sectionIdentifier = "";
    private String content = "";
    private List<ActElement> childrenActElements = new ArrayList<>();

    public ActElement build() {
        return new ActElement(title, sectionName, sectionIdentifier, content);
    }

    public SectionBuilder content(String content) {
        this.content = content;
        return this;
    }

    public SectionBuilder sectionIdentifier(String sectionIdentifier) {
        this.sectionIdentifier = sectionIdentifier;
        return this;
    }

    public SectionBuilder sectionName(String sectionName) {
        this.sectionName = sectionName;
        return this;
    }

    public SectionBuilder title(String title) {
        this.title = title;
        return this;
    }

    public SectionBuilder childrenSections(List<ActElement> childrenActElements) {
        this.childrenActElements = childrenActElements;
        return this;
    }
}
