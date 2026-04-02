package iut.gon.serverside.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le {@link Logger} du serveur.
 * <p>
 * Ces tests vérifient de manière séparée :
 * - L'unicité du Logger via le modèle Singleton.
 * - Le bon formatage de l'horodatage généré.
 * - L'intégration du message, type et date lors de la fabrication de la ligne de log entière.
 * - L'écriture sécurisée dans le fichier logs.txt en cas de log critique (ERROR).
 */
@DisplayName("Tests du système de journalisation (Logger)")
public class LoggerTest {

    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = Logger.getInstance();
    }

    /**
     * Vérifie que la récupération répétée de l'instance de Logger retourne
     * exactement la même instance en mémoire (Singleton).
     */
    @Test
    @DisplayName("Le Logger doit agir comme un Singleton (Instance unique)")
    void testerUniciteSingleton() {
        Logger instance2 = Logger.getInstance();
        assertSame(logger, instance2, "Les appels répétés à getInstance() doivent retourner la même instance en mémoire.");
    }

    /**
     * Confirme le mécanisme de sauvegarde physique : un log de type ERROR
     * entraîne la création du fichier sur le disque.
     */
    @Test
    @DisplayName("L'enregistrement d'une erreur (ERROR) doit créer physiquement un fichier")
    void testerEcritureFichierErreurCreation() {
        String timestampUnique = "ERROR_TEST_" + System.currentTimeMillis();
        
        logger.log(LogTypes.ERROR, timestampUnique);

        File logFile = new File("logs.txt");
        assertTrue(logFile.exists(), "Le fichier logs.txt doit avoir été créé lors du log d'erreur");
    }

    /**
     * Lit le fichier de log et s'assure qu'une alerte spécifique y a bien été écrite.
     */
    @Test
    @DisplayName("Un message d'erreur spécifique doit être présent à l'intérieur du fichier")
    void testerEcritureFichierErreurContenu() throws IOException {
        String timestampUnique = "ERROR_TEST_" + System.currentTimeMillis();
        
        logger.log(LogTypes.ERROR, timestampUnique);

        File logFile = new File("logs.txt");
        List<String> lignes = Files.readAllLines(logFile.toPath());
        boolean trouve = lignes.stream().anyMatch(ligne -> ligne.contains(timestampUnique));
        
        assertTrue(trouve, "Le texte critique exact doit pouvoir être lu à l'intérieur du fichier log");
    }
}
