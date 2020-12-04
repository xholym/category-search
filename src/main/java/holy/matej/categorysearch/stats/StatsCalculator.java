package holy.matej.categorysearch.stats;

import holy.matej.categorysearch.lang.Language;
import holy.matej.categorysearch.process.io.CategoryReader;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class StatsCalculator {

    private final Path parsedDir;
    private final CategoryReader categoryReader;


    public StatsCalculator(Path dataDir) {
        parsedDir = dataDir.resolve("parsed");
        if (!parsedDir.toFile().exists())
            throw new IllegalStateException("No parsed data found! Must first parse data using the index command.");
        categoryReader = new CategoryReader(parsedDir);
    }

    public StatsResult calc(Language lang) {
        var categories = categoryReader.read(lang).iterator();

        int count = 0;

        int maxArticles = 0;
        var withMaxArticles = new HashSet<String>();

        int minArticles = Integer.MAX_VALUE;
        var withMinArticles = new HashSet<String>();

        int sumArticles = 0;

        var articleSizeFrequency = new HashMap<Integer, Integer>();

        while (categories.hasNext()) {
            var cat = categories.next();
            var name = cat.getName();
            var articlesSize = cat.getArticles().size();

            if (articlesSize > maxArticles) {
                maxArticles = articlesSize;
                withMaxArticles.clear();
            }
            if (articlesSize >= maxArticles)
                withMaxArticles.add(name);

            if (articlesSize < minArticles) {
                minArticles = articlesSize;
                withMinArticles.clear();
            }
            if (articlesSize <= minArticles)
                withMinArticles.add(name);

            var frequency = articleSizeFrequency.getOrDefault(articlesSize, 0);
            articleSizeFrequency.put(articlesSize, frequency + 1);

            sumArticles += articlesSize;

            count++;
        }

        double avgArticles = ((double) sumArticles) / count;
        var highestFrequency = articleSizeFrequency.values().stream().mapToInt(i -> i).max().orElse(0);
        int mode = articleSizeFrequency.entrySet().stream()
                .filter(e -> e.getValue().equals(highestFrequency))
                .map(Entry::getKey)
                .findFirst().orElse(0);
        var withModeArticles = new HashSet<String>();

        var avgRoundedArticles = Math.round(avgArticles);
        var withAvgArticles = new HashSet<String>();
        categories = categoryReader.read(lang).iterator();

        while (categories.hasNext()) {
            var cat = categories.next();
            var name = cat.getName();
            var articlesSize = cat.getArticles().size();

            if (articlesSize == avgRoundedArticles)
                withAvgArticles.add(name);

            if (articlesSize == mode)
                withModeArticles.add(name);

        }

        return StatsResult.builder()
                .language(lang)
                .count(count)
                .maxArticlesPerCategory(maxArticles)
                .withMaxArticles(withMaxArticles)
                .minArticlesPerCategory(minArticles)
                .withMinArticles(withMinArticles)
                .averageArticlesPerCategory(avgArticles)
                .withAverageArticles(withAvgArticles)
                .modeArticlesPerCategory(mode)
                .withModeArticles(withModeArticles)
                .build();
    }
}
