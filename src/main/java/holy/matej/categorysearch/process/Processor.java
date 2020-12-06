package holy.matej.categorysearch.process;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.parse.Parser;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.nio.file.Path;

@RequiredArgsConstructor
public class Processor {

    private final DataLoader loader;
    private final Parser parser;
    private final Indexer indexer;

    private final Path mappingsDir;
    private final Path parsedDir;
    private final Path indexDir;

    public Processor(Path dataDir, Path mappingsDir, Path parsedDir, Path indexDir) {
        this.mappingsDir = mappingsDir;
        this.parsedDir = parsedDir;
        this.indexDir = indexDir;

        this.loader = new DataLoader(dataDir);
        this.parser = new Parser();
        this.indexer = new Indexer();
    }

    public void process(Language lang) {
        System.out.println("Processing language (" + lang + ")");
        var data = loader.load(lang);

        var mappingsFile = mappingsDir.resolve(lang.name());
        var parsedFile = parsedDir.resolve(lang.name());
        System.out.println("Parsing categories");

        var categories = parser.parse(data, mappingsFile, parsedFile);

        var indexFile = indexDir.resolve(lang.name());
        System.out.println("Indexing");
        indexer.index(categories, indexFile);
    }

}
