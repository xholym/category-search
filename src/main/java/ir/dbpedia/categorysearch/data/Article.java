package ir.dbpedia.categorysearch.data;

import lombok.Value;

@Value(staticConstructor = "of")
public class Article {

    String name;

    String link;
}
