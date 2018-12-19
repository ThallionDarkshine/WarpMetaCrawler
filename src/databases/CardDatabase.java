package databases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import gameData.Card;
import gameData.Deck;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class CardDatabase {
    private static Map<String, Card> cards = new HashMap<>();
    private static Map<Card, Integer> cardIndices = new HashMap<>();

    public static void add(Card card) {
        cards.put(card.getName(), card);
        cardIndices.put(card, cardIndices.size());
    }

    public static boolean has(String cardName) {
        return cards.containsKey(cardName);
    }

    public static Card get(String cardName) {
        if (has(cardName)) {
            return cards.get(cardName);
        } else {
            for (String s : cards.keySet()) {
                if (s.equalsIgnoreCase(cardName.trim())) {
                    return cards.get(s);
                }
            }

            System.out.println(cardName + " was not found.");

            return null;
        }
    }

    public static Map<String, Card> getCards() {
        return Collections.unmodifiableMap(cards);
    }

    public static void dumpData(File dir) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        for (Card c : cards.values()) {
            try {
                File cardFile = new File(dir, URLEncoder.encode(c.getName(), StandardCharsets.UTF_8.toString()) + ".json");

                String json = gson.toJson(gson.toJsonTree(c));

                PrintWriter w = new PrintWriter(new FileWriter(cardFile));
                w.print(json);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadData(File dir) {
        for (File f : dir.listFiles()) {
            try {
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new FileReader(f));
                Card card = gson.fromJson(reader, Card.class);

                add(card);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Float> buildDeckVector(Deck deck) {
        List<Float> deckVector = new ArrayList<>();

        for (int i = 0;i < cardIndices.size();++i) {
            deckVector.add(0f);
        }

        for (Map.Entry<Card, Integer> cardEntry : deck.getCards().entrySet()) {
            deckVector.set(cardIndices.get(cardEntry.getKey()), (float) cardEntry.getValue());
        }

        return deckVector;
    }
}
