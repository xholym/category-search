package ir.dbpedia.categorysearch.process.io;

import ir.dbpedia.categorysearch.data.Category;
import ir.dbpedia.categorysearch.lang.Language;
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

    public void write(Path targetPath, Stream<Category> categories) {
        var target = targetPath.toFile();

        try (var f = new FileWriter(target, UTF_8)) {

            categories.forEach(it -> write(f, it));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void write(FileWriter f, Category cat) {
        try {
            f.append(cat.getName())
                    .append(categoryArticleSeparator);

            var articleStr = cat.getArticles()
                    .stream()
                    .map(a -> a.getName() + nameLinkSeparator + a.getLink())
                    .collect(joining(articleSeparator));
            f.append(articleStr)
                    .append("\n");

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
