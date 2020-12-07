package ir.dbpedia.categorysearch.stats;

import ir.dbpedia.categorysearch.lang.Language;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class StatsResult {
    Language language;
    long count;

    int maxArticlesPerCategory;
    Set<String> withMaxArticles;

    int minArticlesPerCategory;
    Set<String> withMinArticles;

    double averageArticlesPerCategory;
    Set<String> withAverageArticles;

    int modeArticlesPerCategory;
    Set<String> withModeArticles;
}
