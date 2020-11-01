package holy.matej.categorysearch.process;

import java.nio.file.Path;

public class ProcessorFactory {

    public static Processor create(Path dataDir) {
        var parsedDir = dataDir.resolve("parsed");

        var dir = parsedDir.toFile();
        if (!dir.exists())
            dir.mkdir();

        var indexDir = dataDir.resolve("index");
        dir = indexDir.toFile();
        if (dir.exists())
            dir.delete();

        return new Processor(
                parsedDir,
                new DataLoader(dataDir),
                new Parser(parsedDir),
                new Indexer(indexDir)
        );
    }
}
