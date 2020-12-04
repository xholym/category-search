package holy.matej.categorysearch;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.Processor;
import holy.matej.categorysearch.search.SearchRequest;
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
                Language lang;
                try {
                    lang = Language.valueOf(args[2]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Argument " + args[2] + " is not a language!", e);
                }
                var searchReq = parseSearchRequest(args);
                search(dataDir, searchReq, lang);
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

    public static void search(Path dataDir, SearchRequest searchReq, Language lang) {
        System.out.println("Searching for '" + searchReq + "'");

        var s = new Searcher(dataDir);
        var res = s.search(searchReq, lang);

        System.out.println("Found: " + res.size() + " results");
        var ntop = 5;
        printTopResults(res, ntop);
        saveResult(res);
    }

    private static SearchRequest parseSearchRequest(String []args) {
        if (!args[3].startsWith("-") && args.length == 4)
            return new SearchRequest(args[3], null);

        String category = null;
        String article = null;
        for (int i = 3; i < args.length; i++) {
            switch (args[i]) {
                case "-c", "--category" -> {
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("missing value after " + args[i] + " argument");
                    category = args[i + 1];
                    i++;
                }
                case "-a", "--article" -> {
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("missing value after " + args[i] + " argument");
                    article = args[i + 1];
                    i++;
                }
                default -> throw new IllegalArgumentException("wrong argument " + args[i]);
            }
        }
        return new SearchRequest(category, article);
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
