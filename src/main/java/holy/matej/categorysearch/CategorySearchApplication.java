package holy.matej.categorysearch;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.Processor;
import holy.matej.categorysearch.search.SearchRequest;
import holy.matej.categorysearch.search.SearchResultWriter;
import holy.matej.categorysearch.search.Searcher;
import holy.matej.categorysearch.stats.StatsCalculator;
import holy.matej.categorysearch.stats.StatsResult;
import holy.matej.categorysearch.stats.StatsWriter;

import java.nio.file.Path;
import java.util.ArrayList;

import static holy.matej.categorysearch.lang.Language.*;

public class CategorySearchApplication {

    private static final String indexCmd = "index";
    private static final String searchCmd = "search";
    private static final String statsCmd = "stats";

    public static void main(String[] args) {
        System.out.println("Running...");

        if (args.length < 1)
            throw new IllegalArgumentException("No arguments entered");
        var dataDir = Path.of(args[1]);
        if (!dataDir.toFile().exists())
            throw new IllegalArgumentException("Entered file does not exist");

        var cmd = args[0];
        switch (cmd) {
            case indexCmd -> index(dataDir);
            case statsCmd -> stats(dataDir);
            case searchCmd -> {
                Language lang;
                try {
                    lang = Language.valueOf(args[2]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Argument " + args[2] + " is not a language!");
                }
                var searchReq = parseSearchRequest(args);
                search(dataDir, searchReq, lang);
            }
            default -> throw new IllegalArgumentException(
                    "Not recognized command " + cmd);
        }

    }

    private static void index(Path dataDir) {
        var p = new Processor(dataDir);
        p.process(en);
        p.process(sk);
        p.process(de);
    }

    private static void search(Path dataDir, SearchRequest searchReq, Language lang) {
        System.out.println("Searching for '" + searchReq + "'");

        var s = new Searcher(dataDir);
        var res = s.search(searchReq, lang);

        System.out.println("Found: " + res.size() + " results");
        var ntop = 5;

        var writer = new SearchResultWriter();
        writer.writeTopResults(res, System.out, ntop);
        var path = Path.of(System.getProperty("user.dir")).resolve("stats.txt");
        writer.writeResults(res, path);
        System.out.println("More results can be found in file " + path);
    }

    private static void stats(Path dataDir) {
        var statsMaker = new StatsCalculator(dataDir);
        var res = new ArrayList<StatsResult>();

        for (var lang : Language.values()) {
            res.add(statsMaker.calc(lang));
        }

        var path = Path.of(System.getProperty("user.dir")).resolve("stats.txt");
        var writer = new StatsWriter(path);
        writer.write(res);
        System.out.println("Statistics were written to file " + path);
    }

    private static SearchRequest parseSearchRequest(String[] args) {
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
}
