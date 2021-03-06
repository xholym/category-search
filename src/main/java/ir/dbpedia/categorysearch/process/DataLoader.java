package ir.dbpedia.categorysearch.process;

import ir.dbpedia.categorysearch.lang.Language;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.apache.commons.text.StringEscapeUtils;

@RequiredArgsConstructor
public class DataLoader {

    private final Path dataDir;

    @SneakyThrows
    public Stream<String> load(Language lang) {
        var path = dataDir.resolve(
                        "article_categories_"
                                + lang.name().toLowerCase() + ".nq"
                );
        try {

            return Files.lines(path);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
