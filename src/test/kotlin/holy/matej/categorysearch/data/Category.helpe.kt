package holy.matej.categorysearch.data


fun category(name: String,
             articles: List<Article>) =
        ParsedCategory.of(name, articles)
