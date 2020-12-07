package holy.matej.categorysearch.process.parse;

import com.google.code.externalsorting.ExternalSort;
import holy.matej.categorysearch.data.Category;
import holy.matej.categorysearch.process.io.CategoryReader;
import holy.matej.categorysearch.process.io.CategoryWriter;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static holy.matej.categorysearch.process.io.CategoryWriter.categoryArticleSeparator;

public class Parser {

    private final CategoryArticleParser categoryArticleParser;
    private final CategoryMerger categoryMerger;

    private final CategoryWriter categoryWriter;
    private final CategoryReader categoryReader;

    public Parser() {
        this.categoryArticleParser = new CategoryArticleParser();
        this.categoryMerger = new CategoryMerger();

        this.categoryWriter = new CategoryWriter();
        this.categoryReader = new CategoryReader();
    }

    public Stream<Category> parse(Stream<String> data,
                                  Path mappingsFile,
                                  Path parsedFile) {

        System.out.println("Parsing data to category article mappings");
        var mappings = categoryArticleParser.parse(data);

        System.out.println("Writings mappings");
        categoryWriter.write(mappingsFile, mappings);

        System.out.println("Sorting mappings");
        sortFile(mappingsFile);

        mappings = categoryReader.read(mappingsFile);

        System.out.println("Merging category article mappings to categories");
        categoryMerger.merge(mappings, parsedFile);

        System.out.println("Reading categories");
        return categoryReader.read(parsedFile);
    }

    @SneakyThrows
    private void sortFile(Path path) {
        var f = path.toFile();
        Comparator<String> cmp = (a, b) -> {
            a = a.split(categoryArticleSeparator)[0];
            b = b.split(categoryArticleSeparator)[0];
            return a.compareTo(b);
        };
        ExternalSort.mergeSortedFiles(ExternalSort.sortInBatch(f, cmp), f, cmp);
    }
}
