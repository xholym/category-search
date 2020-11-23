package holy.matej.categorysearch.search;

import holy.matej.categorysearch.data.Article;
import holy.matej.categorysearch.data.ParsedCategory;
import holy.matej.categorysearch.search.SearchResult.CategoryResult;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class CategoryDocumentMapper {

    public Document toDoc(ParsedCategory cat) {
        var res = new Document();

        res.add(new TextField("category", cat.getName(), Field.Store.NO));
        res.add(new StringField("name", cat.getName(), Field.Store.YES));
        res.add(new StringField("article", cat.getArticle().getName(), Field.Store.YES));
        res.add(new StringField("articleLink", cat.getArticle().getLink(), Field.Store.YES));

        return res;
    }

    public CategoryResult toCategory(Document doc) {
        return CategoryResult.of(
                encode(doc.get("name")),
                Article.of(
                        encode(doc.get("article")),
                        encode(doc.get("articleLink"))
                )
        );
    }

    private String encode(String s) {
        return StringEscapeUtils.unescapeJava(s);
    }
}
