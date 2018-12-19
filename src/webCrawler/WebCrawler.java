package webCrawler;

import databases.CardDatabase;
import databases.DeckDatabase;
import databases.MemberDatabase;
import gameData.Card;
import gameData.Deck;
import gameData.Member;
import javafx.util.Callback;
import org.apache.commons.text.WordUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class WebCrawler {
    private static final String WM_PLAYER_CSS_QUERY = "div.registrant-div div.mem-box";
    private static final String WM_PLAYER_NAME_CSS_QUERY = "span.mem-name";
    private static final String WM_PLAYER_DECK_CSS_QUERY = "div.mem-top div form";
    private static final String WM_DECK_ARGUMENTS_CSS_QUERY = "input";
    private static final Pattern WM_DECK_NAME_REGEX = Pattern.compile("###([^\n]+)###");
    private static final Pattern WM_DECK_CARD_REGEX = Pattern.compile("<br>(\\d)\\s(?:\\[card\\])?([^()\\[\\]\n]+)(?:\\[\\/card\\])?\\s?(?:\\([^)]*\\))?\\n");
    private static final String LD_CARD_CSS_QUERY = "div.col_thumb_card";
    private static final String LD_PAGE_NAV_CSS_QUERY = "a.pagin";
    private static final String LD_CARD_DATA_CSS_QUERY = "div.margintop.well_full table.table_large tr";
    private static final String LD_CARD_DATA_EL_CSS_QUERY = "td";
    private static final Pattern LD_CARD_ATTRIBUTE_REGEX = Pattern.compile("alt=\"(\\w+)\"");
    private static final Pattern LD_CARD_PERCENT_USAGE_REGEX = Pattern.compile(">(\\d+)\\s");

    public static void findAndExecute(Element element, String cssQuery, Callback<Element, Integer> elementFunction) {
        for (Element target : element.select(cssQuery)) {
            int statusCode = elementFunction.call(target);

            if (statusCode == -1) break;
        }
    }

    public static void wmPullDeckData(Element element, Member member) {
        String url = element.attr("action");

        Connection connection = Jsoup.connect(url);
        List<String> data = new ArrayList<>();

        findAndExecute(element, WM_DECK_ARGUMENTS_CSS_QUERY, el -> {
            data.add(el.attr("name"));
            data.add(el.attr("value"));

            return 0;
        });

        connection.data(data.toArray(new String[data.size()]));

        try {
            Document doc = connection.post();

            String deckData = doc.selectFirst("div.grid_3_of_12").html();

            System.out.println(deckData);

            Matcher matcher = WM_DECK_NAME_REGEX.matcher(deckData);
            matcher.find();
            String name = matcher.group(1);

            Map<Card, Integer> cards = new HashMap<>();
            matcher = WM_DECK_CARD_REGEX.matcher(deckData);
            while (matcher.find()) {
                String cardName = matcher.group(2).trim();

                int count = Integer.parseInt(matcher.group(1));
                Card card = CardDatabase.get(cardName);

                cards.put(card, count);
            }

            Deck deck = new Deck(name, cards);
            deck.addPilot(member);

            DeckDatabase.add(deck);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.out.println(member.getName());
            System.out.println(data);

            e.printStackTrace();
        }
    }

    public static void wmPullPlayerData(Element element) {
        String memName = element.selectFirst(WM_PLAYER_NAME_CSS_QUERY).text();
        Member member;

        if (!MemberDatabase.has(memName)) {
            member = new Member(memName);

            MemberDatabase.add(member);
        } else {
            member = MemberDatabase.get(memName);
        }

        System.out.println(memName);

        findAndExecute(element, WM_PLAYER_DECK_CSS_QUERY, el -> {
            wmPullDeckData(el, member);

            return 0;
        });
    }

    public static void wmPullTournamentData(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            findAndExecute(doc, WM_PLAYER_CSS_QUERY, element -> {
                wmPullPlayerData(element);

                return 0;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ldPullCardData(Element element) {
        String cardName = element.selectFirst("img").attr("alt");

        if (CardDatabase.has(cardName)) return;

        String url = element.selectFirst("a").absUrl("href");

        try {
            Document doc = Jsoup.connect(url).get();

            Map<String, Element> properties = new HashMap<>();

            findAndExecute(doc, LD_CARD_DATA_CSS_QUERY, el -> {
                Elements data = el.select(LD_CARD_DATA_EL_CSS_QUERY);

                String key = data.eq(0).first().text();
                Element value = data.eq(1).first();

                properties.put(key, value);

                return 0;
            });

            // Get all the simple properties set up first
            String name = properties.get("Name").text();
            Card.Type type = Card.Type.valueOf(properties.get("Type").text().toUpperCase());
            String race = properties.get("Race").text();
            int cost = Integer.parseInt(properties.get("Magicka Cost").text());
            int attack = properties.containsKey("Attack") ? Integer.parseInt(properties.get("Attack").text()) : -1;
            int health = properties.containsKey("Health") ? Integer.parseInt(properties.get("Health").text()) : -1;
            String set = properties.get("Expansion set").text();
            String text = properties.get("Text").text();
            List<String> keywords = Arrays.asList(properties.get("Keywords").text().split(","));

            // Next, determine whether the card is unique before dealing with rarity
            String[] rarityParts = properties.get("Rarity").text().split("\\s\\-\\s");
            boolean unique = rarityParts.length == 2;
            Card.Rarity rarity = Card.Rarity.valueOf(rarityParts[0].toUpperCase());

            // Next, going through the html for attributes
            Set<Card.Attribute> attributes = new HashSet<>();
            Matcher matcher = LD_CARD_ATTRIBUTE_REGEX.matcher(properties.get("Attributes").html());
            while (matcher.find()) {
                attributes.add(Card.Attribute.valueOf(matcher.group(1).toUpperCase()));
            }

            // Next, finding percent usage
            matcher = LD_CARD_PERCENT_USAGE_REGEX.matcher(properties.get("Played in").html());
            matcher.find();
            int percentUsage = Integer.parseInt(matcher.group(1));

            Card card = new Card(name, rarity, unique, type, attributes, race, cost, attack, health, set, text, keywords, percentUsage);

            CardDatabase.add(card);

            System.out.println(card.getName());
        } catch (IOException e) {
            System.out.println(url);

            e.printStackTrace();
        }
    }

    public static void ldPullCardsData(String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            while (true) {
                findAndExecute(doc, LD_CARD_CSS_QUERY, element -> {
                    ldPullCardData(element);

                    return 0;
                });

                System.out.println("page");

                Element nextButton = doc.select(LD_PAGE_NAV_CSS_QUERY).last();
                if (nextButton == null || !nextButton.text().equals(">")) {
                    break;
                } else {
                    doc = Jsoup.connect(nextButton.absUrl("href")).get();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
