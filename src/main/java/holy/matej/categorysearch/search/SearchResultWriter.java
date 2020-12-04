package holy.matej.categorysearch.search;

import lombok.SneakyThrows;
import org.apache.commons.text.StringEscapeUtils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SearchResultWriter {

    public void writeTopResults(List<SearchResult> res, OutputStream out, int ntop) {
        var end = Math.min(res.size(), ntop);
        var top = res.subList(0, end);
        var ps = new PrintStream(out, true, UTF_8);

        ps.println("Top " + end + " results");
        for (var r : top) {
            ps.println(res.indexOf(r) + " -> " + encode(r.asString()));
        }
    }

    @SneakyThrows
    public void writeResults(List<SearchResult> res, Path path) {
        Files.writeString(
                path,
                "Found: " + res.size()
                        + " results [\n" + res.stream()
                        .map(r -> res.indexOf(r) + " -> " + encode(r.asString()))
                        .collect(Collectors.joining(",\n"))
                        + "]"

        );
    }

    public static String encode(String s) {
        return StringEscapeUtils.unescapeJava(s);
    }
}
