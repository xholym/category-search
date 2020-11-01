package holy.matej.categorysearch.process;

import holy.matej.categorysearch.data.ParsedCategory;
import holy.matej.categorysearch.search.CategoryDocumentMapper;
import holy.matej.categorysearch.lang.Language;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Indexer {

    private final Path indexDir;
    private final CategoryDocumentMapper categoryMapper;

    public Indexer(Path indexDir) {
        this.indexDir = indexDir;
        categoryMapper = new CategoryDocumentMapper();
    }

    public void index(Stream<ParsedCategory> categories, Language lang) {
        try (var index = indexWriter(lang)) {

            categories.parallel()
                    .forEach(c -> {
                        var doc = categoryMapper.toDoc(c);
                        try {
                            index.addDocument(doc);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e); // fuj
                        }
                    });

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private IndexWriter indexWriter(Language lang) {
        try {
            var dir = FSDirectory.open(indexDir.resolve(lang.name()));
            var cfg = new IndexWriterConfig(new StandardAnalyzer());

            // TODO change index config
            return new IndexWriter(dir, cfg);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
