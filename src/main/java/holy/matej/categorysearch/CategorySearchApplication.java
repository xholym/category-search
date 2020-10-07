package holy.matej.categorysearch;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.Processor;
import holy.matej.categorysearch.process.ProcessorFactory;

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
            case indexCmd -> index();
            case searchCmd -> search();
            default -> throw new IllegalArgumentException(
                    "Not recognized command "+ cmd);
        }

    }

    public static void index() {
        var p = ProcessorFactory.create();
        p.process(en);
        p.process(sk);
        p.process(de);
    }

    public static void search() {
        System.out.println("mock search result");
    }
}
