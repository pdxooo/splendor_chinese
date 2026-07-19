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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/** Verifies that the production classic card catalogue matches the base game. */
public class ClassicCardCatalogTest {

    private static final String DEVELOPMENT_CATALOG_SHA256 =
            "94fe59dbec892211fd3b61e476ea82f59f3b10377e0557ebd55f7124e999d860";

    /** Checks all 90 development cards, including every printed cost. */
    @Test
    public void classicDevelopmentCardsMatchOfficialCatalog() throws Exception {
        List<String> cards = productionRows().stream()
                .filter(row -> Arrays.asList("1", "2", "3").contains(columns(row)[5]))
                .collect(Collectors.toList());

        assertEquals(90, cards.size());
        assertEquals(40, tierCount(cards, "1"));
        assertEquals(30, tierCount(cards, "2"));
        assertEquals(20, tierCount(cards, "3"));
        assertEquals(DEVELOPMENT_CATALOG_SHA256, sha256(String.join("\n", cards)));

        Map<String, Long> rewards = cards.stream().collect(Collectors.groupingBy(
                row -> columns(row)[0], Collectors.counting()));
        assertEquals(18L, rewards.get("White"));
        assertEquals(18L, rewards.get("Blue"));
        assertEquals(18L, rewards.get("Green"));
        assertEquals(18L, rewards.get("Red"));
        assertEquals(18L, rewards.get("Brown"));
    }

    /** Checks the exact ten nobles and their required permanent card bonuses. */
    @Test
    public void classicNoblesMatchOfficialCatalog() throws IOException {
        List<String> nobles = productionRows().stream()
                .filter(row -> "N".equals(columns(row)[5]))
                .collect(Collectors.toList());
        assertEquals(10, nobles.size());

        Map<String, String> expectedCosts = new HashMap<>();
        expectedCosts.put("20001", "4;4;0;0;0");
        expectedCosts.put("20002", "0;4;4;0;0");
        expectedCosts.put("20003", "0;0;4;4;0");
        expectedCosts.put("20004", "0;0;0;4;4");
        expectedCosts.put("20005", "4;0;0;0;4");
        expectedCosts.put("20006", "3;3;0;0;3");
        expectedCosts.put("20007", "3;3;3;0;0");
        expectedCosts.put("20008", "0;3;3;3;0");
        expectedCosts.put("20009", "0;0;3;3;3");
        expectedCosts.put("20010", "3;0;0;3;3");

        for (String noble : nobles) {
            String[] values = columns(noble);
            assertEquals("3", values[2]);
            assertEquals("Bonus", values[3]);
            assertEquals(expectedCosts.get(values[6]), values[4]);
            assertTrue(expectedCosts.containsKey(values[6]));
        }
        assertEquals(expectedCosts.size(), nobles.size());
    }

    private List<String> productionRows() throws IOException {
        return Files.readAllLines(Path.of("src/main/resources/cards.csv"), StandardCharsets.UTF_8)
                .stream().skip(1).collect(Collectors.toCollection(ArrayList::new));
    }

    private long tierCount(List<String> cards, String tier) {
        return cards.stream().filter(row -> tier.equals(columns(row)[5])).count();
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
