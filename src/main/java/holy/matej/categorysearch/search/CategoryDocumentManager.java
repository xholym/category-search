package holy.matej.categorysearch.search;

import holy.matej.categorysearch.data.Article;
import holy.matej.categorysearch.data.Category;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.util.Set;

public class CategoryDocumentManager {

    public Document toDoc(Category cat) {
        var res = new Document();

        res.add(new TextField("category", cat.getName(), Field.Store.NO));
        res.add(new StringField("name", cat.getName(), Field.Store.YES));

        var articles = cat.getArticles();
        for (int i = 0; i < articles.size(); i++) {
            res.add(new StringField("articleName" + i, articles.get(i).getName(), Field.Store.YES));
            res.add(new StringField("articleLink" + i, articles.get(i).getLink(), Field.Store.YES));
        }

        return res;
    }

    public Category toCategory(Document doc) {
        var cat = Category.empty(encode(doc.get("name")));
        int i = 0;
        while (true) {
            var name = encode(doc.get("articleName" + i));
            var link = encode(doc.get("articleLink" + i));
            if (name == null) {
                break;
            }
            cat.addArticle(Article.of(name, link));
            i++;
        }
        return cat;
    }

    private String encode(String s) {
        return StringEscapeUtils.unescapeJava(s);
    }
}
