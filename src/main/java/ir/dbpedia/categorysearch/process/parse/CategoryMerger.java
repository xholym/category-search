package ir.dbpedia.categorysearch.process.parse;

import ir.dbpedia.categorysearch.data.Category;
import ir.dbpedia.categorysearch.process.io.CategoryWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class CategoryMerger {

    private final CategoryWriter categoryWriter;

    public CategoryMerger() {
        this.categoryWriter = new CategoryWriter();
    }

    public void merge(Stream<Category> mappings, Path mergedPath) {

        try (var out = new FileWriter(mergedPath.toFile())) {

            var iterator = (Iterable<Category>) mappings::iterator;
            Category cat = null;
            for (var walk: iterator) {
                var name = walk.getName();
                var article = walk.getArticles().get(0);
                if (cat == null) {
                    cat = Category.of(name, article);
                } else if (name.equals(cat.getName())) {
                    cat.addArticle(article);
                } else {

                    categoryWriter.write(out, cat);
                    cat = Category.of(name, article);
                }
            }
            if (cat != null)
                categoryWriter.write(out, cat);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

}
