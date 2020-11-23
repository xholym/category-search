package holy.matej.categorysearch.process

import holy.matej.categorysearch.lang.Language.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.stream.Collectors.toList
import java.util.stream.Stream


class DataLoaderTest {

    val sut = DataLoader(Path.of("src/test/resources"))

    @Test
    fun `return right data for english`() {
        val records = sut.load(en)

        assertFirst(records,
                "<http://dbpedia.org/resource/Albedo> " +
                        "<http://purl.org/dc/terms/subject>" +
                        " <http://dbpedia.org/resource/Category:Climate_forcing>" +
                        " <http://en.wikipedia.org/wiki/Albedo?oldid=547554152#section=External_link&relative-line=12&absolute-line=260> ."
        )
    }

    @Test
    fun `return right data for slovak`() {
        val records = sut.load(sk)

        assertFirst(records,
                "<http://sk.dbpedia.org/resource/Isaac_Newton>" +
                        " <http://purl.org/dc/terms/subject>" +
                        " <http://sk.dbpedia.org/resource/Kateg\u00F3ria:Narodenia_v_1643>" +
                        " <http://sk.wikipedia.org/wiki/Isaac_Newton?oldid=5417081#section=Zdroje&relative-line=6&absolute-line=64> ."
        )
    }

    @Test
    fun `return right data for german`() {
        val records = sut.load(de)

        assertFirst(records,
                "<http://de.dbpedia.org/resource/Anschluss_(Soziologie)> " +
                        "<http://purl.org/dc/terms/subject> " +
                        "<http://de.dbpedia.org/resource/Kategorie:Soziologische_Systemtheorie> " +
                        "<http://de.wikipedia.org/wiki/Anschluss_(Soziologie)?oldid=100140917#section=Einzelnachweise&relative-line=4&absolute-line=15> ."
        )
    }

    private fun assertFirst(records: Stream<String>, expected: String) {
        val first = records.limit(2)
                .collect(toList())[1]

        assertThat(first).isEqualTo(expected)
    }
}
