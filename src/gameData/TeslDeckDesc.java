package gameData;

import analysis.clustering.Clusterable;
import data.DeckDesc;

import java.util.*;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class TeslDeckDesc extends DeckDesc<TeslCardDesc> {
    private Set<TeslCardDesc.Attribute> attributes;
    private Map<Member, Integer> pilots;
    private int pilotCount;

    public TeslDeckDesc(String name) {
        super(TeslDeckRestrictions.RESTRICTIONS, name);

        attributes = new HashSet<>();
        pilots = new HashMap<>();
        pilotCount = 0;
    }

    public TeslDeckDesc(String name, Map<TeslCardDesc, Integer> cards) {
        this(name);

        for (Map.Entry<TeslCardDesc, Integer> entry : cards.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
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

    public boolean add(TeslCardDesc card, int count) {
        if (!cards.containsKey(card) && getSize() <= restrictions.maxSize() - count && count <= restrictions.cardLimit(card)) {
            attributes.addAll(card.getAttributes());
        }

        return super.add(card, count);
    }

    public Set<TeslCardDesc.Attribute> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    public boolean matches(TeslDeckDesc other) {
        Map<TeslCardDesc, Integer> cardsCopy = new HashMap<>(cards);

        for (Map.Entry<TeslCardDesc, Integer> cardEntry : other.cards.entrySet()) {
            TeslCardDesc card = cardEntry.getKey();
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

        for (Map.Entry<TeslCardDesc, Integer> cardEntry : cards.entrySet()) {
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

    public void merge(TeslDeckDesc other) {
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
        System.out.println("TeslDeckDesc: " + name);
        System.out.println("\tTeslDeckDesc was played " + pilotCount + " time(s) by " + pilots.size() + " different player(s).");
        System.out.println("\t\t*****");
        for (Map.Entry<TeslCardDesc, Integer> cardEntry : cards.entrySet()) {
            System.out.println(cardEntry.getValue() + " x " + cardEntry.getKey().getName());
        }
        System.out.println("\t\t*****");
        System.out.println();
    }
}
