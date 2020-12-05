package holy.matej.categorysearch.process;

import holy.matej.categorysearch.data.Category;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class CategoryMerger {


    public Stream<Category> merge(Stream<Category> articleCategories) {

        var data = articleCategories.iterator();
//                    .sorted(Comparator.comparing(a -> a.get("category")))

                Category cat = null;
        Category walk;
        Stream.Builder<Category> res = Stream.builder();

        while (data.hasNext()) {
            walk = data.next();
            var name = walk.getName();
            var article = walk.getArticles().get(0);

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

}
