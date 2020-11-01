package holy.matej.categorysearch.process;

import holy.matej.categorysearch.data.Article;
import holy.matej.categorysearch.data.ParsedCategory;
import holy.matej.categorysearch.lang.Language;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class Processor {

    private final Path parsedDir;
    private final DataLoader loader;
    private final Parser parser;
    private final Indexer indexer;

    public void process(Language lang) {
        var data = loader.load(lang);

        parser.parse(data, lang);
        var categories = read(lang);
        indexer.index(categories, lang);
    }

    @SneakyThrows
    private Stream<ParsedCategory> read(Language lang) {
        return Files.lines(parsedDir.resolve(lang.name()))
                .map(l -> {
                            var parts = l.split(";");
                            if (parts.length != 3) {
                                throw new RuntimeException("ERROR: line " + l
                                        + " does not contain all required values");
                            }

                            return ParsedCategory.of(
                                    parts[0],
                                    Article.of(parts[1], parts[2])
                            );
                        }
                );
    }
}
