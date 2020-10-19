package holy.matej.categorysearch.search;

import holy.matej.categorysearch.data.Category;
import holy.matej.categorysearch.data.CategoryDocumentMapper;
import holy.matej.categorysearch.lang.Language;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class Searcher {

    public static final int maxHits = 5;
    private final Path indexDir;
    private final CategoryDocumentMapper categoryMapper;

    public Searcher(Path dataDir) {
        this.indexDir = dataDir.resolve("index");
        categoryMapper = new CategoryDocumentMapper();
    }

    public List<SearchResult> search(String text, Language lang) {
        try (var reader = indexReader(lang)) {
            var searcher = new IndexSearcher(reader);

            // TODO maybe use custom analyzer
            var q = new QueryParser("category", new StandardAnalyzer())
                    .parse(text);

            var res = searcher.search(q, maxHits);

            return stream(res.scoreDocs)
                    .map(d -> SearchResult.of(
                            d.score, findCategory(searcher, d)
                    ))
                    .collect(toList());

        } catch (ParseException e) {
            throw new IllegalStateException("Cannnot parse " + text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Category findCategory(IndexSearcher s, ScoreDoc d) {
        try {
            var doc = s.doc(d.doc);
            return categoryMapper.toCategory(doc);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SneakyThrows
    private IndexReader indexReader(Language lang) {

        return DirectoryReader.open(
                FSDirectory.open(indexDir.resolve(lang.name()))
        );
    }
}
