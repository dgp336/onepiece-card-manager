package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ual.dra.optcg.repository.CardRepository;
import ual.dra.optcg.entity.Card;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardRepository cardRepository ;
    
    @PostMapping
    public ResponseEntity<Card> save(@RequestBody Card card) {
        Card saved = cardRepository.save(card);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
