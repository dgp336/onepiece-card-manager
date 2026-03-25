package ual.dra.optcg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ual.dra.optcg.entity.Card;
import ual.dra.optcg.entity.Product;
import ual.dra.optcg.repository.CardRepository;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @PostMapping
    public ResponseEntity<Card> save(@RequestBody Card card) {
        if (card.getProducts() != null) {
            for (Product p : card.getProducts()) {
                p.setCard(card); // importante para la FK card_id
            }
        }
        Card saved = cardRepository.save(card);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}