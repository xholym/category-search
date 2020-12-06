package holy.matej.categorysearch.process;

import holy.matej.categorysearch.data.Category;
import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.search.CategoryDocumentMapper;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Indexer {

    private final CategoryDocumentMapper categoryMapper;

    public Indexer() {
        categoryMapper = new CategoryDocumentMapper();
    }

    public void index(Stream<Category> categories, Path indexFile) {
        try (var index = indexWriter(indexFile)) {

            categories.forEach(c -> {
                var doc = categoryMapper.toDoc(c);
                addToIndex(index, doc);
            });

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SneakyThrows
    private void addToIndex(IndexWriter index, Document doc) {
        index.addDocument(doc);
    }

    private IndexWriter indexWriter(Path indexFile) {
        try {
            var dir = FSDirectory.open(indexFile);
            var cfg = new IndexWriterConfig(new StandardAnalyzer());

            return new IndexWriter(dir, cfg);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
