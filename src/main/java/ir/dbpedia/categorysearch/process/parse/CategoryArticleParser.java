package ir.dbpedia.categorysearch.process.parse;

import ir.dbpedia.categorysearch.data.Article;
import ir.dbpedia.categorysearch.data.Category;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CategoryArticleParser {

    public static final Pattern pattern = Pattern.compile("<.*\\/(.+)>"
            + " <.*>"
            + " <.*(?:Category|Kateg\\\\u00F3ria|Kategorie):(.*?)>"
            + " <(.+)>");

    public Stream<Category> parse(Stream<String> lines) {

        return lines.filter(l -> !l.startsWith("#"))
                .map(this::parseData);
    }

    private Category parseData(String line) {
        var regex = pattern.matcher(line);

        if (!regex.find())
            throw new IllegalStateException("Cannot match pattern");

        return new Category(
                regex.group(1).replace("_", " "),
                List.of(
                        Article.of(
                                regex.group(2).replace("_", " "),
                                regex.group(3)
                        )
                )
        );
    }
}
