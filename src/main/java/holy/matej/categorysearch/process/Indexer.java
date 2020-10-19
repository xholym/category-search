package holy.matej.categorysearch.process;

import holy.matej.categorysearch.data.Category;
import holy.matej.categorysearch.data.CategoryDocumentMapper;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class Indexer {

    private final Path indexDir;
    private final CategoryDocumentMapper categoryMapper;

    public Indexer(Path dataDir) {
        indexDir = dataDir.resolve("index");
        categoryMapper = new CategoryDocumentMapper();
    }

    public void index(Collection<Category> categories) {
        try (var index = indexWriter()) {

            var docs = categories.stream()
                    .map(categoryMapper::toDoc)
                    .collect(toList());

            index.addDocuments(docs);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private IndexWriter indexWriter() {
        try {
            var dir = FSDirectory.open(indexDir);

            // TODO change index config
            return new IndexWriter(dir, new IndexWriterConfig());

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
