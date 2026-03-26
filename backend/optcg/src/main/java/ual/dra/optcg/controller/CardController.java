package ual.dra.optcg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ual.dra.optcg.entity.Card;
import ual.dra.optcg.entity.Product;
import ual.dra.optcg.repository.CardRepository;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @GetMapping("/{cardId}/products")
    public ResponseEntity<List<Product>> getProducts(@PathVariable long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + cardId));
        List<Product> products = card.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PutMapping("/{cardId}/products")
    public ResponseEntity<Card> addProducts(@PathVariable long cardId, @RequestBody List<Product> products) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + cardId));

        if (products != null && !products.isEmpty()) {
            for (Product p : products) {
                card.addProduct(p);
            }
        }

        Card updated = cardRepository.save(card);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{cardId}/products/{productId}")
    public ResponseEntity<Card> deleteProduct(@PathVariable long cardId, @PathVariable long productId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + cardId));

        if (card.getProducts() != null) {
            card.getProducts().removeIf(p -> p.getProductId() == productId);
        }

        Card updated = cardRepository.save(card);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{cardId}/products")
    public ResponseEntity<Card> deleteAllProducts(@PathVariable long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with id: " + cardId));

        card.setProducts(null);

        Card updated = cardRepository.save(card);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}