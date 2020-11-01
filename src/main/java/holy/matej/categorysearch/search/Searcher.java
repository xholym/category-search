package holy.matej.categorysearch.search;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.search.SearchResult.CategoryResult;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class Searcher {

    public static final int maxHits = 1000;
    private final Path indexDir;
    private final CategoryDocumentMapper categoryMapper;

    public Searcher(Path dataDir) {
        this.indexDir = dataDir.resolve("index");
        categoryMapper = new CategoryDocumentMapper();
    }

    public Collection<SearchResult> search(String text, Language lang) {
        try (var reader = indexReader(lang)) {
            var searcher = new IndexSearcher(reader);

            // TODO maybe use custom analyzer
            var q = new QueryParser("category", new StandardAnalyzer())
                    .parse(text);

            var res = searcher.search(q, maxHits);

            var results = stream(res.scoreDocs)
                    .map(d -> SearchResult.of(
                            d.score, findCategory(searcher, d)
                    ));

            return mergeSameCategories(results);

        } catch (ParseException e) {
            throw new IllegalStateException("Cannnot parse " + text);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Collection<SearchResult> mergeSameCategories(Stream<SearchResult> found) {
        var res = new HashMap<String, SearchResult>();

        found.forEach(it -> {
                var c = it.getCategory();
                var name = c.getName();

                if (res.containsKey(name)) {
                    var previous = res.get(name).getCategory();
                    previous.addArticles(c.getArticles());
                } else {
                    res.put(name, it);
                }
            }
        );
        return res.values();
    }

    private CategoryResult findCategory(IndexSearcher s, ScoreDoc d) {
        try {
            var doc = s.doc(d.doc);
            return categoryMapper.toCategory(doc);
        } catch (IndexNotFoundException e) {
            throw new IllegalStateException("Index must be created first!");
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
