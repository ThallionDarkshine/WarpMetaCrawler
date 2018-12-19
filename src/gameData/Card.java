package gameData;

import java.util.List;
import java.util.Set;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class Card {
    public enum Rarity {
        COMMON, RARE, EPIC, LEGENDARY
    }

    public enum Type {
        CREATURE, ACTION, SUPPORT, ITEM
    }

    public enum Attribute {
        STRENGTH, INTELLIGENCE, WILLPOWER, AGILITY, ENDURANCE, NEUTRAL
    }

    private String name;
    private Rarity rarity;
    private boolean unique;
    private Type type;
    private Set<Attribute> attributes;
    private String race;
    private int cost;
    private int attack;
    private int health;
    private String set;
    private String text;
    private List<String> keywords;
    private int percentUsage;

    public Card(String name, Rarity rarity, boolean unique, Type type, Set<Attribute> attributes, String race, int cost, int attack, int health, String set, String text, List<String> keywords, int percentUsage) {
        this.name = name;
        this.rarity = rarity;
        this.unique = unique;
        this.type = type;
        this.attributes = attributes;
        this.race = race;
        this.cost = cost;
        this.attack = attack;
        this.health = health;
        this.set = set;
        this.text = text;
        this.keywords = keywords;
        this.percentUsage = percentUsage;
    }

    public String getName() {
        return name;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public boolean getUnique() {
        return unique;
    }

    public Type getType() {
        return type;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public String getRace() {
        return race;
    }

    public int getCost() {
        return cost;
    }

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    public String getSet() {
        return set;
    }

    public String getText() {
        return text;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public int getPercentUsage() {
        return percentUsage;
    }
}
