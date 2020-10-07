package holy.matej.categorysearch.process;

import holy.matej.categorysearch.data.Article;
import holy.matej.categorysearch.data.Category;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Parser {

    public static final String pattern = "<.*\\/(.+)>"
            + " <.*>"
            + " <.*(Category|Kateg\\\\u00F3ria|Kategorie):(.*?)>"
            + " <(.+)>";

    public Collection<Category> parse(Stream<String> lines) {
        return lines.parallel()
                .filter(l -> !l.startsWith("#"))
                .map(this::parseData)
                .collect(
                        (Supplier<HashMap<String, Category>>) HashMap::new,
                        (res, cur) -> {
                            System.out.println("cur " + cur);
                            System.out.println("res" + cur);
                            var a = Article.of(
                                    cur.get("article"), cur.get("articleLink")
                            );

                            var name = cur.get("name");

                            if (res.containsKey(name)) {
                                res.get(name).addArticle(a);
                                System.out.println("adding article");
                            }
                            else {
                                var cat = Category.of(
                                        name,
                                        List.of(a)
                                );
                                res.put(name, cat);
                                System.out.println("adding category");
                            }
                        },
                        (m1, m2) -> m2.forEach(m1::putIfAbsent)
                )
                .values();
    }

    private Map<String, String> parseData(String line) {
        var regex = Pattern.compile(pattern).matcher(line);

        if (!regex.find())
            throw new IllegalStateException("Cannot match pattern");

        return Map.of(
                "article", regex.group(1).replace("_", " "),
                "name", regex.group(3).replace("_", " "),
                "articleLink", regex.group(4)
        );
    }
}
