package holy.matej.categorysearch.data;

import lombok.NonNull;
import lombok.Value;

@Value(staticConstructor = "of")
public class ParsedCategory {

    @NonNull
    String name;

    Article article;
}
