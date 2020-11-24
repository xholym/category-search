package holy.matej.categorysearch.process;

import holy.matej.categorysearch.data.Article;
import holy.matej.categorysearch.data.Category;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class Parser {

    public static final Pattern pattern = Pattern.compile("<.*\\/(.+)>"
            + " <.*>"
            + " <.*(?:Category|Kateg\\\\u00F3ria|Kategorie):(.*?)>"
            + " <(.+)>");

    public Stream<Category> parse(Stream<String> lines) {

            var data = lines.filter(l -> !l.startsWith("#"))
                    .map(this::parseData)
                    .sorted(Comparator.comparing(a -> a.get("category")))
                    .iterator();


            Category cat = null;
            Map<String, String> walk;
            Stream.Builder<Category> res = Stream.builder();

            while (data.hasNext()) {
                walk = data.next();
                var name = walk.get("category");
                var article = Article.of(walk.get("article"), walk.get("articleLink"));

                if (cat == null) {
                    cat = Category.of(name, article);
                } else if (name.equals(cat.getName())) {
                    cat.addArticle(article);
                } else {
                    res.accept(cat);
                    cat = Category.of(name, article);
                }
            }
            if (cat != null)
                res.accept(cat);

            return res.build();
    }

    private Map<String, String> parseData(String line) {
        var regex = pattern.matcher(line);

        if (!regex.find())
            throw new IllegalStateException("Cannot match pattern");

        return Map.of(
                "article", regex.group(1).replace("_", " "),
                "category", regex.group(2).replace("_", " "),
                "articleLink", regex.group(3)
        );
    }
}
