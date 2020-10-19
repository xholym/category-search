package holy.matej.categorysearch.process;

import java.nio.file.Path;

public class ProcessorFactory {

    public static Processor create(Path dataDir) {
        return new Processor(
                new DataLoader(dataDir),
                new Parser(),
                new Indexer(dataDir)
        );
    }
}
