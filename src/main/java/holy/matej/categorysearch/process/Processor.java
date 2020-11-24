package holy.matej.categorysearch.process;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.io.CategoryReader;
import holy.matej.categorysearch.process.io.CategoryWriter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
public class Processor {

    private final DataLoader loader;
    private final Parser parser;
    private final CategoryWriter categoryWriter;
    private final CategoryReader categoryReader;
    private final Indexer indexer;

    public Processor(Path dataDir) {
        var parsedDir = dataDir.resolve("parsed");

        var dir = parsedDir.toFile();
        if (!dir.exists())
            dir.mkdir();

        var indexDir = dataDir.resolve("index");
        dir = indexDir.toFile();
        if (dir.exists())
            dir.delete();

        this.loader = new DataLoader(dataDir);
        this.parser = new Parser();
        this.categoryWriter = new CategoryWriter(parsedDir);
        this.categoryReader = new CategoryReader(parsedDir);
        this.indexer = new Indexer(indexDir);
    }


    public void process(Language lang) {
        var data = loader.load(lang);

        System.out.println("Parsing data to categories (" + lang + ")...");
        var categories = parser.parse(data);

        System.out.println("Writing categories (" + lang + ")...");
        categoryWriter.write(categories, lang);

        System.out.println("Reading categories (" + lang + ")...");
        categories = categoryReader.read(lang);

        System.out.println("Indexing (" + lang + ")...");
        indexer.index(categories, lang);
    }

}
