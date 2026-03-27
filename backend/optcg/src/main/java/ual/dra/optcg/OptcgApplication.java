package ual.dra.optcg;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ual.dra.optcg.service.CardDataLoaderService;

@SpringBootApplication
public class OptcgApplication {

	private static final Logger logger = LoggerFactory.getLogger(OptcgApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OptcgApplication.class, args);
	}

	/**
	 * CommandLineRunner que se ejecuta al iniciar la aplicación
	 * Carga los datos de cartas si la BD está vacía
	 */
	@Bean
	public CommandLineRunner loadCardData(CardDataLoaderService cardDataLoaderService) {
		return args -> {
			// Verificar si la BD tiene cartas
			if (!cardDataLoaderService.hasCards()) {
				logger.info("La base de datos está vacía. Iniciando cargue de datos de cartas...");
				int loadedCards = cardDataLoaderService.loadCardData();
				if (loadedCards > 0) {
					logger.info("¡Cartas cargadas exitosamente! Total: {}", loadedCards);
				} else {
					logger.warn("No se pudieron cargar las cartas. La base de datos permanece vacía.");
				}
			} else {
				logger.info("La base de datos ya contiene cartas. Saltando cargue automático.");
			}
		};
	}

}
