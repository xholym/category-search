package holy.matej.categorysearch.data;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.util.List;

public class CategoryDocumentMapper {

    public Document toDoc(Category cat) {
        var res = new Document();
        // TODO maybe other fields
        res.add(new TextField("category", cat.getName(), Field.Store.NO));
        res.add(new StringField("name", cat.getName(), Field.Store.YES));

        return res;
    }

    public Category toCategory(Document doc) {
        return Category.of(doc.get("name"), List.of());
    }
}
