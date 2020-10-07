package holy.matej.categorysearch.process;

import holy.matej.categorysearch.lang.Language;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

public class DataLoader {

    @SneakyThrows
    public Stream<String> load(Language lang) {
        var file = getClass().getClassLoader()
                .getResource(
                        "article_categories_"
                                + lang.name().toLowerCase() + ".nq"
                );
        var path = Path.of(Objects.requireNonNull(file).toURI());

        try {

            return Files.lines(path);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
