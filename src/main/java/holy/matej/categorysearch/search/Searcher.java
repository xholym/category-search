package holy.matej.categorysearch.search;

import holy.matej.categorysearch.data.Category;
import holy.matej.categorysearch.lang.Language;
import lombok.SneakyThrows;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import static holy.matej.categorysearch.search.CategoryDocumentMapper.ARTICLES_FIELD;
import static holy.matej.categorysearch.search.CategoryDocumentMapper.CATEGORY_FIELD;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Searcher {

    public static final int MAX_HITS = 1000;
    private final CategoryDocumentMapper categoryMapper;

    public Searcher() {
        categoryMapper = new CategoryDocumentMapper();
    }

    public List<SearchResult> search(SearchRequest req, Path indexFile) {
        try (var reader = indexReader(indexFile)) {
            var searcher = new IndexSearcher(reader);

            var q = buildQuery(req);

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

    private Query buildQuery(SearchRequest req) throws ParseException {
        var res = new BooleanQuery.Builder();
        var analyzer = new StandardAnalyzer();

        if (req.getCategory() != null) {
            var q = new QueryParser(CATEGORY_FIELD, analyzer)
                    .parse(req.getCategory());
            res.add(q, BooleanClause.Occur.MUST);
        }
        if (req.getArticle() != null) {
            var q = new QueryParser(ARTICLES_FIELD, analyzer)
                    .parse(req.getArticle());
            res.add(q, BooleanClause.Occur.SHOULD);
        }

        return res.build();
    }

    @SneakyThrows
    private IndexReader indexReader(Path indexFile) {

        return DirectoryReader.open(
                FSDirectory.open(indexFile)
        );
    }
}
