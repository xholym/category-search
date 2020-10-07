package holy.matej.categorysearch.process;

public class ProcessorFactory {

    public static Processor create() {
        return new Processor(
                new DataLoader(),
                new Parser(),
                new Indexer()
        );
    }
}
