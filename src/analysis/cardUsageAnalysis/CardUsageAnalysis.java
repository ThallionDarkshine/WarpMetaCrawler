package analysis.cardUsageAnalysis;

import gameData.TeslCardDesc;
import gameData.TeslDeckDesc;

import java.util.*;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class CardUsageAnalysis {
    public static void analyzeCardUsage(Collection<TeslCardDesc> cards, Collection<TeslDeckDesc> decks) {
        Map<TeslCardDesc, CardUsageData> cardUsageData = new HashMap<>();

        for (TeslCardDesc c : cards) {
            cardUsageData.put(c, new CardUsageData(c));
        }

        for (TeslDeckDesc d : decks) {
            for (Map.Entry<TeslCardDesc, CardUsageData> cardEntry : cardUsageData.entrySet()) {
                TeslCardDesc card = cardEntry.getKey();
                CardUsageData data = cardEntry.getValue();

                boolean eligible = d.getAttributes().containsAll(card.getAttributes());
                int copies = d.getCards().containsKey(card) ? d.getCards().get(card) : 0;

                data.addDeckData(eligible, copies, d.getPilotCount());
            }
        }

        List<Map.Entry<TeslCardDesc, CardUsageData>> sortedData = new ArrayList<>(cardUsageData.entrySet());
        sortedData.sort((o1, o2) -> Float.compare(o1.getValue().getPercentUsage(), o2.getValue().getPercentUsage()));

        List<String> cardNames = new ArrayList<>();
        List<Integer> cardDeckCounts = new ArrayList<>(), cardEligibleDeckCounts = new ArrayList<>();
        List<Float> cardDeckPercentages = new ArrayList<>(), cardAverageUsages = new ArrayList<>();

        for (Map.Entry<TeslCardDesc, CardUsageData> cardEntry : sortedData) {
            TeslCardDesc card = cardEntry.getKey();
            CardUsageData data = cardEntry.getValue();

            if (data.getDeckCount() == 0) continue;

            cardNames.add(card.getName());
            cardDeckCounts.add(data.getDeckCount());
            cardEligibleDeckCounts.add(data.getEligibleDeckCount());
            cardDeckPercentages.add(data.getPercentUsage());
            cardAverageUsages.add(data.getAverageCopies());

            System.out.printf("%s was played in %d of the %d eligible decks (%.2f%% of decks), with an average of %.2f copies per deck.\n", card.getName(), data.getDeckCount(), data.getEligibleDeckCount(), data.getPercentUsage(), data.getAverageCopies());
        }

        System.out.println(cardNames.size() + ", " + cardDeckCounts.size() + ", " + cardEligibleDeckCounts.size() + ", " + cardDeckPercentages.size() + ", " + cardAverageUsages.size());

        System.out.println(cardNames);
        System.out.println(cardDeckCounts);
        System.out.println(cardEligibleDeckCounts);
        System.out.println(cardDeckPercentages);
        System.out.println(cardAverageUsages);
    }
}
