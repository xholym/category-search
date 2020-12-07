package holy.matej.categorysearch;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.Processor;
import holy.matej.categorysearch.search.SearchRequest;
import holy.matej.categorysearch.search.SearchResultWriter;
import holy.matej.categorysearch.search.Searcher;
import holy.matej.categorysearch.stats.StatsCalculator;
import holy.matej.categorysearch.stats.StatsResult;
import holy.matej.categorysearch.stats.StatsWriter;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class CategorySearchApplication {

    private static final String indexCmd = "index";
    private static final String searchCmd = "search";
    private static final String statsCmd = "stats";
    private static final String helpCmd = "help";

    public static void main(String[] args) {
        System.out.println("Running...");

        try {
            if (args.length < 1)
                throw new IllegalArgumentException("No arguments entered");
            Path dataDir = null;
            if (args.length > 1) {
                dataDir = Path.of(args[1]);
                if (!dataDir.toFile().exists())
                    throw new IllegalArgumentException("Entered file does not exist");
            }

            var cmd = args[0];
            switch (cmd) {
                case helpCmd -> help();
                case indexCmd -> {
                    if (dataDir == null)
                        throw new IllegalArgumentException("No data directory entered");
                    index(dataDir);
                }
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
        } catch (Exception e) {
            handleError(e);
        }

    }

    private static void index(Path dataDir) {
        var parsedDir = dataDir.resolve("parsed");
        var mappingsDir = dataDir.resolve("parsedmappings");
        var indexDir = dataDir.resolve("index");
        ensureClearDir(parsedDir);
        ensureClearDir(mappingsDir);
        ensureClearDir(indexDir);
        var p = new Processor(dataDir, mappingsDir, parsedDir, indexDir);

        for (var l : Language.values())
            p.process(l);

        deleteDir(mappingsDir.toFile());
    }

    private static void search(Path dataDir, SearchRequest searchReq, Language lang) {
        System.out.println("Searching for '" + searchReq.asString() + "'");

        var indexFile = dataDir.resolve("index").resolve(lang.name());

        var s = new Searcher();
        var res = s.search(searchReq, indexFile);

        System.out.println("Found: " + res.size() + " results");
        var ntop = 5;

        var writer = new SearchResultWriter();
        writer.writeTopResults(res, System.out, ntop);
        var path = Path.of(System.getProperty("user.dir")).resolve("result.txt");
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

    private static void help() {
        System.out.print(
                "category-search: searches for categories and their articles\n"
                        + "program takes these arguments: <operation> [operation args]\n"
                        + "    - operation args are based on operation\n\n"
                        + "  operations and their arguments are:\n"
                        + "    index <data-directory>\n"
                        + "    - indexes data for each language\n"
                        + "    - creates index directory and parsed directory directory in <data-directory>\n"
                        + "    - <data-directory> must contain dataset for each language named article_categories_<language>.nq\n"
                        + "    search <data-directory> <language> [-c <category>] [-a <articles>] [category]\n"
                        + "    - searches for category based on category argument and articles argument\n"
                        + "    - top search results are printed and others are saved to results.txt in current working directory\n"
                        + "    - data must be indexed first\n"
                        + "    - at least one of category or articles arguments must be specified\n"
                        + "    - if <category> or <articles> arguments consist of multiple words they must be in '' or \"\"\n"
                        + "    - <language> argument can have values <en>, <sk> or <de>\n"
                        + "    stats <data-directory>\n"
                        + "    - calculates statistics from parsed data\n"
                        + "    - statistics are saved to stats.txt in current working directory\n"
                        + "    - data must be indexed first\n"
                        + "    help\n"
                        + "    - prints this help message\n"
        );
        System.exit(0);
    }

    private static SearchRequest parseSearchRequest(String[] args) {
        String category = null;
        String article = null;
        for (int i = 3; i < args.length; i++) {
            switch (args[i]) {
                case "-c", "--category" -> {
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("missing value after " + args[i] + " argument");
                    if (category != null)
                        throw new IllegalArgumentException("Category already entered! Wrong argument " + args[i] + " " + args[i + 1]);
                    category = args[i + 1];
                    i++;
                }
                case "-a", "--article" -> {
                    if (i + 1 >= args.length)
                        throw new IllegalArgumentException("missing value after " + args[i] + " argument");
                    article = args[i + 1];
                    i++;
                }
                default -> {
                    if (category != null)
                        throw new IllegalArgumentException("Category already entered! Wrong argument " + args[i]);
                    category = args[i];
                }
            }
        }
        return new SearchRequest(category, article);
    }

    private static void handleError(Exception e) {
        var msg = e.getMessage();
        System.err.println("ERROR: " + msg);
        var errfile = Path.of(System.getProperty("user.dir")).resolve("error-log.txt");
        System.out.println("Try help to print help message.");
        System.out.println("Full stack trace can be found at " + errfile);
        try (var out = new PrintStream(new FileOutputStream(errfile.toFile()))) {
            e.printStackTrace(out);
        } catch (IOException ioE) {
            throw new UncheckedIOException(ioE);
        }
        System.exit(-1);
    }

    private static void ensureClearDir(Path path) {
        var dir = path.toFile();
        if (dir.exists())
            deleteDir(dir);
        dir.mkdir();
    }

    private static void deleteDir(File dir) {
        for (var subFile : dir.listFiles()) {
            if(subFile.isDirectory()) {
                deleteDir(subFile);
            } else {
                subFile.delete();
            }
        }
        dir.delete();
    }
}
