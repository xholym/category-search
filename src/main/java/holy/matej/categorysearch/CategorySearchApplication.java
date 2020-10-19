package holy.matej.categorysearch;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.ProcessorFactory;
import holy.matej.categorysearch.search.SearchResult;
import holy.matej.categorysearch.search.Searcher;

import java.nio.file.Path;
import java.util.stream.Collectors;

import static holy.matej.categorysearch.lang.Language.*;

public class CategorySearchApplication {

    public static final String indexCmd = "index";
    public static final String searchCmd = "search";

    public static void main(String[] args) {
        System.out.println("Running...");

        if (args.length < 1)
            throw new IllegalArgumentException("No arguments entered");

        var cmd = args[0];
        switch (cmd) {
            case indexCmd -> {
                var dataDir = Path.of(args[1]);
                index(dataDir);
            }
            case searchCmd -> {
                var dataDir = Path.of(args[1]);
                var lang = Language.valueOf(args[2]);
                var searchStr = args[3];
                search(dataDir, searchStr, lang);
            }
            default -> throw new IllegalArgumentException(
                    "Not recognized command " + cmd);
        }

    }

    public static void index(Path dataDir) {
        var p = ProcessorFactory.create(dataDir);
        p.process(en);
        p.process(sk);
        p.process(de);
    }

    public static void search(Path dataDir, String searchText, Language lang) {
        System.out.println("Searching for '" + searchText + "'");
        var s = new Searcher(dataDir);
        var results = s.search(searchText, lang);
        System.out.println(
                "Found: [\n" + results.stream()
                        .map(SearchResult::toString)
                        .collect(Collectors.joining("\n"))
                + "]"
        );
    }
}
