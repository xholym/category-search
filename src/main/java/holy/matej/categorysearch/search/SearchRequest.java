package holy.matej.categorysearch.search;

import lombok.Value;

import java.util.Objects;
import java.util.stream.Stream;

import static holy.matej.categorysearch.search.CategoryDocumentMapper.ARTICLES_FIELD;
import static holy.matej.categorysearch.search.CategoryDocumentMapper.CATEGORY_FIELD;

@Value
public class SearchRequest {

    String category;
    String article;

    public String[] fields() {
        return Stream.of(
                category != null ? CATEGORY_FIELD : null,
                article != null ? ARTICLES_FIELD : null
        )
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }

    public String[] searches() {
        return Stream.of(category, article)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }

    public String queryStr() {
        var s = new StringBuilder();
        if (category != null) {
            s.append("(" + CATEGORY_FIELD + ":" + category + ") ");
        }
        if (article != null)
            s.append("(" + ARTICLES_FIELD + ":" + article + ")");

        return s.toString();
    }
}
