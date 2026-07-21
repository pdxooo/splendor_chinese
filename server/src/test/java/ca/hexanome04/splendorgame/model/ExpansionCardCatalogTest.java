package ca.hexanome04.splendorgame.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/** Verifies the production Orient and Cities component catalogues. */
public class ExpansionCardCatalogTest {

    private static final Set<String> ORIENT_TYPES = Set.of("O1", "O2", "O3");

    /** Checks all 30 Orient cards, their tier split, printed data, and special effects. */
    @Test
    public void orientCardsMatchOfficialCatalog() throws Exception {
        List<String> cards = productionCardRows().stream()
                .filter(row -> ORIENT_TYPES.contains(columns(row)[5]))
                .collect(Collectors.toList());

        assertEquals(30, cards.size());
        assertTier(cards, "O1", 10, "073aa97e08a71fa51b4ef2c70bcabe0cf3f9b832c1c1b9b71f9d7ccf59c616c7");
        assertTier(cards, "O2", 10, "8721f27310acbf49f62ac7f75fca0e1f72ea9d1cef09f40591ca300fc10f84bc");
        assertTier(cards, "O3", 10, "f4d84c395149b2d071ffe3037f1db957c42d7795fe9778d667c6c1b236bbf01f");

        assertEquals(5, cards.stream().filter(row -> "Gold".equals(columns(row)[0])
                && "2".equals(columns(row)[1])).count());
        assertEquals(3, cards.stream().filter(row -> "1".equals(columns(row)[8])).count());
        assertEquals(2, cards.stream().filter(row -> "O1".equals(columns(row)[7])).count());
        assertEquals(5, cards.stream().filter(row -> "O2".equals(columns(row)[7])).count());
    }

    /** Checks the additional Orient noble and its printed requirements. */
    @Test
    public void orientNobleMatchesOfficialCatalog() throws Exception {
        List<String> nobles = productionCardRows().stream()
                .filter(row -> "ON".equals(columns(row)[5]))
                .collect(Collectors.toList());

        assertEquals(1, nobles.size());
        assertEquals(",0,3,Bonus,3;0;1;6;0,ON,20011,,,0", nobles.get(0));
    }

    /** Checks the seven double-sided Cities tiles (14 valid faces) exactly. */
    @Test
    public void cityTilesMatchOfficialCatalog() throws Exception {
        List<String> cities = Files.readAllLines(Path.of("src/main/resources/citycards.csv"),
                        StandardCharsets.UTF_8).stream().skip(1)
                .collect(Collectors.toCollection(ArrayList::new));

        assertEquals(14, cities.size());
        assertEquals("c2c870e75443e100afe8a1a33e0bebbe0fccc3aa54f55a1b3336794ca0e71115",
                sha256(String.join("\n", cities)));
        assertTrue(cities.stream().noneMatch(row -> "40015".equals(columns(row)[6])));
    }

    private void assertTier(List<String> cards, String tier, int expectedCount, String expectedHash)
            throws NoSuchAlgorithmException {
        List<String> tierCards = cards.stream().filter(row -> tier.equals(columns(row)[5])).toList();
        assertEquals(expectedCount, tierCards.size());
        assertEquals(expectedHash, sha256(String.join("\n", tierCards)));
    }

    private List<String> productionCardRows() throws IOException {
        return Files.readAllLines(Path.of("src/main/resources/cards.csv"), StandardCharsets.UTF_8)
                .stream().skip(1).collect(Collectors.toCollection(ArrayList::new));
    }

    private String[] columns(String row) {
        return row.split(",", -1);
    }

    private String sha256(String value) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte part : digest) {
            result.append(String.format("%02x", part));
        }
        return result.toString();
    }
}
