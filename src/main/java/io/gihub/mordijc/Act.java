package io.gihub.mordijc;

import io.gihub.mordijc.container.ActElement;
import io.gihub.mordijc.parser.ActParserSection;

import java.util.*;

import static io.gihub.mordijc.parser.ActParserSection.CHAPTER;
import static io.gihub.mordijc.parser.ActParserSection.SECTION;

public class Act {
    public final List<ActElement> articles;
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

        if(actElement.type == ActParserSection.ARTICLE) {
            result.add(actElement);
        } else {
            for(ActElement element: actElement.getChildrenActElements()) {
                if(element.type == ActParserSection.ARTICLE) {
                    result.add(element);
                } else if(element.type.ordinal() > ActParserSection.ARTICLE.ordinal()) {
                    result.addAll(
                            getAllArticles(element)
                    );
                }
            }
        }

        return result;
    }

    public ActElement getArticleByIdentifier(String identifier) {
        for(ActElement element: articles) {
            if(element.identifier.equalsIgnoreCase(identifier)) {
                return element;
            }
        }

        throw new NoSuchElementException(String.format("Article with identifier `%s` has not been found", identifier));
    }

    public ActElement getArticleElementByIdentifier(String articleIdentifier, String[] path) {
        ActElement result = getArticleByIdentifier(articleIdentifier);

        for(String location: path) {
            ActElement searchResult =
                    result.getChildrenActElements()
                            .stream()
                            .filter(e->e.identifier.equalsIgnoreCase(location))
                            .findFirst()
                            .orElse(null);

            if(searchResult == null) {
                throw new NoSuchElementException(
                        String.format("Article `%s` does not have element under path: `%s`",
                                articleIdentifier,
                                String.join("/", path))
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

        for(int i = 0; i < articles.size(); ++i) {
            if(articles.get(i).identifier.equalsIgnoreCase(startArticle)) {
                startIndex = i;
            } else if(articles.get(i).identifier.equalsIgnoreCase(endArticle)) {
                endIndex = i;
            }
        }

        if(startIndex > endIndex) {
            throw new IndexOutOfBoundsException("Starting article cannot be greater than ending one.");
        }

        if(startIndex == -1) {
            throw new IndexOutOfBoundsException("Starting article not found.");
        }

        if(endIndex == -1) {
            throw new IndexOutOfBoundsException("Ending index not found.");
        }


        return articles.subList(startIndex, endIndex);
    }

    public ActElement getChapterByIdentifier(String identifier) {
       return findElement(rootElement, identifier, CHAPTER);
    }

    public ActElement getSectionByIdentifier(String identifier) {
        return findElement(rootElement, identifier, SECTION);
    }

    private ActElement findElement(ActElement actElement, String identifier, ActParserSection type) {
        ActElement root = rootElement;

        for(ActElement element: root.getChildrenActElements()) {
            if(element.type == type
                    && element.identifier.equalsIgnoreCase(identifier)) {
                return element;
            } else if(element.type.ordinal() > type.ordinal()) {
                return findElement(element, identifier, type);
            }
        }

        throw new NoSuchElementException("Chapter not found.");
    }
}
