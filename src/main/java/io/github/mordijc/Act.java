package io.github.mordijc;

import io.github.mordijc.container.ActElement;
import io.github.mordijc.parser.ActParserSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class Act {
    private final List<ActElement> articles;
    private final ActElement rootElement;

    public Act(ActElement actElement) {
        if (actElement == null || actElement.isEmpty()) {
            throw new IllegalArgumentException("ActElement cannot be null or empty");
        }

        this.rootElement = actElement;
        this.articles = getAllArticles(actElement);
    }

    private List<ActElement> getAllArticles(ActElement actElement) {
        List<ActElement> result = new ArrayList<>();

        if (actElement.type == ActParserSection.ARTICLE) {
            result.add(actElement);
        } else {
            for (ActElement element : actElement.getChildrenActElements()) {
                if (element.type == ActParserSection.ARTICLE) {
                    result.add(element);
                } else if (element.type.ordinal() < ActParserSection.ARTICLE.ordinal()) {
                    result.addAll(
                            getAllArticles(element)
                    );
                }
            }
        }

        return result;
    }

    public ActElement getArticleByIdentifier(String identifier) {
        for (ActElement element : articles) {
            if (element.identifier.replaceAll("[.)]", "").equalsIgnoreCase(identifier)) {
                return element;
            }
        }

        throw new NoSuchElementException(String.format("Article with identifier `%s` has not been found", identifier));
    }

    public ActElement getArticleElementByIdentifier(String path) {
        String[] pathElements = path.split("\\.");
        ActElement result = getArticleByIdentifier(pathElements[0]);

        if (pathElements.length < 2) {
            return result;
        }

        for (String location : Arrays.asList(pathElements).subList(1, pathElements.length)) {
            ActElement searchResult =
                    result.getChildrenActElements()
                            .stream()
                            .filter(e -> e.identifier.replaceAll("[.)]", "").equalsIgnoreCase(location))
                            .findFirst()
                            .orElse(null);

            if (searchResult == null) {
                throw new NoSuchElementException(
                        String.format("Article `%s` does not have element under path: `%s`",
                                pathElements[0],
                                String.join(".", path))
                );
            } else {
                result = searchResult;
            }
        }

        return result;
    }

    public List<ActElement> getArticlesRange(String startArticle, String endArticle) {
        int startIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < articles.size(); ++i) {
            if (articles.get(i).identifier.replaceAll("[.)]", "").equalsIgnoreCase(startArticle)) {
                startIndex = i;
            } else if (articles.get(i).identifier.replaceAll("[.)]", "").equalsIgnoreCase(endArticle)) {
                endIndex = i;
            }
        }

        if (startIndex > endIndex) {
            throw new IndexOutOfBoundsException("Starting article cannot be greater than ending one.");
        }

        if (startIndex == -1) {
            throw new IndexOutOfBoundsException("Starting article not found.");
        }

        if (endIndex == -1) {
            throw new IndexOutOfBoundsException("Ending index not found.");
        }


        return articles.subList(startIndex, endIndex+1);
    }

    public ActElement getChapterByIdentifier(String identifier) {
        return findElement(rootElement, identifier, ActParserSection.CHAPTER);
    }

    public ActElement getSectionByIdentifier(String identifier) {
        return findElement(rootElement, identifier, ActParserSection.SECTION);
    }

    public String getTableOfContents() {
        return rootElement.getStringRepresentation(false);
    }

    public String getFillContents() {
        return rootElement.toString();
    }

    private ActElement findElement(ActElement actElement, String identifier, ActParserSection type) {

        for (ActElement element : actElement.getChildrenActElements()) {
            if (element.type == type
                    && element.identifier.replaceAll("[.)]", "").equalsIgnoreCase(identifier)) {
                return element;
            } else if (element.type.ordinal() <= type.ordinal()) {
                try {
                    return findElement(element, identifier, type);
                } catch (NoSuchElementException ignored) {

                }
            }
        }

        throw new NoSuchElementException("Chapter not found.");
    }
}
