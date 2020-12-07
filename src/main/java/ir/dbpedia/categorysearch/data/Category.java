package ir.dbpedia.categorysearch.data;

import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Value
public class Category {

    @NonNull
    String name;

    @NonNull
    List<Article> articles;

    public void addArticle(Article a) {
        articles.add(a);
    }

    public static Category empty(String name) {
        var a = new ArrayList<Article>();
        return new Category(name, a);
    }

    public static Category of(String name, Article ...articles) {
        var a = new ArrayList<>(Arrays.asList(articles));

        return new Category(name, a);
    }
}
