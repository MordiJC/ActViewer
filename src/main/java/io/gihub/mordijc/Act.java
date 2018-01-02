package io.gihub.mordijc;

import io.gihub.mordijc.container.ActElement;

import java.util.HashMap;
import java.util.Map;

public class Act {
    private final ActElement rootElement;
    private final Map<String, IndexElement> indexTable;

    public class IndexElement {
        public final ActElement element;
        public final Map<String, IndexElement> children;

        IndexElement(ActElement element, Map<String, IndexElement> childrenMap) {
            this.element = element;
            this.children = childrenMap;
        }
    }

    public Act(ActElement actElement) {
        if(actElement == null || actElement.isEmpty()) {
            throw new IllegalArgumentException("ActElement cannot be null or empty");
        }

        this.rootElement = actElement;
        this.indexTable = mapElement(rootElement);
    }

    private Map<String, IndexElement> mapElement(ActElement actElement) {
        Map<String, IndexElement> result = new HashMap<>();

        for (ActElement child: actElement.getChildrenActElements()) {
            result.put(
                    child.strippedIdentifier(),
                    new IndexElement(
                            child,
                            mapElement(child)
                    )
            );
        }

        return  result;
    }
}
