package ual.dra.optcg.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ual.dra.optcg.entity.Card;
import ual.dra.optcg.repository.CardRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardDataLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(CardDataLoaderService.class);
    private static final String CARD_DATA_URL = "https://cdn.cardkaizoku.com/card_data.json";

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Descarga los datos de cartas desde la URL remota y los guarda en la BD
     *
     * @return El número de cartas cargadas
     */
    public int loadCardData() {
        try {
            logger.info("Iniciando descarga de datos de cartas desde {}", CARD_DATA_URL);

            // Descargar el JSON
            String jsonData = restTemplate.getForObject(CARD_DATA_URL, String.class);

            if (jsonData == null || jsonData.isEmpty()) {
                logger.error("No se pudo descargar los datos de cartas");
                return 0;
            }

            // Parsear el JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonData);

            List<Card> cardsToSave = new ArrayList<>();

            // Procesar cada carta del JSON
            if (root.isArray()) {
                for (JsonNode cardNode : root) {
                    Card card = parseCardFromJson(cardNode);
                    if (card != null) {
                        cardsToSave.add(card);
                    }
                }
            } else if (root.isObject() && root.has("cards")) {
                // Si el JSON tiene un objeto con un campo "cards"
                JsonNode cardsArray = root.get("cards");
                if (cardsArray.isArray()) {
                    for (JsonNode cardNode : cardsArray) {
                        Card card = parseCardFromJson(cardNode);
                        if (card != null) {
                            cardsToSave.add(card);
                        }
                    }
                }
            }

            // Guardar todas las cartas
            if (!cardsToSave.isEmpty()) {
                cardRepository.saveAll(cardsToSave);
                logger.info("Se han cargado {} cartas exitosamente", cardsToSave.size());
                return cardsToSave.size();
            } else {
                logger.warn("No se encontraron cartas para cargar");
                return 0;
            }

        } catch (Exception e) {
            logger.error("Error al cargar los datos de cartas: {}", e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Parsea un nodo JSON y lo convierte en una entidad Card
     *
     * @param cardNode El nodo JSON de la carta
     * @return Una instancia de Card o null si hay error en el parseo
     */
    private Card parseCardFromJson(JsonNode cardNode) {
        try {
            String number = getFirstStringValue(cardNode, new String[] { "number", "cardNumber" }, "");
            String name = getFirstStringValue(cardNode, new String[] { "name", "cardName" }, "");

            // Validar que al menos tenga número y nombre
            if (number.isEmpty() || name.isEmpty()) {
                logger.warn("Carta sin número o nombre: {}", cardNode);
                return null;
            }

            Card card = new Card(
                    number,
                    getStringValue(cardNode, "cost", null),
                    getStringValue(cardNode, "attribute", null),
                    getFirstStringValue(cardNode, new String[] { "type", "cardType" }, null),
                    getStringValue(cardNode, "power", null),
                    getStringValue(cardNode, "counter", null),
                    getStringValue(cardNode, "color", null),
                    getStringValue(cardNode, "feature", null),
                    getStringValue(cardNode, "text", null),
                    getStringValue(cardNode, "rarity", null),
                    getStringValue(cardNode, "trigger", null),
                    getStringValue(cardNode, "block", null),
                    getFirstStringValue(cardNode, new String[] { "set", "cardSet" }, null),
                    getFirstStringValue(cardNode, new String[] { "img", "bucketImg" }, null));

            // Establecer el nombre que no está en el constructor
            card.setName(name);

            return card;

        } catch (Exception e) {
            logger.error("Error al parsear carta: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el valor string de un campo JSON, retornando un valor por defecto si
     * no existe
     *
     * @param node         El nodo JSON
     * @param fieldName    El nombre del campo
     * @param defaultValue El valor por defecto
     * @return El valor del campo o el valor por defecto
     */
    private String getStringValue(JsonNode node, String fieldName, String defaultValue) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return defaultValue;
    }

    private String getFirstStringValue(JsonNode node, String[] fieldNames, String defaultValue) {
        for (String fieldName : fieldNames) {
            if (node.has(fieldName) && !node.get(fieldName).isNull()) {
                return node.get(fieldName).asText();
            }
        }
        return defaultValue;
    }

    /**
     * Verifica si hay cartas en la BD
     *
     * @return true si hay cartas, false si está vacía
     */
    public boolean hasCards() {
        return cardRepository.count() > 0;
    }

    /**
     * Limpia todas las cartas de la BD
     */
    public void clearAllCards() {
        logger.info("Eliminando todas las cartas de la BD");
        cardRepository.deleteAll();
        logger.info("Cartas eliminadas");
    }
}
