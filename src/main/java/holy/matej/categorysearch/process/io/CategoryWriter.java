package holy.matej.categorysearch.process.io;

import holy.matej.categorysearch.data.Category;
import holy.matej.categorysearch.lang.Language;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
public class CategoryWriter {

    public static final String categoryArticleSeparator = ";;;";
    public static final String articleSeparator = ":::";
    public static final String nameLinkSeparator = ",,,";

    private final Path parsedDir;

    public void write(Stream<Category> categories, Language lang) {
        var target = parsedDir.resolve(lang.name() + ".csv").toFile();

        try (var f = new FileWriter(target, UTF_8)) {
            writeHeader(f);

            categories.forEach(it -> write(f, it));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SneakyThrows
    private void write(FileWriter f, Category cat) {
        f.append(cat.getName())
                .append(categoryArticleSeparator);

        var articleStr = cat.getArticles()
                .stream()
                .map(a -> a.getName() + nameLinkSeparator + a.getLink())
                .collect(joining(articleSeparator));
        f.append(articleStr)
                .append("\n");
    }


    @SneakyThrows
    private void writeHeader(FileWriter f) {
        f.append("category" + categoryArticleSeparator
                + "articleName 0" + nameLinkSeparator + "articleLink 0"
                + articleSeparator
                + "articleName 1" + nameLinkSeparator + "articleLink 1"
                + articleSeparator + "..."
                + "articleName n" + nameLinkSeparator + "articleLink n"
        );
    }
}
