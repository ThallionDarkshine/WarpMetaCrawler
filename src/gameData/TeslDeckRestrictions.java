package gameData;

import data.DeckDesc;
import data.DeckRestrictions;

/**
 * Created by ThallionDarkshine on 12/19/2018.
 */
public class TeslDeckRestrictions extends DeckRestrictions<TeslCardDesc> {
    public static final TeslDeckRestrictions RESTRICTIONS = new TeslDeckRestrictions();

    public int cardLimit(TeslCardDesc card) {
        return card.getUnique() ? 1 : 3;
    }

    public int minSize() {
        return 50;
    }

    public int maxSize() {
        return 100;
    }

    public boolean isValid(TeslCardDesc card, DeckDesc<TeslCardDesc> deck) {
        if (!(deck instanceof TeslDeckDesc)) return false;

        return ((TeslDeckDesc) deck).getAttributes().containsAll(card.getAttributes());
    }

    public boolean hasSideDeck() {
        return false;
    }
}
