package holy.matej.categorysearch.process;

import holy.matej.categorysearch.lang.Language;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Processor {

    private final DataLoader loader;
    private final Parser parser;
    private final Indexer indexer;

    public void process(Language lang) {
        var data = loader.load(lang);

        var categories = parser.parse(data);
        indexer.index(categories);
    }
}
