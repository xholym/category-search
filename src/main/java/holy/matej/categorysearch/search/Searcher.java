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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static holy.matej.categorysearch.search.CategoryDocumentMapper.CATEGORY_FIELD;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Searcher {

    public static final int MAX_HITS = 1000;
    public static final float CATEGORY_BOOST = 1.5f;
    private final Path indexDir;
    private final CategoryDocumentMapper categoryMapper;

    public Searcher(Path dataDir) {
        this.indexDir = dataDir.resolve("index");
        categoryMapper = new CategoryDocumentMapper();
    }

    public List<SearchResult> search(SearchRequest req, Language lang) {
        try (var reader = indexReader(lang)) {
            var searcher = new IndexSearcher(reader);

            var q = new MultiFieldQueryParser(
                    req.fields(),
                    new StandardAnalyzer(),
                    Map.of(CATEGORY_FIELD, CATEGORY_BOOST)
            ).parse(req.queryStr());

            var res = searcher.search(q, MAX_HITS);

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
