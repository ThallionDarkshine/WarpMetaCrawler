package databases;

import analysis.clustering.Clusterable;
import gameData.Deck;
import gameData.Member;

import java.util.*;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class DeckDatabase {
    private static List<Deck> decks = new ArrayList<>();

    public static void add(Deck deck) {
        for (Deck d : decks) {
            if (d.matches(deck)) {
                d.merge(deck);

                return;
            }
        }

        decks.add(deck);
    }

    public static List<Deck> getDecks() {
        return Collections.unmodifiableList(decks);
    }

    public static List<Map.Entry<List<Float>, Clusterable>> buildDecksMatrix() {
        List<Map.Entry<List<Float>, Clusterable>> decksMatrix = new Vector<>();

        for (Deck d : decks) {
            decksMatrix.add(new AbstractMap.SimpleEntry<>(CardDatabase.buildDeckVector(d), d));
        }

        return decksMatrix;
    }
}
