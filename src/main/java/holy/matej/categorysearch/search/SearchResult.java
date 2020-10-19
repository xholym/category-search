package holy.matej.categorysearch.search;

import holy.matej.categorysearch.data.Category;
import lombok.Value;

@Value(staticConstructor = "of")
public class SearchResult {
    float score;
    Category cat;
}