package analysis.cardUsageAnalysis;

import gameData.TeslCardDesc;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class CardUsageData {
    private TeslCardDesc card;
    private int deckCount, eligibleDeckCount, totalCopies;

    public CardUsageData(TeslCardDesc card) {
        this.card = card;
        deckCount = 0;
        eligibleDeckCount = 0;
        totalCopies = 0;
    }

    public CardUsageData(TeslCardDesc card, int deckCount, int eligibleDeckCount, int totalCopies) {
        this.card = card;
        this.deckCount = deckCount;
        this.eligibleDeckCount = eligibleDeckCount;
        this.totalCopies = totalCopies;
    }

    public int getDeckCount() {
        return deckCount;
    }

    public int getEligibleDeckCount() {
        return eligibleDeckCount;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public float getAverageCopies() {
        return totalCopies / (float) deckCount;
    }

    public float getPercentUsage() {
        return 100.0f * deckCount / eligibleDeckCount;
    }

    public void addDeckData(boolean eligible, int copies, int pilotCount) {
        deckCount += copies > 0 ? pilotCount : 0;
        eligibleDeckCount += eligible ? pilotCount : 0;
        totalCopies += copies * pilotCount;
    }
}
