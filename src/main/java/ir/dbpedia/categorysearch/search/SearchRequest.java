package ir.dbpedia.categorysearch.search;

import lombok.Value;

@Value
public class SearchRequest {

    String category;
    String article;

    public String asString() {
        var res = new StringBuilder("[ ");
        if (category != null) {
            res.append("category=\"").append(category).append("\"");
            if (article != null)
                res.append(", ");
        }
        if (article != null)
            res.append("article=\"").append(article).append("\"");

        return res.append(" ]")
                .toString();
    }
}
