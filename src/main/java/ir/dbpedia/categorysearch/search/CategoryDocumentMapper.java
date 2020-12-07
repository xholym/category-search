package ir.dbpedia.categorysearch.search;

import ir.dbpedia.categorysearch.data.Article;
import ir.dbpedia.categorysearch.data.Category;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import static java.util.stream.Collectors.joining;

public class CategoryDocumentMapper {

    public static final String CATEGORY_FIELD = "category";
    public static final String ARTICLES_FIELD = "articles";

    public Document toDoc(Category cat) {
        var res = new Document();

        res.add(new TextField(CATEGORY_FIELD, cat.getName(), Field.Store.NO));
        res.add(new StringField("name", cat.getName(), Field.Store.YES));

        var articles = cat.getArticles();
        for (int i = 0; i < articles.size(); i++) {
            res.add(new StringField("articleName" + i, articles.get(i).getName(), Field.Store.YES));
            res.add(new StringField("articleLink" + i, articles.get(i).getLink(), Field.Store.YES));
        }
        res.add(
                new TextField(
                        ARTICLES_FIELD,
                        cat.getArticles().stream().map(Article::getName).collect(joining(", ")),
                        Field.Store.NO
                )
        );

        return res;
    }

    public Category toCategory(Document doc) {
        var cat = Category.empty(doc.get("name"));
        int i = 0;
        while (true) {
            var name = doc.get("articleName" + i);
            var link = doc.get("articleLink" + i);
            if (name == null) {
                break;
            }
            cat.addArticle(Article.of(name, link));
            i++;
        }
        return cat;
    }

}
