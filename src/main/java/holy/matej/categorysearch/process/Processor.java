package holy.matej.categorysearch.process;

import com.google.code.externalsorting.ExternalSort;
import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.io.CategoryReader;
import holy.matej.categorysearch.process.io.CategoryWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.Comparator;

import static holy.matej.categorysearch.process.io.CategoryWriter.categoryArticleSeparator;

@RequiredArgsConstructor
public class Processor {

    private final DataLoader loader;

    private final CategoryArticleParser categoryArticleParser;
    private final CategoryMerger categoryMerger;

    private final CategoryWriter categoryWriter;
    private final CategoryReader categoryReader;

    private final Indexer indexer;

    private final Path mappingsDir;
    private final Path parsedDir;

    public Processor(Path dataDir) {
        this.parsedDir = dataDir.resolve("parsed");
        this.mappingsDir = dataDir.resolve("parsedmappings");
        var indexDir = dataDir.resolve("index");
        ensureClearDir(parsedDir);
        ensureClearDir(mappingsDir);
        ensureClearDir(indexDir);

        this.loader = new DataLoader(dataDir);
        this.categoryArticleParser = new CategoryArticleParser();

        this.categoryMerger = new CategoryMerger();

        this.categoryWriter = new CategoryWriter();
        this.categoryReader = new CategoryReader();
        this.indexer = new Indexer(indexDir);
    }


    public void process(Language lang) {
        System.out.println("Processing language " + lang);

        var data = loader.load(lang);

        // parsing
        System.out.println("Parsing data to category article mappings");
        var mappings = categoryArticleParser.parse(data);

        System.out.println("Writings mappings");
        var mappingsPath = mappingsDir.resolve(lang.name());
        categoryWriter.write(mappingsPath, mappings);

        System.out.println("Sorting mappings");
        sortFile(mappingsPath);

        System.out.println("Reading mappings");
        mappings = categoryReader.read(mappingsPath);

        System.out.println("Merging category article mappings to categories");
        var categories = categoryMerger.merge(mappings);
        // - parsing

        var parsedPath = parsedDir.resolve(lang.name());
        System.out.println("Writing categories");
        categoryWriter.write(parsedPath, categories);

        System.out.println("Reading categories");
        categories = categoryReader.read(parsedPath);

        System.out.println("Indexing");
        indexer.index(categories, lang);
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

    private static void ensureClearDir(Path path) {
        var dir = path.toFile();
        if (dir.exists())
            dir.delete();
        dir.mkdir();
    }
}
