package holy.matej.categorysearch.data;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class Article {

    @NonNull
    String name;

    @NonNull
    String link;
}
