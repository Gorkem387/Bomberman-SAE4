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
     * S'assure que la génération d'horodatage ne renvoie pas une chaîne vide.
     */
    @Test
    @DisplayName("L'horodatage généré ne doit pas être nul")
    void testerFormatHorodatageNotNull() {
        String time = logger.getCurrentTimeFormated();
        assertNotNull(time, "L'heure générée ne doit pas être nulle");
    }

    /**
     * Vérifie que la longueur et les séparateurs de l'horodatage correspondent bien
     * au pattern défini (dd-MM-yyyy  HH:mm:ss).
     */
    @Test
    @DisplayName("L'horodatage généré doit respecter le formatage attendu (taille et séparateurs)")
    void testerFormatHorodatagePattern() {
        String time = logger.getCurrentTimeFormated();
        // Le format complet "dd-MM-yyyy  HH:mm:ss" devrait faire 20 ou 21 caractères selon la JVM
        assertTrue(time.length() >= 20, "La longueur du texte horodaté doit correspondre au pattern : au moins 20 caractères");
        assertTrue(time.contains("-"), "La date doit être séparée par des tirets");
        assertTrue(time.contains(":"), "L'heure doit être séparée par des deux-points");
    }

    /**
     * Vérifie que le type du message de log (ex: INFO) est bien présent
     * dans la chaine finale formatée.
     */
    @Test
    @DisplayName("La ligne de log complète inclut visuellement le type")
    void testerCreationLigneDeLogType() {
        String msgContent = "Test de journalisation";
        String logLine = logger.createLogMessage(LogTypes.INFO, msgContent);
        
        assertTrue(logLine.contains("INFO"), "La ligne de log finale doit indiquer le type (INFO)");
    }

    /**
     * S'assure que le contenu brut (message) passé au log est bien conservé 
     * intact l'intérieur de la chaine générée.
     */
    @Test
    @DisplayName("La ligne de log complète inclut intactement le message")
    void testerCreationLigneDeLogContenu() {
        String msgContent = "Test de journalisation";
        String logLine = logger.createLogMessage(LogTypes.INFO, msgContent);
        
        assertTrue(logLine.contains(msgContent), "Le contenu du message doit être présent intact");
    }

    /**
     * Vérifie par sa longueur que des métadonnées (comme l'heure) sont bien ajoutées au contenu.
     */
    @Test
    @DisplayName("La ligne de log complète est prolongée par l'horodatage")
    void testerCreationLigneDeLogLongeur() {
        String msgContent = "Test de journalisation";
        String logLine = logger.createLogMessage(LogTypes.INFO, msgContent);
        
        assertTrue(logLine.length() > msgContent.length() + 8, "Le message final doit être plus grand que son contenu brut pour contenir l'heure (et marge de sécurité)");
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
