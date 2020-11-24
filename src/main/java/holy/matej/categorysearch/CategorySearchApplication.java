package holy.matej.categorysearch;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.Processor;
import holy.matej.categorysearch.search.SearchResult;
import holy.matej.categorysearch.search.Searcher;
import lombok.SneakyThrows;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static holy.matej.categorysearch.lang.Language.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CategorySearchApplication {

    public static final String indexCmd = "index";
    public static final String searchCmd = "search";

    public static void main(String[] args) {
        System.out.println("Running...");

        if (args.length < 1)
            throw new IllegalArgumentException("No arguments entered");
        var dataDir = Path.of(args[1]);
        if (!dataDir.toFile().exists())
            throw new IllegalArgumentException("Entered file does not exist");

        var cmd = args[0];
        switch (cmd) {
            case indexCmd -> {
                index(dataDir);
            }
            case searchCmd -> {
                var lang = Language.valueOf(args[2]);
                var searchStr = args[3];
                search(dataDir, searchStr, lang);
            }
            default -> throw new IllegalArgumentException(
                    "Not recognized command " + cmd);
        }

    }

    public static void index(Path dataDir) {
        var p = new Processor(dataDir);
        p.process(en);
        p.process(sk);
        p.process(de);
    }

    public static void search(Path dataDir, String searchText, Language lang) {
        System.out.println("Searching for '" + searchText + "'");

        var s = new Searcher(dataDir);
        var res = s.search(searchText, lang);

        System.out.println("Found: " + res.size() + " results");
        var ntop = 5;
        printTopResults(res, ntop);
        saveResult(res);
    }

    private static void printTopResults(List<SearchResult> res, int ntop) {
        var end = Math.min(res.size(), ntop);
        var top = res.subList(0, end);
        var ps = new PrintStream(System.out, true, UTF_8);


        ps.println("Top " + end + " results");
        for (var r : top) {
            ps.println(res.indexOf(r) + " -> " + r.asString());
        }
    }

    @SneakyThrows
    private static void saveResult(List<SearchResult> res) {
        var f = Path.of("./result");
        Files.writeString(
                f,
                "Found: " + res.size()
                        + " results [\n" + res.stream()
                        .map(r -> res.indexOf(r) + " -> " + r.asString())
                        .collect(Collectors.joining(",\n"))
                        + "]"

        );
    }
}
