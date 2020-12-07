package ir.dbpedia.categorysearch.search;

import ir.dbpedia.categorysearch.data.Article;
import ir.dbpedia.categorysearch.data.Category;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Value(staticConstructor = "of")
public class SearchResult {

    float score;

    @NonNull
    Category category;

    public String asString() {
        var res = "{\n"
                + "  score: " + score + ", \n"
                + "  category: { \n"
                + "    name: \"" + category.getName() + "\",\n"
                + "    articles: [ \n      ";
        var articles = category.getArticles().stream()
                .map(a ->
                        "\"" + a.getName() + "\""
                )
                .collect(Collectors.joining(", "));

        return res + articles
                + "\n    ]\n"
                + "  }\n"
                + "}";
    }

    @Value
    public static class CategoryResult {

        @NonNull
        String name;

        List<Article> articles;

        public static CategoryResult of(String name, Article a) {
            var articles = new ArrayList<Article>();
            articles.add(a);
            return new CategoryResult(name, articles);
        }

        public void addArticle(Article a) {
            this.articles.add(a);
        }

        public void addArticles(List<Article> a) {
            this.articles.addAll(a);
        }

    }
}
