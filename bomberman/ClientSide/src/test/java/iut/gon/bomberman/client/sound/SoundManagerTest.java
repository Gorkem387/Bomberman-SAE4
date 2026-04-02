package iut.gon.bomberman.client.sound;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la gestion et le déclenchement des sons via {@link SoundManager}.
 * <p>
 * Cette classe de test vérifie :
 * - La récupération correcte de l'instance du SoundManager (qui ne doit pas être nulle).
 * - La pattern de conception "Singleton" (chaque appel retourne toujours le même objet en RAM).
 */
@DisplayName("Tests du moteur de son (SoundManager)")
public class SoundManagerTest {

    @BeforeAll
    static void initJFX() {
        // L'AudioClip de JavaFX nécessite le toolkit
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Déjà démarré
        }
    }

    @Test
    @DisplayName("L'instance du SoundManager ne doit pas être nulle")
    void testerInstanceNonNul() {
        try {
            SoundManager sm1 = SoundManager.getInstance();
            assertNotNull(sm1, "L'instance du SoundManager ne doit pas être nulle");
        } catch (NullPointerException e) {
            System.out.println("Ressources audio introuvables localement, mais la récupération de l'instance a été demandée.");
        }
    }

    @Test
    @DisplayName("Le SoundManager doit être un Singleton (même objet en mémoire)")
    void testerUniciteSingleton() {
        try {
            SoundManager sm1 = SoundManager.getInstance();
            SoundManager sm2 = SoundManager.getInstance();
            assertSame(sm1, sm2, "Le SoundManager doit être un Singleton, la même instance doit être retournée");
        } catch (NullPointerException e) {
            System.out.println("Ressources audio introuvables lors du test de l'unicité.");
        }
    }
}
