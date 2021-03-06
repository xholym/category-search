package ir.dbpedia.categorysearch.process.io;

import ir.dbpedia.categorysearch.data.Article;
import ir.dbpedia.categorysearch.data.Category;
import ir.dbpedia.categorysearch.lang.Language;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static ir.dbpedia.categorysearch.process.io.CategoryWriter.*;

@RequiredArgsConstructor
public class CategoryReader {

    @SneakyThrows
    public Stream<Category> read(Path target) {
        return Files.lines(target)
                .skip(1)    // skip header
                .map(this::parseLine);
    }

    public Category parseLine(String line) {
        var parts = line.split(categoryArticleSeparator);
        if (parts.length < 2) {
            throw new InvalidLine(parts[0], parts.length, categoryArticleSeparator);
        }

        var category = Category.empty(parts[0]);

        for (var a : parts[1].split(articleSeparator)) {
            var props = a.split(nameLinkSeparator);
            if (props.length != 2)
                throw new InvalidLine(parts[0], parts.length, nameLinkSeparator);

            category.addArticle(
                    Article.of(
                            props[0],
                            props[1]
                    )
            );
        }

        return category;
    }

    public static class InvalidLine extends RuntimeException {
        public InvalidLine(String line, int found, String separator) {
            super("Line starting with " + line + " does not contain all required values."
                    + " Found " + found + " parts after for separator '"
                    + separator + "'."
            );
        }
    }
}
