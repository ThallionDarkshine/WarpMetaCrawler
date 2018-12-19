package gameData;

import analysis.clustering.Clusterable;
import javafx.beans.property.ReadOnlyMapWrapper;

import java.util.*;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class Deck extends Clusterable {
    private String name;
    private Map<Card, Integer> cards;
    private Set<Card.Attribute> attributes;
    private Map<Member, Integer> pilots;
    private int pilotCount;
    private int cardCount;

    public Deck(String name) {
        this.name = name;
        cards = new HashMap<>();
        attributes = new HashSet<>();
        pilots = new HashMap<>();
        pilotCount = 0;
        cardCount = 0;
    }

    public Deck(String name, Map<Card, Integer> cards) {
        this(name);

        for (Map.Entry<Card, Integer> entry : cards.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }

    public String getName() {
        return name;
    }

    public void addPilot(Member member) {
        int count = 0;

        if (pilots.containsKey(member)) {
            count = pilots.get(member);
        }

        pilots.put(member, count + 1);
        ++pilotCount;
    }

    public int getPilotCount() {
        return pilotCount;
    }

    public Map<Member, Integer> getPilots() {
        return Collections.unmodifiableMap(pilots);
    }

    public void add(Card card, int count) {
        int n = 0;

        if (card == null) return;

        if (cards.containsKey(card)) {
            n = cards.get(card);
        } else {
            attributes.addAll(card.getAttributes());
        }

        cards.put(card, n + count);
        cardCount += count;
    }

    public Map<Card, Integer> getCards() {
        return Collections.unmodifiableMap(cards);
    }

    public int getSize() {
        return cardCount;
    }

    public Set<Card.Attribute> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    public boolean matches(Deck other) {
        Map<Card, Integer> cardsCopy = new HashMap<>(cards);

        for (Map.Entry<Card, Integer> cardEntry : other.cards.entrySet()) {
            Card card = cardEntry.getKey();
            int count = cardEntry.getValue();

            if (cardsCopy.containsKey(card) && cardsCopy.get(card) == count) {
                cardsCopy.remove(card);
            } else {
                return false;
            }
        }

        return cardsCopy.isEmpty();
    }

    public Map<Integer, Integer> getCurve() {
        Map<Integer, Float> curve = new HashMap<>();
        float totalCount = 0;

        for (Map.Entry<Card, Integer> cardEntry : cards.entrySet()) {
            int cost = cardEntry.getKey().getCost();
            int count;

            for (int i = 0;i < 2;++i) {
                count = 0;
                totalCount += cardEntry.getValue() * Math.pow(0.5, i);

                if (curve.containsKey(cost)) {
                    count = curve.get(cost).intValue();
                }
                curve.put(cost, new Float(count + cardEntry.getValue() * Math.pow(0.5, i)));
            }
        }

        Map<Integer, Integer> cleanCurve = new HashMap<>();
        for (Map.Entry<Integer, Float> curveEntry : curve.entrySet()) {
            cleanCurve.put(curveEntry.getKey(), (int) (curveEntry.getValue() * 100 / totalCount));
        }

        return cleanCurve;
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
        if (!(other instanceof Deck)) return -1;

        Deck d = (Deck) other;

        return metricDistance(cards, d.cards);

        /*Map<Card, Integer> cardsCopy = new HashMap<>(cards);
        int difCount = 0;

        for (Map.Entry<Card, Integer> cardEntry : d.cards.entrySet()) {
            Card card = cardEntry.getKey();
            int count = cardEntry.getValue();
            int oCount = cardsCopy.containsKey(card) ? cardsCopy.get(card) : 0;

            if (count == oCount) {
                cardsCopy.remove(card);
            } else {
                difCount += Math.abs(count - oCount);
                cardsCopy.remove(card);
            }
        }

        for (Map.Entry<Card, Integer> cardEntry : cardsCopy.entrySet()) {
            Card card = cardEntry.getKey();
            int count = cardEntry.getValue();
            int oCount = d.cards.containsKey(card) ?  d.cards.get(card) : 0;

            if (count != oCount) {
                difCount += Math.abs(count - oCount);
            }
        }

        return difCount / (float) (cardCount + d.getSize());*/
    }

    private float curveDistance(Clusterable other) {
        if (!(other instanceof Deck)) return -1;

        Deck d = (Deck) other;

        return metricDistance(getCurve(), ((Deck) other).getCurve());
    }

    public float distance(Clusterable other) {
        return 0.5f * decklistDistance(other) + 0.5f * curveDistance(other);
    }

    public void merge(Deck other) {
        if (!matches(other)) {
            throw new RuntimeException("ERROR: Cannot merge decks that do not match.");
        }

        // TODO: Actually do something with names

        for (Map.Entry<Member, Integer> pilot : other.pilots.entrySet()) {
            int count = 0;

            if (pilots.containsKey(pilot.getKey())) {
                count = pilots.get(pilot.getKey());
            }

            pilots.put(pilot.getKey(), count + pilot.getValue());
            pilotCount += pilot.getValue();
        }
    }

    public void print() {
        System.out.println("Deck: " + name);
        System.out.println("\tDeck was played " + pilotCount + " time(s) by " + pilots.size() + " different player(s).");
        System.out.println("\t\t*****");
        for (Map.Entry<Card, Integer> cardEntry : cards.entrySet()) {
            System.out.println(cardEntry.getValue() + " x " + cardEntry.getKey().getName());
        }
        System.out.println("\t\t*****");
        System.out.println();
    }
}
