package ual.dra.optcg.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @NotBlank(message = "Card number is required")
    private String number;

    private int cost;
    private String attribute;
    private String type;
    private int power;
    private int counter;
    private String color;
    private String feature;
    private String text;
    private String rarity;
    private String trigger;
    private int block;
    private String set;
    private String img;

    @OneToMany(mappedBy = "card")
    private ArrayList<Product> products;

    public Card() {
        this.products = new ArrayList<>();
    }

    public Card(String number, int cost, String attribute, String type, int power, int counter, String color, String feature, String text, String rarity, String trigger, int block, String set, String img) {
        this.number = number;
        this.cost = cost;
        this.attribute = attribute;
        this.type = type;
        this.power = power;
        this.counter = counter;
        this.color = color;
        this.feature = feature;
        this.text = text;
        this.rarity = rarity;
        this.trigger = trigger;
        this.block = block;
        this.set = set;
        this.img = img;
        this.products = new ArrayList<>();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public String getAttribute() {
        return attribute;
    }
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getPower() {
        return power;
    }
    public void setPower(int power) {
        this.power = power;
    }
    public int getCounter() {
        return counter;
    }
    public void setCounter(int counter) {
        this.counter = counter;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getFeature() {
        return feature;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getRarity() {
        return rarity;
    }
    public void setRarity(String rarity) {
        this.rarity = rarity;
    }
    public String getTrigger() {
        return trigger;
    }
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }
    public int getBlock() {
        return block;
    }
    public void setBlock(int block) {
        this.block = block;
    }
    public String getSet() {
        return set;
    }
    public void setSet(String set) {
        this.set = set;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public ArrayList<Product> getProducts() {
        return products;
    }
    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}