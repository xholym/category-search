package holy.matej.categorysearch.search;

import holy.matej.categorysearch.data.Category;
import holy.matej.categorysearch.lang.Language;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
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
import static java.util.stream.Collectors.toSet;

public class Searcher {

    public static final int maxHits = 1000;
    private final Path indexDir;
    private final CategoryDocumentMapper categoryMapper;

    public Searcher(Path dataDir) {
        this.indexDir = dataDir.resolve("index");
        categoryMapper = new CategoryDocumentMapper();
    }

    public List<SearchResult> search(SearchRequest req, Language lang) {
        try (var reader = indexReader(lang)) {
            var searcher = new IndexSearcher(reader);

            var q = MultiFieldQueryParser.parse(
                    req.fields(),
                    req.searches(),
                    new StandardAnalyzer()
            );

            var res = searcher.search(q, maxHits);

            return stream(res.scoreDocs)
                    .map(d -> SearchResult.of(
                            d.score, findCategory(searcher, d)
                    ))
                    .filter(c -> !c.getCategory().getArticles().isEmpty())
                    .collect(toSet())
                    .stream()
                    .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))
                    .collect(toList());

        } catch (ParseException e) {
            throw new IllegalStateException("Cannnot parse " + req);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Category findCategory(IndexSearcher s, ScoreDoc d) {
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
