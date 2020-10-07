package holy.matej.categorysearch.data;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class Category {

    String name;

    List<Article> articles;

    public void addArticle(Article a) {
        this.articles.add(a);
    }
}
