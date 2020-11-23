package holy.matej.categorysearch.process;

import holy.matej.categorysearch.lang.Language;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
public class Parser {

    public static final Pattern pattern = Pattern.compile("<.*\\/(.+)>"
            + " <.*>"
            + " <.*(?:Category|Kateg\\\\u00F3ria|Kategorie):(.*?)>"
            + " <(.+)>");
    private final Path parsedDir;

    public void parse(Stream<String> lines, Language lang) {
        var target = parsedDir.resolve(lang.name() + ".csv").toFile();

        try (var f = new FileWriter(target, UTF_8)) {

            // header
            f.append("category;article;article link\n");

            lines.forEach(l -> {
                        if (l.startsWith("#"))
                            return;
                        var data = parseData(l);

                        writeData(f, data);
                    }
            );

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    @SneakyThrows
    private void writeData(FileWriter f, Map<String, String> data) {
        f.append(data.get("name"))
                .append(";")
                .append(data.get("article"))
                .append(";")
                .append(data.get("articleLink"))
                .append("\n");
    }

    private Map<String, String> parseData(String line) {
        var regex = pattern.matcher(line);

        if (!regex.find())
            throw new IllegalStateException("Cannot match pattern");

        return Map.of(
                "article", regex.group(1).replace("_", " "),
                "name", regex.group(2).replace("_", " "),
                "articleLink", regex.group(3)
        );
    }
}
