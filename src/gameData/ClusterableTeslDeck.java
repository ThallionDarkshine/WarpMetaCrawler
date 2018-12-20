package gameData;

import analysis.clustering.Clusterable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ThallionDarkshine on 12/19/2018.
 */
public class ClusterableTeslDeck extends Clusterable {
    public TeslDeckDesc deck;

    public ClusterableTeslDeck(TeslDeckDesc deck) {
        this.deck = deck;
    }

    private <T> float metricDistance(Map<T, Integer> stats, Map<T, Integer> oStats) {
        Map<T, Integer> statsCopy = new HashMap<>(stats);
        int difCount = 0, size = 0, oSize = 0;

        for (Map.Entry<T, Integer> statsEntry : oStats.entrySet()) {
            T el = statsEntry.getKey();
            int count = statsEntry.getValue();
            int oCount = statsCopy.containsKey(el) ? statsCopy.get(el) : 0;

            oSize += count;
            size += oCount;

            if (count == oCount) {
                statsCopy.remove(el);
            } else {
                difCount += Math.abs(count - oCount);
                statsCopy.remove(el);
            }
        }

        for (Map.Entry<T, Integer> statsEntry : statsCopy.entrySet()) {
            T el = statsEntry.getKey();
            int count = statsEntry.getValue();
            int oCount = oStats.containsKey(el) ?  oStats.get(el) : 0;

            size += count;

            if (count != oCount) {
                difCount += Math.abs(count - oCount);
            }
        }

        return difCount / (float) (size + oSize);
    }

    private float decklistDistance(Clusterable other) {
        if (!(other instanceof ClusterableTeslDeck)) return -1;

        TeslDeckDesc d = ((ClusterableTeslDeck) other).deck;

        return metricDistance(deck.getCards(), d.getCards());

        /*Map<TeslCardDesc, Integer> cardsCopy = new HashMap<>(cards);
        int difCount = 0;

        for (Map.Entry<TeslCardDesc, Integer> cardEntry : d.cards.entrySet()) {
            TeslCardDesc card = cardEntry.getKey();
            int count = cardEntry.getValue();
            int oCount = cardsCopy.containsKey(card) ? cardsCopy.get(card) : 0;

            if (count == oCount) {
                cardsCopy.remove(card);
            } else {
                difCount += Math.abs(count - oCount);
                cardsCopy.remove(card);
            }
        }

        for (Map.Entry<TeslCardDesc, Integer> cardEntry : cardsCopy.entrySet()) {
            TeslCardDesc card = cardEntry.getKey();
            int count = cardEntry.getValue();
            int oCount = d.cards.containsKey(card) ?  d.cards.get(card) : 0;

            if (count != oCount) {
                difCount += Math.abs(count - oCount);
            }
        }

        return difCount / (float) (cardCount + d.getSize());*/
    }

    private float curveDistance(Clusterable other) {
        if (!(other instanceof ClusterableTeslDeck)) return -1;

        TeslDeckDesc d = ((ClusterableTeslDeck) other).deck;

        return metricDistance(deck.getCurve(), d.getCurve());
    }

    public float distance(Clusterable other) {
        return 0.5f * decklistDistance(other) + 0.5f * curveDistance(other);
    }
}
