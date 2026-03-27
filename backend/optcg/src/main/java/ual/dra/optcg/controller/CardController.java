package ual.dra.optcg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ual.dra.optcg.entity.Card;
import ual.dra.optcg.entity.Product;
import ual.dra.optcg.repository.CardRepository;
import ual.dra.optcg.service.CardDataLoaderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardDataLoaderService cardDataLoaderService;

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

    /**
     * Endpoint para cargar los datos de cartas desde la URL remota
     * POST /cards/load-data
     *
     * @return Un mapa con el estado de la operación
     */
    @PostMapping("/load-data")
    public ResponseEntity<Map<String, Object>> loadCardData() {
        Map<String, Object> response = new HashMap<>();
        try {
            int loadedCards = cardDataLoaderService.loadCardData();
            response.put("success", true);
            response.put("message", "Cartas cargadas exitosamente");
            response.put("cardsLoaded", loadedCards);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cargar las cartas: " + e.getMessage());
            response.put("cardsLoaded", 0);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para limpiar todas las cartas de la base de datos
     * DELETE /cards/clear-all
     *
     * @return Un mapa con el estado de la operación
     */
    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllCards() {
        Map<String, Object> response = new HashMap<>();
        try {
            long totalCards = cardRepository.count();
            cardDataLoaderService.clearAllCards();
            response.put("success", true);
            response.put("message", "Todas las cartas han sido eliminadas");
            response.put("cardsDeleted", totalCards);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al limpiar las cartas: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para recargar las cartas (limpiar y cargar de nuevo)
     * POST /cards/reload-data
     *
     * @return Un mapa con el estado de la operación
     */
    @PostMapping("/reload-data")
    public ResponseEntity<Map<String, Object>> reloadCardData() {
        Map<String, Object> response = new HashMap<>();
        try {
            cardDataLoaderService.clearAllCards();
            int loadedCards = cardDataLoaderService.loadCardData();
            response.put("success", true);
            response.put("message", "Datos recargados exitosamente");
            response.put("cardsLoaded", loadedCards);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al recargar las cartas: " + e.getMessage());
            response.put("cardsLoaded", 0);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}