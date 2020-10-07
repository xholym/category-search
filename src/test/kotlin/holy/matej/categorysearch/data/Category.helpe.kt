package holy.matej.categorysearch.data


fun category(name: String,
             articles: List<Article>) =
        Category.of(name, articles)