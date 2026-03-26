package ual.dra.optcg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "products")
public class Product {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int productId;
    private String url;
    private String marketPrice;
    private String lowPrice;
    private String priceDate;
    private String variant;

    public Product() {
    }

    public Product(Card card, int productId, String url, String marketPrice, String lowPrice, String priceDate,
            String variant) {
        this.card = card;
        this.productId = productId;
        this.url = url;
        this.marketPrice = marketPrice;
        this.lowPrice = lowPrice;
        this.priceDate = priceDate;
        this.variant = variant;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(String priceDate) {
        this.priceDate = priceDate;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

}