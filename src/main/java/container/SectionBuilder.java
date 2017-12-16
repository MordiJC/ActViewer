package container;

import java.util.ArrayList;
import java.util.List;

public class SectionBuilder {
    private String title = "";
    private String sectionName = "";
    private String sectionIdentifier = "";
    private String content = "";
    private List<Section> childrenSections = new ArrayList<>();

    public Section build() {
        return new Section(title, sectionName, sectionIdentifier, content);
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

    public SectionBuilder childrenSections(List<Section> childrenSections) {
        this.childrenSections = childrenSections;
        return this;
    }
}
