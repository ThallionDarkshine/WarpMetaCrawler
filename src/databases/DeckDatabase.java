package databases;

import analysis.clustering.Clusterable;
import gameData.ClusterableTeslDeck;
import gameData.TeslDeckDesc;

import java.util.*;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class DeckDatabase {
    private static List<TeslDeckDesc> decks = new ArrayList<>();

    public static void add(TeslDeckDesc deck) {
        for (TeslDeckDesc d : decks) {
            if (d.matches(deck)) {
                d.merge(deck);

                return;
            }
        }

        decks.add(deck);
    }

    public static List<TeslDeckDesc> getDecks() {
        return Collections.unmodifiableList(decks);
    }

    public static List<Map.Entry<List<Float>, Clusterable>> buildDecksMatrix() {
        List<Map.Entry<List<Float>, Clusterable>> decksMatrix = new Vector<>();

        for (TeslDeckDesc d : decks) {
            decksMatrix.add(new AbstractMap.SimpleEntry<>(CardDatabase.buildDeckVector(d), new ClusterableTeslDeck(d)));
        }

        return decksMatrix;
    }
}
