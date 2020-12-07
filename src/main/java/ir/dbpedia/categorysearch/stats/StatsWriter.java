package ir.dbpedia.categorysearch.stats;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import static ir.dbpedia.categorysearch.search.SearchResultWriter.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
public class StatsWriter {

    private final Path output;

    public void write(List<StatsResult> stats) {
        try (var f = new FileWriter(output.toFile(), UTF_8)) {
            f.append("Statistics\n");

            for (var stat : stats) {
                f.append("\n---------------\n");
                append(f, "Language", stat.getLanguage().name());
                append(f, "Number of categories", stat.getCount());

                append(f, "Maximum number of articles per category", stat.getMaxArticlesPerCategory());
                append(f, "Number of categories with maximum number of articles", stat.getWithMaxArticles().size());
                append(f, "Categories with maximum number of articles", stat.getWithMaxArticles());

                append(f, "Minimum number of articles per category", stat.getMinArticlesPerCategory());
                append(f, "Number of categories with minimum number of articles", stat.getWithMinArticles().size());
                append(f, "Categories with minimum number of articles", stat.getWithMinArticles());

                append(f, "Average number of articles per category", Math.round(stat.getAverageArticlesPerCategory() * 100) / 100.0);
                append(f, "Number of categories with average number of articles", stat.getWithAverageArticles().size());
                append(f, "Categories with average number of articles", stat.getWithAverageArticles());

                append(f, "Modes of number of articles per category", stat.getModeArticlesPerCategory());
                append(f, "Number of categories with mode of number of articles", stat.getWithModeArticles().size());
                append(f, "Categories with mode of number of articles", stat.getWithModeArticles());
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SneakyThrows
    private void append(FileWriter f, String prop, Object value) {
        f.append(prop)
                .append(": ")
                .append(encode(value.toString()))
                .append("\n");
    }
}
