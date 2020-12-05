package holy.matej.categorysearch.process;

import holy.matej.categorysearch.data.Category;
import holy.matej.categorysearch.process.io.CategoryReader;
import holy.matej.categorysearch.process.io.CategoryWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class CategoryMerger {

    private final CategoryWriter categoryWriter;

    public CategoryMerger() {
        this.categoryWriter = new CategoryWriter();
    }

    public void merge(Stream<Category> mappings, Path mergedPath) {

//                    .sorted(Comparator.comparing(a -> a.get("category")))
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
