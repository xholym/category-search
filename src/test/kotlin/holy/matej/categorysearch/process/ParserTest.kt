package holy.matej.categorysearch.process

import holy.matej.categorysearch.data.article
import holy.matej.categorysearch.data.category
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.stream.Stream

class ParserTest {

    val sut = Parser()

    @Test
    fun `parses correct categories`() {
        val data = Stream.of(
                "<http://sk.dbpedia.org/resource/Isaac_Newton>" +
                        " <http://purl.org/dc/terms/subject>" +
                        " <http://sk.dbpedia.org/resource/Kateg\\u00F3ria:Narodenia_v_1643>" +
                        " <http://sk.wikipedia.org/wiki/Isaac_Newton?oldid=5417081#section=Zdroje&relative-line=6&absolute-line=64> .",
                "<http://de.dbpedia.org/resource/Aussagenlogik>" +
                        " <http://purl.org/dc/terms/subject>" +
                        " <http://de.dbpedia.org/resource/Kategorie:Logik>" +
                        " <http://de.wikipedia.org/wiki/Aussagenlogik?oldid=116187293#section=Einzelnachweise_und_Anmerkungen&relative-line=5&absolute-line=711> .",
                "<http://dbpedia.org/resource/Albedo>" +
                        " <http://purl.org/dc/terms/subject>" +
                        " <http://dbpedia.org/resource/Category:Climate_forcing>" +
                        " <http://en.wikipedia.org/wiki/Albedo?oldid=547554152#section=External_link&relative-line=12&absolute-line=260> ."
        )
        val res = sut.parse(data)

        assertThat(res).containsExactlyInAnyOrder(
                category(
                        name = "Narodenia v 1643",
                        articles = listOf(
                                article(
                                        name = "Isaac Newton",
                                        link = "http://sk.wikipedia.org/wiki/Isaac_Newton?oldid=5417081#section=Zdroje&relative-line=6&absolute-line=64",
                                )
                        )
                ),
                category(
                        name = "Logik",
                        articles = listOf(
                                article(
                                        name = "Aussagenlogik",
                                        link = "http://de.wikipedia.org/wiki/Aussagenlogik?oldid=116187293#section=Einzelnachweise_und_Anmerkungen&relative-line=5&absolute-line=711",
                                )
                        )
                ),
                category(
                        name = "Climate forcing",
                        articles = listOf(
                                article(
                                        name = "Albedo",
                                        link = "http://en.wikipedia.org/wiki/Albedo?oldid=547554152#section=External_link&relative-line=12&absolute-line=260",
                                )
                        )
                ),
        )
    }
}